let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
		</g-window-header>
		<g-window-section>
			<g-progress>
			</g-progress>
		</g-window-section>
		<footer>
			<g-digital-clock>
			</g-digital-clock>
		</footer>
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

main
{
	height: auto;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 800px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 48px 24px;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: var(--g-window-border);
}

g-window-section {
	padding: 4px;
}

footer {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	background-color: var(--g-window-header-background-color);
	background-image: var(--g-window-header-background-image);
}

g-digital-clock
{
	color: white;
	font-size: 16px;
}</style>`;

/* global customElements, template */

import './g-progress.mjs';
import './g-window-header.mjs';
import './g-window-section.mjs';
import GModal from './g-modal.mjs';

export default class GBlock extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set caption(text)
	{
		this.shadowRoot.querySelector("g-window-header").innerText = text;
	}

	get caption()
	{
		return this.shadowRoot.querySelector("g-window-header").innerText;
	}

	static show(text)
	{
		if (window.top.GBlock)
			return;

		window.top.GBlock = window.top.document.createElement("g-block");
		window.top.GBlock.caption = text || "Aguarde";
		window.top.GBlock.show();
	}

	static  hide()
	{
		if (!window.top.GBlock)
			return;

		window.top.GBlock.hide();
		window.top.GBlock = null;
	}
}

customElements.define('g-block', GBlock);

GBlock.hide();

Array.from(document.querySelectorAll("form[data-block]"))
	.forEach(e => e.addEventListener("submit", () => GBlock.show(e.getAttribute("data-block"))));

Array.from(document.querySelectorAll("a[data-block]"))
	.forEach(e => e.addEventListener("click", () => GBlock.show(e.getAttribute("data-block"))));

Array.from(document.querySelectorAll("button[data-block]")).forEach(e =>
	{
		e.addEventListener("click", () =>
		{
			if (e.form)
				e.form.addEventListener("submit", () => GBlock.show(e.getAttribute("data-block"), {once: true}));
			else
				GBlock.show(e.getAttribute("data-block"));
		});
	});