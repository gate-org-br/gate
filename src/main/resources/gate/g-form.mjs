let template = document.createElement("template");
template.innerHTML = `
 <style>* {
	box-sizing: border-box
}

:host(*) {
	display: grid;
	grid-template-columns: repeat(16, 1fr);
}

label {
	padding: 4px;
	display: flex;
	grid-column: span 16;
	flex-direction: column;
}

span {
	flex-grow: 1;
	display: block;
	flex-basis: 32px;
}

input,
select {
	padding: 8px 4px 8px 4px;
}

input,
textarea,
select,
g-selectn {
	width: 100%;
	height: 100%;
	border-radius:  5px;
	border: 1px solid #CCCCCC;
}

textarea {
	resize: none
}

input:invalid,
select:invalid,
textarea:invalid
{
	box-shadow: inset 0 0 1px 1px rgba(255, 0, 0, 0.75);
}

@media only screen and (min-width: 576px) {
	label[data-size="1"] {
		grid-column: span 8;
	}
}

@media only screen and (min-width: 768px) {
	label[data-size="1"] {
		grid-column: span 4;
	}

	label[data-size="2"] {
		grid-column: span 8;
	}
}

@media only screen and (min-width: 992px) {
	label[data-size="1"] {
		grid-column: span 2;
	}

	label[data-size="2"] {
		grid-column: span 4;
	}

	label[data-size="4"] {
		grid-column: span 8;
	}
}


@media only screen and (min-width: 1200px) {
	label[data-size="1"] {
		grid-column: span 1;
	}

	label[data-size="2"] {
		grid-column: span 2;
	}

	label[data-size="4"] {
		grid-column: span 4;
	}

	label[data-size="8"] {
		grid-column: span 8;
	}
}
</style>`;

/* global customElements */

import './g-selectn.mjs';
import mask from './mask.mjs';

function create(form, element)
{
	let label = document.createElement("label");

	if (element.size)
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

	label.innerText = element.name + ': ';

	let span = label.appendChild(document.createElement("span"));
	if (element.multiple)
		span.style.flexBasis = "80px";

	let input;
	if (element.options)
	{
		if (element.multiple)
		{
			input = span.appendChild(document.createElement("g-selectn"));
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
		Array.from(this.shadowRoot.querySelectorAll("label"))
			.forEach(e => e.remove());
		value.forEach(element => this.add(element));
	}

	get value()
	{
		return Array.from(this.shadowRoot.querySelectorAll("label"))
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
		this.shadowRoot.appendChild(create(this, element));
	}

	set(index, element)
	{
		this.shadowRoot.replaceChild(create(this, element),
			this.shadowRoot.querySelectorAll("label")[index]);
	}

	remove(index)
	{
		this.shadowRoot.querySelectorAll("label")[index].remove();
	}

	move(source, target)
	{
		let elements = this.shadowRoot.querySelectorAll("label");
		source = elements[source];
		target = elements[target];
		this.shadowRoot.insertBefore(source, target);
	}

	checkValidity()
	{
		for (let input of Array.from(this.shadowRoot.querySelectorAll("input, select, textarea, g-selectn")))
			if (input.checkValidity && !input.checkValidity())
				return false;
		return true;
	}

	reportValidity()
	{
		for (let input of Array.from(this.shadowRoot.querySelectorAll("input, select, textarea, g-selectn")))
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