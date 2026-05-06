let template = document.createElement("template");
template.innerHTML = `
	<div id="container"><span id="counter"></span><progress></progress></div>
<style data-element="g-job">:host {
    display: inline-grid;
    width: min(220px, 100%);
    color: var(--text1);
}

#container {
    display: grid;
    align-items: center;
    grid-template-columns: auto 1fr;
    gap: 4px;
    padding: 4px 6px;
    border-radius: 4px;
    border: 1px solid var(--main3);
    background-color: var(--main2);
}

#counter {
    display: flex;
    align-items: center;
    font-size: 11px;
    white-space: nowrap;
    font-variant-numeric: tabular-nums;
}

#counter::before {
    content: "";
    width: 6px;
    height: 6px;
    flex: 0 0 auto;
    margin-right: 6px;
    border-radius: 50%;
    background-color: currentColor;
}

:host([status="COMMITED"]) #container {
    border-color: var(--g2);
}

:host([status="CANCELED"]) #container {
    border-color: var(--r2);
}

:host([status="PENDING"]) #container,
:host([status="CREATED"]) #container,
:host([status="CONNECTING"]) #container {
    border-color: var(--main3);
}

:host([status="UNKNOWN"]) #container {
    border-color: var(--r2);
}

:host([status="COMMITED"]) #counter {
	color: var(--g1);
}

:host([status="CANCELED"]) #counter {
	color: var(--r1);
}

:host([status="CONNECTING"]) #counter {
	color: var(--main3);
}

:host([status="PENDING"]) #counter,
:host([status="CREATED"]) #counter {
	color: var(--text1);
}

:host([status="UNKNOWN"]) #counter {
    color: var(--r1);
}

progress {
    width: 100%;
    height: 8px;
}
</style>`;
import Job from './job.js';

const Status = Object.freeze({
	CREATED: 'CREATED',
	PENDING: 'PENDING',
	COMMITED: 'COMMITED',
	CANCELED: 'CANCELED',
	UNKNOWN: 'UNKNOWN',
	CONNECTING: 'CONNECTING',
});

customElements.define('g-job', class extends HTMLElement
{
	#counter;
	#progress;

	static get observedAttributes()
	{
		return ['uuid', 'status', 'todo', 'done'];
	}

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));
		this.#counter = this.shadowRoot.getElementById("counter");
		this.#progress = this.shadowRoot.querySelector("progress");
	}

	get uuid() { return this.getAttribute("uuid"); }

	set status(value) { this.setAttribute("status", value) }

	get status() { return this.getAttribute("status") || Status.CREATED; }

	set todo(value) { this.setAttribute("todo", value) }

	get todo() { return parseInt(this.getAttribute('todo')) || 0; }

	set done(value) { this.setAttribute("done", value) }

	get done() { return parseInt(this.getAttribute('done')) || 0; }

	get percentage() { return this.todo > 0 ? Math.round((this.done / this.todo) * 100) : 0; }

	connectedCallback()
	{
		if (this.uuid
			&& (this.status === Status.CREATED
				|| this.status === Status.PENDING))
		{
			this.status = Status.CONNECTING;
			fetch(`Progress?uuid=${encodeURIComponent(this.uuid)}`)
				.then(response => Job.from(response))
				.then(job =>
				{
					job.addEventListener('Progress', e =>
					{
						const detail = JSON.parse(e.detail);
						this.status = detail.status;
						this.setAttribute('done', detail.done);
						this.setAttribute('todo', detail.todo);
					});
					return job.start();
				})
				.catch(() =>
				{
					this.status = Status.PENDING;
					setTimeout(() => this.isConnected && this.connectedCallback(), 1000);
				});
		}
	}

	attributeChangedCallback()
	{
		if (this.todo > 0 && this.done >= 0)
		{
			this.#progress.max = this.todo;
			this.#progress.value = this.done;
			this.#counter.textContent = `${this.done}/${this.todo} (${this.percentage}%)`;
		} else
		{
			this.#progress.removeAttribute("max");
			this.#progress.removeAttribute("value");
			this.#counter.textContent = `??????????`;
		}
	}
});