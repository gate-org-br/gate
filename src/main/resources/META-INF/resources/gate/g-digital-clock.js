let template = document.createElement("template");
template.innerHTML = `
	<label>
	</label>
`;

/* global customElements */

import Duration from './duration.js';

customElements.define('g-digital-clock', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.innerHTML = template.innerHTML;

		this._private = {tick: () => !this.paused && (this.time = this.time + 1)};
	}

	static get observedAttributes()
	{
		return ['time', 'paused'];
	}

	attributeChangedCallback()
	{
		this.shadowRoot.querySelector("label").innerText
			= new Duration(Number(this.getAttribute("time")))
			.format(this.getAttribute("format") || "hh:mm:ss");
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

	get time()
	{
		return Number(this.getAttribute("time") || 0);
	}

	set time(value)
	{
		this.setAttribute("time", value);
	}

	connectedCallback()
	{
		window.addEventListener("ClockTick", this._private.tick);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ClockTick", this._private.tick);
	}

});

window.setInterval(() => window.dispatchEvent(new CustomEvent("ClockTick")), 1000);