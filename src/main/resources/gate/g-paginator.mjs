let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host {
	margin: 4px;
	float: right;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	border: 0;
	padding: 0;
	color: var(--b);
	font-size: 12px;
	cursor: pointer;
	background: none;
	font-weight: normal;
	text-decoration: none;
}</style>`;

/* global customElements */

customElements.define('g-paginator', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});