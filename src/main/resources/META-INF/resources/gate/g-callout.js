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
	border-radius: 0 3px 3px 0;
	background-color: var(--main1, white);

	border: 1px solid;
	border-left: 6px solid;
	border-color: var(--main6, #CCCCCC);

}

::slotted(*) {
	margin: 0;
}

::slotted(hr) {
	margin: 0;
	width: 100%;
	align-self: center;
	border: 1px solid var(--main3, "#F8F8F8");
}

:host(.fill) {
	color: var(--text, black);
	border-color: var(--text, black);
	background-color: var(--main4, #F0F0F0);
}

:host(.fill) ::slotted(hr) {
	border: 1px solid var(--main5, #DDDDDD);
}

:host(.success) {
	color: var(--g1, #006600);
	border-color: var(--g1, #006600);
}

:host(.success.fill) {
	background-color: var(--g3, #c7ecc7);
}

:host(.success.fill) ::slotted(hr) {
	border-color: var(--g2, #a3d8a3);
}

:host(.warning) {
	color: var(--y1, #808000);
	border-color: var(--y1, #808000);
}

:host(.warning.fill) {
	background-color: var(--y3, #FFFFCC);
}

:host(.warning.fill) ::slotted(hr) {
	border-color: var(--y2, #EEDD82);
}

:host(.danger) {
	color: var(--r1, #660000);
	border-color: var(--r1, #660000);
}

:host(.danger.fill) {
	background-color: var(--r3, #FFE4E1);
}

:host(.danger.fill) ::slotted(hr) {
	border-color: var(--r2, #FFCCCC);
}

:host(.question) {
	color: var(--b1, #444488);
	border-color: var(--b1, #444488);
}

:host(.question.fill) {
	background-color: var(--b3, #F0F8FF);
}

:host(.question.fill) ::slotted(hr) {
	border-color: var(--b2, #B0C4DE);
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