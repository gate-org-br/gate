let template = document.createElement("template");
template.innerHTML = `
	<div>
		<button id="insert" title='Inserir tópico'>
			<g-icon>
				&#X1002;
			</g-icon>
		</button>

		<button id="update" title='Alterar tópico'>
			<g-icon>
				&#X2057;
			</g-icon>
		</button>

		<button id="delete" title='Remover tópico'>
			<g-icon>
				&#X1001;
			</g-icon>
		</button>
	</div>
	<ul id='root'>
	</ul>
 <style>:host(*)
{
	gap: 8px;
	display: grid;
	overflow: auto;
	min-width: 200px;
	max-width: 400px;
	resize: horizontal;
	border-radius: 5px;
	grid-template-rows: 40px 1fr;
	border: 1px solid var(--main6);
}

ul {
	list-style-type: decimal;
}

li > label {
	padding: 4px;
	flex-grow: 1;
	display: flex;
	cursor: pointer;
	align-items: center;
	justify-content: space-between;
}

li > label::hover {
	background-color: black;
}

div {
	gap: 4px;
	padding: 4px;
	display: flex;
	overflow: auto;
	align-items: center;
	border-radius: 5px 5px 0 0;
	justify-content: flex-start;
	background-color: var(--main6);
}

button
{
	color: black;
	height: 32px;
	display: flex;
	flex: 1 0 32px;
	font-size: 16px;
	background: none;
	border-radius: 5px;
	align-items: center;
	font-family: "gate";
	justify-content: center;
	border: 1px solid var(--main5);
	background-color: var(--main4);
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './g-help-topic.js';
import GMessageDialog from './g-message-dialog.js';
import GHelpEditorTopicDialog from './g-help-editor-topic-dialog.js';

function _import(values)
{
	return values.map(value =>
	{
		let li = document.createElement("li");

		let topic = li.appendChild(document.createElement("g-help-topic"));
		topic.name = value.name;
		topic.text = value.text;
		topic.value = value.value;

		let ul = li.appendChild(document.createElement("ul"));
		_import(value.topics).forEach(e => ul.appendChild(e));
		return li;
	});
}

function _export(values)
{
	return	Array.from(values).map(li => ({
			name: li.firstElementChild.name,
			text: li.firstElementChild.text,
			value: li.firstElementChild.value,
			topics: _export(li.lastElementChild.children)}));
}

customElements.define('g-help-editor-index', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("insert").addEventListener("click", () =>
		{
			GHelpEditorTopicDialog.edit().then(value =>
			{
				if (value)
				{
					let li = document.createElement("li");
					let topic = li.appendChild(document.createElement("g-help-topic"));
					topic.name = value.name;
					topic.text = value.text;
					li.appendChild(document.createElement("ul"));
					let selected = this.selected;
					if (selected)
						selected.closest("li").querySelector("ul").appendChild(li);
					else
						this.shadowRoot.querySelector("ul").appendChild(li);
				}
			});
		});
		this.shadowRoot.getElementById("update").addEventListener("click", () =>
		{
			let topic = this.selected;
			if (!topic)
				return GMessageDialog.error("Seleciona o tópico a ser alterado", 1000);
			GHelpEditorTopicDialog.edit({name: topic.name, text: topic.text}).then(value =>
			{
				if (value)
				{
					topic.name = value.name;
					topic.text = value.text;
				}
			});
		});
		this.shadowRoot.getElementById("delete").addEventListener("click", () =>
		{
			let topic = this.selected;
			if (!topic)
				return GMessageDialog.error("Seleciona o tópico a ser removido", 1000);
			topic.closest("li").remove();
		});
	}

	get value()
	{
		return _export(this.shadowRoot.getElementById("root").children);
	}

	set value(value)
	{
		let root = this.shadowRoot.getElementById("root");
		_import(value).forEach(li => root.appendChild(li));
	}

	get selected()
	{
		return this.shadowRoot.querySelector("g-help-topic[selected]");
	}
});

