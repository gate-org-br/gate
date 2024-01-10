let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<label id='caption'></label>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-form>
			</g-form>
		</section>
		<footer>
			<g-coolbar>
				<button id='commit' class="primary">
					Concluir<g-icon>&#X1000;</g-icon>
				</button>
				<hr/>
				<button id='cancel' class="tertiary">
					Desistir<g-icon>&#X1001;</g-icon>
				</button>
			</g-coolbar>
		</footer>
	</dialog>
 <style>dialog
{
	width: 100%;
	max-height: 100%;
	height: fit-content;
}

@media only screen and (min-width: 640px)
{
	dialog {
		border-radius: 3px;
		width: calc(100% - 80px);
		max-height: calc(100% - 80px);
	}
}

g-form {
	flex-grow: 1;
}</style>`;

/* global customElements, template, fetch, Promise */

import './g-form.js';
import './g-icon.js';
import './g-coolbar.js';
import GWindow from './g-window.js';
import CancelError from './cancel-error.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import TriggerEvent from './trigger-event.js';
import {TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent} from './trigger-event.js';

export default class GFormDialog extends GWindow
{
	constructor()
	{
		super();
		this.addEventListener("cancel", () => this.hide());
		this.addEventListener("commit", () => this.hide());
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent('cancel')));

		let form = this.shadowRoot.querySelector("g-form");
		let commit = this.shadowRoot.getElementById("commit");
		commit.addEventListener("click", () => form.reportValidity() && this.dispatchEvent(new CustomEvent("commit", {detail: form.value})));
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption").innerHTML = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	set value(value)
	{
		this.shadowRoot.querySelector("g-form").value = value;
	}

	get value()
	{
		this.shadowRoot.querySelector("g-form").value;
	}

	set width(value)
	{
		this.shadowRoot.querySelector("dialog").style.width = value;
	}

	set height(value)
	{
		this.shadowRoot.querySelector("dialog").style.height = value;
	}

	static pick(value, options)
	{
		return GFormDialog.edit(value.map(e => typeof e === 'string' ? {name: e, required: true} : e), options)
			.then(fields => fields.map(field => field.multiple || !field.value ? field.value : field.value[0]));
	}

	static edit(value, options)
	{
		value = value.map(e => typeof e === 'string' ? {name: e, required: true} : e);
		return new Promise((resolve, reject) =>
		{
			let dialog = window.top.document.createElement("g-form-dialog");
			dialog.value = value;
			if (typeof options === 'object')
			{
				if (options.caption)
					dialog.caption = options.caption;
				if (options.width)
					dialog.width = options.width;
				if (options.height)
					dialog.height = options.height;
			} else if (typeof options === 'string')
				dialog.caption = options;
			dialog.show();

			dialog.addEventListener("commit", e => resolve(e.detail));
			dialog.addEventListener("cancel", () => reject(new CancelError()));
		});
	}

	static update(url, options)
	{
		return fetch(url)
			.then(response => ResponseHandler.json(response))
			.then(form => GFormDialog.edit(form, options))
			.then(result => result
					? fetch(url, {method: "post", headers: {'Content-Type': 'application/json'}, body: JSON.stringify(result)})
					: Promise.resolve({"ok": true}))
			.then(response => ResponseHandler.json(response));
	}
}

customElements.define('g-form-dialog', GFormDialog);

window.addEventListener("@form", function (event)
{
	let parameters = event.detail.parameters;

	let target = parameters[0];
	if (!target)
		throw new Error("@form trigger target not specified");

	let path = event.composedPath();
	let trigger = path[0] || event.target;
	let {method, action, form} = event.detail;

	let options = {};
	options.caption = trigger.title;
	options.width = parameters[1];
	options.height = parameters[2] || options.width;

	fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.json)
		.then(form => GFormDialog.edit(form, options))
		.then(result => `data:application/json;base64,${btoa(JSON.stringify(result))}`)
		.then(action => trigger.dispatchEvent(new TriggerEvent(event, "get", action, target)))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});

