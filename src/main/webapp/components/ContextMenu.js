/* global NodeList, Clipboard, customElements */

class ContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this._modal = new Modal();
		this._items = Array.isArray(arguments[0]) ? arguments[0] :
			Array.from(arguments[0]);
	}

	connectedCallback()
	{
		this._items.forEach(item => this.appendChild(item));
	}

	show(context, element, x, y)
	{
		this._target = {"context": context, "element": element};
		this._modal.element().appendChild(this);
		this._modal.show();

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
		this._target = null;
		this._modal.hide();
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

	get target()
	{
		return this._target;
	}
}

customElements.define('g-context-menu', ContextMenu);

window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("g-context-menu"))
		.forEach(element => element.register(document.querySelectorAll("*[data-context-menu=" + element.id + "]")));
});