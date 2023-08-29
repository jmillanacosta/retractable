#!/usr/bin/env python3
import xmltodict
import pandas as pd
import requests
from concurrent.futures import ThreadPoolExecutor

USER_AGENT = "Mozilla/5.0"
page_size = 1000
format_type = 'dc'

def make_request(url):
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response
    except requests.exceptions.RequestException as e:
        print(f"An error occurred while making the request: {e}")
        return None

def get_retraction_info(article_url, source, id):
    try:
        print(f'Fetching retraction data for {source}{id}')
        article_url_i = article_url.format(source, id, 'json')
        article_retract = make_request(article_url_i).json().get('result', {}).get("commentCorrectionList", {}).get("commentCorrection", [])[0]
        retract_id = article_retract.get('id', 'NA')
        retract_source = article_retract.get('source', 'NA')
        retraction_url = article_url.format(retract_source, retract_id, 'dc')
        retraction_json = xmltodict.parse(make_request(retraction_url).text)['responseWrapper']

        rdf_retract = retraction_json.get('rdf:RDF', None)
        if rdf_retract is not None:
            retraction = rdf_retract.get('rdf:Description', None)
            if retraction is not None:
                return retraction
    except Exception as e:
        print(f"No retraction for {source}{id}: {e}")
    return None

def get_retracted_articles_epmc(query_url, article_url):
    url = query_url.format("*", "1", format_type)
    retracted_articles_epmc = []

    response = make_request(url)
    if response is None:
        raise requests.exceptions.InvalidURL

    dict_response = xmltodict.parse(response.text)['responseWrapper']
    total = int(dict_response.get('hitCount', 0))
    num_requests = (total + page_size) // page_size + 1

    with ThreadPoolExecutor() as executor:
        futures = []
        for i in range(num_requests):
            cursor_mark = dict_response.get('nextCursorMark')
            if cursor_mark is None:
                break
            url = query_url.format(cursor_mark, page_size, format_type)
            print(f"\nRequest {i + 1} of {num_requests + 1}: {url}")
            response = make_request(url)
            if response is None:
                continue
            dict_response = xmltodict.parse(response.text)['responseWrapper']
            if dict_response is None:
                continue
            try:
                results = dict_response.get('rdf:RDF', {}).get('rdf:Description', [])
            except Exception as e:
                print(f'Skipping item due to {e}')
                continue
            for j, item in enumerate(results):
                try:
                    if 'dcterms:abstract' in item:
                        item.pop('dcterms:abstract')
                        # Get retraction reason here? TODO

                    source = item['@rdf:about'].split('/', 4)[-1].split('/')[0]
                    id = item['@rdf:about'].split('/', 4)[-1].split('/')[1]
                    futures.append(executor.submit(get_retraction_info, article_url, source, id))

                except Exception as e:
                    print(f"Skipping item due to {e}")
                    continue

        for j, future in enumerate(futures):
            retraction = future.result()
            if retraction:
                article_info = results[j]  # Get the corresponding article info
                retracted_articles_epmc.append((article_info, retraction))

    return retracted_articles_epmc
