let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione um período
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-date-time-interval-selector>
			</g-date-time-interval-selector>
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

g-date-time-interval-selector {
	flex-grow: 1;
}

main > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import GWindow from './g-window.js';
import './g-date-time-interval-selector.js';

export default class GDateTimeIntervalPicker extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());

		let commit = this.shadowRoot.getElementById("commit");
		let selector = this.shadowRoot.querySelector("g-date-time-interval-selector");

		setTimeout(() =>
		{
			let dateTime = new Date();
			selector.min = dateTime;
			selector.max = dateTime;
			commit.innerText = selector.selection;
		}, 0);

		setTimeout(() => commit.innerText = selector.selection, 0);
		selector.addEventListener("selected", () => commit.innerText = selector.selection);

		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: commit.innerText})) | this.hide());
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-date-time-interval-picker");
		picker.show();

		return new Promise(resolve =>
		{
			picker.addEventListener("cancel", () => resolve());
			picker.addEventListener("picked", e => resolve(e.detail));
		});
	}

	static register(input)
	{
		var link = input.parentNode.appendChild(document.createElement("a"));
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
				GDateTimeIntervalPicker.pick().then(value =>
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
}

customElements.define('g-date-time-interval-picker', GDateTimeIntervalPicker);

Array.from(document.querySelectorAll("input.DateTimeInterval")).forEach(input => GDateTimeIntervalPicker.register(input));