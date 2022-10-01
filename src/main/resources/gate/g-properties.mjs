let template = document.createElement("template");
template.innerHTML = `
	<slot>
	</slot>
 <style>:host(*) {
	display: grid;
	grid-template-columns: auto auto;
}

::slotted(label) {
	padding: 4px;
	display: flex;
	font-weight: bold;
	align-items: center;
	justify-content: flex-end;
}

::slotted(span) {
	padding: 4px;
	display: flex;
	align-items: center;
	justify-content: flex-start;
}</style>`;

/* global customElements, template */

customElements.define('g-properties', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get value()
	{
		let value = {};
		Array.from(this.shadowRoot.querySelectorAll("label"))
			.forEach(e => value[e.value] = e.nextElementSibling.value);
		return value;
	}

	set value(value)
	{
		Array.from(this.shadowRoot.querySelectorAll("label, span"))
			.forEach(e => e.remove());
		Object.getOwnPropertyNames(value).forEach(e =>
		{
			this.appendChild(document.createElement("label")).innerHTML = e;
			this.appendChild(document.createElement("span")).innerHTML = value[e];
		});
	}

	attributeChangedCallback()
	{
		this.value = JSON.parse(this.getAttribute("value"));
	}

	static get observedAttributes()
	{
		return ["value"];
	}
});