let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Selecione um ícone
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-icon-selector>
			</g-icon-selector>
		</section>
	</dialog>
 <style>dialog
{
	height: 600px;
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

g-icon-selector {
	flex-grow: 1;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import DOM from './dom.js';
import './g-icon-selector.js';
import GWindow from './g-window.js';

export default class GIconPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let selector = this.shadowRoot.querySelector("g-icon-selector");
		selector.addEventListener("selected", e => this.dispatchEvent(new CustomEvent("commit", {detail: e.detail.icon})));
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-icon-picker");
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
}

customElements.define('g-icon-picker', GIconPicker);

const REGISTRY = new WeakMap();
DOM.forEveryElement(e => e.tagName === "INPUT"
		&& !REGISTRY.has(e)
		&& e.classList.contains("Icon"), input =>
{
	REGISTRY.set(input);

	let link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	if (input.hasAttribute('tabindex'))
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
	let icon = link.appendChild(document.createElement("g-icon"));

	icon.innerHTML = input.value ? "&#x1001;" : "&#x2009;";
	input.addEventListener("input", () => icon.innerHTML = input.value ? "&#x1001;" : "&#x2009;");
	input.addEventListener("change", () => icon.innerHTML = input.value ? "&#x1001;" : "&#x2009;");

	link.addEventListener("click", function (event)
	{
		event.preventDefault();

		if (input.value)
		{
			input.value = '';
			input.dispatchEvent(new Event('change', {bubbles: true}));
		} else
			GIconPicker.pick()
				.then(value => input.value = value)
				.then(() => input.dispatchEvent(new Event('change', {bubbles: true})))
				.catch(() => undefined);

		link.focus();
		link.blur();
	});
});



