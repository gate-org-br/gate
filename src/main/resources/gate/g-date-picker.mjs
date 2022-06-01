let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			Selecione uma data
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
			<g-date-selector>
			</g-date-selector>
		</g-window-section>
	</main>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main
{
	height: 400px;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 600px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 1fr;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}</style>`;

/* global customElements */

import './g-date-selector.mjs';
import './g-window-section.mjs';
import './g-window-header.mjs';
import GModal from './g-modal.mjs';

customElements.define('g-date-picker', class extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
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

