let template = document.createElement("template");
template.innerHTML = `
	<main>
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
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

main > section
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

export default class GSearchPicker extends GWindow
{
	constructor()
	{
		super();
		let prev = "";
		let result = null;
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("select", {detail: {index: e.detail.index, value: e.detail.value}})) | this.hide());

		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () =>
		{
			let text = input.value.toLowerCase();

			if (text.length)
			{
				if (result && text.includes(prev))
				{
					grid.innerText = "Nenhum registro encontrado para os critérios de pesquisa selecionados";
					if (Array.isArray(result[0]))
					{
						let values = result.filter((row, index) => index === 0
								|| row.some(col => col.toLowerCase().includes(text)));
						grid.dataset = values;
						this.dispatchEvent(new CustomEvent("update", {detail: values.slice(1)}));
					} else
					{
						let values = result.filter(e =>
						{
							let keys = Object.keys(e);
							if (keys.length === 2
								&& keys.includes("label")
								&& keys.includes("value"))
								return e.label.toLowerCase().startsWith(text);
							else if (keys.length === 3
								&& keys.includes("label")
								&& keys.includes("value")
								&& keys.includes("properties"))
								return e.label.toLowerCase().startsWith(text)
									|| Object.values(e.properties).some(property => property.toLowerCase().startsWith(text));
							else
								return Object.values(e).some(property => property.toLowerCase().startsWith(text));
						});

						grid.dataset = values;
						this.dispatchEvent(new CustomEvent("update", {detail: values}));
					}
				} else
				{
					input.disabled = true;
					fetch(this.options, {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: input.value})
						.then(options => options.json())
						.then(options =>
						{
							if (Array.isArray(options))
							{
								result = options;
								grid.dataset = result;
								grid.innerText = "Nenhum registro encontrado para os critérios de pesquisa selecionados";
								this.dispatchEvent(new CustomEvent("update", {detail: Array.isArray(result[0]) ? result.slice(1) : result}));
							} else if (typeof options === "string")
							{
								result = null;
								grid.innerText = options;
								this.dispatchEvent(new CustomEvent("update", {detail: null}));
							} else
								alert("Dados inválidos retornados pelo servidor");
						})
						.catch(error => GMessageDialog.error(error.message))
						.finally(() => {
							input.disabled = false;
							input.focus();
						})
				}
			} else
			{
				grid.clear();
				result = null;
				grid.innerText = "Entre com o critério de pesquisa";
			}

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
		{
			let promise = new Promise(resolve =>
			{
				picker.addEventListener("cancel", () => reject(new Error("Cancel")));
				picker.addEventListener("select", e => resolve(e.detail));
				picker.addEventListener("update", e =>
				{
					if (e.detail && e.detail.length === 1)
					{
						resolve(e.detail[0]);
						picker.hide();
					}
				});
			});
			picker.text = text;
			return promise;
		} else
			return new Promise((resolve, reject) =>
			{
				picker.addEventListener("cancel", () => reject(new Error("Cancel")));
				picker.addEventListener("select", e => resolve(e.detail));
			});
	}
};

customElements.define('g-search-picker', GSearchPicker);