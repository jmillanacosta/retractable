[![Fetch data](https://github.com/jmillanacosta/retractable/actions/workflows/fetch_data.yml/badge.svg)](https://github.com/jmillanacosta/retractable/actions/workflows/fetch_data.yml)

# Retractable

This projects queries journal article databases for retracted papers and collects them in a machine readable way. It aims to serve as an open-source alternative to the [Retraction Watch](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwj767jFpbGAAxWEG-wKHR1TB3AQFnoECB0QAQ&url=https%3A%2F%2Fretractionwatch.com%2F&usg=AOvVaw3oJSvWLxnlsBGenPZTl2rG&opi=89978449), though the reports are by no means as comprehensive as theirs. 

(Work in progress)

## Data

The `data` directory contains is structured as follows:
- all.tsv
- PMIDS_DOIS.csv
- retracts.json
- data/retractable.ttl

The subsets for each database (for now only EuropePMC) are provided under the `data` directory.

## Schema

Documentation on the RDF schema is available on:

https://www.jmillanacosta.com/retractable/schema


## Stats

Some stats available [here](/docs/basic_visualization.md)

## Tests

TBD