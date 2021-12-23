/* global customElements */

import GDialog from './g-dialog.mjs';
import GOverflow from "./g-overflow.mjs";

customElements.define('g-dialog-commands', class extends GOverflow
{
	constructor()
	{
		super();
		GDialog.commands = this;
		this.container.style.flexDirection = "row-reverse";
	}
});