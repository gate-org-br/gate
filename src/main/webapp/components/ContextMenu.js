/* global NodeList, Clipboard, customElements */

class ContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {};
		this.addEventListener("click", () => this.hide());
	}

	show(context, target, x, y)
	{
		this._private.target = target;
		this._private.context = context;
		this._private.dialog = new Modal();
		this._private.dialog.appendChild(this);
		this._private.dialog.show();

		this.style.top = y + "px";
		this.style.left = x + "px";

		this.style.display = "flex";
		var rec = this.getBoundingClientRect();
		if (rec.top + rec.height > window.innerHeight)
			this.style.top = rec.top - rec.height + "px";
		if (rec.left + rec.width > window.innerWidth)
			this.style.left = rec.left - rec.width + "px";
	}

	hide()
	{
		this.style.display = "none";
		this._private.target = null;
		this._private.context = null;
		this._private.dialog.hide();
		this._private.dialog = null;
	}

	register(element)
	{
		if (Array.isArray(element))
			element.forEach(element => this.register(element));
		else if (element instanceof NodeList)
			Array.from(element).forEach(element => this.register(element));
		else if (element.addEventListener)
			element.addEventListener("contextmenu", event =>
			{
				event.preventDefault();
				event.stopPropagation();
				this.show(element, event.target,
					event.clientX, event.clientY);
			});
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

customElements.define('g-context-menu', ContextMenu);

window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("g-context-menu"))
		.forEach(element => element.register(document.querySelectorAll("*[data-context-menu=" + element.id + "]")));
});