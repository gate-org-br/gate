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
			<div>
				<g-grid auto-select>
					Entre com o critério pesquisa
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
 <style data-element="g-search-picker">dialog
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
/* global customElements, template */

import './g-icon.js';
import './g-grid.js';
import GWindow from './g-window.js';
import debounce from './debounce.js';
import CancelError from "./cancel-error.js";

export default class GSearchPicker extends GWindow
{
	#fetcher;
	#request = 0;
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
		input.addEventListener("input", debounce(() => this.populate(this.#fetcher, {text: input.value, columns: this.#columns})));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").textContent = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").textContent;
	}

	populate(fetcher, { text = "", columns = [] } = {})
	{
		if (typeof fetcher !== "function")
			throw new Error("fetcher must be a function");

		this.#fetcher = fetcher;
		this.#columns = columns;

		let input = this.shadowRoot.querySelector("input");
		input.value = text;

		let grid = this.shadowRoot.querySelector("g-grid");

		input.disabled = true;
		const request = ++this.#request;

		Promise.resolve()
			.then(() => fetcher(text))
			.then(options =>
			{
				if (request !== this.#request)
					return;
				if (typeof options === "string")
					throw new Error(options);
				if (!Array.isArray(options))
					throw new Error("Invalid json data returned by the server");

				grid.populate(options, {columns, filter: text});
				grid.innerText = "Nenhum registro encontrado";
			}).catch(error =>
		{
			grid.populate([]);
			grid.innerText = error.message;
			this.dispatchEvent(new CustomEvent("update", {detail: null}));
		}).finally(() =>
		{
			input.disabled = false;
			input.focus();
		});
	}

	static pick(fetcher, { caption = "", text = "", columns = [] } = {})
	{
		let picker = window.top.document.createElement("g-search-picker");
		picker.caption = caption;
		picker.show();
		picker.populate(fetcher, {text, columns});

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new CancelError()));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

customElements.define('g-search-picker', GSearchPicker);