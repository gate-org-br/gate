let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			<label id='caption'>
			</label>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<fieldset>
				<label>
					Nome
					<input required type="text" id='name'>
				</label>

				<label>
					Texto
					<input required type="text" id='text'>
				</label>
			</fieldset>
		</section>
		<footer>
			<g-coolbar>
				<button class='primary' id='commit'>
					Concluir<g-icon>&#X1000;</g-icon>
				</button>
				<hr/>
				<button class='tertiary' id='cancel'>
					Desistir<g-icon>&#X2027;</g-icon>
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

fieldset {
	flex-grow: 1
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './g-coolbar.js';
import GWindow from './g-window.js';
export default class GHelpEditorTopicDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		let name = this.shadowRoot.getElementById("name");
		let text = this.shadowRoot.getElementById("text");
		let commit = this.shadowRoot.getElementById("commit");
		let cancel = this.shadowRoot.getElementById("cancel");
		cancel.addEventListener("click", () => this.dispatchEvent(new CustomEvent("cancel")) | this.hide());
		commit.addEventListener("click", () =>
		{
			if (name.reportValidity() && text.reportValidity())
			{
				this.dispatchEvent(new CustomEvent('commit', {detail: this.value}));
				this.hide();
			}
		});
		this.shadowRoot.querySelector("main").addEventListener("click", event => event.stopPropagation());
	}

	get value()
	{
		let name = this.shadowRoot.getElementById("name").value || "";
		let text = this.shadowRoot.getElementById("text").value || "";
		return {name, text};
	}

	set value(value)
	{
		this.shadowRoot.getElementById("name").value = value.name || "";
		this.shadowRoot.getElementById("text").value = value.text || "";
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption").innerHTML;
	}

	set caption(value)
	{
		this.shadowRoot.getElementById("caption").innerHTML = value;
	}

	static edit(value)
	{
		let dialog = window.top.document.createElement("g-help-editor-topic-dialog");

		if (value)
		{
			dialog.value = value;
			dialog.caption = "Alterar tópico";
		} else
			dialog.caption = "Inserir tópico";

		dialog.show();
		return new Promise(resolve =>
		{
			dialog.addEventListener("cancel", () => resolve());
			dialog.addEventListener("commit", e => resolve(e.detail));
		});
	}
}

customElements.define('g-help-editor-topic-dialog', GHelpEditorTopicDialog);