import re
import pandas as pd
import hashlib
import json
from rdflib import Graph, RDF, RDFS, FOAF, DC, Namespace, URIRef, Literal, OWL, DCTERMS
from rdflib.serializer import Serializer

def generate_uris(original_uri, existent_uris):
    # Convert 'existent_uris'['original'] to a list to perform the comparison
    if original_uri in existent_uris['original'].tolist():
        # Filter the DataFrame to get the 'uri' where 'original' matches 'original_uri'
        uri = existent_uris.loc[existent_uris['original'] == original_uri, 'uri'].values[0]
        return uri
    else:
        # Generate a unique hash for the original URI using SHA-256
        hash_value = hashlib.sha256(original_uri.encode()).hexdigest()

        # Take the first 8 characters from the hash as the identifier
        uri_identifier = hash_value[:8]

        # Ensure the identifier is unique by appending a numeric suffix if necessary
        count = 1
        while uri_identifier in existent_uris['uri'].tolist():  # Also, check for 'uri' in the list
            uri_identifier = f"{hash_value[:7]}{count:02d}"
            count += 1

        return uri_identifier

def make_rdf(input_json, existent_uri_file):
    seen = []
    # Get existent uris
    try:
        existent_uris = pd.read_csv(existent_uri_file)    
    except pd.errors.EmptyDataError:
        existent_uris = pd.DataFrame(columns=['uri', 'original'])
        existent_uris.to_csv(existent_uri_file, index=False)
    except FileNotFoundError:
        existent_uris = pd.DataFrame(columns=['uri', 'original'])
        existent_uris.to_csv(existent_uri_file, index=False)
    # Create graph
    g = Graph()
    # Add classes and properties
    RTT = Namespace("https://www.jmillanacosta.com/retractable/schema#")
    RTRCT = Namespace("https://www.jmillanacosta.com/retractable/")
    g.bind('dc', DC)
    g.bind('rdf', RDF)
    g.bind('foaf', FOAF)
    g.bind('rdfs', RDFS)
    g.bind('retrct', RTRCT)
    g.bind('retrct_terms', RTT)
    g.parse(source='data/rdf/classes.ttl', format='turtle')
    i=0
    for item in input_json:
        try:
            about = item.get("@rdf:about")
            seen.append(about)
            if not about:
                continue
            # Assign an internal identifier to "@rdf:about" field
            new_id = generate_uris(about, existent_uris)
            # Update URIS DataFrame
            row_data = pd.DataFrame([[about, new_id]], columns=['original','uri'])
            existent_uris = pd.concat([existent_uris, row_data])
            # Add the original value of "@rdf:about" as foaf:page
            foaf_page = about
            # Add to graph
            g.add((RTRCT[new_id], URIRef("http://www.w3.org/2000/01/rdf-schema#about"), URIRef(f'https://www.jmillanacosta.com/retractable/{new_id}')))
            g.add((RTRCT[new_id], FOAF.page, URIRef(foaf_page)))
            g.add((RTRCT[new_id], RDF.type, RTT['RetractedPaper']))
            # Retrieve the rest of the keys
            rest = [key for key in list(item.keys()) if key not in ["@rdf:about"]]
            if len(rest) != 0:
                for key in rest:
                    if ':' in key:
                        parts = key.split(":")
                        if 'dcterms' in key:
                            predicate = DCTERMS[parts[1]]
                        if 'dc:' in key:
                            predicate = DC[parts[1]]
                            g.add((RTRCT[new_id], predicate, Literal(item[key])))
            # Check for retraction and process it
            try:
                retraction = item['retraction']
                about_r = retraction.get("@rdf:about")
                if about_r in seen:
                    continue
                foaf_page_r = about_r
                if not about_r:
                    raise Exception("Missing '@rdf:about' in retraction")
                # Assign an internal identifier to "@rdf:about" field in retraction
                new_id_r = generate_uris(about_r, existent_uris)
                row_data = pd.DataFrame([[about_r, new_id_r]], columns=['original','uri'])
                # Update URIs DataFrame 
                existent_uris = pd.concat([existent_uris, row_data])
                # Add to graph
                g.add((RTRCT[new_id_r], RDF.type, RTT['RetractionNotice']))
                g.add((RTRCT[new_id_r], URIRef("http://www.w3.org/2000/01/rdf-schema#about"), URIRef(f'https://www.jmillanacosta.com/retractable/{new_id_r}')))
                g.add((RTRCT[new_id_r], FOAF.page, URIRef(foaf_page_r)))
                g.add((RTRCT[new_id_r], RTT['isRetractionNoticeOf'], URIRef(f'https://www.jmillanacosta.com/retractable/{new_id}')))
                g.add((RTRCT[new_id], RTT['hasRetractionNotice'], URIRef(f'https://www.jmillanacosta.com/retractable/{new_id_r}')))
                rest = [key for key in list(retraction.keys()) if key not in ["@rdf:about"]]
                if len(rest) != 0:
                    for key in rest:
                        if ':' in key:
                            parts = key.split(":")
                            if 'dcterms' in key:
                                predicate = DCTERMS[parts[1]]
                            if 'dc:' in key:
                                predicate = DC[parts[1]]
                                g.add((RTRCT[new_id_r], predicate, Literal(retraction[key])))
                print(f'{i} resources added to retractable RDF')
                i +=1
            except Exception as e:
                print(f'Exception {e}\tSkipping item')
        except Exception as e:
            print(f'Exception {e}\tSkipping item')
    print('Saving to json-ld...')
    g.serialize(format='json-ld', destination='data/retractable.jsonld') 
    print('\t- data/retractable.jsonld saved')
    print('Saving to turtle...')
    g.serialize(destination='data/rdf/retractable.ttl')
    print('\t- data/rdf/retractable.ttl saved')