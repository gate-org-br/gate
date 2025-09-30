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
	background-color: var(--main1, #FFFFFF);

	border: 1px solid;
	border-left: 6px solid;
	border-color: var(--main3, #DDDDDD);

}

::slotted(*) {
	margin: 0;
}

::slotted(hr) {
	margin: 0;
	width: 100%;
	opacity: 0.2;
	align-self: center;
	border: 1px solid var(--text1, #000000);
}

:host(.fill) {
	color: var(--text1, #000000);
	border-color: var(--main3, #DDDDDD);
	background-color: var(--main3, #DDDDDD);
}

:host(.success) {
	color: var(--g1, #003D26);
	border-color: var(--g1, #003D26);
}

:host(.success) ::slotted(hr) {
	border-color: var(--g1, #003D26);
}

:host(.success.fill) {
	border-color: var(--g3, #A8F0C8);
	background-color: var(--g3, #A8F0C8);
}

:host(.warning) {
	color: var(--y1, #808000);
	border-color: var(--y1, #808000);
}

:host(.warning) ::slotted(hr) {
	border-color: var(--y1, #808000);
}

:host(.warning.fill) {
	border-color: var(--y3, #FFFFCC);
	background-color: var(--y3, #FFFFCC);
}

:host(.danger) {
	color: var(--r1, #5e0000);
	border-color: var(--r1, #5e0000);
}

:host(.danger) ::slotted(hr) {
	border-color: var(--r1, #5e0000);
}

:host(.danger.fill) {
	border-color: var(--r3, #F0A8A8);
	background-color: var(--r3, #F0A8A8);
}

:host(.question) {
	color: var(--b1, #1A2D5F);
	border-color: var(--b1, #1A2D5F);
}

:host(.question) ::slotted(hr) {
	border-color: var(--b1, #1A2D5F);
}

:host(.question.fill) {
	border-color: var(--b3, #A8D8F0);
	background-color: var(--b3, #A8D8F0);
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