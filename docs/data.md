This page gives a table view of the [retracted paper data](data/retractable.json) as an html table.

    <script>
          let retractable;

    fetch('https://raw.githubusercontent.com/jmillanacosta/retractable/main/data/retractable.json')
  .then(response => response.json())
  .then(data => {
    retractable = data.retracted_articles; // access the retracted_articles array inside the object
    renderTable(retractable);
})
  .catch(error => console.error(error));

     
      // Function to render table from JSON data
      function renderTable(data) {
        

        const table = document.createElement("table");
        const headers = Object.keys(data[0]);
        const headerRow = document.createElement("tr");
        for (const header of headers) {
          const th = document.createElement("th");
          th.appendChild(document.createTextNode(header));
          headerRow.appendChild(th);
        }
        table.appendChild(headerRow);
        for (const row of data) {
          const tr = document.createElement("tr");
          for (const header of headers) {
            const td = document.createElement("td");
            td.appendChild(document.createTextNode(row[header]));
            tr.appendChild(td);
          }
          table.appendChild(tr);
        }
        document.body.appendChild(table);
      }
    </script>