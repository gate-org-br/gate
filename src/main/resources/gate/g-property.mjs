let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*) {
	display: grid;
	grid-template-columns: 1fr 1fr;
}

:host(*)::before
{
	padding: 4px;
	display: flex;
	font-weight: bold;
	align-items: center;
	content: attr(label);
	justify-content: flex-end;
}

:host(*)::after
{
	padding: 4px;
	display: flex;
	align-items: center;
	content: attr(value);
	justify-content: flex-start;
}</style>`;

/* global customElements, template */

customElements.define('g-property', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get label()
	{
		return this.getAttribute("label");
	}

	get value()
	{
		return this.getAtribute("value");
	}

	set label(label)
	{
		this.setAttribute("label", label);
	}

	set value(value)
	{
		this.setAttribute("value", value);
	}
});