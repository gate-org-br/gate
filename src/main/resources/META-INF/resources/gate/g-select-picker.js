let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<label id='caption'>
			</label>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<input type="TEXT" placeholder="Pesquisar"/>
			<div>
				<g-grid auto-select>
					Nenhum registro encontrado
				</g-grid>
			</div>
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
 <style data-element="g-select-picker">dialog
{
	height: fit-content;
	width: clamp(320px, calc(100% - 120px), 1024px);
	height: clamp(320px, calc(100% - 120px), 768px);
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
	grid-template-rows: 40px auto;
}

div {
	overflow: auto;
}</style>`;
/* global customElements, template, fetch */

import './g-icon.js';
import './g-grid.js';
import GWindow from './g-window.js';
import CancelError from "./cancel-error.js";

export default class GSelectPicker extends GWindow
{
	#options = [];
	#columns = [];

	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("clear").addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {detail: {index: 0, value: []}})));

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("commit", {detail: {index: e.detail.index, value: e.detail.value}})));

		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () => grid
				.populate(this.#options, {columns: this.#columns, filter: input.value}));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").textContent = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").textContent;
	}

	populate(options = [], {columns = []} = {})
	{
		this.#options = options;
		this.#columns = columns;
		this.shadowRoot.querySelector("g-grid")
			.populate(options, {columns});
	}

	static pick(options = [], {caption = "", columns = []} = {})
	{
		let picker = window.top.document.createElement("g-select-picker");
		if (caption)
			picker.caption = caption;
		picker.populate(options, {columns});
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new CancelError()));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

customElements.define('g-select-picker', GSelectPicker);