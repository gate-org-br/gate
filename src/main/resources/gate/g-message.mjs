let template = document.createElement("template");
template.innerHTML = `
	<main>
		<slot>
		</slot>
	</main>
 <style>* {
	box-sizing: border-box;

}

:host(*) {
	gap: 12px;
	padding: 16px;
	display: flex;
	border-radius: 3px;
	align-items: center;
	background-color: #FCFCFC;
	border: 1px solid #f0f0f0;
	box-shadow: 1px 1px 2px 0px #CCCCCC;
}

main {
	display: flex;
	text-align: justify;
	align-items: stretch;
	flex-direction: column;
}

:host(.icon)::before {
	display: flex;
	color: inherit;
	flex-shrink: 0;
	font-size: 36px;
	content: '\\2015';
	align-items: center;
	font-family: 'gate';
	justify-content: center;
}

:host(.fill) {
	color: #000000;
	box-shadow: none;
	background-color: #F0F0F0;
}

:host(.error) {
	color: #660000;
}

:host(.error.fill) {
	background-color: #f8d7da;
}

:host(.error.icon)::before {
	content: '\\1001';
}

:host(.success) {
	color: #006600;
}

:host(.success.fill) {
	background-color: #d4edda;
}

:host(.success.icon)::before {
	content: '\\1000';
}

:host(.warning) {
	color: #666600;
}

:host(.warning.fill) {
	background-color: #fff3cd;
}

:host(.warning.icon)::before {
	content: '\\1007';
}

:host(.question) {
	color: #2f5674;
}

:host(.question.fill) {
	background-color: #cfdce6;
}

:host(.question.icon)::before {
	content: '\\1006';
}
</style>`;

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