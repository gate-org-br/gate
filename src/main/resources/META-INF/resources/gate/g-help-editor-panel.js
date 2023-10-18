let template = document.createElement("template");
template.innerHTML = `
	<g-text-editor hidden>
	</g-text-editor>
 <style>:host(*)
{
	border-radius: 5px;
	border: 1px solid var(--main6);
}

g-text-editor {
	height: 100%;
}</style>`;

/* global customElements, template */

import './g-text-editor.js';
customElements.define('g-help-editor-panel', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let editor = this.shadowRoot.querySelector("g-text-editor");
		editor.toolbar.spacer().command("3020", "Salvar", () => this.dispatchEvent(new CustomEvent("commited")));
	}

	set hidden(value)
	{
		this.shadowRoot.querySelector("g-text-editor").hidden = value;
	}

	get hidden()
	{
		return this.shadowRoot.querySelector("g-text-editor").hidden;
	}

	get value()
	{
		return btoa(this.shadowRoot.querySelector("g-text-editor").value);
	}

	set value(value)
	{
		this.shadowRoot.querySelector("g-text-editor").value = atob(value);
	}
});

