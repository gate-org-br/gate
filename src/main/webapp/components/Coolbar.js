
/* global customElements */

class Coolbar extends HTMLElement
{
	constructor()
	{
		super();
		this.addEventListener("click", function ()
		{
			if (this.getAttribute("data-overflow"))
				this.appendChild(new CoolbarDialog(Array.from(this.children)));
		});
	}
}

customElements.define('g-coolbar', Coolbar);
