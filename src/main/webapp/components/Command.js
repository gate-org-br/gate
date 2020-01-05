/* global customElements */

class Command extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this._private.enabled = true;
		this._private.visible = true;
	}

	enabled()
	{
		if (!arguments.length)
			return this._private.enabled;

		this._private.enabled = arguments[0];
		this.style.opacity = this._private.enabled ? "1.0" : "0.2";
		this.style.pointerEvents = this._private.enabled ? "all" : "none";
		return this;
	}

	visible()
	{
		if (!arguments.length)
			return this._private.visible;

		this._private.visible = arguments[0];
		this.style.display = this._private.visible ? "" : "none";
		return this;
	}

	action()
	{
		if (!arguments.length)
			return this.onclick;

		this.onclick = () => arguments[0](this);
		return this;
	}

	connectedCallback()
	{
		this.classList.add("g-command");
	}
}

customElements.define('g-command', Command);