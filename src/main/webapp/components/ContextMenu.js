/* global NodeList, Clipboard, customElements */

class ContextMenu extends HTMLElement
{
	constructor()
	{
		super();
		this.modal = new Modal();
		this.items = Array.from(arguments);
		this.modal.element().appendChild(this);
	}

	connectedCallback()
	{
		this.items.forEach(item => this.appendChild(item));
	}

	show(context, element, x, y)
	{
		this.context = context;
		this.element = element;
		this.style.top = y + "px";
		this.style.left = x + "px";
		this.modal.show();
	}

	hide()
	{
		this.modal.hide();
	}

	register(elements)
	{
		if (Array.isArray(elements))
			elements.forEach(element => this.register(element));
		else if (elements instanceof NodeList)
			Array.from(elements).forEach(element => this.register(element));
		else if (elements.addEventListener)
			elements.addEventListener("contextmenu", event =>
			{
				event.preventDefault();
				event.stopPropagation();
				this.show(elements, event.target, event.clientX, event.clientY);

				var rec = this.getBoundingClientRect();
				if (rec.top + rec.height > window.innerHeight)
					this.style.top = rec.top - rec.height + "px";
				if (rec.left + rec.width > window.innerWidth)
					this.style.left = rec.left - rec.width + "px";

			});
	}
}

customElements.define('g-context-menu', ContextMenu);