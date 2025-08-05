let template = document.createElement("template");
template.innerHTML = `
	<header>
		<g-icon>
			&#X2265;
		</g-icon>
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
    display: flex;
    font-size: 16px;
    overflow: hidden;
    width: max-content;
    border-radius: 8px;
    align-items: stretch;
    flex-direction: column;
    background-color: var(--main3);
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

::slotted(hr) {
    max-height: 0;
}

header
{
    gap: 12px;
    padding: 12px;
    display: flex;
    cursor: pointer;
    overflow: hidden;
    font-size: inherit;
    align-items: center;
    justify-content: space-between;
    background-color: var(--main4);
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
::slotted(.g-command) {
    display: flex;
    align-items: center;

    width: 100%;
    max-height: 40px;
    flex-basis: 40px;

    font-size: 0;
    padding: 12px;
    font-family: inherit;
    text-decoration: none;

    border: none;
    outline: none;
    cursor: pointer;

    overflow: hidden;
    border-radius: 6px;
    white-space: nowrap;
    text-overflow: ellipsis;
    color: var(--main9, #222);
    background-color: transparent;
    transition: background-color 0.2s ease, color 0.2s ease;
}

::slotted(a:hover),
::slotted(button:hover),
::slotted(.g-command:hover) {
    color: var(--main9-hover, #000);
    background-color: var(--hover-bg, #f0f0f0);
}

:host(:hover) ::slotted(*),
:host([open]) ::slotted(*)
{
    gap: 12px;
    font-size: inherit;
}
</style>`;
/* global customElements */

import GBlock from './g-block.js';
import loading from './loading.js';
import EventHandler from './event-handler.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

const sheet = new CSSStyleSheet();
sheet.replaceSync(`g-side-menu g-icon { order: -1; font-size: 16px }`);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

customElements.define('g-side-menu', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		this.shadowRoot.querySelector("header")
			.addEventListener("click", event => this.toggleAttribute("open"));

		this.shadowRoot.querySelector("section")
			.addEventListener("click", event =>
				window.innerWidth <= 768 &&
					this.removeAttribute("open"));
	}

	connectedCallback()
	{
		Array.from(this.children)
			.filter(e => e.tagName !== "HR")
			.filter(e => !e.querySelector('g-icon'))
			.forEach(e => e.appendChild(document.createElement("g-icon")).innerHTML = '&#x1003;');
	}
});