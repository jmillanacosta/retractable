#!/usr/bin/env python3
"""
Main Script to Execute get_epmc.py and collate.py

This script runs the 'get_epmc.py' script to query EuropePMC for
Retracted Publications and then runs the 'collate.py' script to
collate the results from each module into the main files in data.

Author: Javier Millán Acosta
"""

import argparse
import logging
import yaml
from get_epmc import get_retracted_articles_epmc
from collate import load_json
from to_rdf_epmc import make_rdf_epmc
import pandas as pd 
import json

# Set up logging
logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')


def main():
    parser = argparse.ArgumentParser(description="Process data from different sources.")
    parser.add_argument("--command", choices=["fetch", "RDF"], required=True, help="Specify the command.")
    parser.add_argument("--sources", nargs="+", required=False, help="Specify the sources to process.")
    args = parser.parse_args()
    # Read the configuration
    try:
        with open('config.yaml', 'r') as config_file:
            config = yaml.safe_load(config_file)
    except FileNotFoundError:
        logging.exception("Error: config.yaml not found.")
        exit(1)
    if args.command == "fetch":
        for source in args.sources:
            if source in config['sources']:
                if source == 'epmc':
                    print('1.   EuropePMC\n')
                    query_url = config['sources']['epmc']['query_url']
                    article_url = config['sources']['epmc']['article_url']
                    print(f'Query URL: {query_url}\n')
                    if query_url:
                        # Execute get_retracted_articles() from get_epmc.py
                        epmc = get_retracted_articles_epmc(query_url, article_url)
                        # Save to json
                        with open('data/epmc/retracts.json', 'w') as f:
                            json.dump(epmc, f)
                    else:
                        print("Query URL not found in the configuration file.")
                        exit(1)
                else:
                    logging.error(f'{source} is in the config file but not allowed.')
            else:
                logging.error(f"Source '{source}' is not in the list of allowed sources.")
        # Load data from sources specified in the config for collate.py
        json_all = []
        for source in config['sources']:
            try:
                json_source = load_json(source)
                json_all.extend(json_source)
            except Exception as e:
                logging.exception(f"Error loading JSON from '{source}' in collate.py: {e}")
                exit(1)
        with open('data/data.json', 'w') as f:
            json.dump(json_all, f)
    

    elif args.command == "RDF":
        uri_file = config.get('uri_file')
        try:
            json_all = load_json('data/data.json')
        except FileNotFoundError:
            print('No data to RDFy - fetch data first')
        for source in args.sources:
            if source in config['sources']:
                if source == 'epmc':
                    
                    make_rdf_epmc(json_all, uri_file)

                else:
                    logging.error(f'{source} is in the config file but not allowed.')
            else:
                logging.error(f"Source '{source}' is not in the list of allowed sources.")

if __name__ == "__main__":
    main()