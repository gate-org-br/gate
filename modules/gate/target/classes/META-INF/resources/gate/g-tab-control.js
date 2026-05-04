let template = document.createElement("template");
template.innerHTML = `
	<header><slot name="head"></slot></header><section><slot name="body"></slot></section>
<style data-element="g-tab-control">* {
	box-sizing: border-box;
}

:host(*) {
	min-width: 0;
	display: grid;
	grid-template-rows: auto 1fr;
	background-color: transparent;
	border: 1px outset var(--main2, #F0F0F0);
}

header {
	display: flex;
	flex-wrap: wrap;
}

section {
	min-width: 0;
	min-height: 0;
	display: flex;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

::slotted(div) {
	gap: 12px;
	min-width: 0;
	min-height: 0;
	display: none;
	padding: 18px;
	overflow-y: auto;
	align-items: stretch;
	flex-direction: column;
	background-color: var(--main1, white);
}

:host([type='dummy']) ::slotted(div) {
	display: flex;
}

::slotted(a),
::slotted(button) {
	gap: 8px;
	padding: 8px;
	height: 32px;
	border: none;
	display: flex;
	cursor: pointer;
	flex-basis: 50%;
	font-size: 0.75rem;
	align-items: center;
	text-decoration: none;
	color: var(--text1, #000000);
	justify-content: flex-start;
	background-color: var(--main2, #F0F0F0);
}

header> ::slotted(:not(:first-child)) {
	border-left: 1px solid var(--main3, #F8F8F8);
}


::slotted(a:hover),
::slotted(button:hover) {
	background-color: var(--hovered, #FFFACD);
}

::slotted(a:focus),
::slotted(button:focus) {
	border: none;
	outline: none;
}

::slotted(a[data-selected=true]),
::slotted(button[data-selected=true]) {
	color: var(--text1, #000000);
	font-weight: bold;
	background-color: var(--main1, white);
}

@media only screen and (min-width: 768px) {
	header> ::slotted(*) {
		flex-basis: 25%;
	}

	:host([size='9'])>header> ::slotted(*),
	:host([size='10'])>header> ::slotted(*),
	:host([size='20'])>header> ::slotted(*),
	:host([size='30'])>header> ::slotted(*),
	:host([size='40'])>header> ::slotted(*),
	:host([size='50'])>header> ::slotted(*),
	:host([size='60'])>header> ::slotted(*),
	:host([size='70'])>header> ::slotted(*),
	:host([size='80'])>header> ::slotted(*),
	:host([size='90'])>header> ::slotted(*),
	:host([size='100'])>header> ::slotted(*) {
		flex-basis: 20%;
	}
}

@media only screen and (min-width: 1200px) {
	header> ::slotted(*) {
		flex-basis: 12.5%;
	}

	:host([size='9'])>header> ::slotted(*),
	:host([size='10'])>header> ::slotted(*),
	:host([size='20'])>header> ::slotted(*),
	:host([size='30'])>header> ::slotted(*),
	:host([size='40'])>header> ::slotted(*),
	:host([size='50'])>header> ::slotted(*),
	:host([size='60'])>header> ::slotted(*),
	:host([size='70'])>header> ::slotted(*),
	:host([size='80'])>header> ::slotted(*),
	:host([size='90'])>header> ::slotted(*),
	:host([size='100'])>header> ::slotted(*) {
		flex-basis: 10%;
	}
}

::slotted(:is(a, button, .g-command)[data-loading]) {
	position: relative;
}

::slotted(:is(a, button, .g-command)[data-loading])::before {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	content: "";
	color: inherit;
	position: absolute;
	background-size: 25%;
	border-radius: inherit;
	background-color: inherit;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}</style>`;
/* global customElements */

import GBlock from './g-block.js';
import loading from './loading.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

customElements.define('g-tab-control', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.innerHTML = template.innerHTML;

		this.addEventListener("click", event =>
		{
			if (event.target.closest("g-tab-control") === this
				&& Array.from(this.children)
				.some(e => e.hasAttribute("data-loading")))
				EventHandler.cancel(event);
		}, true);
	}

	get type()
	{
		return this.getAttribute("type") || "fetch";
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get reload()
	{
		return this.getAttribute("reload") || "never";
	}

	set reload(reload)
	{
		this.setAttribute("reload", reload);
	}

	reset()
	{
		Array.from(this.children)
			.filter(e => e.tagName === "DIV")
			.forEach(e => e.innerHTML = "");
		this.load();
	}

	reload() {
		const link = Array.from(this.children)
			.filter(e => e.tagName === "A" || e.tagName === "BUTTON")
			.find(tab => tab.getAttribute("data-selected") === "true");

		if (!link)
			return false;

		const page = link.nextElementSibling;
		if (!page || page.tagName !== "DIV")
			return false;

		page.innerHTML = "";
		page.removeAttribute("data-loading");
		link.click();
	}

	connectedCallback()
	{
		loading(this.parentNode);

		let links = Array.from(this.children).filter(e => e.tagName === "A" || e.tagName === "BUTTON");
		links.forEach(e => e.setAttribute("slot", "head"));
		this.setAttribute("size", links.length);

		if (this.getAttribute("type") === "dummy")
		{
			Array.from(this.children).filter(e => e.tagName === "DIV")
				.forEach(e => e.setAttribute("slot", "body"));
			return;
		}

		links.filter(link => !link.nextElementSibling
				|| link.nextElementSibling.tagName !== "DIV")
			.forEach(link =>
			{
				const page = document.createElement("div");
				page.classList.add("content");
				if (link.hasAttribute("data-page-style"))
					page.style = link.getAttribute("data-page-style");
				else if (this.hasAttribute("page-style"))
					page.style = this.getAttribute("page-style");
				link.after(page);
			});

		var pages = Array.from(this.children).filter(e => e.tagName === "DIV");
		pages.forEach(e => e.setAttribute("slot", "body"));

		links.forEach(link =>
		{
			link.addEventListener("click", event =>
			{
				const page = link.nextElementSibling;
				const type = link.getAttribute("data-type") || this.type;
				const reload = link.getAttribute("data-reload") || this.reload;

				if (link.getAttribute("href") !== '#'
					&& (reload === "always" || (event.ctrlKey && type === "fetch")))
					page.innerHTML = "";

				pages.forEach(e => e.style.display = "none");
				links.forEach(e => e.setAttribute("data-selected", "false"));
				link.nextElementSibling.style.display = "flex";
				link.setAttribute("data-selected", "true");

				if (page.childNodes.length)
				{
					event.preventDefault();
					event.stopPropagation();
					return;
				}

				switch (type)
				{
					case "fetch":
						event.preventDefault();
						event.stopPropagation();
						const method = link.getAttribute('method') || link.form?.method || "get";
						const action = link.getAttribute('href') || link.getAttribute('formaction') || link.form?.action;

						link.setAttribute("data-loading", "");
						fetch(RequestBuilder.build(method, action, link.form))
							.then(ResponseHandler.text)
							.then(result => document.createRange().createContextualFragment(result))
							.then(result => page.replaceChildren(...Array.from(result.childNodes)))
							.catch(error => GMessageDialog.error(error.message))
							.finally(() => link.removeAttribute("data-loading"));
						break;
					case "frame":
						const iframe = document.createElement("iframe");
						iframe.scrolling = "no";
						iframe.style.margin = "0";
						iframe.style.width = "100%";
						iframe.style.border = "none";
						iframe.style.overflow = "hidden";
						iframe.style.height = "400px";

						const name = crypto.randomUUID();
						iframe.setAttribute("id", name);
						iframe.setAttribute("name", name);
						iframe.setAttribute("allowfullscreen", "true");

						if (link.tagName === "A")
							link.setAttribute("target", name);
						else if (link.tagName === "BUTTON")
							link.setAttribute("formtarget", name);

						link.setAttribute("data-loading", "");

						iframe.addEventListener("load", () =>
						{
							const body = iframe.contentDocument.body;
							const resize = () => iframe.style.height = body.scrollHeight + "px";
							resize();
							new iframe.contentWindow.ResizeObserver(resize).observe(body);
							link.removeAttribute("data-loading");
						});

						page.appendChild(iframe);
						break;
				}
			});

			if (link.getAttribute("data-selected") &&
				link.getAttribute("data-selected").toLowerCase() === "true")
				link.click();
		});

		if (links.length && links.every(e => !e.hasAttribute("data-selected")
				|| e.getAttribute("data-selected").toLowerCase() === "false"))
			links[0].click();
	}
});