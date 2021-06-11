/* global customElements, GOverflow, Proxy, Dialog */

customElements.define('g-dialog-caption', class extends HTMLElement
{
	constructor()
	{
		super();
		window.addEventListener("load", () =>
			GDialog.caption = this.innerText);
	}
});