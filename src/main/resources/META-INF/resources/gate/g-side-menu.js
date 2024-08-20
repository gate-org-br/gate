let template = document.createElement("template");
template.innerHTML = `
	<header>
	</header>
	<section>
		<slot>
		</slot>
	</section>
 <style data-element="g-side-menu">* {
	box-sizing: border-box;
}

:host(*)
{
	width: 32px;
	display: flex;
	font-size: 16px;
	overflow: hidden;
	align-items: stretch;
	flex-direction: column;
	background-color: var(--main3);
}

:host([open])
{
	width: max-content;
}

::slotted(hr) {
	max-height: 0;
}

header
{
	padding: 8px;
	display: flex;
	cursor: pointer;
	overflow: hidden;
	font-size: inherit;
	align-items: center;
	background-color: var(--main4);
	justify-content: space-between;
}

header::before
{
	display: flex;
	content: '\\2265';
	font-family: gate;
	align-items: center;
	justify-content: center;
}

:host([open]) header::after
{
	display: flex;
	font-size: 8px;
	color: #CCCCCC;
	content: '\\1001';
	font-family: gate;
	align-items: center;
	justify-content: center;
}

section {
	display: flex;
	overflow-y: auto;
	overflow-x: hidden;
	font-size: inherit;
	align-items: stretch;
	flex-direction: column;
	justify-content: stretch;
}

::slotted(a),
::slotted(button),
::slotted(.g-command)
{
	gap: 8px;
	padding: 8px;
	border: none;
	color: black;
	display: flex;
	cursor: pointer;
	overflow: hidden;
	flex-basis: 32px;
	max-height: 32px;
	font-size: inherit;
	align-items: center;
	text-decoration: none;
	background-color: transparent;
}


::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover)
{
	background-color:  #FFFACD;
}

::slotted([_iconless])::before
{
	display: flex;
	color: #CCCCCC;
	content: '\\1003';
	font-family: gate;
	align-items: center;
	justify-content: center;
}

@media only screen and (min-width: 1000px)
{
	:host(*)
	{
		width: max-content;
	}

	header {
		display: none;
	}
}</style>`;
/* global customElements */

import GBlock from './g-block.js';
import loading from './loading.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';


customElements.define('g-side-menu', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.shadowRoot.querySelector("header")
			.addEventListener("click", event =>
			{
				if (!this.hasAttribute("open"))
					this.setAttribute("open", "");
				else
					this.removeAttribute("open");
			});

		this.shadowRoot.querySelector("section")
			.addEventListener("click", event => this.removeAttribute("open"));
	}

	connectedCallback()
	{
		Array.from(this.children)
			.flatMap(e => Array.from(e.childNodes))
			.filter(e => e.nodeType === Node.TEXT_NODE)
			.forEach(e => e.parentNode.appendChild(e));

		Array.from(this.children)
			.filter(e => e.tagName !== "HR")
			.filter(e => !e.querySelector('g-icon'))
			.forEach(e => e.setAttribute("_iconless", ""));
	}
});