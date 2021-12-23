let template = document.createElement("template");
template.innerHTML = `
	<g-date-time-selector id='min'>
	</g-date-time-selector>
	<g-date-time-selector id='max'>
	</g-date-time-selector>
 <style>:host(*)
{
	display:grid;
	border: 1px solid #CCCCCC;
	grid-template-rows: 1fr 1fr;
}</style>`;

/* global customElements, template */

customElements.define('g-date-time-interval-selector', class extends HTMLElement
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
		let min = this.shadowRoot.getElementById("min").selection;
		let max = this.shadowRoot.getElementById("max").selection;
		return min && max ? min + " - " + max : null;
	}

	set min(dateTime)
	{
		this.shadowRoot.getElementById("min").date = dateTime;
	}

	set max(dateTime)
	{
		this.shadowRoot.getElementById("max").date = dateTime;
	}
});