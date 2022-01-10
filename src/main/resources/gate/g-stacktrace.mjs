let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			<label id='caption'>
			</label>
			<a id='close' href="#">
				&#X1011;
			</a>
		</header>
		<section>
			<slot>
			</slot>
		</section>
	</main>
 <style>:host(*) {
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
	height: auto;
	display: grid;
	max-height: 100%;
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
		max-height: calc(100% - 80px);
	}
}

header{
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

section {
	display: flex;
	overflow-y: auto;
	align-items: stretch;
	justify-content: center;
	background-image: var(--g-window-section-background-image);
	background-color: var(--g-window-section-background-color);
}

::slotted {
	display: block;
}

#close {
	color: white;
	display: flex;
	font-size: 16px;
	font-family: gate;
	font-weight: bold;
	align-items: center;
	text-decoration: none;
	justify-content: center;
}</style>`;

/* global customElements, template */

import GModal from './g-modal.mjs';

export default class GStacktrace extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
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

	static show(caption, errors)
	{
		let popup = window.top.document.createElement("g-popup");
		popup.caption = caption || template.getAttribute("title") || "";
		popup.show();

		let table = popup.appendChild(document.createElement("table"))
			.appendChild(document.createElement("tbody"));
		errors.forEach(erro =>
		{
			let td = table.appendChild(document.createElement("tr")).appendChild(document.createElement("td"));
			td.style.fontWeight = 'bold';
			td.innerText = erro.message;
			erro.stacktrace.forEach(trace => table
					.appendChild(document.createElement("tr"))
					.appendChild(document.createElement("td")).innerText = trace);
		});
	}
}

customElements.define('g-stacktrace', GStacktrace);