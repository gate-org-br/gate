let template = document.createElement("template");
template.innerHTML = `
	<progress></progress><span id="counter"></span>
<style data-element="g-job">:host {
	width: 100%;
	display: inline-grid;

	gap: 4px;
	padding: 4px 6px;
	border-radius: 4px;
	align-items: center;
	border: 1px solid var(--main3);
	background-color: var(--main2);
	grid-template-columns: 16px 100px 1fr;
}

:host::before { font-family: "gate"; color: inherit; }

:host([status="CONNECTING"])::before { content: "\\2051";}

:host([status="PENDING"])::before { content: "\\2206";}

:host([status="COMMITED"])::before { content: "\\1000";}

:host([status="CANCELED"])::before { content: "\\1001";}

:host([status="UNKNOWN"])::before { content: "\\1006";}

progress { width: 100%; height: 8px; }

#counter {
	padding: 2px;
	display: flex;
	font-size: 11px;
	white-space: nowrap;
	align-items: center;
	font-variant-numeric: tabular-nums;
}

:host([status="PENDING"]) { color: var(--text1);}

:host([status="COMMITED"]) { color: var(--g2);}

:host([status="CANCELED"]) { color: var(--r2);}

:host([status="UNKNOWN"]) { color: var(--r2);}

:host([status="CONNECTING"]) { color: var(--y2);}</style>`;
import Job from './job.js';

const Status = Object.freeze({
	PENDING: 'PENDING',
	COMMITED: 'COMMITED',
	CANCELED: 'CANCELED',
	UNKNOWN: 'UNKNOWN',
	CONNECTING: 'CONNECTING',
});

customElements.define('g-job', class extends HTMLElement
{
	#timeout;
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

	get status() { return this.getAttribute("status") || Status.PENDING; }

	set todo(value) { this.setAttribute("todo", value) }

	get todo() { return parseInt(this.getAttribute('todo')) || 0; }

	set done(value) { this.setAttribute("done", value) }

	get done() { return parseInt(this.getAttribute('done')) || 0; }

	get percentage() { return this.todo > 0 ? Math.round((this.done / this.todo) * 100) : 0; }

	connectedCallback()
	{
		if (this.uuid && this.status === Status.PENDING)
		{
			this.status = Status.CONNECTING;
			fetch(`Progress?uuid=${encodeURIComponent(this.uuid)}`)
				.then(response => Job.from(response))
				.then(job =>
				{
					this.status = Status.PENDING;
					job.addEventListener('Progress', e =>
					{
						const detail = e.detail;
						this.status = detail.status;
						this.setAttribute('done', detail.done);
						this.setAttribute('todo', detail.todo);
					});
					return job.start();
				})
				.catch(() =>
				{
					this.status = Status.PENDING;
					this.#timeout = setTimeout(() =>
						this.isConnected && this.connectedCallback(), 5000);
				});
		}
	}

	disconnectedCallback() { this.#timeout && clearTimeout(this.#timeout); }

	attributeChangedCallback()
	{
		if (this.todo)
		{
			this.#progress.max = this.todo;
			this.#progress.value = this.done;
			this.#counter.textContent = `${this.done}/${this.todo} (${this.percentage}%)`;
		} else
		{
			this.#counter.textContent = this.done;
			this.#progress.removeAttribute('max');
			this.#progress.removeAttribute('value');
		}
	}
});