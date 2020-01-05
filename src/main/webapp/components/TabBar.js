/* global customElements */

class TabBar extends HTMLElement
{
	constructor(element)
	{
		super(element);
	}

	connectedCallback()
	{
		this.parentNode.style.overflow = "hidden";
		this.appendChild(document.createElement("g-overflow"))
			.innerHTML = "Mais<i>&#X3017;</i>";
	}

	disconnectedCallback()
	{
		this.parentNode.style.overflow = "";
	}
}

customElements.define('g-tabbar', TabBar);