let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-text-viewer">:host(*)
{
	display: block;
	overflow: auto;
}</style>`;
/* global customElements */

customElements.define('g-text-viewer', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set value(value)
	{
		this.shadowRoot.innerHTML = value || "";
	}

	get value()
	{
		return this.shadowRoot.innerHTML;
	}

	attributeChangedCallback()
	{
		this.value = this.getAttribute("value");
	}

	static get observedAttributes()
	{
		return ["value"];
	}
});