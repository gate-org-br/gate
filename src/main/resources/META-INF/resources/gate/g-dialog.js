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
	display: flex;
	align-items: stretch;
	flex-direction: column;
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
	gap: 8px;
	padding: 8px;
	display: flex;
	flex-direction: column;
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
			iframe.setAttribute("scrolling", "no");
			iframe.addEventListener("load", () => iframe.focus());
			iframe.addEventListener("mouseenter", () => iframe.focus());

			this.shadowRoot.querySelector("g-navbar")
				.addEventListener("update", event => iframe.src = event.detail.target);
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

			this.shadowRoot.querySelector("g-navbar")
				.addEventListener("update", event =>
					fetch(event.detail.target)
						.then(ResponseHandler.text)
						.then(html => div.innerHTML = html)
						.catch(e => GMessageDialog.error(e) || event.preventDefault()));
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
	let {method, action, form, parameters} = event.detail;
	let trigger = event.composedPath()[0] || event.target;

	let dialog = window.top.document.createElement("g-dialog");
	dialog.caption = trigger.getAttribute("title");

	let type = parameters.filter(e => e === "fetch" || e === "frame")[0] || "fetch";
	let size = parameters.filter(e => e !== "fetch" && e !== "frame")[0];
	let height = parameters.filter(e => e => e !== "fetch" && e !== "frame")[1] || size;

	if (size)
		dialog.width = dialog.height = size;
	if (height)
		dialog.height = height;


	if (trigger.hasAttribute("data-navigator") || trigger.parentNode.hasAttribute("data-navigator"))
	{
		let triggers = Array.from(trigger.parentNode.children);
		dialog.navbar.targets = triggers.map(e => e.href || e.formaction || e.getAttribute("data-action"));
		for (let index = 0; index < triggers.length; index++)
			if (triggers[index] === trigger)
				dialog.navbar.index = index;
	}


	dialog.show().finally(() => setTimeout(() => event.target.dispatchEvent(new TriggerResolveEvent(event)), 0));


	switch (type)
	{
		case "fetch":
			fetch(RequestBuilder.build(method, action, form))
				.then(ResponseHandler.text)
				.then(html => dialog.content.innerHTML = html)
				.catch(GMessageDialog.error);
			break;

		case "frame":
			if (event.detail.method === "get")
				dialog.iframe.src = event.detail.action;
			else
				fetch(RequestBuilder.build(method, action, form))
					.then(ResponseHandler.text)
					.then(html => dialog.iframe.srcDoc = html)
					.catch(GMessageDialog.error);
			break;
	}
});

