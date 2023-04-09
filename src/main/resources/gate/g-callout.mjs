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
	background-color: var(--main1);
	border-radius: 0 3px 3px 0;

	border: 1px solid;
	border-left: 6px solid;
	border-color: var(--main6);

}

::slotted(*)
{
	margin: 0;
}

::slotted(hr) {
	margin: 0;
	width: 100%;
	align-self: center;
	border: 1px solid var(--main3);
}

:host(.fill) {
	color: #000000;
	border-color: #000000;
	background-color: var(--main4);
}

:host(.fill) ::slotted(hr) {
	border: 1px solid var(--main5);
}

:host(.success) {
	color: #006600;
	border-color: #006600;
}

:host(.success.fill) {
	background-color: #c7ecc7;
}

:host(.success.fill) ::slotted(hr) {
	border-color: #a3d8a3;
}

:host(.warning) {
	color: #808000;
	border-color: #808000;
}

:host(.warning.fill) {
	background-color: #FFFFCC;
}

:host(.warning.fill) ::slotted(hr) {
	border-color: #EEDD82;
}

:host(.danger) {
	color: #660000;
	border-color: #660000;
}

:host(.danger.fill) {
	background-color: #FFE4E1  ;
}

:host(.danger.fill) ::slotted(hr) {
	border-color: #EEBBBB;
}

:host(.question) {
	color: #444488;
	border-color: #444488;
}

:host(.question.fill) {
	background-color: #F0F8FF ;
}

:host(.question.fill) ::slotted(hr) {
	border-color: #B0C4DE ;
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