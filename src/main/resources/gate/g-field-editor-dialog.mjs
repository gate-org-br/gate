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
			<g-field-editor>
			</g-field-editor>
			<g-coolbar>
				<a id='commit' href='#'>
					Concluir<g-icon>&#X1000;</g-icon>
				</a>
				<a id='delete' href='#'>
					Remover<g-icon>&#X2026;</g-icon>
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
	max-width: 1200px;
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
	padding: 4px;
	display: grid;
	align-items: stretch;
	justify-content: stretch;
	grid-template-rows: auto 60px;
}

#delete {
	color: #660000;
}</style>`;

/* global customElements, template */

import './g-window-header.mjs';
import './g-window-section.mjs';
import './g-window-footer.mjs';
import './g-field-editor.mjs';
import GModal from './g-modal.mjs';

export default class GFieldEditorDialog extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		let editor = this.shadowRoot.querySelector("g-field-editor");

		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.hide());
		this.shadowRoot.getElementById("delete").addEventListener("click", () => this.dispatchEvent(new CustomEvent("delete")) | this.hide());
		this.shadowRoot.getElementById("commit").addEventListener("click", () =>
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
			this.shadowRoot.getElementById("delete").removeAttribute("hidden");
		else
			this.shadowRoot.getElementById("delete").setAttribute("hidden", "true");

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