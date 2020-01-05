/* global customElements */

class ReportSelector extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		this.add("PDF", "&#x2218;");
		this.add("XLS", "&#x2221;");
		this.add("DOC", "&#x2220;");
		this.add("CSV", "&#x2222;");
	}

	add(type, icon)
	{
		var link = this.appendChild(document.createElement("a"));
		link.appendChild(document.createTextNode(type));
		link.appendChild(document.createElement("i")).innerHTML = icon;
		link.addEventListener("click", () => this.dispatchEvent(new CustomEvent('selected', {cancelable: false, detail: type})));
		return link;
	}
}

window.addEventListener("load", () => customElements.define('g-report-selector', ReportSelector));