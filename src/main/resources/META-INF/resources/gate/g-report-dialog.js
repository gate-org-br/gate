let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Selecione um tipo de relatório
			<a id='cancel' href="#">
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
	</dialog>
 <style>dialog {
	width: 600px;
	height: fit-content;
}</style>`;

/* global customElements */

import './g-icon.js';
import './g-download-status.js';
import './g-report-selector.js';
import GWindow from './g-window.js';
import Command from './command.js';


customElements.define('g-report-dialog', class extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("cancel").addEventListener("click", () => this.hide());
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

window.addEventListener("@report", function (event)
{
	event.preventDefault();
	event.stopPropagation();
	let trigger = event.composedPath()[0] || event.target;
	let dialog = window.top.document.createElement("g-report-dialog");

	dialog.caption = trigger.getAttribute("title") || "Imprimir";

	if (event.detail.method === "post")
	{
		let form = trigger.closest("form");
		if (trigger.form)
			form = trigger.form;
		else if (trigger.hasAttribute("data-form"))
			form = trigger.getRootNode().getElementById(trigger.getAttribute("data-form"));

		dialog.post(event.detail.action, new FormData(form));
	} else if (event.detail.method === "get")
		dialog.get(event.detail.action);
});