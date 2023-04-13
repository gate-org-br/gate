let template = document.createElement("template");
template.innerHTML = `
	<link rel='stylesheet'
	      type='text/css' href='./gate/input.css'/>
	<link rel='stylesheet'
	      type='text/css' href='./gate/fieldset.css'/>

	<fieldset>
		<label data-size="4">
			Nome:
			<span>
				<input required id='name' type='text' tabindex="1"/>
			</span>
		</label>
		<label data-size="4">
			Máscara:
			<span>
				<input id='mask' type='text' tabindex="1"/>
			</span>
		</label>
		<label data-size="2">
			Colunas:
			<span>
				<select id='size' tabindex="1">
					<option value=""></option>
					<option value="0">1 Coluna</option>
					<option value="1">2 Colunas</option>
					<option value="2">4 Colunas</option>
					<option value="3">8 Colunas</option>
				</select>
			</span>
		</label>
		<label data-size="2">
			Multiplo:
			<span>
				<select id='multiple' tabindex="1">
					<option value=""></option>
					<option value="true">Sim</option>
					<option value="false">Não</option>
				</select>
			</span>
		</label>
		<label data-size="2">
			Requerido:
			<span>
				<select id='required' tabindex="1">
					<option value=""></option>
					<option value="true">Sim</option>
					<option value="false">Não</option>
				</select>
			</span>
		</label>
		<label data-size="2">
			Tamanho Max:
			<span>
				<input id='maxlength' min='1' type='number' tabindex="1"/>
			</span>
		</label>
		<label data-size="8">
			Expressão Regular:
			<span style='flex-basis: 120px;'>
				<textarea id="pattern" tabindex="1"></textarea>
			</span>
		</label>
		<label data-size="8">
			Descrição:
			<span style='flex-basis: 120px;'>
				<textarea id="description" tabindex="1"></textarea>
			</span>
		</label>
		<label data-size="8">
			Opções:
			<span style='flex-basis: 120px;'>
				<textarea id="options" tabindex="1"></textarea>
			</span>
		</label>
		<label data-size="8">
			Valor:
			<span style='flex-basis: 120px;'>
				<textarea id="value" tabindex="1"></textarea>
			</span>
		</label>
	</fieldset>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	display: flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

fieldset {
	padding: 0;
	border: none;
}</style>`;

/* global customElements */

customElements.define('g-field-editor', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set value(value)
	{
		let name = this.shadowRoot.getElementById("name");
		let mask = this.shadowRoot.getElementById("mask");
		let size = this.shadowRoot.getElementById("size");
		let multiple = this.shadowRoot.getElementById("multiple");
		let required = this.shadowRoot.getElementById("required");
		let maxlength = this.shadowRoot.getElementById("maxlength");
		let pattern = this.shadowRoot.getElementById("pattern");
		let description = this.shadowRoot.getElementById("description");
		let options = this.shadowRoot.getElementById("options");
		let values = this.shadowRoot.getElementById("value");

		if (value)
		{
			name.value = value.name || "";
			mask.value = value.mask || "";
			size.value = value.size || "";
			multiple.value = value.multiple || false;
			required.value = value.required || false;
			maxlength.value = value.maxlength || "";
			pattern.value = value.pattern || "";
			description.value = value.description || "";

			if (value.options)
				value.options.forEach(e => options.value = options.value ? options.value + "\n" + e : e);
			if (value.value)
				value.value.forEach(e => values.value = values.value ? values.value + "\n" + e : e);
		} else
			name.value = mask.value = size.value = multiple.value = required.value
				= maxlength.value = pattern.value = description.value = options.value = values.value = "";
	}

	get value()
	{
		let result = {};

		let name = this.shadowRoot.getElementById("name").value;
		result.name = name || "";

		let mask = this.shadowRoot.getElementById("mask").value;
		if (mask)
			result.mask = mask;

		let size = this.shadowRoot.getElementById("size").value;
		if (size)
			result.size = size;

		let multiple = this.shadowRoot.getElementById("multiple").value;
		if (multiple === "true")
			result.multiple = true;

		let required = this.shadowRoot.getElementById("required").value;
		if (required === "true")
			result.required = true;

		let maxlength = this.shadowRoot.getElementById("maxlength").value;
		if (maxlength)
			result.maxlength = maxlength;

		let pattern = this.shadowRoot.getElementById("pattern").value;
		if (pattern)
			result.pattern = pattern;

		let description = this.shadowRoot.getElementById("description").value;
		if (description)
			result.description = description;

		let options = this.shadowRoot.getElementById("options").value;
		if (options)
			result.options = options.split("\n");

		let value = this.shadowRoot.getElementById("value").value;
		if (value)
			result.value = value.split("\n");

		return result;
	}

	validate()
	{
		let name = this.shadowRoot.getElementById("name");
		if (!name.value || !name.value.trim().length)
		{
			name.reportValidity();
			return false;
		}
		return true;

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