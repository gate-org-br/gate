let template = document.createElement("template");
template.innerHTML = `
	<g-slider id='m' size="6">
	</g-slider>
	<g-slider id='y' size="6">
	</g-slider>
 <style>:host(*)
{
	flex-grow: 1;
	display: grid;
	grid-template-columns: 1fr 1fr;
}</style>`;

/* global customElements */

import './g-slider.js';

let months = ["Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"];

customElements.define('g-month-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		let date = new Date();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		let m = this.shadowRoot.getElementById("m");
		m.value = date.getMonth();
		m.prev = e => e > 0 ? e - 1 : 11;
		m.next = e => e < 11 ? e + 1 : 0;
		m.format = e => months[e];
		m.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		let y = this.shadowRoot.getElementById("y");
		y.value = date.getFullYear();
		y.prev = e => e - 1;
		y.next = e => e + 1;
		y.format = e => "0000".concat(String(e)).slice(-4);
		y.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		let m = this.shadowRoot.getElementById("m");
		let y = this.shadowRoot.getElementById("y");
		return "00".concat(String(m.value + 1)).slice(-2) + "/" + "0000".concat(String(y.value)).slice(-4);
	}
});