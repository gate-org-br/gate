class ReportSelector extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		var selector = this;

		this.appendChild(createLink("PDF", "&#x2218;"));
		this.appendChild(createLink("XLS", "&#x2219;"));
		this.appendChild(createLink("CSV", "&#x2220;"));

		function createLink(type, icon)
		{
			var link = selector.appendChild(document.createElement("a"));
			link.innerHTML = icon;
			link.setAttribute("data-type", type);
			link.addEventListener("click", () => selector.dispatchEvent(new CustomEvent('selected', {cancelable: false, detail: type})));
			return link;
		}
	}
}

window.addEventListener("load", () => customElements.define('report-selector', ReportSelector));