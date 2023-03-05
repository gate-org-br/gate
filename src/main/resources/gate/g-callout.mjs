let template = document.createElement("template");
template.innerHTML = `
	<div>
	</div>
	<main part='main'>
		<slot>
		</slot>
	</main>
 <style>* {
	box-sizing: border-box;

}

:host(*) {
	padding: 1px;
	display: grid;
	background-color: #777777;
	grid-template-columns: 8px 1fr;
}

div {
	flex-basis: 8px;
	background-color: transparent;
}

main {
	padding: 16px;
	display: flex;
	font-size: 16px;
	align-items: center;
	background-color: #FFFFFF;
}

:host(.fill) {
	color: #FFFFFF;
	background-color: #666666;
}

:host(.fill) > main {
	background-color: #777777;
}

:host(.primary) {
	color: #428bca;
	background-color: #428bca;
}

:host(.primary.fill) {
	color: #FFFFFF;
	background-color: #317ab9;
}

:host(.primary.fill) > main {
	background-color: #428bca;
}

:host(.success) {
	color: #5cb85c;
	background-color: #5cb85c;
}

:host(.success.fill) {
	color: #FFFFFF;
	background-color: #5cb85c;
}

:host(.success.fill) > main {
	background-color: #4ba74b;
}

:host(.info) {
	color: #5bc0de;
	background-color: #5bc0de;
}

:host(.info.fill) {
	color: #FFFFFF;
	background-color: #4ab9cd;
}

:host(.info.fill) > main {
	background-color: #5bc0de;
}

:host(.warning) {
	color: #f0ad4e;
	background-color: #f0ad4e;
}

:host(.warning.fill) {
	color: #FFFFFF;
	background-color: #dd8913;
}

:host(.warning.fill) > main {
	background-color: #f0ad4e;
}

:host(.danger) {
	color: #d9534f;
	background-color: #d9534f;
}

:host(.danger.fill) {
	color: #FFFFFF;
	background-color: #c8423e;
}

:host(.danger.fill) > main {
	background-color: #d9534f;
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

	set type(value)
	{
		this.setAttribute("type", value);
	}

	get type()
	{
		return this.getAtribute("type");
	}
});