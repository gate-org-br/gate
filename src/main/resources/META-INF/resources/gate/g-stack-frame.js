let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<iframe name="@stack" scrolling="no">
		</iframe>
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

dialog {
	width: 100%;
	height: 100%;
	display:  flex;
	overflow: auto;
	max-width: unset;
	max-height: unset;
	align-items: stretch;
	justify-content: center;
}

iframe {
	flex-grow: 1;
	overflow:  hidden;
}</style>`;

/* global customElements, template */

import './trigger.js';
import GModal from './g-modal.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

customElements.define('g-stack-frame', class GStackFrame extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let iframe = this.iframe;

		iframe.dialog = this;
		iframe.addEventListener("load", () => iframe.focus());
		iframe.addEventListener("mouseenter", () => iframe.focus());
	}

	get iframe()
	{
		return this.shadowRoot.querySelector("iframe");
	}

	set target(target)
	{
		this.iframe.setAttribute('src', target);
	}
});

window.addEventListener("@stack", function (event)
{
	let trigger = event.composedPath()[0] || event.target;
	let stack = window.top.document.createElement("g-stack-frame");
	stack.addEventListener("show", () => trigger.dispatchEvent(new CustomEvent('show', {bubbles: true, detail: {modal: stack}})));
	stack.addEventListener("hide", () => trigger.dispatchEvent(new CustomEvent('hide', {bubbles: true, detail: {modal: stack}})));


	stack.show();
	if (event.detail.method === "get")
		stack.iframe.src = event.detail.action;
	else
		fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
			.then(ResponseHandler.text)
			.then(html => stack.iframe.srcDoc = html)
			.catch(GMessageDialog.error);
});
