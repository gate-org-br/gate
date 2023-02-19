let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	display: flex;
	font-family: gate;
	font-size: inherit;
	font-style: normal;
	align-items: center;
	justify-content: center;
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