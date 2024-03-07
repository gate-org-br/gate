let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
 <style data-element="g-grid-cell">* {
	box-sizing: border-box;
}

:host(*)
{
	padding: 4px;
	cursor: inherit;
	display: table-cell;
	vertical-align: middle;
	background-color: transparent;
}</style>`;
import './g-properties.js';

function element(content)
{
	switch (typeof content)
	{
		case "string":
			return document.createTextNode(content);
		case "number":
			return document.createTextNode(content);
		case "undefined":
			return document.createTextNode("");
		case "boolean":
			return document.createTextNode(content ? "Sim" : "Não");
		case "function":
			return element(content());
		case "object":
			if (!content)
				return document.createTextNode("");

			if (content instanceof HTMLElement)
				return content;

			if (Array.isArray(content))
			{
				let ul = document.createElement("ul");
				content.forEach(e => ul.appendChild(document.createElement("li")).appendChild(element(e)));
				return ul;
			}

			let properties = document.createElement("g-properties");
			properties.value = content;
			return properties;
	}
}

customElements.define('g-grid-cell', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
	}

	set value(value)
	{
		if (this.firstChild)
			this.firstChild.remove();
		this.appendChild(element(value));
	}
});