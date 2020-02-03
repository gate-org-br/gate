/* global customElements, GOverflow, Proxy */

customElements.define('g-tabbar', class extends GOverflow
{
	constructor()
	{
		super();
		this._private.container.style.flexDirection = "row";
	}

	connectedCallback()
	{
		let selected = GSelection.getSelectedLink(this.children);
		if (selected)
			selected.setAttribute("aria-selected", "true");
		super.connectedCallback();
	}
});