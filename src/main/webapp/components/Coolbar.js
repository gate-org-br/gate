
/* global customElements */

class Coolbar extends HTMLElement
{
	constructor()
	{
		super();

	}

	distribute()
	{
		this.lastElementChild.style.display = "none";
		Array.from(this.lastElementChild.children)
			.forEach(e => this.firstElementChild.appendChild(e));
		while (this.firstElementChild.lastElementChild
			&& (this.firstElementChild.scrollWidth > this.firstElementChild.clientWidth
				|| this.firstElementChild.scrollHeight > this.firstElementChild.clientHeight))
		{
			this.lastElementChild.appendChild(this.firstElementChild.lastElementChild);
			this.lastElementChild.style.display = "flex";
		}


	}

	connectedCallback()
	{
		var children = Array.from(this.children);
		var links = this.appendChild(document.createElement("div"));
		children.forEach(e => links.appendChild(e));

		var link = this.appendChild(document.createElement("a"));
		link.innerHTML = "&#X3018;"

		link.addEventListener("click", () => this.appendChild(new CoolbarDialog(Array.from(link.children))));

		window.addEventListener("load", () => this.distribute());
		window.addEventListener("resize", () => this.distribute());
	}
}

customElements.define('g-coolbar', Coolbar);
