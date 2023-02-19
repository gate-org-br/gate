let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione uma data
			<a id='close' href="#">
				&#X1011;
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


g-date-selector {
	flex-grow: 1;
}</style>`;

/* global customElements */

import './g-icon.mjs';
import './g-date-selector.mjs';
import GWindow from './g-window.mjs';

customElements.define('g-date-picker', class extends GWindow
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

