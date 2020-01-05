/* global customElements, Proxy */

class Overflow extends Command
{
	constructor()
	{
		super();

		this.addEventListener("click", () =>
		{
			var elements = Array.from(this.parentNode.children)
				.filter(element => element.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");

			var menu = this.appendChild(new SideMenu(elements));
			var center = this.getBoundingClientRect();
			center = center.left + (center.width / 2);

			if (center <= window.innerWidth / 2)
				menu.showL();
			else
				menu.showR();
		});
	}

	connectedCallback()
	{
		super.connectedCallback();
		window.addEventListener("load", () => this.update());
		window.addEventListener("resize", () => this.update());
	}

	update()
	{
		this.style.display = "none";

		Array.from(this.parentNode.children)
			.filter(e => e !== this)
			.forEach(e => e.style.display = "");

		if (Overflow.isOverflowed(this.parentNode))
			this.style.display = "flex";

		for (var element = this.previousElementSibling;
			element;
			element = element.previousElementSibling)
			if (Overflow.isOverflowed(this.parentNode))
				if (!element.hasAttribute("aria-selected"))
					element.style.display = "none";


		for (var element = this.nextElementSibling;
			element;
			element = element.nextElementSibling)
			if (Overflow.isOverflowed(this.parentNode))
				if (!element.hasAttribute("aria-selected"))
					element.style.display = "none";
	}

	static isOverflowed(element)
	{
		return element.scrollWidth > element.clientWidth
			|| element.scrollHeight > element.clientHeight;
	}
}


customElements.define('g-overflow', Overflow);



window.addEventListener("load", () => Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true")));

window.addEventListener("resize", () => {
	Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.hasAttribute("data-overflow"))
		.forEach(e => e.removeAttribute("data-overflow"));

	Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true"));
});



window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a"))
		.filter(e => window.location.href.endsWith(e.href))
		.forEach(e => e.setAttribute("aria-selected", "true"));

	Array.from(document.querySelectorAll("button"))
		.filter(e => window.location.href.endsWith(e.formaction))
		.forEach(e => e.setAttribute("aria-selected", "true"));
});
