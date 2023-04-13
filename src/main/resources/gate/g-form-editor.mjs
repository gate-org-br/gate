let template = document.createElement("template");
template.innerHTML = `
	<g-form></g-form>
	<g-coolbar>
		<a class='primary' id='new' href='#'>
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
	align-items: stretch;
	flex-direction: column;

}</style>`;

/* global customElements */

import './g-form.mjs';
import './g-grid.mjs';
import './g-field-editor.mjs';
import GFieldEditorDialog from './g-field-editor-dialog.mjs';

function fill(row, campo)
{
	row.value = campo;
	row.cell(0).value = campo.name;
	row.cell(1).value = campo.description;
	row.cell(2).value = ["1", "2", "4", "8", "16"][Number(campo.size || "4")];
	row.cell(3).value = campo.required || false;
	row.cell(4).value = campo.multiple || false;
	row.cell(5).value = campo.maxlength;
	row.cell(6).value = campo.pattern;
	row.cell(7).value = campo.mask;
	row.cell(8).value = campo.options;
	row.cell(9).value = campo.value;
}

customElements.define('g-form-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.movable = true;
		grid.caption = "CAMPOS";

		grid.column(0).header.value = "Nome";

		grid.column(1).header.value = "Descrição";

		grid.column(2).header.value = "Colunas";
		grid.column(2).style.width = "80px";
		grid.column(2).style.textAlign = "center";

		grid.column(3).header.value = "Requerido";
		grid.column(3).style.width = "80px";
		grid.column(3).style.textAlign = "center";

		grid.column(4).header.value = "Múltiplo";
		grid.column(4).style.width = "80px";
		grid.column(4).style.textAlign = "center";

		grid.column(5).header.value = "Tam Max";
		grid.column(5).style.width = "80px";
		grid.column(5).style.textAlign = "center";

		grid.column(6).header.value = "Padrão";
		grid.column(6).style.width = "160px";
		grid.column(6).style.textAlign = "center";

		grid.column(7).header.value = "Máscara";
		grid.column(7).style.width = "160px";
		grid.column(7).style.textAlign = "center";

		grid.column(8).header.value = "Opções";
		grid.column(8).style.width = "160px";
		grid.column(8).style.textAlign = "center";

		grid.column(9).header.value = "Valor";
		grid.column(9).style.width = "160px";
		grid.column(9).style.textAlign = "center";

		let form = this.shadowRoot.querySelector("g-form");

		grid.addEventListener("select", event =>
		{
			GFieldEditorDialog.edit(event.detail.value, "Campo").then(campo =>
			{
				let row = event.detail;
				if (campo)
				{
					fill(row, campo);
					form.set(row.index, campo);
				} else
				{
					form.remove(row.index);
					row.remove();
				}
				this.dispatchEvent(new CustomEvent("change"));
			});
		});

		grid.addEventListener("move", event =>
			form.move(event.detail.source, event.detail.target));

		this.shadowRoot.getElementById("new").addEventListener("click", () =>
		{
			GFieldEditorDialog.edit(null, "Novo campo").then(campo =>
			{
				form.add(campo);

				let row = grid.row();
				fill(row, campo);
				this.dispatchEvent(new CustomEvent("change"));
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
		let form = this.shadowRoot.querySelector("g-form");
		form.value = value;

		let grid = this.shadowRoot.querySelector("g-grid");
		grid.rows.forEach(e => e.remove());
		Array.from(value).forEach(campo => fill(grid.row(), campo));
		this.dispatchEvent(new CustomEvent("change"));
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