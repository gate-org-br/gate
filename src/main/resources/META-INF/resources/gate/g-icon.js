let template = document.createElement("template");
template.innerHTML = `<i><slot></slot></i> <style>* {
	box-sizing: border-box;
}

:host(*)
{
	display: flex;
	cursor: inherit;
	font-size: inherit;
	align-items: center;
	justify-content: center;
}

i
{
	cursor: inherit;
	font-family: gate;
	font-size: inherit;
	font-style: normal;
}</style>`;

/* global customElements, template */

customElements.define('g-icon', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});