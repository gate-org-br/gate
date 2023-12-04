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
}

dialog {
	width: 100vw;
	height: 100vh;
	min-width: 100vw;
	min-height: 100vh;
	display:  flex;
	overflow: auto;
	align-items: stretch;
	justify-content: center;
}

iframe {
	flex-grow: 1;
	overflow:  hidden;
}

iframe[name] {
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}</style>`;

/* global customElements, template */

import './trigger.js';
import hide from './hide.js';
import GModal from './g-modal.js';

customElements.define('g-stack-frame', class GStackFrame extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let iframe = this.iframe;

		iframe.dialog = this;
		iframe.onmouseenter = () => this.iframe.focus();

		iframe.addEventListener("load", () =>
		{
			iframe.removeAttribute("name");
			iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));
		});
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

window.addEventListener("trigger", function (event)
{
	if (event.detail.target === "@stack")
	{
		let stack = window.top.document.createElement("g-stack-frame");
		stack.addEventListener("show", () => event.detail.element.dispatchEvent(new CustomEvent('show', {detail: {modal: stack}})));
		stack.addEventListener("hide", () => event.detail.element.dispatchEvent(new CustomEvent('hide', {detail: {modal: stack}})));
		if (event.detail.element.hasAttribute("data-on-hide"))
			stack.addEventListener("hide", () => hide(event.detail.element));
		stack.show();
	}
});
