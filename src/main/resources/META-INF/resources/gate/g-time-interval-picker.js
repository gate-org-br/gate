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
			<g-time-interval-selector>
			</g-time-interval-selector>
		</section>
		<footer>
			<button id='commit'>
			</button>
		</footer>
	</dialog>
 <style data-element="g-time-interval-picker">dialog
{
	height: 440px;
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

dialog > section {
	align-items: stretch;
}

g-time-interval-selector {
	flex-grow: 1;
}

dialog > footer > button {
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
}

customElements.define('g-time-interval-picker', GTimeIntervalPicker);