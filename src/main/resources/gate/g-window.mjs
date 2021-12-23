/* global customElements */

import GModal from './g-modal.mjs';
import FullScreen from './fullscreen.mjs';

export default class GWindow extends GModal
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
				this.head.appendChild(document.createElement("g-dialog-commands"));
		return this._private.commands;
	}

	set commands(commands)
	{
		if (this._private.commands)
			this.head.removeChild(this._private.commands);
		this.head.appendChild(this._private.commands = commands);
	}

	get minimizeButton()
	{
		if (!this._private.minimizeButton)
		{
			this._private.minimizeButton = this.head.appendChild(window.top.document.createElement("a"));
			this._private.minimizeButton.href = "#";
			this._private.minimizeButton.innerHTML = "<i>&#x3019;<i/>";
			this._private.minimizeButton.onclick = event => event.preventDefault() | this.minimize();
		}

		return this._private.minimizeButton;
	}

	get fullScreenButton()
	{
		if (!this._private.fullScreenButton)
		{
			this._private.fullScreenButton = this.head.appendChild(window.top.document.createElement("a"));
			this._private.fullScreenButton.href = "#";
			this._private.fullScreenButton.innerHTML = (FullScreen.status() ? "<i>&#x3016;</i>" : "<i>&#x3015;</i>");
			this._private.fullScreenButton.onclick = event => this._private.fullScreenButton.innerHTML = (FullScreen.switch(this.main) ? "<i>&#x3015;</i>" : "<i>&#x3016;</i>");
		}

		return this._private.fullScreenButton;
	}

	get hideButton()
	{
		if (!this._private.hideButton)
		{
			this._private.hideButton = this.head.appendChild(window.top.document.createElement("a"));
			this._private.hideButton.href = "#";
			this._private.hideButton.innerHTML = "<i>&#x1011;<i/>";
			this._private.hideButton.onclick = event => event.preventDefault() | this.hide();
		}

		return this._private.hideButton;
	}

	set size(value)
	{
		let size = /^([0-9]+)(\/([0-9]+))?$/g.exec(value);
		if (!size)
			throw new Error(value + " is not a valid size");
		this.main.style.maxWidth = size[1] + "px";
		this.main.style.maxHeight = (size[4] || size[1]) + "px";
	}
}

customElements.define('g-window', GWindow);