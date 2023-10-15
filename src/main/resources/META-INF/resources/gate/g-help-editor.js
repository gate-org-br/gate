let template = document.createElement("template");
template.innerHTML = `
	<g-help-editor-index>
	</g-help-editor-index>
	<g-help-editor-panel>
	</g-help-editor-panel>
 <style>:host(*)
{
	gap: 4px;
	padding: 4px;
	display: grid;
	grid-template-columns: auto 1fr;
}</style>`;

/* global customElements, template */

import './g-help-editor-index.js';
import './g-help-editor-panel.js';
import GMessageDialog from './g-message-dialog.js';

export default class GHelpEditor extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let index = this.shadowRoot.querySelector("g-help-editor-index");
		let editor = this.shadowRoot.querySelector("g-help-editor-panel");

		index.addEventListener("selected", () =>
		{
			let topic = index.selected;
			if (topic)
			{
				editor.hidden = false;
				editor.value = topic.value;
			} else
				editor.hidden = true;
		});
		editor.addEventListener("commited", () =>
		{
			index.selected.value = editor.value;
			GMessageDialog.success("Tópico salvo com sucesso", 1000);
		});
	}

	get value()
	{
		return this.shadowRoot.querySelector("g-help-editor-index").value;
	}

	set value(value)
	{
		this.shadowRoot.querySelector("g-help-editor-index").value = value;
	}
}

customElements.define('g-help-editor', GHelpEditor);

