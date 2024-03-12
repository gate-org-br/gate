let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style data-element="g-callout">* {
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
	color: var(--success1);
	border-color: var(--success1);
}

:host(.success.fill) {
	background-color: var(--success3);
}

:host(.success.fill) ::slotted(hr) {
	border-color: var(--success2);
}

:host(.warning) {
	color: var(--warning1);
	border-color: var(--warning1);
}

:host(.warning.fill) {
	background-color: var(--warning3);
}

:host(.warning.fill) ::slotted(hr) {
	border-color: var(--warning2);
}

:host(.danger) {
	color: var(--error1);
	border-color: var(--error1);
}

:host(.danger.fill) {
	background-color: var(--error3);
}

:host(.danger.fill) ::slotted(hr) {
	border-color: var(--error2);
}

:host(.question) {
	color: var(--question1);
	border-color: var(--question1);
}

:host(.question.fill) {
	background-color: var(--question3);
}

:host(.question.fill) ::slotted(hr) {
	border-color: var(--question2);
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