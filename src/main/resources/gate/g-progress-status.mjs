let template = document.createElement("template");
template.innerHTML = `
	<label id='title'>
		Conectando ao servidor
	</label>
	<progress>
	</progress>
	<g-digital-clock>00:00:00</g-digital-clock>
	<label id='counter'>
		...
	</label>
	<g-logger title="Clique para gerar CSV">
	</g-logger>
 <style>* {
	box-sizing: border-box
}

:host(*) {
	width: 100%;
	display: grid;
	padding: 10px;
	border-radius: 5px;
	place-items: stretch;
	place-content: stretch;
	grid-template-columns: 1fr 1fr;
	grid-template-rows: 32px 32px 32px 120px;
	background-image: linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%);
}

#title
{
	display: flex;
	font-size: 20px;
	align-items: center;
	grid-column: 1 / span 2;
}

progress
{
	width: 100%;
	height: 40px;
	grid-column: 1 / span 2;
}

g-digital-clock
{
	display: flex;
	font-size: 12px;
	align-items: center;
}

#counter{
	display: flex;
	font-size: 12px;
	align-items: center;
	justify-content: flex-end;
}

g-logger
{
	grid-column: 1 / span 2;
}


</style>`;

/* global customElements */

import './g-logger.mjs';
customElements.define('g-progress-status', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		let title = this.shadowRoot.getElementById("title");
		let progress = this.shadowRoot.querySelector("progress");
		let clock = this.shadowRoot.querySelector("g-digital-clock");
		let counter = this.shadowRoot.getElementById("counter");
		let logger = this.shadowRoot.querySelector("g-logger");
		window.addEventListener("ProcessPending", event =>
		{
			if (event.detail.process !== this.process)
				return;
			this.log(event);
			title.style.color = '#000000';
			clock.style.color = '#000000';
			counter.style.color = '#000000';
		});
		window.addEventListener("ProcessCommited", event =>
		{
			if (event.detail.process !== this.process)
				return;
			this.log(event);
			if (!progress.max)
				progress.max = 100;
			if (!progress.value)
				progress.value = 100;
			title.style.color = '#006600';
			clock.style.color = '#006600';
			counter.style.color = '#006600';
			clock.setAttribute("paused", "paused");
		});
		window.addEventListener("ProcessCanceled", event =>
		{
			if (event.detail.process !== this.process)
				return;
			this.log(event);
			if (!progress.max)
				progress.max = 100;
			if (!progress.value)
				progress.value = 0;
			title.style.color = '#660000';
			clock.style.color = '#660000';
			counter.style.color = '#660000';
			clock.setAttribute("paused", "paused");
		});
		window.addEventListener("ProcessError", event =>
		{
			if (event.detail.process !== this.process)
				return;
			this.log(event);
			title.style.color = '#666666';
			clock.style.color = '#666666';
			counter.style.color = '#666666';
		});
	}

	log(event)
	{
		let title = this.shadowRoot.getElementById("title");
		let logger = this.shadowRoot.querySelector("g-logger");
		let counter = this.shadowRoot.getElementById("counter");
		let progress = this.shadowRoot.querySelector("progress");
		if (event.detail.progress)
			counter.innerHTML = event.detail.progress;
		if (event.detail.text !== title.innerHTML)
		{
			logger.append(event.detail.text);
			title.innerHTML = event.detail.text;
		}

		if (event.todo !== -1)
		{
			progress.max = event.detail.todo;
			if (event.done !== -1)
				progress.value = event.detail.done;
		}
	}
});