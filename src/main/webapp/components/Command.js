/* global customElements */

class Command extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
	}

	get action()
	{
		return this._private.action;
	}

	set action(action)
	{
		this._private.action = action;
		if (action)
			this.onclick = () => this._private.action(this);
	}

	connectedCallback()
	{
		this.classList.add("g-command");
	}
}

customElements.define('g-command', Command);