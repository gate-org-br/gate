let template = document.createElement("template");
template.innerHTML = `
	<fieldset>
		<g-form></g-form>
	</fieldset>
	<g-coolbar>
		<a id='new' href='#'>
			Novo campo<g-icon>&#X1002;</g-icon>
		</a>
	</g-coolbar>
	<g-grid>
		Nenhum campo cadastrado
	</g-grid>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	gap: 8px;
	display: flex;
	flex-direction: column;
}

fieldset {
	border: none;
	padding: 12px;
	border-radius: 5px;
	border: 1px solid var(--base);
	background-color: var(--main-shaded10);
}</style>`;

/* global customElements */

import './g-form.mjs';
import './g-grid.mjs';
import './g-field-editor.mjs';
import GFieldEditorDialog from './g-field-editor-dialog.mjs';

customElements.define('g-form-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.draggable = true;
		grid.caption = "CAMPOS";
		grid.header = ["Nome", "Descrição", "Colunas", "Requerido", "Múltiplo", "Tam Max", "Padrão", "Máscara", "Opções", "Valor"];

		grid.widths = ["", "", "80px", "80px", "80px", "80px", "160px", "160px", "160px", "160px"];
		grid.aligns = ["left", "left"];
		grid.mapper = e => [
				e.name,
				e.description,
				["1", "2", "4", "8", "16"][Number(e.size || "4")],
				e.required || false,
				e.multiple || false,
				e.maxlength,
				e.pattern,
				e.mask,
				e.options,
				e.value
			];

		let form = this.shadowRoot.querySelector("g-form");

		grid.addEventListener("select", event =>
		{
			GFieldEditorDialog.edit(event.detail.value, "Campo").then(field =>
			{
				let value = form.value;
				if (field)
					value[event.detail.index] = field;
				else
					value = value.filter((e, index) => index !== event.detail.index);
				form.value = value;
				grid.values = value;

			});
		});

		grid.addEventListener("change", event => form.value = event.detail);

		this.shadowRoot.getElementById("new").addEventListener("click", () =>
		{
			GFieldEditorDialog.edit(null, "Novo campo").then(selected =>
			{
				let value = form.value;
				value.push(selected);
				form.value = value;
				grid.values = value;
			});
		});

	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	set value(value)
	{
		this.shadowRoot.querySelector("g-form").value = value;
		this.shadowRoot.querySelector("g-grid").values = value;
	}

	get value()
	{
		return this.shadowRoot.querySelector("g-form").value;
	}

	connectedCallback()
	{
		let form = this.closest("form");
		if (form)
			form.addEventListener("formdata", event => event.formData.set(this.name, JSON.stringify(this.value)));
	}

	attributeChangedCallback()
	{
		this.value = JSON.parse(this.getAttribute("value"));
	}

	static get observedAttributes()
	{
		return ['value'];
	}
});