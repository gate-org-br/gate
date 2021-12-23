let template = document.createElement("template");
template.innerHTML = `
	<g-time-selector>
	</g-time-selector>
`;

/* global customElements */

import GPicker from './g-picker.mjs';

customElements.define('g-time-picker', class extends GPicker
{
	constructor()
	{
		super();

		this.hideButton;
		this.caption = "Selecione uma hora";
		this.body.appendChild(template.content.cloneNode(true));
		let selector = this.body.querySelector("g-time-selector");

		this.addEventListener("show", () => this.commit.focus());
		setTimeout(() => this.commit.innerText = selector.selection, 0);
		selector.addEventListener("selected", () => this.commit.innerText = selector.selection);
		
		this.commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: this.commit.innerText})) | this.hide());
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-time-picker");
	}
});

Array.from(document.querySelectorAll("input.Time")).forEach(function (input)
{
	var link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	link.setAttribute("tabindex", input.getAttribute('tabindex'));
	link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";

	link.addEventListener("click", function (event)
	{
		event.preventDefault();

		if (input.value)
		{
			input.value = '';
			input.dispatchEvent(new Event('change', {bubbles: true}));
		} else
			window.top.document.createElement("g-time-picker")
				.show().addEventListener("picked", e =>
			{
				input.value = e.detail;
				input.dispatchEvent(new Event('change', {bubbles: true}));
			});


		link.focus();
		link.blur();
	});
});