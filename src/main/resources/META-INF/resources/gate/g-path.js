let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	padding-left: 4px;
	padding-right: 4px;
	align-items: center;
	display: inline-flex;
	justify-content: flex-start;
}

a,
label
{
	display: flex;
	font-size: inherit;
	align-items: center;
	justify-content: space-between;
}

a:not(:last-child):after,
label:not(:last-child):after
{
	display: flex;
	color: #999999;
	margin-left: 4px;
	content: "\\2275";
	margin-right: 4px;
	align-items: center;
	font-family: "gate";
	pointer-events: none;
	justify-content: center;
}

a > i,
label> i {
	order: -1;
	display: flex;
	font-family: gate;
	margin-right: 4px;
	font-size: inherit;
	font-style: normal;
	align-items: center;
	justify-content: center;
}</style>`;

/* global customElements, template */

customElements.define('g-path', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	connectedCallback()
	{
		Array.from(this.children)
			.forEach(e => this.shadowRoot.appendChild(e));
	}
});