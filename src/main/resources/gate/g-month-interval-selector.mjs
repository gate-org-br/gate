let template = document.createElement("template");
template.innerHTML = `
	<g-month-selector id='min'>
	</g-month-selector>
	<g-month-selector id='max'>
	</g-month-selector>
 <style>:host(*)
{
	flex-grow: 1;
	display:grid;
	border: 1px solid #CCCCCC;
	grid-template-columns: 1fr 1fr;
}</style>`;

/* global customElements, template */

customElements.define('g-month-interval-selector', class extends HTMLElement
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

	get selection()
	{
		let min = this.shadowRoot.getElementById("min");
		min = min.selection;

		let max = this.shadowRoot.getElementById("max");
		max = max.selection;

		return min && max ? min + " - " + max : null;
	}
});