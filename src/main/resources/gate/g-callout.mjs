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
	border-radius: 0 3px 3px 0;

	border: 1px solid;
	border-left: 6px solid;
	border-color: #CCCCCC;

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
	border-color: #BBBBBB;
	background-color: #EEEEEE;
}

:host(.fill) ::slotted(hr) {
	border: 1px solid #E4E4E4;
}

:host(.success) {
	color: #006600;
	border-color: #006600;
}

:host(.success.fill) {
	background-color: #d4edda;
}

:host(.success.fill) ::slotted(hr) {
	border-color: #AADDAA;
}

:host(.warning) {
	color: #666600;
	border-color: #666600;
}

:host(.warning.fill) {
	background-color: #fff3cd;
}

:host(.warning.fill) ::slotted(hr) {
	border-color: #DDDDAA;
}

:host(.danger) {
	color: #660000;
	border-color: #660000;
}

:host(.danger.fill) {
	background-color: #f8d7da;
}

:host(.danger.fill) ::slotted(hr) {
	border-color: #EEBBBB;
}

:host(.question) {
	color: #444488;
	border-color: #444488;
}

:host(.question.fill) {
	background-color: #cfdce6;
}

:host(.question.fill) ::slotted(hr) {
	border-color: #CCCCFF;
}
</style>`;

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