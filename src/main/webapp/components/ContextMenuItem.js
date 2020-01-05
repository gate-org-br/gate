/* global NodeList, Clipboard, customElements */

class ContextMenuItem extends HTMLElement
{
	constructor(action)
	{
		super();

		this.addEventListener("click", () =>
		{
			if (action)
			{
				action(this.parentNode._target);
			} else if (this.action)
			{
				this.parentNode.dispatchEvent(new CustomEvent(this.action, {detail: this.parentNode.target}));
			} else
				throw "Invalid menu item action";
			this.parentNode.hide();
		});
	}

	get icon()
	{
		return this.getAttribute("icon");
	}

	set icon(icon)
	{
		this.setAttribute("icon", icon);
	}

	get name()
	{
		return this.getAttribute("name");
	}

	set name(name)
	{
		this.setAttribute("name", name);
	}

	get action()
	{
		return this.getAttribute("action");
	}

	set action(action)
	{
		this.setAttribute("action", "action");
	}
}




customElements.define('g-context-menu-item', ContextMenuItem);