let template = document.createElement("template");
template.innerHTML = `
	<dialog part='dialog'>
		<header part='header' tabindex='1'>
			<label id='caption'>
			</label>
			<nav>
				<slot></slot>
			</nav>
			<g-navbar>
			</g-navbar>
			<a id='hide' href='#'>
				<g-icon>
					&#x1011;
				</g-icon>
			</a>
		</header>
		<section part='section'>
		</section>
	</dialog>
 <style>dialog {
	width: 100%;
	height: 100%;
	border-radius: 0;
}

@media only screen and (min-width: 640px)
{
	dialog{
		border-radius: 3px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}

:host(*) > dialog > section {
	padding: 0;
	align-items: stretch;
}

:host(*) > nav
{
	gap: 8px;
	width: auto;
	padding: 8px;
	display: flex;
	border-radius: 5px;
	align-items: center;
	justify-content: center;
	background-color: var(--main4);
}

::slotted(a),
::slotted(button)
{
	font-size: 14px;
	cursor: pointer;
	color: var(--main);
	text-decoration: none;
}

:host(*) > dialog > section > div {
	padding: 8px;
}

:host(*) > dialog > section >  iframe {
	margin: 0;
	width: 100%;
	border: none;
	padding: 0px;
	flex-grow: 1;
	overflow: hidden
}</style>`;

/* global customElements, template */

import './g-navbar.js';
import GWindow from './g-window.js';
import "./g-dialog-configuration.js";
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';
import { TriggerResolveEvent } from './trigger-event.js';

export default class GDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML += template.innerHTML;
		this.shadowRoot.getElementById("hide").addEventListener("click", () => this.hide());
	}

	get caption()
	{
		return this.shadowRoot
			.getElementById("caption").innerText;
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption")
			.innerText = caption;
	}

	get navbar()
	{
		return this.shadowRoot.querySelector("g-navbar");
	}

	get type()
	{
		let section = this.shadowRoot.querySelector("section");
		return !section.firstElementChild || section.firstElementChild.tagName === "IFRAME" ? "frame" : "fetch";
	}

	get iframe()
	{
		let section = this.shadowRoot.querySelector("section");

		if (!section.firstElementChild)
		{
			let iframe = section.appendChild(document.createElement("iframe"));
			iframe.dialog = this;
			iframe.name = "@dialog";
			iframe.setAttribute("scrolling", "no");
			iframe.addEventListener("load", () => iframe.focus());
			iframe.addEventListener("mouseenter", () => iframe.focus());

			let navbar = this.shadowRoot.querySelector("g-navbar");
			navbar.addEventListener("update", event => iframe.src = event.detail);
			iframe.addEventListener("load", () => navbar.target = iframe.contentWindow.location.href);
		}

		if (section.firstElementChild.tagName !== "IFRAME")
			throw new Error("Attempt to access iframe of a fetch dialog");

		return section.firstElementChild;
	}

	get content()
	{
		let section = this.shadowRoot.querySelector("section");

		if (!section.firstElementChild)
		{
			let div = section.appendChild(document.createElement("div"));

			let navbar = this.shadowRoot.querySelector("g-navbar");
			navbar.addEventListener("update", event => fetch(event.detail)
					.then(ResponseHandler.text)
					.then(html => div.innerHTML = html)
					.catch(GMessageDialog.error));
		}

		if (section.firstElementChild.tagName !== "DIV")
			throw new Error("Attempt to access content of a frame dialog");

		return section.firstElementChild;
	}

	set width(value)
	{
		this.shadowRoot.querySelector("dialog").style.width = value;
	}

	set height(value)
	{
		this.shadowRoot.querySelector("dialog").style.height = value;
	}

	static hide()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}

	static set caption(caption)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.caption = caption;
	}

	static get caption()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.caption;
	}
};

customElements.define('g-dialog', GDialog);

window.addEventListener("@dialog", function (event)
{
	let parameters = event.detail.parameters;
	let trigger = event.composedPath()[0] || event.target;


	let dialog = window.top.document.createElement("g-dialog");
	dialog.caption = trigger.getAttribute("title");

	if (parameters[1])
		dialog.width = parameters[1];
	if (parameters[2])
		dialog.height = parameters[2];

	if (trigger.hasAttribute("data-navigator")
		|| trigger.parentNode.hasAttribute("data-navigator"))
		dialog.navbar.targets = Array.from(trigger.parentNode.children)
			.map(e => e.href || e.formaction || e.getAttribute("data-action"));

	dialog.show().finally(() => setTimeout(() => event.target.dispatchEvent(new TriggerResolveEvent(event)), 0));


	switch (event.detail.parameters[0] || "frame")
	{
		case "frame":
			if (event.detail.method === "get")
				dialog.iframe.src = event.detail.action;
			else
				fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
					.then(ResponseHandler.text)
					.then(html => dialog.iframe.srcDoc = html)
					.catch(GMessageDialog.error);
			break;
		case "fetch":
			fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
				.then(ResponseHandler.text)
				.then(html => dialog.content.innerHTML = html)
				.catch(GMessageDialog.error);
			break;
	}
});

