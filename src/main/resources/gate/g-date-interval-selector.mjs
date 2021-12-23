let template = document.createElement("template");
template.innerHTML = `
	<g-date-selector id='min'>
	</g-date-selector>
	<g-date-selector id='max'>
	</g-date-selector>
 <style>:host(*)
{
	display:grid;
	border: 1px solid #CCCCCC;
	grid-template-columns: 1fr 1fr;
}</style>`;

/* global customElements, template */

customElements.define('g-date-interval-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let min = this.shadowRoot.getElementById("min");
		min.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		let max = this.shadowRoot.getElementById("max");
		max.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	connectedCallback()
	{
		this.min = new Date();
		this.max = new Date();
	}

	set min(value)
	{
		this.shadowRoot.getElementById("min").date = value;
	}

	get min()
	{
		return this.shadowRoot.getElementById("min").date;
	}

	set max(value)
	{
		this.shadowRoot.getElementById("max").date = value;
	}

	get max()
	{
		return this.shadowRoot.getElementById("max").date;
	}

	get selection()
	{
		let min = this.shadowRoot.getElementById("min").selection;
		let max = this.shadowRoot.getElementById("max").selection;
		return min && max ? min + " - " + max : null;
	}
});