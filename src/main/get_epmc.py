#!/usr/bin/env python3

import pandas as pd
import json
import yaml
import numpy as np
import requests
import re

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

def get_retracted_articles():
    """
    Get retracted articles from Europe PMC and save them to files.
    """
    global cursor_mark
    # Initialize the list to store retracted articles
    retracted_articles_ePMC = []

    print("Initial request to Europe PMC to get the retracted paper JSON")
    # Make the initial request to get the total number of results
    url = f'https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PUB_TYPE%3A%22retraction%20of%20publication%22&resultType=idlist&cursorMark={cursor_mark}&pageSize=1&format={format_type}'
    response = make_request(url)

    if response is None:
        return

    total = response.get('hitCount', 0)

    # Calculate the number of requests needed to retrieve all results
    num_requests = (total + page_size) // page_size + 1
    print(f"Will need to perform {num_requests} requests.")

    # Iterate through each page and append the results to the list
    for i in range(num_requests):
        cursor_mark = response.get('nextCursorMark')

        if cursor_mark is None:
            break

        url = f'https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PUB_TYPE%3A%22Retracted%20Publication%22&resultType=core&cursorMark={cursor_mark}&pageSize={page_size}&format={format_type}'
        print(f"Request {i + 1} of {num_requests}: {url}")

        response = make_request(url)
        if response is None:
            continue

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

def main():
    # Read the configuration from the YAML file
    try:
        with open('config.yaml', 'r') as config_file:
            config = yaml.safe_load(config_file)

        query_url = config.get('query_url')
        if query_url:
            get_retracted_articles()
        else:
            print("Query URL not found in the configuration file.")
    except FileNotFoundError as e:
        print("Error: config.yaml not found.")
    except Exception as e:
        print(f"An unexpected error occurred: {e}")

if __name__ == "__main__":
    main()
