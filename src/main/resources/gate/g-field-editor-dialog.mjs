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
			<g-field-editor>
			</g-field-editor>
		</section>
		<footer>
			<button class="primary">
				Concluir<g-icon>&#X1000;</g-icon>
			</button>
			<button class='Delete'>
				Remover<g-icon>&#X2026;</g-icon>
			</button>
			<hr/>
			<button class="tertiary">
				Cancelar<g-icon>&#X1001;</g-icon>
			</button>
		</footer>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 1200px;
	width: calc(100% - 40px);
}

g-field-editor {
	flex-grow: 1;
}</style>`;

/* global customElements, template */

import './g-icon.mjs';
import './g-field-editor.mjs';
import GWindow from './g-window.mjs';

export default class GFieldEditorDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML =
			this.shadowRoot.innerHTML + template.innerHTML;

		let editor = this.shadowRoot.querySelector("g-field-editor");

		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector(".Cancel").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector(".Delete").addEventListener("click", () => this.dispatchEvent(new CustomEvent("delete")) | this.hide());
		this.shadowRoot.querySelector(".Commit").addEventListener("click", () =>
		{
			if (editor.validate())
			{
				this.dispatchEvent(new CustomEvent("commit", {detail: editor.value}));
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
		this.shadowRoot.querySelector("g-field-editor").value = value;
		if (value)
			this.shadowRoot.querySelector(".Delete").removeAttribute("hidden");
		else
			this.shadowRoot.querySelector(".Delete").setAttribute("hidden", "true");

	}

	get value()
	{
		return this.shadowRoot.querySelector("g-field-editor").value;
	}

	static edit(value, caption)
	{
		let dialog = window.top.document.createElement("g-field-editor-dialog");
		dialog.value = value;
		if (caption)
			dialog.caption = caption;
		dialog.show();

		let promise = new Promise(resolve =>
		{
			dialog.addEventListener("commit", e => resolve(e.detail));
			dialog.addEventListener("delete", () => resolve(null));
		});

		return promise;
	}
}

customElements.define('g-field-editor-dialog', GFieldEditorDialog);