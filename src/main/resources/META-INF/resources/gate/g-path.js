let template = document.createElement("template");
template.innerHTML = `
	<slot></slot>
 <style>:host(*)
{
	height: 16px;
	align-items: center;
	display: inline-flex;
	justify-content: flex-start;
}

::slotted(a),
::slotted(label)
{
	gap: 4px;
	height: 100%;
	display: flex;
	font-size: inherit;
	align-items: stretch;
	justify-content: space-between;
}

::slotted(a:not(:last-child)):after,
::slotted(label:not(:last-child)):after
{
	display: flex;
	color: #999999;
	font-size: inherit;
	content: "\\2275";
	align-items: stretch;
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