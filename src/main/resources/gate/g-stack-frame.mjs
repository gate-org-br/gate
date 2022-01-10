let template = document.createElement("template");
template.innerHTML = `
	<iframe>
	</iframe>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	display:  flex;
	overflow: auto;
	position: fixed;
	border-radius: 0;
	align-items: stretch;
	justify-content: center;
	background-color: var(--g-stack-frame-background-color);
	background-image: var(--g-stack-frame-background-image);
}

iframe {
	margin: 0;
	padding: 0;
	border: none;
	flex-grow: 1;
	overflow:  hidden;
	background-position: center;
	background-repeat: no-repeat;
	background-position-y: center;
	background-image: var(--loading);
}</style>`;

/* global customElements, template */

import GModal from './g-modal.mjs';

customElements.define('g-stack-frame', class GStackFrame extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));


		let iframe = this.iframe;

		iframe.dialog = this;
		iframe.scrolling = "no";
		iframe.setAttribute('name', '_stack');
		iframe.onmouseenter = () => this.iframe.focus();

		iframe.addEventListener("load", () =>
		{
			iframe.name = "_frame";
			iframe.setAttribute("name", "_frame");
			iframe.addEventListener("focus", () => autofocus(iframe.contentWindow.document));
			iframe.backgroundImage = "none";
		});

	}

	get iframe()
	{
		return this.shadowRoot.querySelector("iframe");
	}

	set target(target)
	{
		this.iframe.setAttribute('src', target);
	}
});