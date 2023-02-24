let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header part='header'>
			<label>
				Selecione
			</label>
			<a id='close' href="#">
				<g-icon>&#X1011;</g-icon>
			</a>
		</header>
		<section part='section'>
			<g-selectn part='selector'>
			</g-selectn>
		</section>
		<footer part='footer'>
			<button class="Commit">
				Concluir
				<g-icon>
					&#X1000;
				</g-icon>
			</button>
			<hr/>
			<button class="Cancel">
				Cancelar
				<g-icon>
					&#X1001;
				</g-icon>
			</button>
		</footer>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

section {
	flex-basis: 400px;
}

g-selectn {
	flex-grow: 1;
}</style>`;

/* global customElements, template, fetch */

import './g-icon.mjs';
import './g-selectn.mjs';
import GWindow from './g-window.mjs';
import handle from './response-handler.mjs';

export default class GSelectNPicker extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.querySelector(".Cancel").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());

		this.shadowRoot.querySelector(".Commit").addEventListener("click", () =>
		{
			let values = this.shadowRoot.querySelector("g-selectn").value;
			this.dispatchEvent(new CustomEvent("commit", {"detail": values}));
			this.hide();
		});
	}

	set caption(caption)
	{
		this.shadowRoot.querySelector("label").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.querySelector("label").innerHTML;
	}

	set options(options)
	{
		setTimeout(() => this.shadowRoot.querySelector("g-selectn").options = options, 0);
	}

	get options()
	{
		return this.shadowRoot.querySelector("g-selectn").options;
	}

	set values(values)
	{
		setTimeout(() => this.shadowRoot.querySelector("g-selectn").value = values, 0);
	}

	get values()
	{
		return this.shadowRoot.querySelector("g-selectn").value;
	}

	static pick(caption, options, values)
	{
		if (typeof options === "string")
			return fetch(options)
				.then(response => handle(response))
				.then(response => GSelectNPicker.pick(caption, response, values));

		if (typeof values === "string")
			return fetch(options)
				.then(response => handle(response))
				.then(response => GSelectNPicker.pick(caption, options, response));

		let picker = window.top.document.createElement("g-selectn-picker");
		picker.options = options;
		if (values)
			picker.values = values;
		if (caption)
			picker.caption = caption;
		picker.show();

		return new Promise(resolve =>
		{
			picker.addEventListener("cancel", () => resolve());
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

customElements.define('g-selectn-picker', GSelectNPicker);