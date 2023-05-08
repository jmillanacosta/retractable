```python
%pip install openai
```


```python
import openai
import os
import pandas as pd
import json
import re
```


```python
filepath = "../data/retractable.json"
with open(filepath, 'r') as fp:
    data = json.load(fp)

df = pd.DataFrame.from_dict(data["retracted_articles"])
retrieved = df[df['retraction_body'] != ""]

```


```python
retrieved
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
      <th>1</th>
      <td>MED36961293</td>
      <td>PMC10054288</td>
      <td>https://europepmc.org/article/MED/36961293</td>
      <td>{"concerned reader":"We, the Editors and Publi...</td>
    </tr>
    <tr>
      <th>11</th>
      <td>MED37106383</td>
      <td>PMC10142152</td>
      <td>https://europepmc.org/article/MED/37106383</td>
      <td>{"concerned reader":"Retraction Note: Journal ...</td>
    </tr>
    <tr>
      <th>21</th>
      <td>PMC10139839</td>
      <td>PMC10139839</td>
      <td>https://europepmc.org/article/PMC/PMC10139839</td>
      <td>{"research misconduct and data manipulation":"...</td>
    </tr>
    <tr>
      <th>26</th>
      <td>MED37093887</td>
      <td>PMC10124820</td>
      <td>https://europepmc.org/article/MED/37093887</td>
      <td>{"concerned reader":"Following the publication...</td>
    </tr>
    <tr>
      <th>28</th>
      <td>MED37106357</td>
      <td>PMC10142444</td>
      <td>https://europepmc.org/article/MED/37106357</td>
      <td>{"concerned reader":"Retraction Note: Mol Canc...</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>14516</th>
      <td>MED195582</td>
      <td>PMC1164788</td>
      <td>https://europepmc.org/article/MED/195582</td>
      <td>{"unavailable":""}</td>
    </tr>
    <tr>
      <th>14518</th>
      <td>MED34959264</td>
      <td>PMC323952</td>
      <td>https://europepmc.org/article/MED/34959264</td>
      <td>{"unavailable":""}</td>
    </tr>
    <tr>
      <th>14521</th>
      <td>MED16578848</td>
      <td>PMC400275</td>
      <td>https://europepmc.org/article/MED/16578848</td>
      <td>{"unavailable":""}</td>
    </tr>
    <tr>
      <th>14524</th>
      <td>MED9066475</td>
      <td>PMC2126087</td>
      <td>https://europepmc.org/article/MED/9066475</td>
      <td>{"unavailable":""}</td>
    </tr>
    <tr>
      <th>14525</th>
      <td>MED5971970</td>
      <td>PMC2107046</td>
      <td>https://europepmc.org/article/MED/5971970</td>
      <td>{"unavailable":""}</td>
    </tr>
  </tbody>
</table>
<p>5153 rows × 4 columns</p>
</div>




```python
with open('assets/openAI_key.txt', 'r') as f:
    api_key = f.read().strip()

os.environ['OPENAI_API_KEY'] = api_key

import openai
openai.api_key = os.getenv("OPENAI_API_KEY")
```


```python
# Define the prompt to identify retraction notices
notice_prompt = "Please identify if the following text is a retraction notice:"

# Define the prompt to extract retraction reasons
reason_prompt = "Please extract the reason for retraction from the following text:"

# Define the list of retraction notices
notices = [
    "invalid results",
    "plagiarism",
    "conflict of interest, legal reasons",
    "research misconduct and data manipulation",
    "agreement by author(s)",
    "reader concerns",
    "issues with authorship",
    "duplicated paper",
    "could not reproduce results",
]

# Define a dictionary to store the results
results = {
    "invalid results": [],
    "plagiarism": [],
    "conflict of interest, legal reasons": [],
    "research misconduct and data manipulation": [],
    "agreement by author(s)": [],
    "reader concerns": [],
    "issues with authorship": [],
    "duplicated paper": [],
    "could not reproduce results": []
}
```


```python

# Loop over the retraction notices
for entry in data:
    notice = entry["retraction_body"]
    # Generate text to identify the retraction notice
    identification_text = notice_prompt + " " + notice
    identification_result = openai.Completion.create(
        engine="davinci",
        prompt=identification_text,
        max_tokens=1024,
        n=1,
        stop=None,
        temperature=0.7,
    )
    is_notice = identification_result.choices[0].text.strip().lower() == "yes"

    # If the text is a retraction notice, extract the reasons
    if is_notice:
        reason_text = reason_prompt + " " + notice
        reason_result = openai.Completion.create(
            engine="davinci",
            prompt=reason_text,
            max_tokens=1024,
            n=1,
            stop=None,
            temperature=0.7,
        )
        reason_string = reason_result.choices[0].text.strip().lower()
        for reason in results.keys():
            if reason in reason_string:
                match = re.search(reason + r".*", reason_string)
                results[reason].append(match.group())

# Print the results
print(results)

# Write the results to a file
with open("../data/davinci_retract_reason.json", "w") as f:
    json.dump(results, f)

# Print a success message
print("Results written to file.")
```
