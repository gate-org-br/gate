/* global customElements */

class DigitalClock extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {}
		this._private.listener = () =>
		{
			if (!this.hasAttribute("paused"))
			{
				var time = this.hasAttribute("time") ?
					Number(this.getAttribute("time")) : 0;
				this.setAttribute("time", time + 1);
			}
		};
	}

	static get observedAttributes()
	{
		return ['time', 'paused'];
	}

	attributeChangedCallback()
	{
		this.innerHTML = new Duration(Number(this.getAttribute("time")))
			.format(this.getAttribute("format") || "hh:mm:ss");
	}

	connectedCallback()
	{
		window.addEventListener("ClockTick", this._private.listener);
	}

	disconnectedCallback()
	{
		window.removeEventListener("ClockTick", this._private.listener);
	}
}

customElements.define('digital-clock', DigitalClock);

window.addEventListener("load", () =>
{
	window.setInterval(() => this.dispatchEvent(new CustomEvent("ClockTick")), 1000);
});