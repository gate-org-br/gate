/* global NodeList, Clipboard, customElements */

class ContextMenuItem extends HTMLElement
{
	constructor(icon, name, action)
	{
		super();

		this.setAttribute("icon", icon);
		this.setAttribute("name", name);

		switch (typeof action)
		{
			case 'function':
				this.action = action;
				break;
			case 'string':
				this.setAttribute("action", action);
				break;
			default:
				throw "Ïnvalid menu item action";
		}

		this.addEventListener("click", () =>
		{
			switch (typeof this.action)
			{
				case 'function':
					this.action({context: this.parentNode.context,
						element: this.parentNode.element});
					break;
				case 'string':
					window.dispatchEvent(new CustomEvent(this.action,
						{detail: {context: this.parentNode.context,
								element: this.parentNode.element}}));
					break;
				default:
					throw "Ïnvalid menu item action";
			}
			this.parentNode.hide();
		});
	}

	static get observedAttributes()
	{
		return ['icon', 'name', 'action'];
	}

	attributeChangedCallback(name)
	{
		switch (name)
		{
			case 'icon':
				this.icon = this.getAttribute("icon");
				break;
			case 'name':
				this.name = this.getAttribute("name");
				break;
			case 'action':
				this.action = this.getAttribute("action");
				break;
		}
	}
}




customElements.define('g-context-menu-item', ContextMenuItem);