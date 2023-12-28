let template = document.createElement("template");
template.innerHTML = `
	<dialog>
	</dialog>
 <style>* {
	margin: 0;
	padding: 0;
	border: none;
	box-sizing: border-box
}

:host(*) {
	width: 0;
	height: 0;
	max-width: unset;
}

:host(*) > dialog {
	width: 100%;
	height: 100%;
	display:  flex;
	overflow: auto;
	max-width: unset;
	max-height: unset;
	align-items: stretch;
	justify-content: center;
}

:host(*) > dialog > div
{
	padding: 8px;
}


:host(*) > dialog > iframe {
	flex-grow: 1;
	overflow:  hidden;
}</style>`;

/* global customElements, template */

import './trigger.js';
import GModal from './g-modal.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';
import { TriggerResolveEvent } from './trigger-event.js';

customElements.define('g-stack-frame', class GStackFrame extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	get iframe()
	{
		let dialog = this.shadowRoot.querySelector("dialog");

		if (!dialog.firstElementChild)
		{
			let iframe = dialog.appendChild(document.createElement("iframe"));
			iframe.dialog = this;
			iframe.name = "@stack";
			iframe.setAttribute("scrolling", "no");
			iframe.addEventListener("load", () => iframe.focus());
			iframe.addEventListener("mouseenter", () => iframe.focus());
		}

		if (dialog.firstElementChild.tagName !== "IFRAME")
			throw new Error("Attempt to access iframe of a fetch stack-frame");

		return dialog.firstElementChild;
	}

	get content()
	{
		let dialog = this.shadowRoot.querySelector("dialog");

		if (!dialog.firstElementChild)
			dialog.appendChild(document.createElement("div"));
		if (dialog.firstElementChild.tagName !== "DIV")
			throw new Error("Attempt to access content of a frame stack-frame");

		return dialog.firstElementChild;
	}
});

window.addEventListener("@stack", function (event)
{
	let stack = window.top.document.createElement("g-stack-frame");

	stack.show().finally(() => setTimeout(() => event.target.dispatchEvent(new TriggerResolveEvent(event)), 0));

	switch (event.detail.parameters[0] || "frame")
	{
		case "frame":
			if (event.detail.method === "get")
				stack.iframe.src = event.detail.action;
			else
				fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
					.then(ResponseHandler.text)
					.then(html => stack.iframe.srcDoc = html)
					.catch(GMessageDialog.error);
			break;
		case "fetch":
			fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
				.then(ResponseHandler.text)
				.then(html => stack.content.innerHTML = html)
				.catch(GMessageDialog.error);
			break;
	}

});
