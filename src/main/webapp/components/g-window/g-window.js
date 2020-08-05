/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class GWindow extends GModal
{
	constructor()
	{
		super();
		this._private.main = window.top.document.createElement("main");
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.classList.add("g-window");
		this.appendChild(this._private.main);
		this.main.addEventListener("click", e => e.stopPropagation());
		this.main.addEventListener("contextmenu", e => e.stopPropagation());
	}

	get main()
	{
		return this._private.main;
	}

	get head()
	{
		if (!this._private.head)
			this._private.head = this.main
				.appendChild(window.top.document.createElement("header"));
		return this._private.head;
	}

	get caption()
	{
		if (!this._private.caption)
			this._private.caption = this.head
				.appendChild(window.top.document.createElement("label"));
		return this._private.caption.innerText;
	}

	set caption(caption)
	{
		if (!this._private.caption)
			this._private.caption = this.head
				.appendChild(window.top.document.createElement("label"));
		this._private.caption.innerText = caption;
	}

	get body()
	{
		if (!this._private.body)
			this._private.body = this.main
				.appendChild(window.top.document.createElement("section"));
		return this._private.body;
	}

	get foot()
	{
		if (!this._private.foot)
			this._private.foot = this.main
				.appendChild(window.top.document.createElement("footer"));
		return this._private.foot;
	}

	get commands()
	{
		if (!this._private.commands)
			this._private.commands =
				this.head.appendChild(new GDialogCommands());
		return this._private.commands;
	}

	set commands(commands)
	{
		if (this._private.commands)
			this.head.removeChild(this._private.commands);
		this.head.appendChild(this._private.commands = commands);
	}
}

customElements.define('g-window', GWindow);