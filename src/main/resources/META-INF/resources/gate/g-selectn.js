let template = document.createElement("template");
template.innerHTML = `
 <style>:host(*)
{
	gap: 4px;
	padding: 8px;
	display: grid;
	overflow: auto;
	overflow-y: auto;
	border-radius: 3px;
	align-content: start;
	background-color: white;
	grid-template-columns: 24px 1fr;
}

:host([columns='1'])  {
	grid-template-columns: 32px 1fr
}
:host([columns='2'])  {
	grid-template-columns: 32px 1fr 32px 1fr
}
:host([columns='3'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='4'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='5'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='6'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='7'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}
:host([columns='8'])  {
	grid-template-columns: 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr 32px 1fr
}

label {
	display: flex;
	cursor: pointer;
	align-items: center;
}
</style>`;

/* global customElements */

import GContextMenu from './g-context-menu.js';
import GMessageDialog from './g-message-dialog.js';

customElements.define('g-selectn', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.addEventListener("contextmenu", event =>
		{
			if (!event.ctrlKey)
			{
				event.preventDefault();
				event.stopPropagation();
				GContextMenu.show(event.clientX, event.clientY,
					{"icon": "2205", "text": "Inverter seleção", "action": () => Array.from(this.shadowRoot.querySelectorAll("input")).forEach(e => e.checked = !e.checked)},
					{"icon": "1011", "text": "Selecionar tudo", "action": () => Array.from(this.shadowRoot.querySelectorAll("input")).forEach(e => e.checked = true)},
					{"icon": "1014", "text": "Desmarcar tudo", "action": () => Array.from(this.shadowRoot.querySelectorAll("input")).forEach(e => e.checked = false)});
			}
		});

		this.shadowRoot.addEventListener("click", event =>
		{
			if (event.target.tagName === "LABEL")
				event.target.previousElementSibling.click();
		});
	}

	set options(options)
	{
		Array.from(this.shadowRoot.querySelectorAll("input, label"))
			.forEach(e => e.remove());
		options.forEach(option =>
		{
			let checkbox = this.shadowRoot.appendChild(document.createElement("input"));
			checkbox.addEventListener("change", () => this.dispatchEvent(new CustomEvent("change")));
			checkbox.type = "checkbox";
			checkbox.value = option.value;

			let label = this.shadowRoot.appendChild(document.createElement("label"));
			label.innerText = option.label;
		});
	}

	get options()
	{
		return Array.from(this.shadowRoot.querySelectorAll("input"))
			.map(checkbox => ({
					"value": checkbox.value,
					"label": checkbox.previousElementSibling.innerText
				}));
	}

	get value()
	{
		return Array.from(this.shadowRoot.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.map(checkbox => checkbox.value);
	}

	set value(value)
	{
		Array.from(this.shadowRoot.querySelectorAll("input"))
			.forEach(checkbox => checkbox.checked = value.includes(checkbox.value));
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	get required()
	{
		return this.getAttribute("required");
	}

	set required(required)
	{
		if (required)
			this.setAttribute("required", "required");
		else
			this.removeAttribute("required");
	}

	set max(max)
	{
		this.setAttribute("max", "max");
	}

	get max()
	{
		return this.hasAttribute("max") ? Number(this.getAttribute("max")) : null;
	}

	set min(min)
	{
		this.setAttribute("min", "min");
	}

	get min()
	{
		return this.hasAttribute("min") ? Number(this.getAttribute("min")) : null;
	}

	connectedCallback()
	{
		Array.from(this.children)
			.forEach(e => this.shadowRoot.appendChild(e));

		if (this.name)
		{
			let form = this.closest("form");
			if (form)
				form.addEventListener("formdata", event =>
					Array.from(this.shadowRoot.querySelectorAll("input"))
						.filter(checkbox => checkbox.checked)
						.forEach(checkbox => event.formData.append(this.name, checkbox.value)));
		}
	}

	attributeChangedCallback(attribute)
	{
		if (attribute === "value")
			this.value = JSON.parse(this.getAttribute("value"));
		else
			this.options = JSON.parse(this.getAttribute("options"));
	}

	checkValidity()
	{
		let checked =
			Array.from(this.shadowRoot.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.length;

		if (this.required && !checked)
			return false;

		if (this.min && checked < min)
			return false;

		if (this.max && checked > max)
			return false;

		return true;
	}

	reportValidity()
	{
		let checked =
			Array.from(this.shadowRoot.querySelectorAll("input"))
			.filter(checkbox => checkbox.checked)
			.length;

		if (this.required && !checked)
			return false & GMessageDialog.error("Selecione ao menos uma opção");

		if (this.min && checked < min)
			return false & GMessageDialog.error(`Selecione ao menos ${min} opções`);

		if (this.max && checked > max)
			return false & GMessageDialog.error(`Selecione no máximo ${max} opções`);

		return true;
	}

	static get observedAttributes()
	{
		return ['value', "options"];
	}
});