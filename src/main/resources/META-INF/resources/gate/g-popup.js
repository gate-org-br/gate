let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			<label id='caption'>
			</label>
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
	</main>
 <style>main {
	width: 100%;
	height: 100%;
	max-height: 100%;
	border-radius: 0;
}

@media only screen and (min-width: 640px)
{
	main{
		border-radius: 3px;
		width: calc(100% - 80px);
		max-height: calc(100% - 80px);
	}
}</style>`;

/* global customElements, template */

import './g-icon.js';
import GWindow from './g-window.js';

export default class GPopup extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
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

	static show(template, caption)
	{
		let popup = window.top.document.createElement("g-popup");
		popup.caption = caption || template.getAttribute("title") || "";
		let parent = template.parentNode;
		popup.addEventListener("hide", () => parent.appendChild(popup.firstElementChild));
		popup.show();
		popup.appendChild(template);
	}

	static get observedAttributes()
	{
		return ["caption"];
	}

	attributeChangedCallback()
	{
		this.caption = this.getAttribute("caption") || "";
	}
}

customElements.define('g-popup', GPopup);