let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Selecione um tipo de relatório
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<footer>
			<g-report-selector>
			</g-report-selector>
			<g-download-status>
			</g-download-status>
		</footer>
	</main>
 <style>main {
	width: 600px;
}</style>`;

/* global customElements */

import './g-icon.mjs';
import './g-download-status.mjs';
import './g-report-selector.mjs';
import GWindow from './g-window.mjs';
import Command from './command.mjs';


customElements.define('g-report-dialog', class extends GWindow
{
	constructor()
	{
		super();
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