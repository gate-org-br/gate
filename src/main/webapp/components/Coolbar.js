/* global customElements */

class Coolbar extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		if (!this.getElementsByTagName("g-overflow").length)
			this.appendChild(document.createElement("g-overflow"))
				.innerHTML = "<i>&#X3018;</i>";
	}
}

customElements.define('g-coolbar', Coolbar);