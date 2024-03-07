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
 <style data-element="g-icon-picker">dialog
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