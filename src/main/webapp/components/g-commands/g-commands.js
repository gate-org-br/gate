/* global customElements, Overflow, Proxy */

class Commands extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		let overflow = new Command();
		overflow.innerHTML = "<i>&#X3018;</i>";

		overflow.addEventListener("click", () =>
		{
			var elements = Array.from(this.children)
				.filter(e => e !== overflow)
				.filter(e => e.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");
			document.documentElement.appendChild(new SideMenu(elements))
				.show(overflow);
		});

		overflow.update = () =>
		{
			Array.from(this.children).forEach(e => e.style.display = "flex");

			overflow.style.display = "none";
			if (Overflow.isOverflowed(this))
				overflow.style.display = "flex";

			for (let element = overflow.previousElementSibling;
				element;
				element = element.previousElementSibling)
				if (Overflow.isOverflowed(this))
					if (!element.hasAttribute("aria-selected"))
						element.style.display = "none";
		};

		this.parentNode.style.overflow = "hidden";
		this.appendChild(overflow);
		overflow.update();

		window.top.addEventListener("load", () => overflow.update());
		window.top.addEventListener("resize", () => overflow.update());
	}
}

customElements.define('g-commands', Commands);