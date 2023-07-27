#!/usr/bin/env python3
<<<<<<< HEAD
"""
This script collates the data from the different sources.

Author: Javier Millan Acosta <javier.millan.acosta@gmail.com>
"""
=======

>>>>>>> b1f4747cdc970936d57c8e610aeffa1fea130268
import pandas as pd
import json
import yaml

def load_data(source):
    """
    Load data from a TSV file for a given source.

    Parameters:
        source (str): The source from which to load the data.

    Returns:
        pd.DataFrame: A DataFrame containing the loaded data.
    """
    try:
        with open(f'data/{source}/all.tsv', 'r') as f:
            data_source = pd.read_csv(f, sep='\t')
    except FileNotFoundError:
        raise FileNotFoundError(f"TSV file for '{source}' not found.")
    return data_source

def load_json(source):
    """
    Load data from a JSON file for a given source.

    Parameters:
        source (str): The source from which to load the data.

    Returns:
        list: A list containing the loaded JSON data.
    """
    try:
        with open(f'data/{source}/retracts.json', 'r') as f:
            json_source = json.load(f)
    except FileNotFoundError:
        raise FileNotFoundError(f"JSON file for '{source}' not found.")
    return json_source

def save_data_to_csv(data, file_path, sep=',', index=False):
    """
    Save data to a CSV file.

    Parameters:
        data (pd.DataFrame): The DataFrame to be saved.
        file_path (str): The path to the output CSV file.
        sep (str, optional): The delimiter for the CSV file. Defaults to ','.
        index (bool, optional): Whether to include the index in the CSV. Defaults to False.
    """
    try:
<<<<<<< HEAD
        data.to_csv(file_path, sep=sep, index=False)
=======
        data.to_csv(file_path, sep=sep, index=index)
>>>>>>> b1f4747cdc970936d57c8e610aeffa1fea130268
    except Exception as e:
        raise Exception(f"Failed to save data to CSV: {e}")

def save_data_to_json(data, file_path):
    """
    Save data to a JSON file.

    Parameters:
        data (list): The list containing data to be saved as JSON.
        file_path (str): The path to the output JSON file.
    """
    try:
        with open(file_path, 'w') as f:
            json.dump(data, f)
    except Exception as e:
<<<<<<< HEAD
        raise Exception(f"Failed to save data to JSON: {e}")
=======
        raise Exception(f"Failed to save data to JSON: {e}")

def main():
    """
    Main function to load data from sources specified in the config, process it, and save it to output files.
    """
    # Read config file
    try:
        with open('config.yaml', 'r') as config_file:
            config = yaml.safe_load(config_file)
    except FileNotFoundError:
        raise FileNotFoundError("Config file 'config.yaml' not found.")

    # Load data from sources specified in the config
    all_data = pd.DataFrame()
    json_all = []
    for source in config['sources']:
        try:
            data_source = load_data(source)
            all_data = pd.concat([all_data, data_source])
        except Exception as e:
            raise Exception(f"Error loading data from '{source}': {e}")

        try:
            json_source = load_json(source)
            json_all.append(json_source)
        except Exception as e:
            raise Exception(f"Error loading JSON from '{source}': {e}")

    # Save processed data to respective files
    save_data_to_csv(all_data, 'data/all.tsv', sep='\t')
    save_data_to_csv(all_data[['pmid', 'doi']], 'data/PMIDS_DOIS.csv', sep=',', index=False)
    save_data_to_csv(all_data[['id']], 'data/ids.csv', sep=',', index=False)

    try:
        save_data_to_json(json_all, 'data/retracts.json')
    except Exception as e:
        raise Exception(f"Error saving JSON data: {e}")

if __name__ == "__main__":
    try:
        main()
    except Exception as e:
        print(f"Error occurred: {e}")
>>>>>>> b1f4747cdc970936d57c8e610aeffa1fea130268
