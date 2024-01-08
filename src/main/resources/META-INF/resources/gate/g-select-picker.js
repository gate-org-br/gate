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
				Nenhum registro encontrado
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
import DOM from './dom.js';
import GWindow from './g-window.js';
import Extractor from './extractor.js';
import ObjectFilter from './object-filter.js';
import GMessageDialog from './g-message-dialog.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';

export default class GSelectPicker extends GWindow
{
	#options;
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.addEventListener("select", e => this.dispatchEvent(new CustomEvent("commit", {detail: {index: e.detail.index, value: e.detail.value}})));


		let input = this.shadowRoot.querySelector("input");
		input.addEventListener("input", () => grid.dataset = ObjectFilter.filter(this.options, input.value));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	get options()
	{
		return this.#options || [];
	}

	set options(options)
	{
		this.#options = options;
		this.shadowRoot.querySelector("g-grid").dataset = options;
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

		return new Promise((resolve, reject) =>
		{
			picker.addEventListener("cancel", () => reject(new Error("Cancel")));
			picker.addEventListener("commit", e => resolve(e.detail));
		});
	}
};

window.addEventListener("@select", function (event)
{
	let trigger = event.composedPath()[0] || event.target;

	let parameters = event.detail.parameters.map(e => DOM.navigate(trigger, e).orElse(null));

	let value = parameters[0] || trigger.parentNode.querySelector("input[type=hidden]");
	if (!value)
		throw new Error("Value input not found");


	let label = parameters[1] || trigger.parentNode.querySelector("input[type=text]");
	if (!label)
		throw new Error("Label input not found");

	if (label.value || value.value)
		return label.value = value.value = '';

	trigger.style.pointerEvents = "none";
	fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(options => ResponseHandler.json(options))
		.catch(error => GMessageDialog.error(error.message))
		.then(options => GSelectPicker.pick(options, trigger.title))
		.then(object =>
		{
			label.value = Extractor.label(object.value);
			value.value = Extractor.value(object.value);
		})
		.catch(() => undefined)
		.finally(() => trigger.style.pointerEvents = "");
});

customElements.define('g-select-picker', GSelectPicker);