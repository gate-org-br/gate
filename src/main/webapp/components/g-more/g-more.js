/* global GOverflow, Proxy, customElements */

class GMore extends GCommand
{
	constructor()
	{
		super();

		window.top.addEventListener("resize", () => this.update());

		this.addEventListener("click", () =>
		{
			let elements = Array.from(this.parentNode.children)
				.filter(e => e !== this)
				.filter(e => e.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");
			document.documentElement.appendChild(new SideMenu(elements)).show(this);
		});
	}

	update()
	{
		Array.from(this.parentNode.children)
			.forEach(e => e.style.display = "flex");

		this.style.display = "none";
		if (this.parentNode.container.clientWidth > this.parentNode.clientWidth)
			this.style.display = "flex";

		for (let element = this.previousElementSibling;
			element;
			element = element.previousElementSibling)
			if (this.parentNode.container.clientWidth > this.parentNode.clientWidth)
				if (!element.hasAttribute("aria-selected"))
					element.style.display = "none";
	}

	connectedCallback()
	{
		super.connectedCallback();
		this.update();
	}
}


customElements.define('g-more', GMore);