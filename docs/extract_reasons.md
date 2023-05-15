```python
%pip install openai
```

    Requirement already satisfied: openai in /home/javier/miniconda3/lib/python3.9/site-packages (0.27.6)
    Requirement already satisfied: tqdm in /home/javier/miniconda3/lib/python3.9/site-packages (from openai) (4.64.1)
    Requirement already satisfied: requests>=2.20 in /home/javier/miniconda3/lib/python3.9/site-packages (from openai) (2.28.1)
    Requirement already satisfied: aiohttp in /home/javier/miniconda3/lib/python3.9/site-packages (from openai) (3.8.4)
    Requirement already satisfied: urllib3<1.27,>=1.21.1 in /home/javier/miniconda3/lib/python3.9/site-packages (from requests>=2.20->openai) (1.26.11)
    Requirement already satisfied: charset-normalizer<3,>=2 in /home/javier/miniconda3/lib/python3.9/site-packages (from requests>=2.20->openai) (2.0.4)
    Requirement already satisfied: certifi>=2017.4.17 in /home/javier/miniconda3/lib/python3.9/site-packages (from requests>=2.20->openai) (2022.12.7)
    Requirement already satisfied: idna<4,>=2.5 in /home/javier/miniconda3/lib/python3.9/site-packages (from requests>=2.20->openai) (3.3)
    Requirement already satisfied: frozenlist>=1.1.1 in /home/javier/miniconda3/lib/python3.9/site-packages (from aiohttp->openai) (1.3.3)
    Requirement already satisfied: multidict<7.0,>=4.5 in /home/javier/miniconda3/lib/python3.9/site-packages (from aiohttp->openai) (6.0.4)
    Requirement already satisfied: aiosignal>=1.1.2 in /home/javier/miniconda3/lib/python3.9/site-packages (from aiohttp->openai) (1.3.1)
    Requirement already satisfied: attrs>=17.3.0 in /home/javier/miniconda3/lib/python3.9/site-packages (from aiohttp->openai) (22.2.0)
    Requirement already satisfied: yarl<2.0,>=1.0 in /home/javier/miniconda3/lib/python3.9/site-packages (from aiohttp->openai) (1.9.2)
    Requirement already satisfied: async-timeout<5.0,>=4.0.0a3 in /home/javier/miniconda3/lib/python3.9/site-packages (from aiohttp->openai) (4.0.2)
    Note: you may need to restart the kernel to use updated packages.



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
with open('assets/openAI_key.txt', 'r') as f:
    api_key = f.read().strip()

os.environ['OPENAI_API_KEY'] = api_key

openai.api_key = os.getenv("OPENAI_API_KEY")
```


```python
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
results = {reason:[] for reason in notices}

# Define the prompt to extract retraction reasons
reasons_string = "\n-".join(results.keys())
reason_prompt = f"Given a list of possible reasons for retraction, please extract the reason(s) for retraction from a given text and return a comma-separated string of the identified reasons. The list of possible reasons is as follows: \n-{reasons_string} \nFor example, if the input text is: \"The article is suspected to contain manipulated data (...) The authors agreed to retract the article.\", the output should be \"research misconduct and data manipulation, agreement by author(s)\". There must not be any sentence in the output phrased differently than the provided retraction reason list."

print(results)
```

    {'invalid results': [], 'plagiarism': [], 'conflict of interest, legal reasons': [], 'research misconduct and data manipulation': [], 'agreement by author(s)': [], 'reader concerns': [], 'issues with authorship': [], 'duplicated paper': [], 'could not reproduce results': []}



```python

# Loop over the retraction notices
i=0
tokens = 0
llm_reasons_unfiltered = {}
llm_reasons_filtered = {}
for index, row in retrieved.iterrows(): 
    i+=1
    if i<100: #limit to 100 to keep it cheap for now
        notice = row["retraction_body"]
        #print(notice)
        id = row["article"]
        word_count = len(notice.split())
        tokens += word_count
        #print(f"Article with identifier {id} has a length of {word_count} characters, approximately {word_count*1000/750} tokens. ~{tokens}tokens used so far")
        reason_text = reason_prompt + " " + notice
        reason_result = openai.Completion.create(
            model="text-davinci-003",
            prompt=reason_text,
            max_tokens=1024,
            stop=None,
            temperature=0.0,# As deterministic as possible
        )
        reasons_string = reason_result["choices"][0]["text"].strip()
        #print(reasons_string)
        reasons_filtered = []
        for reason in notices: 
            if reason in reasons_string:
                reasons_filtered.append(reason)

        llm_reasons_unfiltered[id] = reasons_string.split(", ")
        llm_reasons_filtered[id] = reasons_filtered
        retrieved.at[index, "llm-reason"] = ", ".join(reasons_filtered)
        

```

    /tmp/ipykernel_165498/2830199522.py:32: SettingWithCopyWarning: 
    A value is trying to be set on a copy of a slice from a DataFrame.
    Try using .loc[row_indexer,col_indexer] = value instead
    
    See the caveats in the documentation: https://pandas.pydata.org/pandas-docs/stable/user_guide/indexing.html#returning-a-view-versus-a-copy
      retrieved.at[index, "llm-reason"] = ", ".join(reasons_filtered)



```python
with open('../data/llm_reason_unfiltered.json', 'w') as f:
    json.dump(llm_reasons_unfiltered, f)
with open('../data/llm_reason_filtered.json', 'w') as f:
    json.dump(llm_reasons_filtered, f)
```


```python
retrieved.to_html("../docs/_includes/retractable_data_llm.html")
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
      <th>retraction_body</th>
      <th>llm-reason</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>1</th>
      <td>MED36961293</td>
      <td>PMC10054288</td>
      <td>https://europepmc.org/article/MED/36961293</td>
      <td>{"concerned reader":"We, the Editors and Publi...</td>
      <td>We, the Editors and Publisher of the journal A...</td>
      <td>research misconduct and data manipulation, agr...</td>
    </tr>
    <tr>
      <th>6</th>
      <td>MED37141222</td>
      <td>PMC10159185</td>
      <td>https://europepmc.org/article/MED/37141222</td>
      <td>{"research misconduct and data manipulation":"...</td>
      <td>The PLOS ONE Editors retract this article [1] ...</td>
      <td>invalid results, research misconduct and data ...</td>
    </tr>
    <tr>
      <th>7</th>
      <td>MED37141216</td>
      <td>PMC10159117</td>
      <td>https://europepmc.org/article/MED/37141216</td>
      <td>{"research misconduct and data manipulation":"...</td>
      <td>After this article was published, similarities...</td>
      <td>invalid results, reader concerns, issues with ...</td>
    </tr>
    <tr>
      <th>10</th>
      <td>MED37137930</td>
      <td>PMC10156653</td>
      <td>https://europepmc.org/article/MED/37137930</td>
      <td></td>
      <td>Retraction of: Scientific Reports 10.1038/srep...</td>
      <td>plagiarism, research misconduct and data manip...</td>
    </tr>
    <tr>
      <th>11</th>
      <td>MED37143044</td>
      <td>PMC10161624</td>
      <td>https://europepmc.org/article/MED/37143044</td>
      <td>{"concerned reader":"Retraction Note: BMC Med ...</td>
      <td>Retraction Note: BMC Med 17, 223 (2019)https:/...</td>
      <td>research misconduct and data manipulation, dup...</td>
    </tr>
    <tr>
      <th>...</th>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
      <td>...</td>
    </tr>
    <tr>
      <th>14073</th>
      <td>MED12569089</td>
      <td>PMC2173750</td>
      <td>https://europepmc.org/article/MED/12569089</td>
      <td></td>
      <td>Reese, E.L., and L.T. Haimo. 2000. Dynein, dyn...</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>14087</th>
      <td>MED17554198</td>
      <td>PMC2637132</td>
      <td>https://europepmc.org/article/MED/17554198</td>
      <td>{"research misconduct and data manipulation":"...</td>
      <td>The article "Does the Oropharyngeal Fat Tissue...</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>14088</th>
      <td>MED17407607</td>
      <td>PMC1855922</td>
      <td>https://europepmc.org/article/MED/17407607</td>
      <td></td>
      <td>The corresponding author, Dr Wenbao Wang, subm...</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>14095</th>
      <td>PMC2001301</td>
      <td>PMC2001301</td>
      <td>https://europepmc.org/article/PMC/PMC2001301</td>
      <td></td>
      <td>The corresponding author submitted this articl...</td>
      <td>NaN</td>
    </tr>
    <tr>
      <th>14295</th>
      <td>MED9244806</td>
      <td>PMC2198959</td>
      <td>https://europepmc.org/article/MED/9244806</td>
      <td>{"research misconduct and data manipulation":"...</td>
      <td>Retraction: Kiehntopf, M., F. Herrmann, and M....</td>
      <td>NaN</td>
    </tr>
  </tbody>
</table>
<p>4704 rows × 6 columns</p>
</div>


