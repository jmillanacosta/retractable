# Basic visualization

This notebook provides some figures and stats on the Retractable data.



```python
import pandas as pd
import json
import numpy as np
import re
from IPython.display import display, Markdown
```

## Importing the data


```python
df = pd.read_csv('../../../data/all.tsv', sep='\t', dtype=str)[['doi','pmid','pmcid','id']]
df.head(5)
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
      <th>doi</th>
      <th>pmid</th>
      <th>pmcid</th>
      <th>id</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>10.1186/s40337-023-00808-w</td>
      <td>37210529</td>
      <td>PMC10199517</td>
      <td>MED37210529</td>
    </tr>
    <tr>
      <th>1</th>
      <td>10.1016/s0140-6736(23)00394-x</td>
      <td>36965963</td>
      <td>PMC10036125</td>
      <td>MED36965963</td>
    </tr>
    <tr>
      <th>2</th>
      <td>10.15586/aei.v51i3.810</td>
      <td>37169565</td>
      <td>NaN</td>
      <td>MED37169565</td>
    </tr>
    <tr>
      <th>3</th>
      <td>10.1111/cbdd.14243</td>
      <td>37089058</td>
      <td>NaN</td>
      <td>MED37089058</td>
    </tr>
    <tr>
      <th>4</th>
      <td>10.1177/11206721231169538</td>
      <td>37073083</td>
      <td>NaN</td>
      <td>MED37073083</td>
    </tr>
  </tbody>
</table>
</div>




```python
# Number of NAs or missing values
missing_values_info = df.isna().sum()
len_df = len(df['pmcid'])
display(Markdown("### Number of NAs or missing values"))
for col, count in missing_values_info.items():
    display(Markdown(f"- {col}: {count} of {len_df}"))

# Number of unique values
unique_values_info = df.nunique()
display(Markdown("### Number of unique values"))
for col, count in unique_values_info.items():
    display(Markdown(f"- {col}: {count}"))
```


### Number of NAs or missing values



- doi: 643 of 15550



- pmid: 0 of 15550



- pmcid: 7892 of 15550



- id: 0 of 15550



### Number of unique values



- doi: 14904



- pmid: 15550



- pmcid: 7658



- id: 15550

