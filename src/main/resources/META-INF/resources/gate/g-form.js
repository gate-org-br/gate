let template = document.createElement("template");
template.innerHTML = `
	<fieldset>
	</fieldset>
 <style data-element="g-form">* {
	box-sizing: border-box
}

:host(*) {
	display: flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

fieldset {
	padding: 0;
	border: none;
}</style>`;
/* global customElements */

import './g-selectn.js';
import mask from './mask.js';
import stylesheets from './stylesheets.js';

function create(form, element)
{
	let label = document.createElement("label");

	if (element.size)
	{
		switch (Number(element.size))
		{
			case 0:
				label.setAttribute("data-size", 1);
				break;
			case 1:
				label.setAttribute("data-size", 2);
				break;
			case 2:
				label.setAttribute("data-size", 4);
				break;
			case 3:
				label.setAttribute("data-size", 8);
				break;
		}
	} else if (element.columns)
		label.setAttribute("data-size", element.columns);

	label.innerText = element.name + ': ';

	let span = label.appendChild(document.createElement("span"));
	if (element.multiple)
		span.style.flexBasis = "128px";

	let input;
	if (element.options)
	{
		if (element.multiple)
		{
			input = document.createElement("g-selectn");
			span.appendChild(input);
			input.addEventListener("change", () => form.dispatchEvent(new CustomEvent("change")));
			input.options = element.options.map(option => ({"label": option, "value": option}));

			if (element.value)
				if (Array.isArray(element.value)
					&& element.value.length)
					input.value = element.value;
				else
					input.value = [element.value];
		} else
		{
			input = span.appendChild(document.createElement("select"));
			input.addEventListener("change", () => form.dispatchEvent(new CustomEvent("change")));
			input.appendChild(document.createElement("option")).value = "";
			element.options.forEach(value => {
				let option = input.appendChild(document.createElement("option"));
				option.value = value;
				option.innerText = value;
			});

			if (element.value)
				if (Array.isArray(element.value)
					&& element.value.length)
					input.value = element.value[0];
				else
					input.value = element.value;
		}
	} else if (element.multiple)
	{
		input = span.appendChild(document.createElement("textarea"));
		input.addEventListener("input", () => form.dispatchEvent(new CustomEvent("input")));
		input.addEventListener("change", () => form.dispatchEvent(new CustomEvent("change")));
		if (element.value)
			if (Array.isArray(element.value)
				&& element.value.length)
				input.value = element.value.join("\n");
			else
				input.value = element.value;
	} else
	{
		input = span.appendChild(document.createElement("input"));
		input.addEventListener("input", () => form.dispatchEvent(new CustomEvent("input")));
		input.addEventListener("change", () => form.dispatchEvent(new CustomEvent("change")));
		if (element.value)
			if (Array.isArray(element.value)
				&& element.value.length)
				input.value = element.value[0];
			else
				input.value = element.value;
	}

	input.name = element.name;

	if (element.placeholder)
		input.setAttribute("placeholder", element.placeholder);
	if (element.required)
		input.setAttribute("required", "required");
	if (element.description)
		input.title = element.description;
	if (element.readonly)
		input.setAttribute("readonly", "readonly");
	if (element.maxlength)
		input.setAttribute("maxlength", element.maxlength);
	if (element.pattern)
		input.setAttribute("pattern", element.pattern);
	if (element.mask)
	{
		input.setAttribute("data-mask", element.mask);
		mask(input);
	}

	return label;
}

customElements.define('g-form', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		stylesheets('input.css', 'fieldset.css')
			.forEach(e => this.shadowRoot.appendChild(e));
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	set value(value)
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		Array.from(fieldset.querySelectorAll("label")).forEach(e => e.remove());
		if (value)
			value.forEach(element => fieldset.appendChild(create(this, element)));
	}

	get value()
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		return Array.from(fieldset.querySelectorAll("label"))
			.map(label => {

				let object = {};

				if (label.hasAttribute("data-size"))
					switch (label.getAttribute("data-size"))
					{
						case "1":
							object.size = "0";
							break;
						case "2":
							object.size = "1";
							break;
						case "4":
							object.size = "2";
							break;
						case "8":
							object.size = "3";
							break;
					}

				let element = label.children[0].children[0];

				object.name = element.name;

				if (element.hasAttribute("required"))
					object.required = true;
				if (element.hasAttribute("readonly"))
					object.readonly = true;
				if (element.title)
					object.description = element.title;
				if (element.hasAttribute("pattern"))
					object.maxlength = element.getAttribute("pattern");
				if (element.hasAttribute("data-mask"))
					object.mask = element.getAttribute("data-mask");
				if (element.hasAttribute("maxlength"))
					object.maxlength = Number(element.getAttribute("maxlength"));


				switch (element.tagName)
				{
					case "TEXTAREA":
						object.multiple = true;
						if (element.value)
							object.value = element.value.split("\n");
						break;
					case "G-SELECTN":
						object.multiple = true;
						object.options = element.options.map(e => e.value);
						if (element.value && element.value.length)
							object.value = element.value;
						break;
					case "SELECT":
						object.options = Array.from(element.children)
							.slice(1).map(option => option.value);
						if (element.value)
							object.value = [element.value];
						break;
					case "INPUT":
						if (element.value)
							object.value = [element.value];
						break;
				}

				return object;
			});
	}

	add(element)
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		fieldset.appendChild(create(this, element));
	}

	set(index, element)
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		fieldset.replaceChild(create(this, element),
			fieldset.querySelectorAll("label")[index]);
	}

	remove(index)
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		fieldset.querySelectorAll("label")[index].remove();
	}

	move(source, target)
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		let elements = fieldset.querySelectorAll("label");
		source = elements[source];
		target = elements[target];
		fieldset.insertBefore(source, target);
	}

	checkValidity()
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		for (let input of Array.from(fieldset.querySelectorAll("input, select, textarea, g-selectn")))
			if (input.checkValidity && !input.checkValidity())
				return false;
		return true;
	}

	reportValidity()
	{
		let fieldset = this.shadowRoot.querySelector("fieldset");
		for (let input of Array.from(fieldset.querySelectorAll("input, select, textarea, g-selectn")))
		{
			if (input.checkValidity && !input.checkValidity())
			{
				input.reportValidity();
				return false;
			}
		}

		return true;
	}

	connectedCallback()
	{
		let form = this.closest("form");
		if (form)
		{
			form.addEventListener("submit", event =>
			{
				if (!this.reportValidity())
					event.preventDefault();
			});

			form.addEventListener("formdata", event => event.formData.set(this.name, JSON.stringify(this.value)));
		}
	}

	attributeChangedCallback()
	{
		this.value = JSON.parse(this.getAttribute("value"));
	}

	static get observedAttributes()
	{
		return ['value'];
	}
});