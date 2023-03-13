let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>* {
	box-sizing: border-box;

}

:host(*) {
	gap: 12px;
	padding: 12px;
	display: flex;
	font-size: 16px;
	text-align: justify;
	align-items: stretch;
	flex-direction: column;
	justify-content: center;
	background-color: #FFFFFF;
	border: 1px solid #cccccc;
	border-radius: 0 3px 3px 0;
	border-left: 6px solid #BBBBBB;
}

::slotted(*)
{
	margin: 0;
}

::slotted(hr) {
	margin: 0;
	width: 100%;
	align-self: center;
	border: 1px solid #F4F4F4;
}

:host(.fill) {
	color: #000000;
	border-color: #888888;
	background-color: #F4F4F4;
}

:host(.fill) ::slotted(hr)
{
	border-color: #dbdbdb;
}

:host(.primary) {
	color: #428bca;
	border-color: #428bca;
}

:host(.primary.fill) {
	color: #FFFFFF;
	border-color: #346fa1;
	background-color: #428bca;
}

:host(.primary.fill) ::slotted(hr)
{
	border-color: #5496cf;
}

:host(.success) {
	color: #5cb85c;
	border-color: #5cb85c;
}

:host(.success.fill) {
	color: #FFFFFF;
	border-color: #499349;
	background-color: #5cb85c;
}

:host(.success.fill) ::slotted(hr)
{
	border-color: #6cbf6c;
}

:host(.info) {
	color: #5bc0de;
	border-color: #5bc0de;
}

:host(.info.fill) {
	color: #FFFFFF;
	border-color: #4899b1;
	background-color: #5bc0de;
}

:host(.info.fill) ::slotted(hr)
{
	border-color: #6bc6e1;
}

:host(.warning) {
	color: #DAA520;
	border-color: #DAA520;
}

:host(.warning.fill) {
	color: #FFFFFF;
	border-color: #ae8419;
	background-color: #DAA520;
}

:host(.warning.fill) ::slotted(hr)
{
	border-color: #ddae36;
}

:host(.danger) {
	color: #d9534f;
	border-color: #d9534f;
}

:host(.danger.fill) {
	color: #FFFFFF;
	border-color: #ad423f;
	background-color: #d9534f;
}

:host(.danger.fill) ::slotted(hr)
{
	border-color: #dc6460;
}</style>`;

/* global customElements, template */

customElements.define('g-callout', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});