let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione um período
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-month-interval-selector>
			</g-month-interval-selector>
		</section>
		<footer>
			<button id='commit'>
			</button>
		</footer>
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 600px;
	width: calc(100% - 40px);
}

g-month-interval-selector {
	flex-grow: 1;
}

main > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements */

import './g-icon.mjs';
import GWindow from './g-window.mjs';
import './g-month-interval-selector.mjs';

customElements.define('g-month-interval-picker', class extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());

		let commit = this.shadowRoot.getElementById("commit");
		let selector = this.shadowRoot.querySelector("g-month-interval-selector");

		setTimeout(() => commit.innerText = selector.selection, 0);
		selector.addEventListener("selected", () => commit.innerText = selector.selection);

		commit.addEventListener("click", () => this.dispatchEvent(new CustomEvent("picked", {detail: commit.innerText})) | this.hide());
	}
});

Array.from(document.querySelectorAll("input.MonthInterval")).forEach(function (input)
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
			window.top.document.createElement("g-month-interval-picker")
				.show().addEventListener("picked", e =>
			{
				input.value = e.detail;
				input.dispatchEvent(new Event('change', {bubbles: true}));
			});


		link.focus();
		link.blur();
	});
});