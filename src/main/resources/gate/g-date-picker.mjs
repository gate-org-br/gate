let template = document.createElement("template");
template.innerHTML = `
	<g-date-selector>
	</g-date-selector>
`;

/* global customElements */

import GPicker from './g-picker.mjs';

customElements.define('g-date-picker', class extends GPicker
{
	constructor()
	{
		super();
		this.hideButton;
		this.caption = "Selecione uma data";
		this.body.appendChild(template.content.cloneNode(true));
		let selector = this.body.querySelector("g-date-selector");
		selector.addEventListener("selected", e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail})) | this.hide());
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-date-picker");
	}
});

Array.from(document.querySelectorAll("input.Date")).forEach(function (input)
{
	var link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	link.setAttribute("tabindex", input.getAttribute('tabindex'));
	link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

	link.addEventListener("click", function (event)
	{
		event.preventDefault();

		if (input.value)
		{
			input.value = '';
			input.dispatchEvent(new Event('change', {bubbles: true}));
		} else
			window.top.document.createElement("g-date-picker")
				.show().addEventListener("picked", e =>
			{
				input.value = e.detail;
				input.dispatchEvent(new Event('change', {bubbles: true}));
			});


		input.dispatchEvent(new Event('change', {bubbles: true}));
		link.focus();
		link.blur();
	});
});

