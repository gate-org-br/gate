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

/* global customElements */

import './g-icon.mjs';
import './g-icon-selector.mjs';
import GWindow from './g-window.mjs';

customElements.define('g-icon-picker', class extends GWindow
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
});

Array.from(document.querySelectorAll("input.Icon")).forEach(function (input)
{
	var link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	link.setAttribute("tabindex", input.getAttribute('tabindex'));
	link.appendChild(document.createElement("i")).innerHTML = "&#x2009;";

	link.addEventListener("click", function (event)
	{
		event.preventDefault();

		if (input.value)
		{
			input.value = '';
			input.dispatchEvent(new Event('change', {bubbles: true}));
		} else
			window.top.document.createElement("g-icon-picker")
				.show().addEventListener("picked", e =>
			{
				input.value = e.detail.toString();
				input.dispatchEvent(new Event('change', {bubbles: true}));
			});


		input.dispatchEvent(new Event('change', {bubbles: true}));
		link.focus();
		link.blur();
	});
});

