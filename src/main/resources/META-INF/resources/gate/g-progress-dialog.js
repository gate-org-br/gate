let template = document.createElement("template");
template.innerHTML = `
	<main>
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
	</main>
 <style>main
{
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}

main > footer > button {
	flex-grow: 1;
	justify-content: center;
}</style>`;

/* global customElements */

import './g-icon.js';
import './g-progress-status.js';
import GWindow from './g-window.js';

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