let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione um ícone
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-icon-selector>
			</g-icon-selector>
		</section>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

section {
	overflow: auto;
	flex-basis: 600px;
}

g-icon-selector {
	flex-grow: 1;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './g-icon-selector.js';
import GWindow from './g-window.js';

export default class GIconPicker extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let selector = this.shadowRoot.querySelector("g-icon-selector");
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
		selector.addEventListener("selected", e => this.dispatchEvent(new CustomEvent("picked", {detail: e.detail.icon})) | this.hide());
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-icon-picker");
		picker.show();

		return new Promise(resolve =>
		{
			picker.addEventListener("cancel", () => resolve());
			picker.addEventListener("picked", e => resolve(e.detail));
		});
	}

	static register(input)
	{
		let link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("g-icon")).innerHTML = "&#x2009;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				GIconPicker.pick().then(value =>
				{
					if (value)
					{
						input.value = value;
						input.dispatchEvent(new Event('change', {bubbles: true}));
					}
				});


			input.dispatchEvent(new Event('change', {bubbles: true}));
			link.focus();
			link.blur();
		});
	}
}

customElements.define('g-icon-picker', GIconPicker);

Array.from(document.querySelectorAll("input.Icon")).forEach(input => GIconPicker.register(input));

