let template = document.createElement("template");
template.innerHTML = `
	<dialog><header>
			Progresso
			<a id='close' href="#"><g-icon>
					&#X1011;
				</g-icon></a></header><section><g-progress-status></g-progress-status></section><footer><button id='commit'></button></footer></dialog>
<style data-element="g-progress-dialog">dialog
{
	height: fit-content;
	min-width: 320px;
	max-width: 800px;
	width: calc(100% - 40px);
}

dialog > footer > button {
	flex-grow: 1;
	justify-content: center;
}

#commit::after
{
	content: "Processando";
}

:host([status="commited"]) #commit,
:host([status="redirect"]) #commit
{
	color: var(--question1);
}

:host([status="canceled"]) #commit
{
	color: var(--error1);
}

:host([status="commited"]) #commit::after,
:host([status="canceled"]) #commit::after,
:host([status="redirect"]) #commit::after
{
	content: "Ok";
}
</style>`;
/* global customElements */

import './g-icon.js';
import './trigger.js';
import './g-progress-status.js';
import GWindow from './g-window.js';

customElements.define('g-progress-dialog', class extends GWindow
{
	#status;
	#commit;
	#redirect;

	constructor()
	{
		super();
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.#status = this.shadowRoot.querySelector("g-progress-status");
		this.#commit = this.shadowRoot.getElementById("commit");
		const close = this.shadowRoot.getElementById("close");

		this.#commit.onclick = close.onclick = event =>
		{
			event.preventDefault();
			event.stopPropagation();
			switch (this.getAttribute("status"))
			{
				case "commited":
				case "canceled":
					this.hide();
					break;
				case "redirect":
					window.location = this.#redirect;
					break;
				default:
					if (confirm("Tem certeza de que deseja fechar o progresso?"))
						this.hide();
			}
		};

		this.addEventListener('Progress', e =>
		{
			this.#status.dispatchEvent(new CustomEvent('Progress', {detail: e.detail}));
			if (e.detail.status === 'COMMITED')
				this.setAttribute("status", "commited");
			else if (e.detail.status === 'CANCELED')
				this.setAttribute("status", "canceled");
		});

		this.addEventListener('Redirect', e =>
		{
			this.#redirect = e.detail.url;
			this.setAttribute("status", "redirect");
		});
		this.addEventListener('error', e => this.#status.dispatchEvent(new CustomEvent('error', {detail: e.detail})));
	}
});
