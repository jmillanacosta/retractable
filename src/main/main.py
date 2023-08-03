#!/usr/bin/env python3
"""
Main Script to Execute get_epmc.py and collate.py

This script runs the 'get_epmc.py' script to query EuropePMC for
Retracted Publications and then runs the 'collate.py' script to
collate the results from each module into the main files in data.

Author: Javier Millán Acosta
"""

import logging
import yaml
from get_epmc import get_retracted_articles_epmc
from collate import load_json
from to_rdf import make_rdf
import pandas as pd 
import json

def main():
    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')
    print('_____________\nRETRACTABLE\n_____________\n')

    try:
        # Read the configuration from the YAML file for get_epmc.py
        try:
            with open('config.yaml', 'r') as config_file:
                config = yaml.safe_load(config_file)
            # Europe PMC
            print('1.   EuropePMC\n')
            query_url = config['sources']['ePMC']['query_url']
            article_url = config['sources']['ePMC']['article_url']
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
        except FileNotFoundError:
            logging.exception("Error: config.yaml not found.")
            exit(1)
        except Exception as e:
            logging.exception(f"An unexpected error occurred in get_epmc.py: {e}")
            exit(1)

        # Read the configuration from the YAML file for collate.py
        try:
            with open('config.yaml', 'r') as config_file:
                config = yaml.safe_load(config_file)
        except FileNotFoundError:
            logging.exception("Config file 'config.yaml' not found.", FileNotFoundError)
            exit(1)

        # Load data from sources specified in the config for collate.py
        json_all = []
        for source in config['sources']:
            try:
                json_source = load_json(source)
                json_all.extend(json_source)
            except Exception as e:
                logging.exception(f"Error loading JSON from '{source}' in collate.py: {e}")
                exit(1)

        # Fix json for json-ld
        try:
            uri_file = config.get('uri_file')
            make_rdf(json_all, uri_file)
            
        except Exception as e:
            logging.exception(f"Error saving data: {e}")

    except Exception as e:
        logging.exception("Error occurred while executing the scripts.")
        exit(1)

if __name__ == "__main__":
    main()
