This page gives a table view of the [retracted paper data](data/retractable.json) as an html table.

<html>
  <script>
    let retractable;
    let currentPage = 1;
    const rowsPerPage = 20;

    fetch(
      "https://raw.githubusercontent.com/jmillanacosta/retractable/main/data/retractable.json"
    )
      .then((response) => response.json())
      .then((data) => {
        retractable = data.retracted_articles; // access the retracted_articles array inside the object
        renderTable(retractable);
        renderPagination(retractable);
      })
      .catch((error) => console.error(error));

    // Function to render table from JSON data
    function renderTable(data) {
      const startIndex = (currentPage - 1) * rowsPerPage;
      const endIndex = startIndex + rowsPerPage;
      const table = document.createElement("table");
      const headers = Object.keys(data[0]);
      const headerRow = document.createElement("tr");
      for (const header of headers) {
        const th = document.createElement("th");
        th.appendChild(document.createTextNode(header));
        headerRow.appendChild(th);
      }
      table.appendChild(headerRow);
      for (let i = startIndex; i < endIndex && i < data.length; i++) {
        const row = data[i];
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

    // Function to render pagination buttons
    function renderPagination(data) {
      const totalPages = Math.ceil(data.length / rowsPerPage);
      const prevBtn = document.createElement("button");
      prevBtn.innerText = "Previous";
      prevBtn.disabled = true;
      const nextBtn = document.createElement("button");
      nextBtn.innerText = "Next";
      const pagination = document.createElement("div");
      pagination.appendChild(prevBtn);
      pagination.appendChild(nextBtn);
      document.body.appendChild(pagination);
      prevBtn.addEventListener("click", () => {
        currentPage--;
        renderTable(data);
        updatePagination();
      });
      nextBtn.addEventListener("click", () => {
        currentPage++;
        renderTable(data);
        updatePagination();
      });
      function updatePagination() {
        prevBtn.disabled = currentPage === 1;
        nextBtn.disabled = currentPage === totalPages;
      }
    }
  </script>
</html>
