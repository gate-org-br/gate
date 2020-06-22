/* global customElements, GOverflow, Proxy, GDialog */

class GDialogCommands extends GOverflow
{
	constructor()
	{
		super();
		window.addEventListener("load", () =>
			GDialog.commands = this);
	}
}

customElements.define('g-dialog-commands', GDialogCommands);