/* global customElements */

class CommandDialog extends HTMLElement
{
	constructor(elements)
	{
		super();
		this._elements = elements;
		this.addEventListener("click", e =>
		{
			e.stopPropagation();
			window.setTimeout(() => this.parentNode.removeChild(this), 0);
		});
	}

	connectedCallback()
	{
		var elements = this.appendChild(document.createElement("div"));
		this._elements.forEach(e =>
		{
			var clone = e.cloneNode(true);
			clone.addEventListener("click", () => e.click());
			clone.addEventListener("click", () => this.click());
			elements.appendChild(clone);
		});
	}
}

customElements.define('g-command-dialog', CommandDialog);