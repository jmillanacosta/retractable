---
layout: default

techArticle:
  "@context": http://schema.org/
  "@type": TechArticle
  abstract: Specification for the Retractable schema
  name: Retractable Schema
  author:
    - "@type": Person
      name: "Javier Millán Acosta"
      sameAs: "https://orcid.org/0000-0002-4166-7093"
  keywords: "RDF, ontology, schema, specification"
  license: "CC-BY 4.0"
  url:
    - "@type": URL
      url: "https://www.jmillanacosta.com/retractable/rdf/schema"
  version: 1.0
---

# RDF Schema Documentation for Retractable

## Introduction

The Retractable RDF schema provides a structured way to describe papers, retracted papers, retraction notices, retraction reasons, and more. This documentation covers the classes and properties defined in the schema and how they are intended to be used.

## Namespace

The schema uses the following namespace:

| Namespace URI                                          | Prefix          |
|--------------------------------------------------------|-----------------|
| https://www.jmillanacosta.com/retractable/schema#     | retrct_terms    |

## Classes

### RetractedPaper

The `retrct_terms:RetractedPaper` class represents a paper that has been retracted.

| Property                | Description                                              |
|-------------------------|----------------------------------------------------------|
| retrct_terms:hasRetractionNotice | Relates a retraction notice to its retracted paper     |

### RetractionNotice

The `retrct_terms:RetractionNotice` class represents a notice indicating the retraction of a paper.

| Property                | Description                                              |
|-------------------------|----------------------------------------------------------|
| retrct_terms:isRetractionNoticeOf | Relates a retraction notice to its retracted paper     |

### RetractionReason

The `retrct_terms:RetractionReason` class represents one or more reasons stated for the retraction of a paper.

| Property         | Description                                                      |
|------------------|------------------------------------------------------------------|
| cito:isConfirmedBy | Relates a retraction reason to its corresponding retraction     |
| cito:refutes     | Relates a retraction reason to the retracted paper it refutes    |

### Retraction

The `retrct_terms:Retraction` class represents causally related entities to retractions.

| Property         | Description                                                      |
|------------------|------------------------------------------------------------------|
| No specific properties defined for this class.                      |

## Object Properties

### hasRetractionNotice

The `retrct_terms:hasRetractionNotice` object property relates a retracted paper to its corresponding retraction notice.

| Property                 | Description                                              |
|--------------------------|----------------------------------------------------------|
| rdfs:range               | `retrct_terms:RetractionNotice` class                   |
| rdfs:domain              | `retrct_terms:RetractedPaper` class                     |

### isRetractionNoticeOf

The `retrct_terms:isRetractionNoticeOf` object property is the inverse of `retrct_terms:hasRetractionNotice`, relating a retraction notice to the publication it retracts.

| Property                 | Description                                              |
|--------------------------|----------------------------------------------------------|
| rdfs:range               | `retrct_terms:RetractedPaper` class                     |
| rdfs:domain              | `retrct_terms:RetractionNotice` class                   |

## Usage

You can use the "Retractable" schema to create RDF statements that describe academic papers, retracted papers, retraction notices, retraction reasons, and more. For example:

```turtle
@prefix retrct_terms: <https://www.jmillanacosta.com/retractable/schema#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
<https://example.com/paper/123> a retrct_terms:Paper ;
    rdfs:label "Paper Title" ;
    dc:title "Paper Title" ;
    dcterms:creator "Fulanito" .
<https://example.com/retracted/456> a retrct_terms:RetractedPaper ;
    rdfs:label "Retracted Paper Title" ;
    retrct_terms:hasRetractionNotice <https://example.com/retractionNotice/789> .
<https://example.com/retracted/456> a retrct_terms:RetractedPaper ;
    rdfs:label "Retracted Paper Title" ;
    retrct_terms:hasRetractionNotice <https://example.com/retractionNotice/789> .
<https://example.com/retractionNotice/789> a retrct_terms:RetractionNotice ;
    rdfs:label "Retraction Notice Title" ;
    dcterms:description "..." .
<https://example.com/retractionReason/abc> a retrct_terms:RetractionReason ;
    rdfs:label "Retraction Reason as stated" ;
    cito:isConfirmedBy <https://example.com/retractionNotice/789> ;
    cito:refutes <https://example.com/retracted/456> .
``````