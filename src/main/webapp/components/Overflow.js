/* global customElements, Proxy */

class Overflow extends Command
{
	constructor()
	{
		super();
		window.addEventListener("load", () => this.update());
		window.addEventListener("resize", () => this.update());
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

	static disable(element)
	{
		element.style.overflow = "hidden";
		Array.from(element.children).forEach(e => Overflow.disable(e));
		window.top.document.documentElement.removeEventListener("touchmove", Overflow.PREVENT_BODY_SCROLL, false);
	}

	static enable(element)
	{
		element.style.overflow = "";
		Array.from(element.children).forEach(e => Overflow.enable(e));
		window.top.document.documentElement.removeEventListener("touchmove", Overflow.PREVENT_BODY_SCROLL, false);
	}

	static determineOverflow(container)
	{
		if (!container.firstElementChild)
			return "none";

		var containerMetrics = container.getBoundingClientRect();
		var containerMetricsRight = Math.floor(containerMetrics.right);
		var containerMetricsLeft = Math.floor(containerMetrics.left);

		var left = Math.floor(container.firstElementChild.getBoundingClientRect().left);
		var right = Math.floor(container.lastElementChild.getBoundingClientRect().right);


		if (containerMetricsLeft > left
			&& containerMetricsRight < right)
			return "both";
		else if (left < containerMetricsLeft)
			return "left";
		else if (right > containerMetricsRight)
			return "right";
		else
			return "none";
	}
}

customElements.define('g-overflow', Overflow);

Overflow.PREVENT_BODY_SCROLL = e => e.preventDefault();

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