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
			<iframe name="@dialog" scrolling="no">
			</iframe>
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

dialog > section {
	padding: 0;
	align-items: stretch;
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


::slotted(a),
::slotted(button)
{
	font-size: 14px;
	cursor: pointer;
	color: var(--main);
	text-decoration: none;
}

iframe {
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

export default class GDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML += template.innerHTML;
		this.shadowRoot.getElementById("hide").addEventListener("click", () => this.hide());

		let iframe = this.shadowRoot.querySelector("iframe");
		iframe.dialog = this;
		iframe.onmouseenter = () => iframe.focus();
		iframe.addEventListener("load", () => iframe.removeAttribute("name"));

		let navbar = this.shadowRoot.querySelector("g-navbar");
		navbar.style.display = "none";
		navbar.addEventListener("update", event => iframe.src = event.detail);
		iframe.addEventListener("load", () => navbar.target = iframe.contentWindow.location.href);
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

	get iframe()
	{
		return this.shadowRoot.querySelector("iframe");
	}

	set target(target)
	{
		this.iframe.src = target;
	}

	set size(value)
	{
		let size = /^([0-9]+)(\/([0-9]+))?$/g.exec(value);
		if (!size)
			throw new Error(value + " is not a valid size");
		let dialog = this.shadowRoot.querySelector("dialog");

		let width = size[1];
		let height = size[3] || size[1];
		dialog.style.width = width + "px";
		dialog.style.height = height + "px";

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
	let trigger = event.composedPath()[0] || event.target;

	let dialog = window.top.document.createElement("g-dialog");
	dialog.caption = trigger.getAttribute("title");
	dialog.addEventListener("show", () => trigger.dispatchEvent(new CustomEvent('show', {bubbles: true, detail: {modal: dialog}})));
	dialog.addEventListener("hide", () => trigger.dispatchEvent(new CustomEvent('hide', {bubbles: true, detail: {modal: dialog}})));

	if (event.detail.parameters[0])
		dialog.size = event.detail.parameters[0];

	if (trigger.hasAttribute("data-navigator")
		|| trigger.parentNode.hasAttribute("data-navigator"))
		dialog.navbar.targets = Array.from(trigger.parentNode.children)
			.map(e => e.href || e.formaction || e.getAttribute("data-action"));

	dialog.show();
	if (event.detail.method === "get")
		dialog.iframe.src = event.detail.action;
	else
		fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
			.then(ResponseHandler.text)
			.then(html => dialog.iframe.srcDoc = html)
			.catch(GMessageDialog.error);

});

