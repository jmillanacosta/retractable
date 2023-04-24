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
df
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }

    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>article</th>
      <th>pmcid</th>
      <th>url</th>
      <th>retraction_reason</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>MED36658737</td>
      <td></td>
      <td>https://europepmc.org/article/MED/36658737</td>
      <td></td>
    </tr>
    <tr>
      <th>1</th>
      <td>MED36961293</td>
      <td>PMC10054288</td>
      <td>https://europepmc.org/article/MED/36961293</td>
      <td></td>
    </tr>
    <tr>
      <th>2</th>
      <td>MED36658746</td>
      <td></td>
      <td>https://europepmc.org/article/MED/36658746</td>
      <td></td>
    </tr>
    <tr>
      <th>3</th>
      <td>MED37077123</td>
      <td></td>
      <td>https://europepmc.org/article/MED/37077123</td>
      <td></td>
    </tr>
    <tr>
      <th>4</th>
      <td>MED37000220</td>
      <td></td>
      <td>https://europepmc.org/article/MED/37000220</td>
      <td></td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>14494</th>
      <td>MED1079321</td>
      <td></td>
      <td>https://europepmc.org/article/MED/1079321</td>
      <td></td>
    </tr>
    <tr>
      <th>14495</th>
      <td>MED1088818</td>
      <td></td>
      <td>https://europepmc.org/article/MED/1088818</td>
      <td></td>
    </tr>
    <tr>
      <th>14496</th>
      <td>MED1203488</td>
      <td></td>
      <td>https://europepmc.org/article/MED/1203488</td>
      <td></td>
    </tr>
    <tr>
      <th>14497</th>
      <td>MED5409980</td>
      <td></td>
      <td>https://europepmc.org/article/MED/5409980</td>
      <td></td>
    </tr>
    <tr>
      <th>14498</th>
      <td>MED5644050</td>
      <td></td>
      <td>https://europepmc.org/article/MED/5644050</td>
      <td></td>
    </tr>
  </tbody>
</table>
<p>14499 rows × 4 columns</p>
</div>



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
