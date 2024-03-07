let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
 <style data-element="g-path">:host(*)
{
	gap: 4px;
	height: 16px;
	font-size: inherit;
	align-items: center;
	display: inline-flex;
	justify-content: flex-start;
}

::slotted(*)
{
	gap: 4px;
	height: 100%;
	display: flex;
	font-size: inherit;
	align-items: center;
	justify-content: space-between;
}

::slotted([data-icon])::before
{
	display: flex;
	font-size: inherit;
	font-family: "gate";
	align-items: center;
	justify-content: center;
	content: attr(data-icon);
}

::slotted(:not(:last-child)):after
{
	display: flex;
	color: #999999;
	content: "\\2275";
	font-size: inherit;
	align-items: center;
	font-family: "gate";
	pointer-events: none;
	justify-content: center;
}</style>`;
/* global customElements, template */

customElements.define('g-path', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});