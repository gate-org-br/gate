let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<label id='caption'>
				Selecione um ítem
			</label>
			<a id='cancel' href="#">
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
import Extractor from './extractor.js';
import ObjectFilter from './object-filter.js';
import GMessageDialog from './g-message-dialog.js';

export default class GSearchPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));

		let prev = "";
		let result = null;

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("commit", {detail: {index: e.detail.index, value: e.detail.value}})));

		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () =>
		{
			let text = input.value.toLowerCase();

			if (!text.length)
			{
				grid.clear();
				result = null;
				grid.innerText = "Entre com o critério de pesquisa";
				return;
			}

			if (result && text.includes(prev))
			{
				grid.innerText = "Nenhum registro encontrado para os critérios de pesquisa selecionados";
				let values = ObjectFilter.filter(result, text);
				grid.dataset = values;
				this.dispatchEvent(new CustomEvent("update", {detail: Array.isArray(result[0]) ? values.slice(1) : values}));
				return;
			}

			input.disabled = true;
			fetch(this.options, {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: input.value})
				.then(options => options.json())
				.then(options =>
				{
					if (typeof options === "string")
					{
						result = null;
						grid.innerText = options;
						this.dispatchEvent(new CustomEvent("update", {detail: null}));
						return;
					}

					if (!Array.isArray(options))
						throw new Error("Dados inválidos retornados pelo servidor");

					result = options;
					grid.innerText = "Nenhum registro encontrado para os critérios de pesquisa selecionados";
					let values = ObjectFilter.filter(result, text);
					grid.dataset = values;
					this.dispatchEvent(new CustomEvent("update", {detail: Array.isArray(result[0]) ? values.slice(1) : values}));
				})
				.catch(error => GMessageDialog.error(error.message))
				.finally(() =>
				{
					input.disabled = false;
					input.focus();
				});


			prev = text;
		});
	}

	set text(text)
	{
		let input = this.shadowRoot.querySelector("input");
		input.value = text;
		input.dispatchEvent(new Event('input'));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	set options(options)
	{
		this._private.options = options;
		this.shadowRoot.querySelector("input").value = "";
	}

	get options()
	{
		return this._private.options;
	}

	static pick(options, caption, text)
	{
		let picker = window.top.document.createElement("g-search-picker");
		picker.options = options;
		if (caption)
			picker.caption = caption;
		picker.show();

		if (text)
			return new Promise((resolve, reject) =>
			{
				picker.addEventListener("update", e =>
				{
					if (e.detail && e.detail.length === 1)
					{
						resolve(e.detail[0]);
						picker.hide();
					}
				});
				picker.addEventListener("cancel", () => reject(new Error("Cancel")));
				picker.addEventListener("commit", e => resolve(e.detail));
				picker.text = text;
			});

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

customElements.define('g-search-picker', GSearchPicker);

window.addEventListener("click", function (event)
{
	if (event.button !== 0)
		return;

	let link = event.target.closest("a[target='@search']");
	if (!link && event.composed)
		link = event.composedPath()[0].closest("a[target='@search']");
	if (link)
	{
		event.preventDefault();
		event.stopPropagation();

		let label = link.parentNode.querySelector("input[type=text]");
		if (!label)
			throw new Error("Label input not found");

		let value = link.parentNode.querySelector("input[type=hidden]");
		if (!value)
			throw new Error("Value input not found");

		if (label.value || value.value)
			return label.value = value.value = '';

		link.style.pointerEvents = "none";
		GSearchPicker.pick(link.href, link.title)
			.then(object =>
			{
				label.value = Extractor.label(object.value);
				value.value = Extractor.value(object.value);
			})
			.catch(() => undefined)
			.finally(() => link.style.pointerEvents = "");
	}
});

window.addEventListener("change", function (event)
{
	let input = event.target || event.srcElement;

	if (input.tagName === "INPUT"
		&& input.hasAttribute("type")
		&& input.getAttribute("type").toUpperCase() === "TEXT")
	{
		let link = input.parentNode.querySelector("a[target='@search']");
		let hidden = input.parentNode.querySelector("input[type='hidden']");
		if (link && hidden)
			if (input.value)
			{
				link.style.pointerEvents = "none";
				GSearchPicker.pick(link.href, link.title, input.value)
					.then(object =>
					{
						input.value = Extractor.label(object.value);
						hidden.value = Extractor.value(object.value);
					})
					.catch(() => undefined)
					.finally(() => link.style.pointerEvents = "");
			} else
				hidden.value = '';
	}
});

