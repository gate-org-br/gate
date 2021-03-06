/* global NodeList, Clipboard, customElements */

class GContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.addEventListener("click", () => this.hide());
		this._private.mousedown = e => !this.contains(e.target) && this.hide();
	}

	show(context, target, x, y)
	{
		this._private.target = target;
		this._private.context = context;
		this.style.display = "flex";

		if (x + this.clientWidth > window.innerWidth)
			x = x >= this.clientWidth
				? x - this.clientWidth
				: x = window.innerWidth / 2 - this.clientWidth / 2;

		if (y + this.clientHeight > window.innerHeight)
			y = y >= this.clientHeight
				? y - this.clientHeight
				: y = window.innerHeight / 2 - this.clientHeight / 2;


		this.style.top = y + "px";
		this.style.left = x + "px";
		this.style.visibility = "visible";
	}

	hide()
	{
		this._private.target = null;
		this._private.context = null;
		this.style.display = "none";
		this.style.visibility = "hidden";
	}

	connectedCallback()
	{
		this.classList.add('g-context-menu');
		window.addEventListener("mousedown", this._private.mousedown);
	}

	disconnectedCallback()
	{
		window.removeEventListener("mousedown", this._private.mousedown);
	}

	get context()
	{
		return this._private.context;
	}

	get target()
	{
		return this._private.target;
	}
}

customElements.define('g-context-menu', GContextMenu);

window.addEventListener("contextmenu", event =>
{
	event = event || window.event;
	let action = event.target || event.srcElement;

	action = action.closest("[data-context-menu]");
	if (action)
	{
		document.getElementById(action.getAttribute("data-context-menu"))
			.show(action, event.target, event.clientX, event.clientY);
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}
}, true);