let template = document.createElement("template");
template.innerHTML = `
	<label id='title'>
		Conectando ao servidor
	</label><progress></progress><g-digital-clock value="0"></g-digital-clock><label id='counter'>
		...
	</label><g-logger title="Clique para gerar CSV"></g-logger>
<style data-element="g-progress-status">* {
	box-sizing: border-box
}

:host(*) {
	width: 100%;
	display: grid;
	padding: 10px;
	color: var(--text1);
	border-radius: 3px;
	place-items: stretch;
	place-content: stretch;
	grid-template-columns: 1fr 1fr;
	grid-template-rows: 32px 32px 32px 120px;
}

:host([status="commited"]) {
	color: var(--success1);
}

:host([status="canceled"]) {
	color: var(--error1);
}

:host([status="unknown"]) {
	color: var(--error1);
}

:host([status="error"]) {
	color: var(--warning1);
}

#title {
	display: flex;
	font-size: 20px;
	align-items: center;
	grid-column: 1 / span 2;
}

progress {
	width: 100%;
	height: 40px;
	grid-column: 1 / span 2;
}

g-digital-clock {
	display: flex;
	font-size: 12px;
	align-items: center;
}

#counter {
	display: flex;
	font-size: 12px;
	align-items: center;
	justify-content: flex-end;
}

g-logger {
	grid-column: 1 / span 2;
}</style>`;
/* global customElements */

import './g-logger.js';
import './g-digital-clock.js';

customElements.define('g-progress-status', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		const title = this.shadowRoot.getElementById("title");
		const progress = this.shadowRoot.querySelector("progress");
		const clock = this.shadowRoot.querySelector("g-digital-clock");
		const counter = this.shadowRoot.getElementById("counter");
		const logger = this.shadowRoot.querySelector("g-logger");

		const update = detail =>
		{
			if (detail.todo > 0 && detail.done >= 0)
				counter.textContent = `${detail.done}/${detail.todo} (${Math.round((detail.done / detail.todo) * 100)}%)`;
			if (detail.text !== title.innerHTML)
			{
				logger.append(detail.text);
				title.innerHTML = detail.text;
			}
			if (detail.todo > 0)
			{
				progress.max = detail.todo;
				if (detail.done >= 0)
					progress.value = detail.done;
			}
		};

		this.addEventListener('Progress', e =>
		{
			update(e.detail);
			switch (e.detail.status)
			{
				case 'COMMITED':
					this.setAttribute("status", "commited");
					clock.setAttribute("paused", "paused");
					break;
				case 'CANCELED':
					this.setAttribute("status", "canceled");
					clock.setAttribute("paused", "paused");
					break;
				case 'UNKNOWN':
					this.setAttribute("status", "unknown");
					clock.setAttribute("paused", "paused");
					break;
				default:
					this.removeAttribute("status");
					clock.removeAttribute("paused");
			}
		});
		this.addEventListener('error', e =>
		{
			this.setAttribute("status", "error");
			update(e.detail);
		});
	}
});