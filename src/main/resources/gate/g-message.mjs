let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			<label id='title'>
			</label>
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<section>
			<label id='text'>
			</label>
		</section>
	</main>
 <style>* {
	box-sizing: border-box
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
	width: 80%;
	height: 280px;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 800px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 1fr;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}

section {
	display: flex;
	overflow: auto;
	align-items: stretch;
	justify-content: center;
	background-image: var(--g-window-section-background-image);
	background-color: var(--g-window-section-background-color);
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
	background-color: white;
}

#text {
	flex-grow: 1;
	padding: 8px;
	display: flex;
	color: inherit;
	font-size: 20px;
	align-items: center;
	background-color: white;
}

label::first-line  {
	text-indent: 40px;
}

:host([type="INFO"]) {
	color: black
}
:host([type="INFO"]) > main > section::before {
	content: "\\2015"
}

:host([type="ERROR"]) {
	color: var(--r)
}
:host([type="ERROR"]) > main > section::before {
	content: "\\1001"
}

:host([type="SUCCESS"]) {
	color: var(--g)
}
:host([type="SUCCESS"]) > main > section::before {
	content: "\\1000"
}

:host([type="WARNING"]) {
	color: var(--y)
}
:host([type="WARNING"]) > main > section::before {
	content: "\\1007"
}</style>`;

/* global customElements, template */

import './g-window-header.mjs';
import GModal from './g-modal.mjs';

export default class Message extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
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
				this.shadowRoot.getElementById("text").innerText = this.text || "";
				break;
		}
	}

	static get observedAttributes() {
		return ['type', 'text', "title"];
	}

	static success(text, timeout)
	{
		var message = window.top.document.createElement("g-message");
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
		var message = window.top.document.createElement("g-message");
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
		var message = window.top.document.createElement("g-message");
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
		var message = window.top.document.createElement("g-message");
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
				Message.success(status.message, timeout);
				break;
			case "WARNING":
				Message.warning(status.message, timeout);
				break;
			case "ERROR":
				Message.error(status.message, timeout);
				break;
			case "INFO":
				Message.info(status.message, timeout);
				break;
		}
	}
}

customElements.define('g-message', Message);