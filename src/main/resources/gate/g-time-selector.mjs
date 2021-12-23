let template = document.createElement("template");
template.innerHTML = `
	<g-slider id='h' size="5" value='0'>
	</g-slider>
	<g-slider id='m' size="5" value='0'>
	</g-slider>
 <style>:host(*)
{
	flex-grow: 1;
	display:grid;
	border: 1px solid #CCCCCC;
	grid-template-columns: 1fr 1fr;
}</style>`;

/* global customElements */

customElements.define('g-time-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let h = this.shadowRoot.getElementById("h");
		h.prev = e => (e + 23) % 24;
		h.next = e => (e + 1) % 24;
		h.format = e => "00".concat(String(e)).slice(-2);
		h.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));

		let m = this.shadowRoot.getElementById("m");
		m.prev = e => (e + 50) % 60;
		m.next = e => (e + 10) % 60;
		m.format = e => "00".concat(String(e)).slice(-2);
		m.addEventListener("update", () => this.dispatchEvent(new CustomEvent('selected', {detail: this.selection})));
	}

	get selection()
	{
		let h = this.shadowRoot.getElementById("h");
		let m = this.shadowRoot.getElementById("m");
		return "00".concat(String(h.value)).slice(-2) + ":" + "00".concat(String(m.value)).slice(-2);
	}
});