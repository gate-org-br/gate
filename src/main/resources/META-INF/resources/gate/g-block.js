let template = document.createElement("template");
template.innerHTML = `
	<dialog>
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
	</dialog>
 <style data-element="g-block">dialog
{
	height: fit-content;
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}


dialog > footer
{
	flex-basis: 40px;
	align-items: center;
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

import './g-progress.js';
import DOM from './dom.js';
import GWindow from './g-window.js';

export default class GBlock extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.innerHTML += template.innerHTML;
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

const REGISTRY = new WeakSet();
window.addEventListener("connected", function (event)
{
	let element = event.target || event.composedPath()[0];
	if (!REGISTRY.has(element)
		&& element.hasAttribute
		&& element.hasAttribute("data-block"))
	{
		REGISTRY.add(element);
		let message = element.getAttribute("data-block");
		switch (element.tagName)
		{
			case "A":
				element.addEventListener("click", () => GBlock.show(message));
				break;
			case "FORM":
				element.addEventListener("submit", () => GBlock.show(message));
				break;
			case "BUTTON":
				if (element.form)
					element.form.addEventListener("submit", () => GBlock.show(message), {once: true});
				else
					element.addEventListener("click", () => GBlock.show(message));
				break;
			default:
				element.addEventListener("click", () => GBlock.show(message));
				break;
		}
	}
});