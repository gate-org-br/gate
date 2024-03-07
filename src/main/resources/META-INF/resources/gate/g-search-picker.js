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
				Entre com o critério pesquisa
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
 <style data-element="g-search-picker">dialog
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
import GMessageDialog from './g-message-dialog.js';

function debounce(func, timeout = 300)
{
	let timer;
	return (...args) =>
	{
		clearTimeout(timer);
		timer = setTimeout(() => func.apply(this, args), timeout);
	};
}

export default class GSearchPicker extends GWindow
{
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
		input.addEventListener("input", debounce(() => this.text = input.value));
	}

	set action(value)
	{
		this.setAttribute("action", value);
		this.shadowRoot.querySelector("input").value = "";
	}

	get action()
	{
		return this.getAttribute("action");
	}

	set filter(value)
	{
		if (value)
			this.setAttribute("filter", value);
		else
			this.removeAttribute("filter");
	}

	get filter()
	{
		return this.getAttribute("filter");
	}

	set text(text)
	{
		let input = this.shadowRoot.querySelector("input");
		input.value = text;

		let grid = this.shadowRoot.querySelector("g-grid");
		let headers = {};
		let action = this.action;
		if (this.filter)
		{
			if (action.indexOf("?") === -1)
				action = action + "?" + this.filter + '=' + text;
			else
				action = action + "&" + this.filter + '=' + text;
		} else
			headers = {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: text};

		fetch(action, headers)
			.then(options => options.json())
			.then(options =>
			{
				if (typeof options === "string")
				{
					grid.dataset = [];
					grid.innerText = options;
					this.dispatchEvent(new CustomEvent("update", {detail: null}));
				} else if (Array.isArray(options))
				{
					grid.dataset = options;
					grid.innerText = "Nenhum registro encontrado";
					this.dispatchEvent(new CustomEvent("update", {detail: Array.isArray(options[0]) ? options.slice(1) : options}));
				} else
					throw new Error("Invalid json data returned by the server");

			})
			.catch(error => GMessageDialog.error(error.message))
			.finally(() =>
			{
				input.disabled = false;
				input.focus();
			});

	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	static pick(action, filter, caption, text)
	{
		let picker = window.top.document.createElement("g-search-picker");
		picker.action = action;
		picker.filter = filter;
		picker.caption = caption || "";
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("update", e =>
			{
				if (e.detail && e.detail.length === 1)
				{
					resolve({index: 0, value: e.detail[0]});
					picker.hide();
				}
			});
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
			picker.text = text || "";
		});
	}
};

customElements.define('g-search-picker', GSearchPicker);