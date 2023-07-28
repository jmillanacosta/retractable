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
      url: "https://www.jmillanacosta.com/retractable/schema"
  version: 1.0
---


# RDF Schema Documentation for Retractable

## Introduction

The Retractable RDF schema provides a structured way to describe papers, retracted papers, and retraction notices. This documentation covers the classes and properties defined in the schema and how they are intended to be used.
Namespace

The schema uses the following namespace:

| Namespace URI                                 | Prefix   |
|-----------------------------------------------|----------|
| https://www.jmillanacosta.com/retractable/schema# | retrct   |


<h2><a href=#Classes>Classes</a></h2>
<h3><a href=#Paper>Paper </a></h3>

The `retrct:Paper` class represents a research paper or academic journal article.

| Object property           | Description                                                                                                                            |
|--------------------|-----------------------|
| rtrct:hasRetractionNotice         | Relates a retracted paper to its retraction notice.|

<h3><a href=#RetractedPaper>RetractedPaper </a></h3>

The `retrct:RetractedPaper` class represents a paper that has been retracted.

| Property           | Description                                                                                                                            |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| retrct:isRectractionNoticeOf         | Relates a retraction notice to its retracted paper|

<h3><a href=#RetractionNotice>RetractionNotice </a></h3>

The `retrct:RetractionNotice` class represents a notice indicating the retraction of a paper.

| Property           | Description                                                                                                                            |
|--------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| retrct:isRectractionNoticeOf         | Relates a retraction notice to its retracted paper|




<h2><a href=#ObjectProperties>Object Properties</a></h2>
<h3><a href=#hasRetractionNotice>hasRetractionNotice </a></h3>


The `retrct:hasRetractionNotice` object property relates a retracted paper" to its corresponding retraction notice.

| Property           | Description                                                                                                                       |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| rdfs:range         | "RetractionNotice" class  |
| rdfs:domain         | "RetractedPaper" class  |


<h3><a href=#isRetractionNoticeOf>isRetractionNoticeOf </a></h3>

The `retrct:isRetractionNoticeOf` object property is the inverse of `retrct:hasRetractionNotice`, relating a retraction notice to the publication it retracts.

| Property           | Description                                                                                                                       |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| rdfs:range         | "RetractedPaper" class
| rdfs:domain         | "RetractionNotice" class  |

<h2><a href=#Usage>Usage</a></h2>

You can use the "Retract" schema to create RDF statements that describe academic papers, retracted papers, and retraction notices. For example:

```turtle
@prefix retrct: <https://www.jmillanacosta.com/retractable/schema#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
```

- Define a research paper
```RDF
<https://example.com/paper/123> a retrct:Paper ;
    rdfs:label "Paper Title" ;
    dcterms:title "Paper Title" ;
    dcterms:creator "John Doe" .
```
- Define a retracted paper
```RDF
<https://example.com/retracted/456> a retrct:RetractedPaper ;
    rdfs:label "Retracted Paper Title" ;
    retrct:hasRetractionNotice <https://example.com/retractionNotice/789> .
```

- Define a retraction notice
```RDF
<https://example.com/retractionNotice/789> a retrct:RetractionNotice ;
    rdfs:label "Retraction Notice Title" ;
    dcterms:description "This paper has been retracted due to xyz reasons." .
```
In the above example, we have created instances of "Paper," "RetractedPaper," and "RetractionNotice" classes. The "RetractedPaper" is linked to its corresponding "RetractionNotice" using the "hasRetractionNotice" property.

