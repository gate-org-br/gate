let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	position: fixed;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
	background-color: var(--main-shaded05);
}</style>`;

/* global customElements, template */

export default class GLoading extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	static show()
	{
		if (!document.querySelector("g-loading"))
			document.documentElement.appendChild(document.createElement("g-loading"));
	}

	static hide()
	{
		if (document.querySelector("g-loading"))
			document.documentElement.removeChild(document.querySelector("g-loading"));
	}
}

customElements.define('g-loading', GLoading);