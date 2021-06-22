/* global customElements, GOverflow */

window.addEventListener("load",
	customElements.define('g-dialog-commands', class extends GOverflow
	{
		constructor()
		{
			super();
			GDialog.commands = this;
			this.container.style.flexDirection = "row-reverse";
		}
	}));