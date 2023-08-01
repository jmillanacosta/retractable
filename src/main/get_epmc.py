#!/usr/bin/env python3
import xmltodict, json
import pandas as pd
import json
import yaml
import numpy as np
import requests
import re

USER_AGENT = "Mozilla/5.0"
page_size = 1000
cursor_mark = '*'
format_type = 'dc'

def make_request(url):
    """
    Make an HTTP GET request to the given URL and return the response.

    Args:
        url (str): The URL to make the request to.

    Returns:
        response: the response object
    """
    try:
        response = requests.get(url)
        response.raise_for_status()  # Raise exception for non-200 status codes
        return response
    except requests.exceptions.RequestException as e:
        print(f"An error occurred while making the request: {e}")
        return None
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return None

def get_retracted_articles_epmc(query_url, article_url):
    """
    Get retracted articles from Europe PMC and save them to files.

    Args:
        query_url (str): The EuropePMC API URL for searching
        article_url (str): The EuropePMC API URL for an article
    """
    url=query_url
    global cursor_mark
    # Initialize the list to store retracted articles
    retracted_articles_ePMC = []

    print("Performing an initial request to Europe PMC to get the list of retracted papers")
    # Make the initial request to get the total number of results
    url = query_url.format(cursor_mark, "1", format_type)
    print(url)
    response = make_request(url)
    if response is None:
        raise requests.exceptions.InvalidURL
    o = xmltodict.parse(response.text)
    dict_response = o['responseWrapper']

    total = int(dict_response.get('hitCount', 0))

    # Calculate the number of requests needed to retrieve all results
    num_requests = (total + page_size) // page_size + 1
    print(f"Will need to perform {num_requests+1} requests.")
    j =0
    # Iterate through each page and append the results to the list
    for i in range(num_requests): 
        cursor_mark = dict_response.get('nextCursorMark')
        if cursor_mark is None:
            break
        url = query_url.format(cursor_mark, page_size, format_type)
        print(f"\nRequest {i+1} of {num_requests+1}: {url}")
        response = make_request(url)
        if response is None:
            continue
        o = xmltodict.parse(response.text)
        dict_response = o['responseWrapper']
        result = dict_response.get('rdf:RDF', {}).get('rdf:Description',{})
        j +=1

        for item in result:

            # Drop abstract
            if 'dcterms:abstract' in item.keys():
                item.pop('dcterms:abstract')
            print('Cummmulative of retracted papers found:', j, end='\r',flush=True)
            j+=1
            # Get retraction notice  
            source = item['@rdf:about'].split('/', 4)[-1].split('/')[0]
            id = item['@rdf:about'].split('/', 4)[-1].split('/')[1]
            article_url_i = article_url.format(source, id, 'json')
            article_retract = make_request(article_url_i).json().get('result', {}).get("commentCorrectionList", {}).get("commentCorrection", [])[0]
            retract_id = article_retract.get('id', 'NA')
            retract_source = article_retract.get('source', 'NA')
            retraction_url = article_url.format(retract_source, retract_id, 'dc')
            retraction_json = xmltodict.parse(make_request(retraction_url).text)['responseWrapper']
            item['retraction'] = retraction_json.get('rdf:RDF', {}).get('rdf:Description',{})
            if 'dcterms:abstract' in item['retraction'].keys():
                item['retraction'].pop('dcterms:abstract')
            if 'retraction' in item['retraction'].keys():
                item['retraction'].pop('retraction')
            retracted_articles_ePMC.extend([item])

    return retracted_articles_ePMC