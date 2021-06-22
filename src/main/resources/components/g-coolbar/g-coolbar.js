/* global customElements */

window.addEventListener("load", () =>
	customElements.define('g-coolbar', class extends GOverflow
	{
		constructor()
		{
			super();
			this.container.style.flexDirection = "row-reverse";
		}
	}));