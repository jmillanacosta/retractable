#!/usr/bin/env python3
"""
This script queries EuropePMC for Retracted Publications

Author: Javier Millan Acosta <javier.millan.acosta@gmail.com>
"""

import pandas as pd
import json
import yaml
import numpy as np
import requests
import re
import logging

USER_AGENT = "Mozilla/5.0"
page_size = 1000
cursor_mark = '*'
format_type = 'json'

def make_request(url):
    """
    Make an HTTP GET request to the given URL and return the JSON response.

    Args:
        url (str): The URL to make the request to.

    Returns:
        dict: The JSON response from the HTTP request.
    """
    try:
        response = requests.get(url)
        response.raise_for_status()  # Raise exception for non-200 status codes
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"An error occurred while making the request: {e}")
        return None
    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        return None

def get_retracted_articles_epmc(url, page_size=1000):
    """
    Get retracted articles from Europe PMC and save them to files.
    """
    global cursor_mark
    global format_type
    # Initialize the list to store retracted articles
    retracted_articles_ePMC = []
    initial_page_size = 1
    print("Initial request to Europe PMC to get the retracted paper JSON")
    # Make the initial request to get the total number of results
    url_init = url.format(cursor_mark, initial_page_size, format_type)
    response = make_request(url_init)

    if response is None:
        return

    total = response.get('hitCount', 0)

    # Calculate the number of requests needed to retrieve all results
    num_requests = (total + page_size) // page_size + 1
    print(f"Will need to perform {num_requests} requests.")

    # Iterate through each page and append the results to the list
    for i in range(num_requests):
        if i == 0: 
            next_cursor_mark = '*'
        else:
            next_cursor_mark = response.get('nextCursorMark')
        if next_cursor_mark is None:
            break
        new_url = url.format(next_cursor_mark, page_size, format_type)
        print(f"Request {i + 1} of {num_requests}: {new_url}")
        response = make_request(new_url)
        if response is None:
            logging.WARN(f'No response for {new_url}!')
            pass
        retracted_articles_ePMC.extend(response.get('resultList', {}).get('result', []))

    # Create the final JSON object and save to file
    output_json = json.dumps(retracted_articles_ePMC)
    try:
        with open("data/ePMC/retracts.json", "w") as f:
            f.write(output_json)
            print('Successful data fetch')
    except Exception as e:
        print(f"An error occurred while writing to file: {e}")

    # Save additional data to CSV
    try:
        with open('data/ePMC/PMIDS_DOIS.csv', 'w') as f:
            data = pd.DataFrame([[element.get('doi'), element.get('pmid'), element.get('pmcid'), "".join([element.get('source'), element.get('id')])] for element in retracted_articles_ePMC], columns=['doi', 'pmid', 'pmcid', 'id'])
            data.to_csv('data/ePMC/all.tsv', sep='\t')
    except Exception as e:
        print(f"An error occurred while writing CSV file: {e}")