let template = document.createElement("template");
template.innerHTML = `
	<dialog><header>
			Selecione uma data
			<a id='cancel' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-date-selector></g-date-selector></section></dialog>
<style data-element="g-date-picker">dialog
{
	height: 440px;
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

dialog > section {
	align-items: stretch;
}

g-date-selector {
	flex-grow: 1;
}</style>`;
/* global customElements, template */

import './g-icon.js';
import './g-date-selector.js';
import GWindow from './g-window.js';
import CancelError from "./cancel-error.js";

export default class GDatePicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));
		this.shadowRoot.querySelector("g-date-selector").addEventListener("selected", e => this.dispatchEvent(new CustomEvent('commit', {detail: e.detail})));
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-date-picker");
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new CancelError()));
		});
	}
};

customElements.define('g-date-picker', GDatePicker);

window.addEventListener("connected", event =>
{
	const target = event.target;
	if (target.getAttribute("data-picker") === "date")
	{
		let link = target.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		if (target.hasAttribute('tabindex'))
			link.setAttribute("tabindex", target.getAttribute('tabindex'));
		let icon = link.appendChild(document.createElement("g-icon"));

		icon.innerHTML = target.value ? "&#x1001;" : "&#x2003;";
		target.addEventListener("input", () => icon.innerHTML = target.value ? "&#x1001;" : "&#x2003;");
		target.addEventListener("change", () => icon.innerHTML = target.value ? "&#x1001;" : "&#x2003;");

		link.addEventListener("click", event =>
		{
			event.preventDefault();

			if (target.value)
			{
				target.value = '';
				target.dispatchEvent(new Event('change', {bubbles: true}));
			} else
				GDatePicker.pick()
					.then(value => target.value = value)
					.then(() => target.dispatchEvent(new Event('change', {bubbles: true})))
					.catch(() => undefined);

			link.focus();
			link.blur();
		});
	}
});