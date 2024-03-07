let template = document.createElement("template");
template.innerHTML = `
	<g-date-selector id='date'>
	</g-date-selector>
	<g-time-selector id='time'>
	</g-time-selector>
 <style data-element="g-date-time-selector">:host(*)
{
	display:grid;
	grid-template-columns: 1fr 1fr;
}</style>`;
/* global customElements, template */

customElements.define('g-date-time-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		let date = this.shadowRoot.getElementById("date");
		date.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		let time = this.shadowRoot.getElementById("time");
		time.addEventListener("selected", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		let date = this.shadowRoot.getElementById("date").selection;
		let time = this.shadowRoot.getElementById("time").selection;
		return date && time ? date + " " + time : null;
	}

	set date(date)
	{
		this.shadowRoot.getElementById("date").date = date;
	}
});