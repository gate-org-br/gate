let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header tabindex='1'>
			<label id='caption'>
			</label>
			<g-dialog-commands>
			</g-dialog-commands>
			<g-navbar>
			</g-navbar>
			<a id='minimize' href='#'>
				&#x3019;
			</a>
			<a id='fullscreen' href='#'>
				&#x3016;
			</a>
			<a id='hide' href='#'>
				&#x1011;
			</a>
		</header>
		<section>
			<iframe scrolling="no">
			</iframe>
		</section>
	</main>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 2;
	display: flex;
	position: fixed;
	align-items: center;
	justify-content: center;
}

main {
	width: 100%;
	height: 100%;
	display: grid;
	border-radius: 0;
	position: relative;
	grid-template-rows: 40px 1fr;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}

@media only screen and (min-width: 640px)
{
	main{
		border-radius: 5px;
		width: calc(100% - 80px);
		height: calc(100% - 80px);
	}
}

header{
	gap: 4px;
	padding: 4px;
	display: flex;
	font-size: 20px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	color: var(--g-window-header-color);
	background-color: var(--g-window-header-background-color);
	background-image: var(--g-window-header-background-image);
}

#caption {
	order: 1;
	flex-grow: 1;
	display: flex;
	color: inherit;
	font-size: inherit;
	align-items: center;
	justify-content: flex-start;
}


g-navbar {
	order: 2;
	display: none;
}

@media only screen and (min-width: 786px)
{
	g-navbar{
		display: flex;
	}
}

g-dialog-commands {
	order: 3;
}

a,  button {
	order: 4;
	color: white;
	padding: 2px;
	display: flex;
	font-size: 16px;
	font-family: gate;
	align-items: center;
	text-decoration: none;
	justify-content: center;
}

section {
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: stretch;
	justify-content: center;
	-webkit-overflow-scrolling: touch;
	background-image: var(--g-window-section-background-image);
	background-color: var(--g-window-section-background-color);
}

iframe,
div,
dialog {
	margin: 0;
	border: none;
	padding: 0px;
	flex-grow: 1;
	overflow: hidden
}

iframe {
	width: 100%;
	overflow:  hidden;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}</style>`;

/* global customElements, template */

import './g-navbar.mjs';
import CSV from './csv.mjs';
import './g-dialog-commands.mjs';
import GModal from './g-modal.mjs';
import resolve from './resolve.mjs';
import FullScreen from './fullscreen.mjs';

const ESC = 27;
const ENTER = 13;

const resize = function (iframe)
{
	if (iframe.contentWindow
		&& !iframe.contentWindow.document
		&& iframe.contentWindow.document.body
		&& iframe.contentWindow.document.body.scrollHeight)
	{
		let height = Math.max(iframe.contentWindow.document.body.scrollHeight,
			iframe.parentNode.offsetHeight) + "px";
		if (iframe.height !== height)
		{
			this.iframe.height = "0";
			this.iframe.height = height;
		}
	}
};
export default class GDialog extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let main = this.shadowRoot.querySelector("main");
		let head = this.shadowRoot.querySelector("header");
		let fullscreen = this.shadowRoot.getElementById("fullscreen");
		let minimize = this.shadowRoot.getElementById("minimize");
		let hide = this.shadowRoot.getElementById("hide");
		let iframe = this.shadowRoot.querySelector("iframe");
		let navbar = this.shadowRoot.querySelector("g-navbar");
		navbar.style.display = "none";
		main.addEventListener("click", e => e.stopPropagation());
		hide.addEventListener("click", event => event.preventDefault() | this.hide());
		this.addEventListener("click", event => event.target === this && this.hide());
		fullscreen.addEventListener("click", () => fullscreen.innerHTML = (FullScreen.switch(main) ? "&#x3015;" : "&#x3016;"));
		minimize.addEventListener("click", event => event.preventDefault() | this.minimize());
		head.focus();
		head.addEventListener("keydown", event =>
		{
			event = event ? event : window.event;
			switch (event.keyCode)
			{
				case ESC:
					this.hide();
					break;
				case ENTER:
					this.focus();
					break;
			}

			event.preventDefault();
			event.stopPropagation();
		});
		iframe.dialog = this;
		iframe.onmouseenter = () => iframe.focus();
		iframe.addEventListener("load", () =>
		{
			iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));
			resize(iframe);
			window.addEventListener("refresh_size", () => resize(iframe));
			iframe.backgroundImage = "none";
		});
		navbar.addEventListener("update", event =>
		{
			let iframe = this.shadowRoot.querySelector("iframe");
			iframe.backgroundImage = "";
			iframe.contentWindow.document.open();
			iframe.contentWindow.document.write("");
			iframe.contentWindow.document.close();
			iframe.setAttribute('src', event.detail.target)
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

	set navigator(navigator)
	{
		if (!navigator || !navigator.length)
			return;
		setTimeout(() =>
		{
			let navbar = this.shadowRoot.querySelector("g-navbar");
			navbar.links = navigator;
			navbar.style.display = "";
		}, 0);
	}

	get iframe()
	{
		return this.shadowRoot.querySelector("iframe");
	}

	set target(target)
	{
		setTimeout(() => this.shadowRoot.querySelector("g-navbar").url = target, 0);
	}

	get()
	{
		this.arguments = arguments;
		this.show();
	}

	ret()
	{
		let size = Math.min(arguments.length, this.arguments.length);
		for (var i = 0; i < size; i++)
			if (this.arguments[i])
				if (this.arguments[i].tagName.toLowerCase() === "textarea")
					this.arguments[i].innerHTML = arguments[i];
				else
					this.arguments[i].value = arguments[i];
		for (var i = 0; i < size; i++)
			if (this.arguments[i])
				this.arguments[i].dispatchEvent(new CustomEvent('changed', {bubbles: true}));
		this.hide();
	}

	set size(value)
	{
		let size = /^([0-9]+)(\/([0-9]+))?$/g.exec(value);
		if (!size)
			throw new Error(value + " is not a valid size");
		let main = this.shadowRoot.querySelector("main");
		main.style.maxWidth = size[1] + "px";
		main.style.maxHeight = (size[3] || size[1]) + "px";

		this.shadowRoot.getElementById("minimize").style.display = "none";
		this.shadowRoot.getElementById("fullscreen").style.display = "none";
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

	static set commands(commands)
	{
		if (window.frameElement && window.frameElement.dialog)
			window.frameElement.dialog.commands = commands;
	}

	static get commands()
	{
		if (window.frameElement && window.frameElement.dialog)
			return window.frameElement.dialog.commands;
	}
};
customElements.define('g-dialog', GDialog);
window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-get]");
	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		var parameters = CSV.parse(action.getAttribute('data-get')).map(e => e.trim())
			.map(e => e !== null ? document.getElementById(e) : null);
		if (parameters.some(e => e && e.value))
		{
			parameters = parameters.filter(e => e && e.value);
			parameters.forEach(e => e.value = "");
			parameters.forEach(e => e.dispatchEvent(new CustomEvent('changed', {bubbles: true})));
		} else
		{
			let dialog = window.top.document.createElement("g-dialog");
			dialog.target = action.href;
			dialog.caption = action.getAttribute("title");
			dialog.get.apply(dialog, parameters);
		}
	}
});
window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");
	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		var ret = CSV.parse(action.getAttribute("data-ret")).map(e => e.trim());
		window.frameElement.dialog.ret.apply(window.frameElement.dialog, ret);
	}
});
window.addEventListener("mouseover", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");
	if (!action)
		return;
	if (!action.hasAttribute("tabindex"))
		action.setAttribute("tabindex", 1000);
	action.focus();
});
window.addEventListener("mouseout", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");
	if (!action)
		return;
	action.blur();
});
window.addEventListener("keydown", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("[data-ret]");
	if (!action)
		return;
	if (event.keyCode === 13)
		action.click();
});
window.addEventListener("change", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	if (action.tagName === "INPUT" && action.hasAttribute("data-getter"))
	{
		event.preventDefault();
		event.stopPropagation();
		var getter = document.getElementById(action.getAttribute("data-getter"));
		var url = resolve(getter.href);
		var parameters = CSV.parse(getter.getAttribute('data-get')).map(id => id.trim())
			.map(id => id !== null ? document.getElementById(id) : null);
		if (action.value)
		{
			parameters.filter(e => e).filter(e => e.value).forEach(e => e.value = "");
			let dialog = window.top.document.createElement("g-dialog");
			dialog.target = url;
			dialog.caption = getter.getAttribute("title");
			dialog.get.apply(dialog, parameters);
			dialog.get.apply(dialog, parameters);
		} else
			parameters.filter(e => e).filter(e => e.value).forEach(e => e.value = "");
	}
});
window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("a.Hide");
	if (action)
	{
		event.preventDefault();
		event.stopPropagation();
		if (window.frameElement
			&& window.frameElement.dialog
			&& window.frameElement.dialog.hide)
			window.frameElement.dialog.hide();
		else
			window.close();
	}
});