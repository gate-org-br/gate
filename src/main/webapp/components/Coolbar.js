
/* global customElements */

class Coolbar extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		this.appendChild(document.createElement("g-overflow"))
			.innerHTML = "<i>&#X3018;</i>";
	}
}

customElements.define('g-coolbar', Coolbar);