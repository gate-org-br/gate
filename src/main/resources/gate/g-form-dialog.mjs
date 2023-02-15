let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			<label id='caption'></label>
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
			<fieldset>
				<g-form>
				</g-form>
			</fieldset>
			<g-coolbar>
				<a id='commit' href='#'>
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
	width: 100%;
	height: 100%;
	display: grid;
	overflow: auto;
	border-radius: 0;
	position: relative;
	grid-template-rows: 40px 1fr;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: var(--g-window-border);
}

g-window-section
{
	padding: 4px;
	display: grid;
	align-items: stretch;
	justify-content: stretch;
	grid-template-rows: auto 60px;
}

fieldset {
	border: none;
	padding: 12px;
	border-radius: 5px;
	border: 1px solid var(--base);
	background-color: var(--main-shaded10);
}

@media only screen and (min-width: 640px)
{
	main{
		border-radius: 5px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}</style>`;

/* global customElements, template, fetch, Promise */

import './g-form.mjs';
import './g-window-header.mjs';
import './g-window-section.mjs';
import GModal from './g-modal.mjs';
import handle from './response-handler.mjs';

export default class GFormDialog extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
		let form = this.shadowRoot.querySelector("g-form");
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		this.shadowRoot.getElementById("commit").addEventListener("click", () =>
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
			.then(response => handle(response))
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
			.then(response => handle(response));
	}
}

customElements.define('g-form-dialog', GFormDialog);