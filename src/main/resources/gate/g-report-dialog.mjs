let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			Selecione um tipo de relatório
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-footer>
			<g-report-selector>
			</g-report-selector>
			<g-download-status>
			</g-download-status>
		</g-window-footer>
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
	max-width: 600px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	width: calc(100% - 40px);
	grid-template-rows: 40px 1fr;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}</style>`;

/* global customElements */

import './g-window-header.mjs';
import './g-window-section.mjs';
import './g-download-status.mjs';
import './g-report-selector.mjs';
import GModal from './g-modal.mjs';
import Command from './command.mjs';


customElements.define('g-report-dialog', class extends GModal
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.addEventListener("click", event => event.target === this && this.hide());
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());
		this.shadowRoot.querySelector("main").addEventListener("click", e => e.stopPropagation());
		this.shadowRoot.querySelector("g-download-status").addEventListener("done", () => this.hide());
	}

	show()
	{
		this.shadowRoot.querySelector("g-report-selector").style.display = "";
		this.shadowRoot.querySelector("g-download-status").style.display = "none";
		super.show();
	}

	get(url)
	{
		let status = this.shadowRoot.querySelector("g-download-status");
		let selector = this.shadowRoot.querySelector("g-report-selector");

		selector.addEventListener("selected", event =>
		{
			status.style.display = "";
			selector.style.display = "none";
			status.download("get", new Command(url).parameter("type", event.detail), null);
		});
		this.show();
	}

	post(url, data)
	{
		let status = this.shadowRoot.querySelector("g-download-status");
		let selector = this.shadowRoot.querySelector("g-report-selector");

		selector.addEventListener("selected", event =>
		{
			status.style.display = "";
			selector.style.display = "none";
			status.download("post", new Command(url).parameter("type", event.detail), data);
		});
		this.show();
	}
});