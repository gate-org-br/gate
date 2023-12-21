let template = document.createElement("template");
template.innerHTML = `
	<dialog>
		<header>
			Progresso
			<a id='close' href="#">
				<g-icon>
					&#X1011;
				</g-icon>
			</a>
		</header>
		<section>
			<g-progress-status>
			</g-progress-status>
		</section>
		<footer>
			<button id='commit'>
				Processando
			</button>
		</footer>
	</dialog>
 <style>dialog
{
	height: fit-content;
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}

dialog > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements */

import './g-icon.js';
import './trigger.js';
import './g-progress-status.js';
import Process from './process.js';
import GWindow from './g-window.js';
import RequestBuilder from './request-builder.js';
import GMessageDialog from './g-message-dialog.js';
import ResponseHandler from './response-handler.js';

customElements.define('g-progress-dialog', class extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		let close = this.shadowRoot.getElementById("close");
		let commit = this.shadowRoot.getElementById("commit");

		commit.onclick = close.onclick = event =>
		{
			event.preventDefault();
			event.stopPropagation();
			if (confirm("Tem certeza de que deseja fechar o progresso?"))
				this.hide();
		};

		window.addEventListener("ProcessCommited", event =>
		{
			if (event.detail.process !== this.process)
				return;

			commit.innerHTML = "Ok";
			commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--question1');
			commit.onclick = close.onclick = click =>
			{
				click.preventDefault();
				click.stopPropagation();
				this.hide();
			};
		});

		window.addEventListener("ProcessCanceled", event =>
		{
			if (event.detail.process !== this.process)
				return;

			commit.innerHTML = "OK";
			commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--error1');
			commit.onclick = close.onclick = click =>
			{
				click.preventDefault();
				click.stopPropagation();
				this.hide();
			};
		});

		window.addEventListener("ProcessRedirect", event =>
		{
			if (event.detail.process !== this.process)
				return;

			commit.innerHTML = "Ok";
			commit.onclick = close.onclick = click =>
			{
				click.preventDefault();
				click.stopPropagation();

				this.hide();
				this.dispatchEvent(new CustomEvent('redirect', {detail: event.detail.url}));
			};
		});
	}

	set process(process)
	{
		this.setAttribute("process", process);
	}

	get process()
	{
		return JSON.parse(this.getAttribute("process"));
	}

	attributeChangedCallback(name)
	{
		this.shadowRoot.querySelector("g-progress-status")
			.process = this.process;
	}

	static get observedAttributes()
	{
		return ["process"];
	}
});

window.addEventListener("@progress", function (event)
{
	fetch(RequestBuilder.build(event.detail.method, event.detail.action, event.detail.form))
		.then(ResponseHandler.json)
		.then(id => new Process(id))
		.then(process =>
		{
			let trigger = event.composedPath()[0] || event.target;
			let dialog = window.top.document.createElement("g-progress-dialog");
			dialog.process = process.id;
			dialog.caption = trigger.getAttribute("title") || "Progresso";
			dialog.addEventListener("show", () => trigger.dispatchEvent(new CustomEvent('show', {detail: {modal: dialog}})));
			dialog.addEventListener("hide", () => trigger.dispatchEvent(new CustomEvent('hide', {detail: {modal: dialog}})));
			dialog.addEventListener("redirect", event => window.location.href = event.detail);
			dialog.show();

		})
		.catch(GMessageDialog.error);
});