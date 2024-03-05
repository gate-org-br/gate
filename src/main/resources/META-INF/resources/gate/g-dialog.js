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

::slotted(section:only-child),
::slotted(iframe:only-child)
{
	margin: 0;
	width: 100%;
	border: none;
	padding: 0px;
	flex-grow: 1;
	height: 100%;
}

::slotted(iframe:only-child) {
	overflow: hidden
}</style>`;

/* global customElements, template */

import './g-navbar.js';
import GWindow from './g-window.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

export default class GDialog extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML += template.innerHTML;
		this.shadowRoot.getElementById("hide").addEventListener("click", () => this.hide());

		this.shadowRoot.querySelector("g-navbar")
			.addEventListener("update", event =>
			{
				if (this.type === "frame")
					this.iframe.srcDoc = event.detail.target;
				else
					fetch(event.detail.target)
						.then(ResponseHandler.text)
						.then(html => this.innerHTML = html)
						.catch(e => GMessageDialog.error(e) || event.preventDefault())
			});
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
		return this.firstElementChild
			&& this.firstElementChild === this.lastElementChild
			&& this.firstElementChild.tagName === "IFRAME" ? "frame" : "fetch";
	}

	get toolbar()
	{
		return this.shadowRoot.querySelector("nav");
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

			this.shadowRoot.querySelector("g-navbar")
				.addEventListener("update", event => iframe.src = event.detail.target);
		}

		if (this.firstElementChild.tagName !== "IFRAME")
			throw new Error("Attempt to access iframe of a fetch dialog");

		return this.firstElementChild;
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

	static get observedAttributes()
	{
		return ["caption", "style"];
	}

	attributeChangedCallback(name, _, val)
	{
		if (name === "caption")
			this.caption = val;
		else if (name === "style")
			this.shadowRoot.querySelector("dialog").style = val;
	}
};

customElements.define('g-dialog', GDialog);