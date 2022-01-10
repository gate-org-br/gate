let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione um período
			<a id='close' href="#">
				&#X1011;
			</a>
		</header>
		<section>
			<g-date-interval-selector>
			</g-date-interval-selector>
		</section>
		<footer>
			<a id='commit' href='#'>
			</a>
		</footer>
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

main
{
	height: 450px;
	display: grid;
	position: fixed;
	min-width: 320px;
	max-width: 600px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 1fr 40px;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
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
	align-items: stretch;
	justify-content: center;
	background-image: var(--g-window-section-background-image);
	background-color: var(--g-window-section-background-color);
}

footer {
	display: flex;
	align-items: stretch;
	justify-content: center;
	background-color: var(--g-window-footer-background-color);
	background-image: var(--g-window-footer-background-image);
}

#commit {
	flex-grow: 1;
	display: flex;
	font-size: 16px;
	color: var(--g);
	font-weight: bold;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	border: 1px solid transparent;
	background-image:  linear-gradient(to bottom, #E3E0D0 0%, #858279 100%);
}

#commit:hover {
	font-weight: bold;
	border-color: var(--hovered);
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

/* global customElements */

import './g-date-interval-selector.mjs';
import GModal from './g-modal.mjs';

customElements.define('g-date-interval-picker', class extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());

		let commit = this.shadowRoot.getElementById("commit");
		let selector = this.shadowRoot.querySelector("g-date-interval-selector");

		setTimeout(() =>
		{
			let date = new Date();
			selector.min = date;
			selector.max = date;
			commit.innerText = selector.selection;
		}, 0);

		setTimeout(() => commit.innerText = selector.selection, 0);
		selector.addEventListener("selected", () => commit.innerText = selector.selection);

		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: commit.innerText})) | this.hide());
	}
});

Array.from(document.querySelectorAll("input.DateInterval")).forEach(function (input)
{
	var link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	link.setAttribute("tabindex", input.getAttribute('tabindex'));
	link.appendChild(document.createElement("i")).innerHTML = "&#x2003;";

	link.addEventListener("click", function (event)
	{
		event.preventDefault();

		if (input.value)
		{
			input.value = '';
			input.dispatchEvent(new Event('change', {bubbles: true}));
		} else
			window.top.document.createElement("g-date-interval-picker")
				.show().addEventListener("picked", e =>
			{
				input.value = e.detail;
				input.dispatchEvent(new Event('change', {bubbles: true}));
			});


		link.focus();
		link.blur();
	});
});