let template = document.createElement("template");
template.innerHTML = `
	<g-time-selector id='min'>
	</g-time-selector>
	<g-time-selector id='max'>
	</g-time-selector>
 <style>:host(*)
{
	flex-grow: 1;
	display:grid;
	border: 1px solid #CCCCCC;
	grid-template-columns: 1fr 1fr;
}</style>`;

/* global customElements, template */

customElements.define('g-time-interval-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("min").addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
		this.shadowRoot.getElementById("max").addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	set min(time)
	{
		this.shadowRoot.getElementById("min").time = time;
	}

	get min()
	{
		return this.shadowRoot.getElementById("min").time;
	}

	set max(time)
	{
		this.shadowRoot.getElementById("max").time = time;
	}

	get max()
	{
		return this.shadowRoot.getElementById("max").time;
	}

	get selection()
	{
		let min = this.shadowRoot.getElementById("min").selection;
		let max = this.shadowRoot.getElementById("max").selection;
		return min && max ? min + " - " + max : null;
	}
});