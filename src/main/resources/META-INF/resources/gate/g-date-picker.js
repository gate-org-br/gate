let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione uma data
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-date-selector>
			</g-date-selector>
		</section>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

main > section {
	flex-basis: 400px;
	align-items: stretch;
}

g-date-selector {
	flex-grow: 1;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './g-date-selector.js';
import GWindow from './g-window.js';

export default class GDatePicker extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let selector = this.shadowRoot.querySelector("g-date-selector");
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
		selector.addEventListener("selected", e => this.dispatchEvent(new CustomEvent('picked', {detail: e.detail})) | this.hide());
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-date-picker");
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("picked", e => resolve(e.detail));
		});
	}

	static register(input)
	{
		let link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		let icon = link.appendChild(document.createElement("g-icon"));

		icon.innerHTML = input.value ? "&#x1001;" : "&#x2003;";
		input.addEventListener("input", () => icon.innerHTML = input.value ? "&#x1001;" : "&#x2003;");
		input.addEventListener("change", () => icon.innerHTML = input.value ? "&#x1001;" : "&#x2003;");

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				GDatePicker.pick().then(value =>
				{
					input.value = value;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				}).catch(() => undefined);

			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	}
};

customElements.define('g-date-picker', GDatePicker);

Array.from(document.querySelectorAll("input.Date")).forEach(input => GDatePicker.register(input));

