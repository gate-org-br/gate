let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<slot></slot>
	</dialog>
 <style data-element="g-stack-frame">* {
	box-sizing: border-box;
}

dialog {
	margin: 0;
	width: 100%;
	height: 100%;
	border: none;
	padding: 8px;
	border-radius: 0;
	max-width: unset;
	max-height: unset;
}

:host(*) {
	gap: 8px;
	padding: 8px;
	flex-grow: 1;
	align-items: stretch;
	flex-direction: column;
}

:host([data-loading]) > dialog > section > *
{
	visibility: hidden;
}

:host([data-loading]) > dialog > section {
	background-position: center;
	background-repeat: no-repeat;
	background-image: var(--loading);
	background-size: clamp(128px, 25%, 256px);
}

::slotted(iframe:only-child) {
	margin: 0;
	width: 100%;
	height: 100%;
	border: none;
	padding: 0px;
	flex-grow: 1;
	overflow: hidden;
}</style>`;
/* global customElements, template */

import './trigger.js';
import GModal from './g-modal.js';
import stylesheets from './stylesheets.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';
import { TriggerResolveEvent } from './trigger-event.js';

function resize(iframe)
{
	let iframeDocument = iframe.contentDocument || iframe.contentWindow.document;
	var contentWidth = iframeDocument.body.scrollWidth;
	var contentHeight = iframeDocument.body.scrollHeight;
	iframe.style.width = contentWidth + 'px';
	iframe.style.height = contentHeight + 'px';
}

customElements.define('g-stack-frame', class GStackFrame extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		stylesheets('table.css', 'input.css', 'fieldset.css')
			.forEach(e => this.shadowRoot.appendChild(e));
	}

	get iframe()
	{

		if (!this.firstElementChild)
		{
			let iframe = this.appendChild(document.createElement("iframe"));
			iframe.dialog = this;
			iframe.setAttribute("scrolling", "no");
			iframe.addEventListener("load", () => iframe.focus());
			iframe.addEventListener("mouseenter", () => iframe.focus());
		}

		if (this.firstElementChild.tagName !== "IFRAME")
			throw new Error("Attempt to access iframe of a fetch stack-frame");

		return this.firstElementChild;
	}

	get content()
	{
		if (!this.firstElementChild)
			this.appendChild(document.createElement("div"));
		if (this.firstElementChild.tagName !== "DIV")
			throw new Error("Attempt to access content of a frame stack-frame");
		return this.firstElementChild;
	}
});