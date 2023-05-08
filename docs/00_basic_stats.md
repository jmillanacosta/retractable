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

Which retraction reasons?


```python
!{sys.executable} -m pip install watermark
%load_ext watermark
%watermark -v -m 
```

    Requirement already satisfied: watermark in /home/javier/miniconda3/lib/python3.9/site-packages (2.3.1)
    Requirement already satisfied: ipython in /home/javier/miniconda3/lib/python3.9/site-packages (from watermark) (8.4.0)
    Requirement already satisfied: decorator in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (5.1.1)
    Requirement already satisfied: jedi>=0.16 in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (0.18.1)
    Requirement already satisfied: setuptools>=18.5 in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (63.4.1)
    Requirement already satisfied: pygments>=2.4.0 in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (2.14.0)
    Requirement already satisfied: traitlets>=5 in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (5.1.1)
    Requirement already satisfied: prompt-toolkit!=3.0.0,!=3.0.1,<3.1.0,>=2.0.0 in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (3.0.20)
    Requirement already satisfied: matplotlib-inline in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (0.1.6)
    Requirement already satisfied: backcall in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (0.2.0)
    Requirement already satisfied: pexpect>4.3 in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (4.8.0)
    Requirement already satisfied: stack-data in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (0.2.0)
    Requirement already satisfied: pickleshare in /home/javier/miniconda3/lib/python3.9/site-packages (from ipython->watermark) (0.7.5)
    Requirement already satisfied: parso<0.9.0,>=0.8.0 in /home/javier/miniconda3/lib/python3.9/site-packages (from jedi>=0.16->ipython->watermark) (0.8.3)
    Requirement already satisfied: ptyprocess>=0.5 in /home/javier/miniconda3/lib/python3.9/site-packages (from pexpect>4.3->ipython->watermark) (0.7.0)
    Requirement already satisfied: wcwidth in /home/javier/miniconda3/lib/python3.9/site-packages (from prompt-toolkit!=3.0.0,!=3.0.1,<3.1.0,>=2.0.0->ipython->watermark) (0.2.5)
    Requirement already satisfied: asttokens in /home/javier/miniconda3/lib/python3.9/site-packages (from stack-data->ipython->watermark) (2.0.5)
    Requirement already satisfied: executing in /home/javier/miniconda3/lib/python3.9/site-packages (from stack-data->ipython->watermark) (0.8.3)
    Requirement already satisfied: pure-eval in /home/javier/miniconda3/lib/python3.9/site-packages (from stack-data->ipython->watermark) (0.2.2)
    Requirement already satisfied: six in /home/javier/miniconda3/lib/python3.9/site-packages (from asttokens->stack-data->ipython->watermark) (1.16.0)
    The watermark extension is already loaded. To reload it, use:
      %reload_ext watermark
    Python implementation: CPython
    Python version       : 3.9.13
    IPython version      : 8.4.0
    
    Compiler    : GCC 11.2.0
    OS          : Linux
    Release     : 5.19.0-38-generic
    Machine     : x86_64
    Processor   : x86_64
    CPU cores   : 8
    Architecture: 64bit
    

