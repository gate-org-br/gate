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
			<g-selectn id='select' part='selector'>
			</g-selectn>
		</section>
		<footer part='footer'>
			<g-coolbar>
				<button id='commit' class="primary">
					Concluir
					<g-icon>
						&#X1000;
					</g-icon>
				</button>
				<hr/>
				<button id='cancel' class="tertiary">
					Cancelar
					<g-icon>
						&#X1001;
					</g-icon>
				</button>
			</g-coolbar>
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

import './g-icon.js';
import './g-selectn.js';
import './g-coolbar.js';
import GWindow from './g-window.js';
import handle from './response-handler.js';

export default class GSelectNPicker extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());

		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));

		let commit = this.shadowRoot.getElementById("commit");
		let select = this.shadowRoot.getElementById("select");

		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("commit", {"detail": select.value})));
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

	static pick(options, caption, values)
	{
		if (typeof options === "string")
			return fetch(options)
				.then(response => handle(response))
				.then(response => GSelectNPicker.pick(response, caption, values));

		if (typeof values === "string")
			return fetch(values)
				.then(response => handle(response))
				.then(response => GSelectNPicker.pick(options, caption, response));

		let picker = window.top.document.createElement("g-selectn-picker");
		picker.options = options;
		if (values)
			picker.values = values;
		if (caption)
			picker.caption = caption;
		picker.show();

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("commit", e => resolve(e.detail));
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
		});
	}
};

customElements.define('g-selectn-picker', GSelectNPicker);