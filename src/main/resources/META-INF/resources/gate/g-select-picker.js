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
			<g-grid filter>
				Nenhum registro encontrado
			</g-grid>
		</section>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}

section {
	overflow: auto;
	flex-basis: 400px;
}

g-grid {
	flex-grow: 1;
}</style>`;

/* global customElements, template, fetch */

import './g-icon.js';
import './g-grid.js';
import GWindow from './g-window.js';

export default class GSelectPicker extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("select", {detail: {index: e.detail.index, value: e.detail.value}})) | this.hide());
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
		setTimeout(() => this.shadowRoot.querySelector("g-grid").dataset = options, 0);
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

		return new Promise(resolve =>
		{
			picker.addEventListener("cancel", () => resolve());
			picker.addEventListener("select", e => resolve(e.detail));
		});
	}
};

customElements.define('g-select-picker', GSelectPicker);