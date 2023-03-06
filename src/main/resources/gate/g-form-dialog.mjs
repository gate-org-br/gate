let template = document.createElement("template");
template.innerHTML = `
	<main>
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
			<button class="primary">
				Concluir<g-icon>&#X1000;</g-icon>
			</button>
			<hr/>
			<button class="tertiary">
				Cancelar<g-icon>&#X1001;</g-icon>
			</button>
		</footer>
	</main>
 <style>main
{
	width: 100%;
	height: auto;
	max-height: 100%;
}

@media only screen and (min-width: 640px)
{
	main {
		border-radius: 3px;
		width: calc(100% - 80px);
		max-height: calc(100% - 80px);
	}
}

g-form {
	flex-grow: 1;
}</style>`;

/* global customElements, template, fetch, Promise */

import './g-form.mjs';
import './g-icon.mjs';
import GWindow from './g-window.mjs';
import ResponseHandler from './response-handler.mjs';

export default class GFormDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML = this.shadowRoot.innerHTML + template.innerHTML;
		let form = this.shadowRoot.querySelector("g-form");
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.querySelector(".Cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.querySelector(".Commit").addEventListener("click", () =>
		{
			if (form.checkValidity())
			{
				this.dispatchEvent(new CustomEvent("commit", {detail: form.value}));
				this.hide();
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

	set value(value)
	{
		this.shadowRoot.querySelector("g-form").value = value;
	}

	get value()
	{
		this.shadowRoot.querySelector("g-form").value;
	}

	static edit(value, caption)
	{
		let dialog = window.top.document.createElement("g-form-dialog");
		dialog.value = value;
		if (caption)
			dialog.caption = caption;
		dialog.show();
		let promise = new Promise(resolve =>
		{
			dialog.addEventListener("commit", e => resolve(e.detail));
			dialog.addEventListener("cancel", () => resolve());
		});
		return promise;
	}

	static update(url, caption)
	{
		return fetch(url)
			.then(response => ResponseHandler.json(response))
			.then(form => GFormDialog.edit(form, caption))
			.then(result =>
			{
				if (!result)
					return Promise.resolve({"ok": true});

				return fetch(url,
					{method: "post",
						headers: {'Content-Type': 'application/json'},
						body: JSON.stringify(result)});
			})
			.then(response => ResponseHandler.json(response));
	}
}

customElements.define('g-form-dialog', GFormDialog);