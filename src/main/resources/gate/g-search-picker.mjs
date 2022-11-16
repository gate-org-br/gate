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
			() => this.dispatchEvent(new CustomEvent("canceled")) | this.hide());
		this.shadowRoot.getElementById("cancel").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("canceled")) | this.hide());

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("selected", e =>
			this.dispatchEvent(new CustomEvent("picked", {detail: e.detail})) | this.hide());

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
						grid.style.textAlign = '';
						grid.mapper = e => e.slice(1);
						grid.header = result[0].slice(1);
						grid.values = result.slice(1).filter(row => row.some(col => col.toLowerCase().includes(text)));
					} else
					{
						grid.style.textAlign = 'left';
						grid.mapper = e => e.properties || e.label;
						grid.header = null;
						grid.values = result.filter(e => e.label.toLowerCase().includes(text)
								|| (e.properties && Object.values(e.properties).some(property => text === property.toLowerCase())));
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
									grid.style.textAlign = '';
									grid.mapper = e => e.slice(1);
									grid.header = result[0].slice(1);
									grid.values = result.slice(1);
								} else
								{
									grid.style.textAlign = 'left';
									grid.mapper = e => e.properties || e.label;
									grid.header = null;
									grid.values = result;
								}
							} else if (typeof options === "string")
							{
								grid.innerText = options;
								result = null;
								grid.mapper = null;
								grid.header = null;
								grid.values = null;
								grid.style.textAlign = '';
							} else
								alert("Dados inválidos retornados pelo servidor");
							input.disabled = false;
						}).catch(() => alert("Error ao tentar obter dados do servidor"));
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

	static pick(options, caption)
	{
		let picker = window.top.document.createElement("g-search-picker");
		picker.options = options;
		if (caption)
			picker.caption = caption;
		picker.show();

		return new Promise(resolve =>
		{
			picker.addEventListener("canceled", () => resolve());
			picker.addEventListener("picked", e => resolve(e.detail));
		});
	}
};

customElements.define('g-search-picker', GSearchPicker);