let template = document.createElement("template");
template.innerHTML = `<i><slot></slot></i> <style>* {
	box-sizing: border-box;
}

i{
	display: flex;
	font-size: inherit;
	font-style: normal;
	font-family: 'gate';
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
		this.shadowRoot.innerHTML = template.innerHTML;
	}
});