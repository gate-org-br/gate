let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			<div id='caption'>
			</div>
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>

		</header>
		<section>
			<slot>
			</slot>
		</section>
	</dialog>
 <style>* {
	box-sizing: border-box
}

:host(*)
{
	display: none;
}

dialog {
	padding: 0;
	border: none;
	display: flex;
	border-radius: 3px;
	width: fit-content;
	height: fit-content;
	flex-direction: column;
	background-color: var(--main3);
	box-shadow: 6px 6px 6px 0px rgba(0,0,0,0.75);
}

dialog > header
{
	gap: 8px;
	padding: 8px;
	display: flex;
	font-size: 16px;
	flex-basis: 40px;
	font-weight: bold;
	align-items: center;
	justify-content: space-between;
	border-bottom: 1px solid var(--main6);
}

dialog > header > div
{
	order: 1;
	flex-grow: 1;
	font-size: inherit;
}

dialog > section
{
	padding: 8px;
	flex-grow: 1;
	display: flex;
	overflow: auto;
	align-items: flex-start;
	justify-content: stretch;
	-webkit-overflow-scrolling: touch;
}


dialog > header > a
{
	order: 2;
	border:none;
	color: black;
	display: flex;
	cursor: pointer;
	font-size: 16px;
	line-height: 16px;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	background-color: transparent;
}</style>`;

/* global customElements, template */

import './g-icon.js';
import './trigger.js';
import GWindow from './g-window.js';

export default class GPopup extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("dialog").addEventListener("click", e => e.stopPropagation());
	}

	set caption(caption)
	{
		this.shadowRoot.getElementById("caption")
			.innerText = caption;
	}

	get caption()
	{
		return this.shadowRoot.getElementById("caption")
			.innerText;
	}

	show()
	{
		this.style.display = "block";
		this.shadowRoot.querySelector("dialog").showModal();
	}

	hide()
	{
		this.shadowRoot.querySelector("dialog").close();
		this.style.display = "";
	}

	static get observedAttributes()
	{
		return ["caption", "style", "open"];
	}

	attributeChangedCallback(attribute, _, value)
	{
		switch (attribute)
		{
			case "open":
				this.hasAttribute("open") && this.show();
				break;
			case "caption":
				this.caption = value || "";
				break;
			case "style":
				this.shadowRoot.querySelector("dialog").style = value || "";
				break;
		}
	}
}

customElements.define('g-popup', GPopup);

window.addEventListener("trigger", function (event)
{
	if (event.detail.type === "_popup")
	{
		event.stopPropagation();
		event.detail.cause.preventDefault();
		event.detail.cause.stopPropagation();
		event.detail.cause.stopImmediatePropagation();

		if (!event.detail.parameters[0])
			throw new Error(`Target popup element not defined`);

		let popup = document.getElementById(event.detail.parameters[0]);
		if (!popup)
			throw new Error(`Target popup element with id ${event.detail.parameters[0]} not found on page`);

		popup.caption = event.detail.target.getAttribute("title") || "";
		popup.show();
	}
});
