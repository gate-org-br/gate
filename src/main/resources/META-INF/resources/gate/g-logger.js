let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	padding: 4px;
	cursor: pointer;
	overflow-y: auto;
	border-radius: 5px;
	background-color: white;
}

span
{
	height: 16px;
	display: flex;
	align-items: center;
}</style>`;

/* global customElements */


customElements.define('g-logger', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	append(text)
	{

		this.shadowRoot.appendChild(document.createElement("span")).innerHTML = text;
		this.shadowRoot.lastElementChild.scrollIntoView();
	}
});