/* global customElements, GOverflow, Proxy */

customElements.define('g-tabbar', class extends GOverflow
{
	constructor()
	{
		super();
		this._private.container.style.flexDirection = "row";
	}
});