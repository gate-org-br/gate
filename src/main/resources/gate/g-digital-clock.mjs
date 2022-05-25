/* global customElements */

import Duration from './duration.mjs';

customElements.define('g-digital-clock', class extends HTMLElement
{
	static get observedAttributes()
	{
		return ['time', 'paused'];
	}

	constructor()
	{
		super();
		this.tick = () => {
			if (this.hasAttribute("paused"))
				return;

			let time = this.hasAttribute("time") ?
				Number(this.getAttribute("time")) : 0;
			this.setAttribute("time", time + 1);
		};
	}

	attributeChangedCallback()
	{
		this.innerHTML = new Duration(Number(this.getAttribute("time")))
			.format(this.getAttribute("format") || "hh:mm:ss");
	}

	connectedCallback()
	{

		window.addEventListener("ClockTick", this.tick);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ClockTick", this.tick);
	}
});

window.setInterval(() => window.dispatchEvent(new CustomEvent("ClockTick")), 1000);