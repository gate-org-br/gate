let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Selecione uma data
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-date-selector>
			</g-date-selector>
		</section>
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

g-date-selector {
	flex-grow: 1;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './g-date-selector.js';
import GWindow from './g-window.js';

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
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
		});
	}
};

customElements.define('g-date-picker', GDatePicker);