let template = document.createElement("template");
template.innerHTML = `
	<div id='editor' tabindex="1" contentEditable='true'></div>
 <style data-element="g-text-viewer">:host(*)
{
	flex-grow: 1;
	display: flex;
	overflow: auto;
	line-height: normal;
	align-items: stretch;
	justify-content: stretch;
}

#editor
{
	flex-grow: 1;
	padding: 12px;
	outline: none;
	overflow: auto;
	font-size: 16px;
	text-align: justify;
	white-space: pre-wrap;
	border-radius: 0 0 5px 5px;
	background-color: transparent;
}

#editor > div {
	padding: 8px;
	display: flex;
	overflow: auto;
	margin: 4px 0 4px 0;
	height: fit-content;
	align-items: stretch;
	justify-content: center;
	border: 1px solid #EFEFEF;
	background-color: var(--hovered);
}</style>`;
/* global customElements */

customElements.define('g-text-viewer', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set value(value)
	{
		this.shadowRoot.querySelector("#editor").innerHTML = value || "";
	}

	get value()
	{
		return this.shadowRoot.querySelector("#editor").innerHTML;
	}

	attributeChangedCallback()
	{
		this.value = this.getAttribute("value");
	}

	static get observedAttributes()
	{
		return ["value"];
	}
});