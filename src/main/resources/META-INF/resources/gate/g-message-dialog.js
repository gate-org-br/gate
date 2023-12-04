let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<label id='title'>
			</label>
			<a id='cancel' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
		</section>
	</dialog>
 <style>dialog
{
	width: 100%;
	height: 240px;
	color: inherit;
	min-width: 320px;
	max-width: 800px;
}

dialog > section {
	padding: 8px;
	display: flex;
	color: inherit;
	font-size: 20px;
	align-items: center;
}

section::before {
	display: flex;
	color: inherit;
	flex-shrink: 0;
	font-size: 80px;
	flex-basis: 160px;
	align-items: center;
	font-family: 'gate';
	justify-content: center;
}

label::first-line  {
	text-indent: 40px;
}

:host([type="INFO"]) {
	color: black
}
:host([type="INFO"]) > dialog > section::before {
	content: "\\2015"
}

:host([type="ERROR"]) {
	color: var(--error1)
}
:host([type="ERROR"]) > dialog > section::before {
	content: "\\1001"
}

:host([type="SUCCESS"]) {
	color: var(--success1)
}
:host([type="SUCCESS"]) > dialog > section::before {
	content: "\\1000"
}

:host([type="WARNING"]) {
	color: var(--warning1)
}
:host([type="WARNING"]) > dialog > section::before {
	content: "\\1007"
}</style>`;

/* global customElements, template */

import './g-icon.js';
import GWindow from './g-window.js';

export default class GMessageDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.hide());
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get type()
	{
		return this.getAttribute("type");
	}

	set text(text)
	{
		this.setAttribute("text", text);
	}

	get text()
	{
		return this.getAttribute("text");
	}

	get title()
	{
		return this.getAttribute("title");
	}

	set title(title)
	{
		return this.setAttribute("title", title);
	}

	attributeChangedCallback(name)
	{
		switch (name)
		{
			case 'title':
				this.shadowRoot.getElementById("title").innerText = this.title || "";
				break;
			case 'text':
				this.shadowRoot.querySelector("section").innerText = this.text || "";
				break;
		}
	}

	static get observedAttributes()
	{
		return ['type', 'text', "title"];
	}

	static success(text, timeout)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "SUCCESS";
		message.text = text;
		message.title = "Sucesso";
		message.timeout = timeout;
		message.show();

		if (timeout)
			window.top.setTimeout(() => message.hide(), timeout);
	}

	static warning(text, timeout)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "WARNING";
		message.text = text;
		message.title = "Alerta";
		message.timeout = timeout;
		message.show();

		if (timeout)
			window.top.setTimeout(() => message.hide(), timeout);
	}

	static error(text, timeout)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "ERROR";
		message.text = text;
		message.title = "Erro";
		message.timeout = timeout;
		message.show();

		if (timeout)
			window.top.setTimeout(() => message.hide(), timeout);
	}

	static  info(text, timeout)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "INFO";
		message.text = text;
		message.title = "Informação";
		message.timeout = timeout;
		message.show();

		if (timeout)
			window.top.setTimeout(() => message.hide(), timeout);
	}

	static  show(status, timeout)
	{
		switch (status.type)
		{
			case "SUCCESS":
				GMessageDialog.success(status.message, timeout);
				break;
			case "WARNING":
				GMessageDialog.warning(status.message, timeout);
				break;
			case "ERROR":
				GMessageDialog.error(status.message, timeout);
				break;
			case "INFO":
				GMessageDialog.info(status.message, timeout);
				break;
		}
	}
}

customElements.define('g-message-dialog', GMessageDialog);