let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			<label id='caption'>
				Selecione
			</label>
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
			<div>
				<g-selectn>
				</g-selectn>
			</div>
			<g-coolbar>
				<a id='commit' href="#">
					Concluir<g-icon>&#X1000;</g-icon>
				</a>
				<hr/>
				<a id='cancel' href="#">
					Cancelar<g-icon>&#X1001;</g-icon>
				</a>
			</g-coolbar>
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
	border: var(--g-window-border);
}

g-window-section
{
	gap: 4px;
	padding: 4px;
	display: grid;
	justify-content: stretch;
	grid-template-rows: 400px 60px;
}

div {
	padding: 4px
}</style>`;

/* global customElements, template, fetch */

import './g-icon.mjs';
import './g-selectn.mjs';
import './g-window-header.mjs';
import './g-window-section.mjs';
import GModal from './g-modal.mjs';
import handle from './response-handler.mjs';

export default class GSelectNPicker extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.getElementById("cancel").addEventListener("click",
			() => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());

		this.shadowRoot.getElementById("commit").addEventListener("click", () =>
		{
			let values = this.shadowRoot.querySelector("g-selectn").value;
			this.dispatchEvent(new CustomEvent("commit", {"detail": values}));
			this.hide();
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
		this.shadowRoot.querySelector("g-selectn").options = options;
	}

	get options()
	{
		return this.shadowRoot.querySelector("g-selectn").options;
	}

	set values(values)
	{
		this.shadowRoot.querySelector("g-selectn").value = values;
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