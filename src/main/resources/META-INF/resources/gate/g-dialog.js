let template = document.createElement("template");
template.innerHTML = `
	<dialog part='dialog'>
		<header part='header' tabindex='1'>
			<label id='caption'>
			</label>
			<nav></nav>
			<g-navbar>
			</g-navbar>
			<a id='hide' href='#'>
				<g-icon>
					&#x1011;
				</g-icon>
			</a>
		</header>
		<section>
			<slot></slot>
		</section>
	</dialog>
 <style>* {
	box-sizing: border-box;
}

:host(*)
{
	gap: 8px;
	display: flex;
	align-items: stretch;
	flex-direction: column;
}


dialog {
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

dialog > section {
	padding: 8px;
	display: flex;
	align-items: stretch;
	flex-direction: column;
}

nav
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

nav:empty {
	display: none;
}

nav > a ,
nav > button,
nav > .g-command
{
	font-size: 14px;
	cursor: pointer;
	color: var(--main);
	text-decoration: none;
}

::slotted(iframe:only-child) {
	margin: 0;
	width: 100%;
	border: none;
	padding: 0px;
	flex-grow: 1;
	height: 100%;
	overflow: hidden
}</style>`;

/* global customElements, template */

import './g-navbar.js';
import "./g-dialog-header.js";
import GWindow from './g-window.js';
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

		this.shadowRoot.querySelector("g-navbar")
			.addEventListener("update", event =>
				fetch(event.detail.target)
					.then(ResponseHandler.text)
					.then(html => this.innerHTML = html)
					.catch(e => GMessageDialog.error(e) || event.preventDefault()));
	}

	get caption ()
	{
		return this.shadowRoot
			.getElementById("caption").innerText;
	}

	set caption (caption)
	{
		this.shadowRoot.getElementById("caption")
			.innerText = caption;
	}

	get navbar ()
	{
		return this.shadowRoot.querySelector("g-navbar");
	}

	get type ()
	{
		let section = this.shadowRoot.querySelector("section");
		return !section.firstElementChild || section.firstElementChild.tagName === "IFRAME" ? "frame" : "fetch";
	}

	get toolbar ()
	{
		return this.shadowRoot.querySelector("nav");
	}

	get iframe ()
	{
		if (!this.firstElementChild)
		{
			let iframe = this.appendChild(document.createElement("iframe"));
			iframe.dialog = this;
			iframe.setAttribute("scrolling", "no");
			iframe.addEventListener("load", () => iframe.focus());
			iframe.addEventListener("mouseenter", () => iframe.focus());

			this.shadowRoot.querySelector("g-navbar")
				.addEventListener("update", event => iframe.src = event.detail.target);
		}

		if (this.firstElementChild.tagName !== "IFRAME")
			throw new Error("Attempt to access iframe of a fetch dialog");

		return this.firstElementChild;
	}

	set width (value)
	{
		this.shadowRoot.querySelector("dialog").style.width = value;
	}

	set height (value)
	{
		this.shadowRoot.querySelector("dialog").style.height = value;
	}

	static hide ()
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.hide();
	}

	static set caption (caption)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.caption = caption;
	}

	static get caption ()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.caption;
	}
};

customElements.define('g-dialog', GDialog);

window.addEventListener("@dialog", function (event)
{
	let { method, action, form, parameters } = event.detail;
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
				.then(result => document.createRange().createContextualFragment(result))
				.then(result => dialog.replaceChildren(...Array.from(result.childNodes)))
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

