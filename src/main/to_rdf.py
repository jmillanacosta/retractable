import re
import pandas as pd
import hashlib
import json
from rdflib import Graph, plugin
from rdflib.serializer import Serializer

def generate_uris(original_uri, existent_uris):
    # Generate a unique hash for the original URI using SHA-256
    hash_value = hashlib.sha256(original_uri.encode()).hexdigest()

    # Take the first 8 characters from the hash as the identifier
    uri_identifier = hash_value[:8]

    # Ensure the identifier is unique by appending a numeric suffix if necessary
    count = 1
    while uri_identifier in existent_uris:
        uri_identifier = f"{hash_value[:7]}{count:02d}"
        count += 1

    return uri_identifier


def to_jsonld(input_json, existent_uri_file):
    # Get existent uris
    try:
        with open(existent_uri_file, 'r') as f:
            existent_uris = list(f.readlines())
        
    except pd.errors.EmptyDataError:
        raise pd.errors.EmptyDataError
        
    # Define the context for the JSON-LD
    context = {
        "@context": {
            "dc": "http://purl.org/dc/elements/1.1/",
            "dcterms": "http://purl.org/dc/terms/",
            "rdf": "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "rdfs": "http://www.w3.org/2000/01/rdf-schema#",
            "retrct": "https://www.jmillanacosta.com/retractable/schema#",
            "foaf": "http://xmlns.com/foaf/0.1/",
            "license" : "https://creativecommons.org/publicdomain/zero/1.0/",
            "isAccessibleForFree" : 'true',
        },
        'name': 'Retractable',
        'description': 'The Retractable RDF provides a structured way to describe retracted papers and their retraction notices.',
        '@base':'http://www.jmillanacosta.com/retractable/',
        '@graph' : []
    }

    # Initialize the JSON-LD document with the context
    json_ld = context.copy()

    # Find all prefixes from the context and store them in a set
    context_json = json_ld["@context"]
    prefixes_set = set(re.findall(r'"(\w+)":', str(context_json)))

    # Iterate through the input JSON list and convert each item to JSON-LD
    for item in input_json:
        about = item.get("@rdf:about")
        item.pop("@rdf:about")
        # Assign an internal identifier to "@rdf:about" field
        new_uri = generate_uris(about, existent_uris) 
        item['@id'] = new_uri
        item["rdf:about"] = 'retrct:'+new_uri
        existent_uris.append(new_uri)
        # Add the original value of "@rdf:about" as foaf:page
        item["foaf:page"] = about
        item['rdf:type'] = 'retrct:RetractedPaper'
        # Check for missing keys and add them to the item with an empty list value
        for prefix in prefixes_set:
            if prefix not in item:
                item[prefix] = []
        
        # Take care of retraction for this item
        try:
            retraction = item['retraction']
            
            about_r = retraction.get("@rdf:about")
            retraction.pop("@rdf:about")
            # Assign an internal identifier to "@rdf:about" field
            new_uri_r = generate_uris(about_r, existent_uris)
            retraction['@id'] = new_uri_r 
            retraction["rdf:about"] = 'retrct:'+new_uri_r
            existent_uris.append(new_uri_r)
            # Add the original value of "@rdf:about" as foaf:page
            retraction["foaf:page"] = about_r
            retraction['rdf:type'] = 'retrct:RetractionNotice'

            # Check for missing keys and add them to the item with an empty list    value
            for prefix in prefixes_set:
                if prefix not in retraction:
                    retraction[prefix] = []
            # Add the item to the JSON-LD document
            item['retrct:hasRetractionNotice'] = 'retrct:'+new_uri_r
            json_ld['@graph'].append(item)
            
            # Add retraction to jsonld
            retraction['retrct:isRetractionNoticeOf'] = 'retrct:'+new_uri
            json_ld['@graph'].append(retraction)
            # Add axioms

        except:
            
            pass
        # Update uris file
    with open(existent_uri_file, 'w') as f:
        f.write("\n".join([i for i in existent_uris if i!=""]))

    return json_ld

def to_turtle(source):
    # Parse the JSON-LD data to an RDF graph
    # Extract the base IRI
    base_iri = 'https://www.jmillanacosta.com/retractable/'
    # Create an RDF graph
    graph = Graph()
    # Parse JSON-LD with the base IRI
    graph.parse(source=source, format='json-ld', publicID=base_iri)
    # Add fields, serialize data

    turtle_data = graph.serialize(format='turtle')
    with open('data/rdf/classes.ttl', 'r') as f:
        classes = f.read()
        turtle_data += classes

    return turtle_data