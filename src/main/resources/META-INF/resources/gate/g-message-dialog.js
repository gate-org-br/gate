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
import './trigger.js';
import GBlock from './g-block.js';
import GWindow from './g-window.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import ResponseHandler from './response-handler.js';
import { TriggerStartupEvent, TriggerSuccessEvent, TriggerFailureEvent, TriggerResolveEvent } from './trigger-event.js';

/**
 * Represents a custom message dialog window that extends GWindow.
 * @extends {GWindow}
 * @class
 */
export default class GMessageDialog extends GWindow
{
	/**
	 * Creates an instance of GMessageDialog.
	 * @constructor
	 */
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.hide());
	}

	/**
	 * Sets the type attribute of the message dialog.
	 * @param {string} type - The type of the message dialog (SUCCESS, WARNING, ERROR, INFO).
	 */
	set type (type)
	{
		this.setAttribute("type", type);
	}

	/**
	 * Gets the type attribute of the message dialog.
	 * @returns {string} - The type of the message dialog.
	 */
	get type ()
	{
		return this.getAttribute("type");
	}

	/**
	 * Sets the text attribute of the message dialog.
	 * @param {string} text - The text content of the message dialog.
	 */
	set text (text)
	{
		this.setAttribute("text", text);
	}

	/**
	 * Gets the text attribute of the message dialog.
	 * @returns {string} - The text content of the message dialog.
	 */
	get text ()
	{
		return this.getAttribute("text");
	}

	/**
	 * Gets the title attribute of the message dialog.
	 * @returns {string} - The title of the message dialog.
	 */
	get title ()
	{
		return this.getAttribute("title");
	}

	/**
	 * Sets the title attribute of the message dialog.
	 * @param {string} title - The title of the message dialog.
	 */
	set title (title)
	{
		return this.setAttribute("title", title);
	}

	attributeChangedCallback (name)
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

	static get observedAttributes ()
	{
		return ['type', 'text', "title"];
	}

	/**
	 * Shows a success message dialog with the provided text and optional duration.
	 * @static
	 * @param {string} text - The text content of the success message.
	 * @param {number} [duration] - The duration in milliseconds for the message dialog.
	 */
	static success (text, duration)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "SUCCESS";
		message.text = text;
		message.title = "Sucesso";
		message.duration = duration;
		message.show();

		if (duration)
			window.top.setTimeout(() => message.hide(), duration);
	}

	/**
	 * Shows a warning message dialog with the provided text and optional duration.
	 * @static
	 * @param {string} text - The text content of the warning message.
	 * @param {number} [duration] - The duration in milliseconds for the message dialog.
	 */
	static warning (text, duration)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "WARNING";
		message.text = text;
		message.title = "Alerta";
		message.duration = duration;
		message.show();

		if (duration)
			window.top.setTimeout(() => message.hide(), duration);
	}

	/**
	 * Shows an error message dialog with the provided text and optional duration.
	 * @static
	 * @param {string} text - The text content of the error message.
	 * @param {number} [duration] - The duration in milliseconds for the message dialog.
	 */
	static error (text, duration)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "ERROR";
		message.text = text;
		message.title = "Erro";
		message.duration = duration;
		message.show();

		if (duration)
			window.top.setTimeout(() => message.hide(), duration);
	}

	/**
	 * Shows an info message dialog with the provided text and optional duration.
	 * @static
	 * @param {string} text - The text content of the info message.
	 * @param {number} [duration] - The duration in milliseconds for the message dialog.
	 */
	static info (text, duration)
	{
		var message = window.top.document.createElement("g-message-dialog");
		message.type = "INFO";
		message.text = text;
		message.title = "Informação";
		message.duration = duration;
		message.show();

		if (duration)
			window.top.setTimeout(() => message.hide(), duration);
	}

	/**
	 * Shows a message dialog based on the provided status object and optional duration.
	 * @static
	 * @param {Object} status - The status object containing type and message properties.
	 * @param {number} [duration] - The duration in milliseconds for the message dialog.
	 */
	static show (type, message, duration)
	{
		switch (type)
		{
			case "success":
				GMessageDialog.success(message, duration);
				break;
			case "warning":
				GMessageDialog.warning(message, duration);
				break;
			case "error":
				GMessageDialog.error(message, duration);
				break;
			case "info":
				GMessageDialog.info(message, duration);
				break;
		}
	}
}

customElements.define('g-message-dialog', GMessageDialog);

window.addEventListener("@message", function (event)
{
	event.preventDefault();
	event.stopPropagation();
	let { method, action, form, parameters } = event.detail;

	let type = parameters.filter(e => ["success", "warning", "error", "info"].includes(e))[0] || "success";
	let duration = parameters.filter(e => /^\d+$/.test(e)).map(parseInt)[0];

	event.target.dispatchEvent(new TriggerStartupEvent(event));
	let path = event.composedPath();
	return fetch(RequestBuilder.build(method, action, form))
		.then(ResponseHandler.text)
		.then(text => GMessageDialog.show(type, text, duration))
		.then(() => EventHandler.dispatch(path, new TriggerSuccessEvent(event)))
		.catch(error => EventHandler.dispatch(path, new TriggerFailureEvent(event, error)))
		.finally(() => EventHandler.dispatch(path, new TriggerResolveEvent(event)));
});