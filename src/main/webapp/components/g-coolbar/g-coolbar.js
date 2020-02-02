/* global customElements */

customElements.define('g-coolbar', class extends GOverflow
{
	constructor()
	{
		super();
		this.more.innerHTML = "<i>&#X3018;</i>";
		this.container.style.flexDirection = "row-reverse";
	}
});