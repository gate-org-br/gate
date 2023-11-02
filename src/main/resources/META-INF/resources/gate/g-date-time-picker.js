let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione uma data e uma hora
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-date-time-selector>
			</g-date-time-selector>
		</section>
		<footer>
			<button id='commit'>
			</button>
		</footer>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

g-date-time-selector {
	flex-grow: 1;
}

main > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './g-date-time-selector.js';
import GWindow from './g-window.js';


export default class GDateTimePicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
		this.addEventListener("click", event => event.target === this && this.dispatchEvent(new CustomEvent('cancel')));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let commit = this.shadowRoot.getElementById("commit");
		let selector = this.shadowRoot.querySelector("g-date-time-selector");

		selector.date = new Date();
		commit.innerText = selector.selection;

		selector.addEventListener("selected", () => commit.innerText = selector.selection);
		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: commit.innerText})));
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-date-time-picker");
		picker.show();

		return new Promise(resolve =>
		{
			picker.addEventListener("cancel", () => resolve());
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}

	static register(input)
	{
		let link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("g-icon")).innerHTML = "&#x2003;";

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				GDateTimePicker.pick().then(value =>
				{
					if (value)
					{
						input.value = value;
						input.dispatchEvent(new Event('change', {bubbles: true}));
					}
				});


			link.focus();
			link.blur();
		});
	}
};


customElements.define('g-date-time-picker', GDateTimePicker);

Array.from(document.querySelectorAll("input.DateTime")).forEach(input => GDateTimePicker.register(input));