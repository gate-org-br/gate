let template = document.createElement("template");
template.innerHTML = `
	<div part='bar'>
	</div>
	<main part='main'>
		<slot>
		</slot>
	</main>
 <style>* {
	box-sizing: border-box;

}

:host(*) {
	display: flex;
}

div {
	border-left: 4px solid #777777;
}

main {
	padding: 16px;
	display: flex;
	font-size: 16px;
	align-items: center;
	border: 1px solid #777777;
}

:host([type='success']) {
	color: #5cb85c;
}

:host([type='success']) div {
	border-left-color: #5cb85c;
}

:host([type='success']) main {
	border-color: #5cb85c;
}

:host([type='primary']) {
	color: #428bca;
}

:host([type='primary']) div {
	border-left-color: #428bca;
}

:host([type='primary']) main {
	border-color: #428bca;
}

:host([type='info']) {
	color: #5bc0de;
}

:host([type='info']) div {
	border-left-color: #5bc0de;
}

:host([type='info']) main {
	border-color: #5bc0de;
}

:host([type='warning']) {
	color: #f0ad4e;
}

:host([type='warning']) div {
	border-left-color: #f0ad4e;
}

:host([type='warning']) main {
	border-color: #f0ad4e;
}

:host([type='danger']) {
	color: #d9534f;
}

:host([type='danger']) div {
	border-left-color: #d9534f;
}

:host([type='danger']) main {
	border-color: #d9534f;
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