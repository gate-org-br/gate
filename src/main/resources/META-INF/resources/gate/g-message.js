let template = document.createElement("template");
template.innerHTML = `
	<main>
		<slot>
		</slot>
	</main>
 <style data-element="g-message">* {
	box-sizing: border-box;

}

:host(*) {
	gap: 12px;
	padding: 16px;
	display: flex;
	font-size: 16px;
	border-radius: 3px;
	align-items: center;
	background-color: var(--main1, #FFFFFF);
	border: 1px solid var(--main2, #F0F0F0);
	box-shadow: 1px 1px 2px 0px var(--main3, #DDDDDD);
}

main {
	display: flex;
	min-height: 2em;
	text-align: justify;
	align-items: stretch;
	flex-direction: column;
	justify-content: center;
}

:host(.icon)::before {
	display: flex;
	color: inherit;
	flex-shrink: 0;
	font-size: 2em;
	content: '\\2015';
	align-items: center;
	font-family: 'gate';
	justify-content: center;
}

:host(.fill) {
	box-shadow: none;
	color: var(--text1, #000000);
	background-color: var(--main3, #FFFFFF);
	border: 1px solid var(--main2, #F0F0F0);
}

:host(.error) {
	color: var(--r1);
}

:host(.error.fill) {
	background-color: var(--r3);
}

:host(.error.icon)::before {
	content: '\\1001';
}

:host(.success) {
	color: var(--g1, #003D26);
}

:host(.success.fill) {
	background-color: var(--g3, #A8F0C8);
}

:host(.success.icon)::before {
	content: '\\1000';
}

:host(.warning) {
	color: var(--y1, #808000);
}

:host(.warning.fill) {
	background-color: var(--y3, #FFFFCC);
}

:host(.warning.icon)::before {
	content: '\\1007';
}

:host(.question) {
	color: var(--b1, #1A2D5F);
}

:host(.question.fill) {
	background-color: var(--b3, #A8D8F0);
}

:host(.question.icon)::before {
	content: '\\1006';
}</style>`;
/* global customElements, template */

customElements.define('g-message', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}
});