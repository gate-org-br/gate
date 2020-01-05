/* global END, HOME, UP, LEFT, DOWN, RIGHT, ESC, ENTER, CSV, arguments, FullScreen, customElements */

class Window extends Modal
{
	constructor(options)
	{
		super(options);
		this.classList.add("g-window");

		if (this._private.options.title)
			this.caption = this._private.options.title;
	}

	get main()
	{
		if (!this._private.main)
			this._private.main = this
				.appendChild(document.createElement("main"));
		return this._private.main;
	}

	get head()
	{
		if (!this._private.head)
			this._private.head = this.main
				.appendChild(document.createElement("header"));
		return this._private.head;
	}

	get caption()
	{
		if (!this._private.caption)
			this._private.caption = this.head
				.appendChild(document.createElement("label"));
		return this._private.caption.innerText;
	}

	set caption(caption)
	{
		if (!this._private.caption)
			this._private.caption = this.head
				.appendChild(document.createElement("label"));
		this._private.caption.innerText = caption;
	}

	get commands()
	{
		if (!this._private.commands)
			this._private.commands = this.head
				.appendChild(document.createElement("g-commands"));
		return this._private.commands;
	}

	get body()
	{
		if (!this._private.body)
			this._private.body = this.main
				.appendChild(document.createElement("section"));
		return this._private.body;
	}

	get foot()
	{
		if (!this._private.foot)
			this._private.foot = this.main
				.appendChild(document.createElement("footer"));
		return this._private.foot;
	}
}

customElements.define('g-window', Window);