/* global customElements */

class CoolbarDialog extends HTMLElement
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
			switch (e.tagName.toLowerCase())
			{
				case "a":
					elements.appendChild(new Link(e.cloneNode(true)).get())
						.addEventListener("click", () => this.click());
					break;
				case "button":
					elements.appendChild(new Button(e.cloneNode(true)).get())
						.addEventListener("click", () => this.click());
					break;
			}
		});
	}
}


customElements.define('g-coolbar-dialog', CoolbarDialog);