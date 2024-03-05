let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<label id='caption'>
				Selecione um ítem
			</label>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<input type="TEXT" placeholder="Pesquisar"/>
			<g-grid>
				Nenhum registro encontrado
			</g-grid>
		</section>
		<footer>
			<g-coolbar>
				<button id='clear' class='primary'>
					Limpar <g-icon>&#X2018;</g-icon>
				</button>
				<hr>
				<button id='cancel' class='tertiary'>
					Cancelar <g-icon>&#X2027;</g-icon>
				</button>
			</g-coolbar>
		</footer>
	</dialog>
 <style>dialog
{
	min-width: 320px;
	max-width: 800px;
	height: fit-content;
	width: calc(100% - 40px);
}

dialog > section
{
	gap: 4px;
	padding: 4px;
	display: grid;
	align-items: stretch;
	justify-items:stretch;
	align-content: stretch;
	justify-content: stretch;
	grid-template-rows: 40px 400px;
}</style>`;

/* global customElements, template, fetch */

import './g-icon.js';
import './g-grid.js';
import GWindow from './g-window.js';
import ObjectFilter from './object-filter.js';

export default class GSelectPicker extends GWindow
{
	#options;
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("clear").addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: {value: {}}})));

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("commit", {detail: {index: e.detail.index, value: e.detail.value}})));


		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () => grid.dataset = ObjectFilter.filter(this.options, input.value));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	get options()
	{
		return this.#options || [];
	}

	set options(options)
	{
		this.#options = options;
		this.shadowRoot.querySelector("g-grid").dataset = options;
	}

	static pick(options, caption)
	{
		if (typeof options === "string")
			return fetch(options)
				.then(response =>
				{
					return response.ok ?
						response.json()
						: response.text().then(message =>
					{
						throw new Error(message);
					});
				}).then(result => GSelectPicker.pick(result, caption));

		let picker = window.top.document.createElement("g-select-picker");
		picker.options = options;
		if (caption)
			picker.caption = caption;
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

customElements.define('g-select-picker', GSelectPicker);