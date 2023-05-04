# Basic Statistics 

This notebook looks at the most common retraction reasons and generates figures.


```python
import pandas as pd
import json
import yaml
import numpy as np
```


```python
filepath = "../data/retractable.json"
with open(filepath, 'r') as fp:
    data = json.load(fp)

df = pd.DataFrame.from_dict(data["retracted_articles"])
# To html
df.to_html("../docs/_includes/retractable_data.html")
```

How many articles have a retraction notice?


```python
retract_notice = df[df['pmcid'] != ""]
len_notice = len(retract_notice)
len_data = len(df)
percentage_notice = round(len_notice/len_data*100, 2)
retrieved = df[df['retraction_reason'] != ""]
percentage_retrieved = round(len(retrieved)/len_data*100,2)
print(f"{percentage_notice}% of retracted articles in Europe PMC articles ({len_notice} out of {len_data}) have an associated retraction notice PMC id from which to extract retraction reasons.")
print(f"{percentage_retrieved}% of retraction reasons identified for retracted articles in Europe PMC articles ({len(retrieved)} out of {len_notice}).")
```

    46.98% of retracted articles in Europe PMC articles (6811 out of 14499) have an associated retraction notice PMC id from which to extract retraction reasons.
    35.37% of retraction reasons identified for retracted articles in Europe PMC articles (5128 out of 6811).


Which retraction reasons?
