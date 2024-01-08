let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Selecione um período
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-date-interval-selector>
			</g-date-interval-selector>
		</section>
		<footer>
			<button id='commit'>
			</button>
		</footer>
	</dialog>
 <style>dialog
{
	height: 440px;
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

dialog > section {
	align-items: stretch;
}

g-date-interval-selector {
	flex-grow: 1;
}

dialog > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import DOM from './dom.js';
import GWindow from './g-window.js';
import './g-date-interval-selector.js';

export default class GDateIntervalPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let commit = this.shadowRoot.getElementById("commit");
		let selector = this.shadowRoot.querySelector("g-date-interval-selector");

		let date = new Date();
		selector.min = date;
		selector.max = date;
		commit.innerText = selector.selection;

		selector.addEventListener("selected", () => commit.innerText = selector.selection);
		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: commit.innerText})));
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-date-interval-picker");
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
}

customElements.define('g-date-interval-picker', GDateIntervalPicker);

const REGISTRY = new WeakMap();
DOM.forEveryElement(e => e.tagName === "INPUT"
		&& !REGISTRY.has(e)
		&& e.classList.contains("DateInterval"), input =>
{
	REGISTRY.set(input);

	let link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	if (input.hasAttribute('tabindex'))
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
			GDateIntervalPicker.pick()
				.then(value => input.value = value)
				.then(() => input.dispatchEvent(new Event('change', {bubbles: true})))
				.catch(() => undefined);

		link.focus();
		link.blur();
	});
});