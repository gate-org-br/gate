/* global customElements, GOverflow, Proxy */

customElements.define('g-tabbar', class extends GOverflow
{
	constructor()
	{
		super();
		window.addEventListener("load", () => this.more.innerHTML
				= this.querySelector("i") ? "Mais<i>&#X3017;</i>" : "Mais");
	}

	connectedCallback()
	{
		super.connectedCallback();
		let selected = GSelection.getSelectedLink(this.children);
		if (selected)
			selected.setAttribute("aria-selected", "true");
	}
});