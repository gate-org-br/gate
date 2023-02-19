let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
		</header>
		<section>
			<g-progress>
			</g-progress>
		</section>
		<footer>
			<g-digital-clock>
			</g-digital-clock>
		</footer>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}


footer {
	flex-basis: 40px;
	align-items: center;
	justify-content: flex-end;
}

g-progress
{
	height: 40px;
}

g-digital-clock
{
	font-size: 16px;
}
</style>`;

/* global customElements, template */

import './g-progress.mjs';
import GWindow from './g-window.mjs';

export default class GBlock extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
	}

	set caption(text)
	{
		this.shadowRoot.querySelector("header").innerText = text;
	}

	get caption()
	{
		return this.shadowRoot.querySelector("header").innerText;
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