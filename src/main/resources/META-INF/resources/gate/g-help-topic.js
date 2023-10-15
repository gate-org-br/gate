let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	padding: 4px;
	display: flex;
	cursor: pointer;
	align-items: center;
}

:host([selected])
{
	color: #000066;
	font-weight: bold;
}

:host(*):before
{
	content: attr(text);
}</style>`;

/* global customElements, template */

customElements.define('g-help-topic', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("click", () =>
		{
			Array.from(this.getRootNode()
				.querySelectorAll('g-help-topic'))
				.forEach(e => e.selected = e === this);
			this.dispatchEvent(new CustomEvent("selected",
				{bubbles: true, composed: true}));
		});
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(value)
	{
		this.setAttribute("name", value);
	}

	get text()
	{
		return this.getAttribute("text");
	}

	set text(value)
	{
		this.setAttribute("text", value);
	}

	get value()
	{
		return this.getAttribute("value") || "";
	}

	set value(value)
	{
		this.setAttribute("value", value);
	}

	get selected()
	{
		return this.hasAttribute("selected");
	}

	set selected(value)
	{
		if (value)
			this.setAttribute("selected", "");
		else
			this.removeAttribute("selected");
	}
});

