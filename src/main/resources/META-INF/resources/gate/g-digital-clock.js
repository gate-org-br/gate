let template = document.createElement("template");
template.innerHTML = `
	<label>
	</label>
 <style data-element="g-digital-clock">* {
	box-sizing: border-box;
}

:host(*)
{
	font-size: 16px;
	font-family: monospace;
}

label {
	font-size: inherit;
	font-family: inherit;
}</style>`;
/* global customElements */
import Duration from './duration.js';

export default class GDigitalClock extends HTMLElement
{
	#ctrl;
	#collector = null;
	#timestamp = null;

	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;
	}

	static get observedAttributes()
	{
		return ['value', 'paused', 'format'];
	}

	attributeChangedCallback(attr, old, val)
	{
		if (attr === "paused")
		{
			if (this.#collector !== null)
			{
				if (val === null)
				{
					this.#timestamp = Date.now();
				} else if (this.#timestamp !== null)
				{
					this.#collector += (Date.now() - this.#timestamp) / 1000;
					this.#timestamp = null;
				}
			}
		} else if (attr === "value")
			this.value = val;
		this.#render();
	}

	get signal()
	{
		return this.#ctrl?.signal;
	}

	get format()
	{

		return this.getAttribute("format") || "hh:mm:ss";
	}

	set format(value)
	{
		this.setAttribute("format", value);
	}

	get paused()
	{
		return this.hasAttribute("paused");
	}

	set paused(value)
	{
		if (value)
			this.setAttribute("paused", "");
		else
			this.removeAttribute("paused");
	}

	get value()
	{
		if (this.#timestamp === null)
			return this.#collector;
		return this.#collector + (Date.now() - this.#timestamp) / 1000;
	}

	set value(value)
	{
		this.#collector = null;
		this.#timestamp = null;

		if (typeof value === "string" && /^[0-9]+$/.test(value))
			value = Number.parseInt(value);

		if (typeof value === "number" && value >= 0)
		{
			this.#collector = value;
			if (!this.paused)
				this.#timestamp = Date.now();
		}
	}

	#render()
	{
		const time = this.value;
		const label = this.shadowRoot.querySelector("label");
		label.innerText = time !== null ? new Duration(time).format(this.format) : "##:##:##";
	}

	connectedCallback()
	{
		this.#ctrl = new AbortController();
		const signal = this.#ctrl.signal;

		if (!this.paused
			&& this.#collector !== null
			&& this.#timestamp === null)
			this.#timestamp = Date.now();

		this.#render();
		window.addEventListener("ClockTick", () => this.#render(), {signal});
	}

	disconnectedCallback()
	{
		this.#ctrl.abort();
		this.#ctrl = null;
	}
}

customElements.define('g-digital-clock', GDigitalClock);
window.setInterval(() => window.dispatchEvent(new CustomEvent("ClockTick")), 1000);