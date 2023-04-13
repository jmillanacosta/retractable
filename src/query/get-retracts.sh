#!/bin/bash
# Gets retracted papers
# Define the query parameters
page_size=1000
cursor_mark='*'
format='json'
# Initialize the output file
output_file='../../data/retracted.json'


echo Initial request to Europe PMC to get the retracted paper list
# Make the initial request to get the total number of results
response=$(curl -X GET --header 'Accept: application/json' 'https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PUB_TYPE%3A%22retraction%20of%20publication%22&resultType=idlist&cursorMark=*&pageSize=1&format=json')
echo $response > initial.json
total=$(echo "$response" | jq '.hitCount')

# Calculate the number of requests needed to retrieve all results
num_requests=$(( ($total + $page_size - 1) / $page_size ))

echo Will need to perform $num_requests requests.
# Make a request for each page and append the results to the output file

echo "[" > "$output_file"
for ((j=0; j<$num_requests; j++)); do
  cursor_mark=$(echo "$response" | jq -r '.nextCursorMark')
  if [ "$cursor_mark" == "null" ]; then
    break
  fi
  if [ $j -ne 0 ]; then
    echo "," >> "$output_file"
  fi
  echo "Request $j"
  response=$(curl -X GET --header 'Accept: application/json' "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=PUB_TYPE%3A%22retraction%20of%20publication%22&resultType=idlist&cursorMark=$cursor_mark&pageSize=$page_size&format=json")
  echo "$response" | jq '.resultList.result[]' >> "$output_file"
done
echo "]" >> "$output_file"

rm initial.json