#!/usr/bin/env python3
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
        print(f'Loading {source}')
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
        data.to_csv(file_path, sep=sep, index=index)
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
        raise Exception(f"Failed to save data to JSON: {e}")

