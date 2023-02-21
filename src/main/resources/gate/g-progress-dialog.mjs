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

import './g-icon.mjs';
import './g-progress-status.mjs';
import GWindow from './g-window.mjs';

customElements.define('g-progress-dialog', class extends GWindow
{
	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click",
			() => confirm("Tem certeza de que deseja fechar o progresso?")
				&& this.hide());

		let commit = this.shadowRoot.getElementById("commit");

		commit.onclick = event =>
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
			commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--g');
			commit.onclick = event => event.preventDefault() | event.stopPropagation() | this.hide();
		});

		window.addEventListener("ProcessCanceled", event =>
		{
			if (event.detail.process !== this.process)
				return;

			commit.innerHTML = "OK";
			commit.style.color = getComputedStyle(document.documentElement).getPropertyValue('--r');
			commit.onclick = event => event.preventDefault() | event.stopPropagation() | this.hide();
		});

		window.addEventListener("ProcessRedirect", event =>
		{
			if (event.detail.process !== this.process)
				return;

			commit.innerHTML = "Ok";
			commit.onclick = () =>
			{
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