let template = document.createElement("template");
template.innerHTML = `
	<main>
		<g-window-header>
			Progresso
			<a id='close' href="#">
				&#X1011;
			</a>
		</g-window-header>
		<g-window-section>
			<g-progress-status>
			</g-progress-status>
		</g-window-section>
		<g-window-footer>
			<a id='commit' href='#'>
				Processando
			</a>
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
	width: 800px;
	height: auto;
	display: grid;
	max-width: 80%;
	position: fixed;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	grid-template-rows: 40px 1fr 40px;
	box-shadow: 3px 10px 5px 0px rgba(0,0,0,0.75);
	border: 4px solid var(--g-window-border-color);
}</style>`;

/* global customElements */

import './g-window-header.mjs';
import './g-window-section.mjs';
import './g-window-footer.mjs';
import './g-progress-status.mjs';
import Scroll from './scroll.mjs';

customElements.define('g-progress-dialog', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.shadowRoot.getElementById("close").addEventListener("click", () => this.hide());

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

	set target(target)
	{
		this.setAttribute("target", target);
	}

	get target()
	{
		return this.getAttribute("target") || "_self";
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
		switch (name)
		{
			case "process":
				this.shadowRoot.querySelector("g-progress-status")
					.process = this.process;
				break;
			case "target":
				this.shadowRoot.getElementById("commit")
					.setAttribute("target", this.target);
				break;
		}

	}

	show()
	{
		Scroll.disable(window.top.document.documentElement);
		window.top.document.documentElement.appendChild(this);
		return this;
	}

	hide()
	{
		Scroll.enable(window.top.document.documentElement);
		this.parentNode.removeChild(this);
		return this;
	}

	static get observedAttributes()
	{
		return ["process", "target"];
	}
});