---
layout: default
---

{% include head.md %}

Takes in a list of DOIs or bib file and tells you if they are retracted according to Europe PMC.

The data queried from ePMC will be programatically updated to [retracted.json](https://raw.githubusercontent.com/jmillanacosta/retractable/main/data/retracted.json).

The data with retracted reason added is under [retractable.json](https://raw.githubusercontent.com/jmillanacosta/retractable/main/data/retractable.json). It can be visualized [here](/retractable/data)

Some stats available under [basic_stats](/retractable/basic_stats)

Notebook with the OpenAI llm (da vinci 003)-generated retraction reasons (demo, in progress)

work in progress...

{% include footer.md %}