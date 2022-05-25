let template = document.createElement("template");
template.innerHTML = `
	<main>
		<header>
			Progresso
			<a id='close' href="#">
				&#X1011;
			</a>
		</header>
		<section>
			<g-progress-status>
			</g-progress-status>
		</section>
		<footer>
			<a id='commit' href='#'>
				Processando
			</a>
		</footer>
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
	color: #000000;
	font-size: 16px;
	font-weight: bold;
	align-items: center;
	text-decoration: none;
	justify-content: center;
	border: 1px solid transparent;
	background-image:  linear-gradient(to bottom, #E3E0D0 0%, #858279 100%);
}

#commit:hover {
	color: var(--b);
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