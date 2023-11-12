let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione um período
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-time-interval-selector>
			</g-time-interval-selector>
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

g-time-interval-selector {
	flex-grow: 1;
}

main > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import GWindow from './g-window.js';
import './g-time-interval-selector.js';

export default class GTimeIntervalPicker extends GWindow
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
		let selector = this.shadowRoot.querySelector("g-time-interval-selector");

		commit.innerText = selector.selection;

		selector.addEventListener("selected", () => commit.innerText = selector.selection);
		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: commit.innerText})) | this.hide());
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-time-interval-picker");
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}

	static register(input)
	{
		let link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		let icon = link.appendChild(document.createElement("g-icon"));

		icon.innerHTML = input.value ? "&#x1001;" : "&#x2167;";
		input.addEventListener("input", () => icon.innerHTML = input.value ? "&#x1001;" : "&#x2167;");
		input.addEventListener("change", () => icon.innerHTML = input.value ? "&#x1001;" : "&#x2167;");

		link.addEventListener("click", function (event)
		{
			event.preventDefault();

			if (input.value)
			{
				input.value = '';
				input.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				GTimeIntervalPicker.pick().then(value =>
				{
					input.value = value;
					input.dispatchEvent(new Event('change', {bubbles: true}));
				}).catch(() => undefined);

			link.focus();
			link.blur();
		});
	}
}

customElements.define('g-time-interval-picker', GTimeIntervalPicker);

Array.from(document.querySelectorAll("input.TimeInterval")).forEach(input => GTimeIntervalPicker.register(input));