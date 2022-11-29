let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			<label id='caption'>
				Selecione um ítem
			</label>
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
			<input type="TEXT" placeholder="Pesquisar"/>
			<g-grid>
				Entre com o critério pesquisa
			</g-grid>
			<div>
				<g-coolbar>
					<a id='cancel' href="#">
						Cancelar<g-icon>&#X1001;</g-icon>
					</a>
				</g-coolbar>
			</div>
		</g-window-section>
	</main>
 <style>* {
	box-sizing: border-box;
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main
{
	height: auto;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 800px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px auto;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}

g-window-section
{
	gap: 4px;
	padding: 4px;
	display: grid;
	align-items: stretch;
	justify-items:stretch;
	align-content: stretch;
	justify-content: stretch;
	grid-template-rows: 40px 400px 60px;
}

div {
	display: flex;
	align-items: center;
	justify-content: center;
}</style>`;

/* global customElements, template, fetch */

import './g-icon.mjs';
import './g-grid.mjs';
import './g-window-header.mjs';
import './g-window-section.mjs';
import GModal from './g-modal.mjs';
import Message from './g-message.mjs';

export default class GSearchPicker extends GModal
{
	constructor()
	{
		super();
		let prev = "";
		let result = null;
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.getElementById("cancel").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("select", {detail: e.detail})) | this.hide());

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
						let values = result.slice(1).filter(row => row.some(col => col.toLowerCase().includes(text)));
						grid.style.textAlign = '';
						grid.mapper = e => e.slice(1);
						grid.header = result[0].slice(1);
						grid.values = values;
						this.dispatchEvent(new CustomEvent("update", {detail: values}));
					} else
					{
						grid.header = null;
						grid.style.textAlign = 'left';

						grid.mapper = e =>
						{
							let keys = Object.keys(e);
							if (keys.length === 2
								&& keys.indexOf("label") >= 0
								&& keys.indexOf("value") >= 0)
								return e.label;
							else if (keys.length === 3
								&& keys.indexOf("label") >= 0
								&& keys.indexOf("value") >= 0
								&& keys.indexOf("properties") >= 0)
								return e.properties;
							else
								return keys.slice(1)
									.reduce((val, key) => {
										val[key] = e[key];
										return val;
									}, {});
						}


						let values = result.filter(e =>
						{
							let keys = Object.keys(e);
							if (keys.length === 2
								&& keys.indexOf("label") >= 0
								&& keys.indexOf("value") >= 0)
								return e.label.toLowerCase().startsWith(text);
							else if (keys.length === 3
								&& keys.indexOf("label") >= 0
								&& keys.indexOf("value") >= 0
								&& keys.indexOf("properties") >= 0)
								return e.label.toLowerCase().startsWith(text)
									|| Object.values(e.properties).some(property => property.toLowerCase().startsWith(text));
							else
								return Object.values(e).some(property => property.toLowerCase().startsWith(text));
						});

						grid.values = values;
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
								grid.innerText = "Nenhum registro encontrado para os critérios de pesquisa selecionados";

								result = options;
								if (Array.isArray(result[0]))
								{
									let values = result.slice(1);
									grid.style.textAlign = '';
									grid.mapper = e => e.slice(1);
									grid.header = result[0].slice(1);
									grid.values = result.slice(1);
									this.dispatchEvent(new CustomEvent("update", {detail: values}));
								} else
								{
									grid.header = null;
									grid.style.textAlign = 'left';

									grid.mapper = e =>
									{
										let keys = Object.keys(e);
										if (keys.length === 2
											&& keys.indexOf("label") >= 0
											&& keys.indexOf("value") >= 0)
											return e.label;
										else if (keys.length === 3
											&& keys.indexOf("label") >= 0
											&& keys.indexOf("value") >= 0
											&& keys.indexOf("properties") >= 0)
											return e.properties;
										else
											return keys.slice(1)
												.reduce((val, key) => {
													val[key] = e[key];
													return val;
												}, {});
									}


									grid.values = result;
									this.dispatchEvent(new CustomEvent("update", {detail: result}));
								}

							} else if (typeof options === "string")
							{
								grid.innerText = options;
								result = null;
								grid.mapper = null;
								grid.header = null;
								grid.values = null;
								grid.style.textAlign = '';
								this.dispatchEvent(new CustomEvent("update", {detail: null}));
							} else
								alert("Dados inválidos retornados pelo servidor");
							input.disabled = false;
						}).catch(error => Message.error(error.message));
				}
			} else
			{
				grid.innerText = "Entre com o critério de pesquisa";
				result = null;
				grid.mapper = null;
				grid.header = null;
				grid.values = null;
				grid.style.textAlign = '';
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
				picker.addEventListener("cancel", () => resolve());
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
			return  new Promise(resolve =>
			{
				picker.addEventListener("cancel", () => resolve());
				picker.addEventListener("select", e => resolve(e.detail));
			});
	}
};

customElements.define('g-search-picker', GSearchPicker);