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
			<div>
				<g-object-selector>
				</g-object-selector>
			</div>
		</g-window-section>
	</main>
 <style>:host(*) {
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
	max-width: 600px;
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
	gap: 8px;
	padding: 4px;
	display: grid;
	align-items: stretch;
	align-content: stretch;
	justify-items:stretch;
	justify-content: stretch;
	grid-template-rows: 32px auto;
}

div {
	height: 400px;
	overflow: auto;
}</style>`;

/* global customElements, template, fetch */

import './g-window-header.mjs';
import './g-window-section.mjs';
import './g-object-selector.mjs';
import GModal from './g-modal.mjs';

export default class GSearchPicker extends GModal
{
	constructor()
	{
		super();
		this._private = {};
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("canceled")) | this.hide());

		let selector = this.shadowRoot.querySelector("g-object-selector");
		selector.addEventListener("selected", e =>
			this.dispatchEvent(new CustomEvent("picked", {detail: e.detail})) | this.hide());

		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () =>
		{
			if (this._private.length)
			{
				let text = input.value.toLowerCase();

				if (text.length < this._private.length)
				{
					this._private.result = null;
					this._private.length = null;
					this.shadowRoot.querySelector("g-object-selector").options = [];
				} else
				{
					this.shadowRoot.querySelector("g-object-selector").options =
						this._private.result.filter(e =>
						{
							let label = e.label.toLowerCase();

							if (label.includes(text))
								return true;

							if (e.properties)
								if (Object.values(e.properties).some(property => text === property.toLowerCase()))
									return true;

							return false;
						});
				}
			} else
			{
				input.disabled = true;
				fetch(this.options, {method: 'POST', headers: {'Content-Type': 'text/plain'}, body: input.value})
					.then(options => options.json())
					.then(options =>
					{
						if (options && options.length)
						{
							this._private.result = options;
							this._private.length = input.value.length;
							this.shadowRoot.querySelector("g-object-selector").options = options;
						}
						input.disabled = false;
					}).catch(() => alert("Error ao tentar obter dados do servidor"));
			}
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