let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Selecione uma hora
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-time-selector>
			</g-time-selector>
		</section>
		<footer>
			<button id='commit'>
			</button>
		</footer>
	</dialog>
 <style data-element="g-time-picker">dialog
{
	height: 440px;
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

dialog > section {
	align-items: stretch;
}

g-time-selector {
	flex-grow: 1;
}

dialog > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;
/* global customElements, template */

import './g-icon.js';
import './g-time-selector.js';
import GWindow from './g-window.js';
import CancelError from "./cancel-error.js";

export default class GTimePicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let commit = this.shadowRoot.getElementById("commit");
		let selector = this.shadowRoot.querySelector("g-time-selector");

		commit.innerText = selector.selection;
		selector.addEventListener("selected", () => commit.innerText = selector.selection);
		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: commit.innerText})));
	}

	static pick()
	{
		let picker = window.top.document.createElement("g-time-picker");
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new CancelError()));
		});
	}
}

customElements.define('g-time-picker', GTimePicker);

window.addEventListener("connected", event =>
{
	const target = event.target;
	if (target.getAttribute("data-picker") === "time")
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
				GTimePicker.pick()
					.then(value => target.value = value)
					.then(() => target.dispatchEvent(new Event('change', {bubbles: true})))
					.catch(() => undefined);

			link.focus();
			link.blur();
		});
	}
});