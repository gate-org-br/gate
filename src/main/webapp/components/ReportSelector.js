class ReportSelector extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		var selector = this;
		var table = this.appendChild(document.createElement("table"));

		table.appendChild(document.createElement("col")).style.width = "64px";
		table.appendChild(document.createElement("col"));

		var tbody = table.appendChild(document.createElement("tbody"));


		tbody.appendChild(createRow("PDF", "&#x2218;"));
		tbody.appendChild(createRow("XLS", "&#x2219;"));
		tbody.appendChild(createRow("CSV", "&#x2220;"));


		function createRow(type, icon)
		{
			var line = document.createElement("tr");

			var td = line.appendChild(document.createElement("td"))
			td.style.textAlign = "center";
			td.appendChild(document.createElement("i")).innerHTML = icon;
			line.appendChild(document.createElement("td")).innerHTML = type;
			line.addEventListener("click", () => selector.dispatchEvent(new CustomEvent('selected',
					{cancelable: false, detail: type})));

			return line;
		}
	}
}


window.addEventListener("load", () =>
	customElements.define('report-selector', ReportSelector));