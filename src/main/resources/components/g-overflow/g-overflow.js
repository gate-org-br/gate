/* global customElements, Proxy, GSelection */

class GOverflow extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});

		var container = this.shadowRoot.appendChild(document.createElement("div"));
		container.setAttribute("id", "container");
		container.style.width = "auto";
		container.style.flex = "1 1 0px";
		container.style.display = "flex";
		container.style.whiteSpace = "nowrap";

		container.appendChild(document.createElement("slot"));

		var more = container.appendChild(document.createElement("a"));
		more.setAttribute("id", "more");

		more.href = "#";
		more.innerHTML = "&#X3018;";
		more.style.padding = "0";
		more.style.width = "32px";
		more.style.flexGrow = "0";
		more.style.height = "100%";
		more.style.outline = "none";
		more.style.display = "none";
		more.style.flexShrink = "0";
		more.style.fontSize = "20px";
		more.style.color = "inherit";
		more.style.cursor = "pointer";
		more.style.fontFamily = "gate";
		more.style.marginRight = "auto";
		more.style.alignItems = "center";
		more.style.textDecoration = "none";
		more.style.justifyContent = "center";

		more.addEventListener("click", () =>
		{
			let elements = Array.from(this.children)
				.filter(e => e.tagName !== "HR")
				.filter(e => !e.getAttribute("hidden"))
				.filter(e => e.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");
			document.documentElement.appendChild(new GSideMenu(elements)).show(more);
		});
	}

	get container()
	{
		return this.shadowRoot.getElementById("container");
	}

	get more()
	{
		return this.shadowRoot.getElementById("more");
	}

	update()
	{
		let selected = GSelection.getSelectedLink(this.children);
		if (selected)
			selected.setAttribute("aria-selected", "true");

		Array.from(this.children).forEach(e => e.style.display = "");

		this.more.style.display = this.container.clientWidth > this.clientWidth ? "flex" : "none";

		for (let e = this.lastElementChild; e; e = e.previousElementSibling)
			if (this.container.clientWidth > this.clientWidth)
				if (!e.hasAttribute("aria-selected")
					&& !e.getAttribute("hidden"))
					e.style.display = "none";
	}

	connectedCallback()
	{
		this.update();
		window.addEventListener("resize", () => this.update());
	}

	static isOverflowed(element)
	{
		return element.scrollWidth > element.clientWidth
			|| element.scrollHeight > element.clientHeight;
	}

	static disable(element)
	{
		element.setAttribute("data-scroll-disabled", "data-scroll-disabled");
		Array.from(element.children).forEach(e => GOverflow.disable(e));
		window.top.document.documentElement.addEventListener("touchmove", GOverflow.PREVENT_BODY_SCROLL, false);
	}

	static enable(element)
	{
		element.removeAttribute("data-scroll-disabled");
		Array.from(element.children).forEach(e => GOverflow.enable(e));
		window.top.document.documentElement.removeEventListener("touchmove", GOverflow.PREVENT_BODY_SCROLL, false);
	}

	static determineOverflow(component, container)
	{
		container = container || component;

		if (!component.firstElementChild)
			return "none";

		var containerMetrics = container.getBoundingClientRect();
		var containerMetricsRight = Math.floor(containerMetrics.right);
		var containerMetricsLeft = Math.floor(containerMetrics.left);

		var left = Math.floor(component.firstElementChild.getBoundingClientRect().left);
		var right = Math.floor(component.lastElementChild.getBoundingClientRect().right);

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

window.addEventListener("load", () => customElements.define('g-overflow', GOverflow));

GOverflow.PREVENT_BODY_SCROLL = e => e.preventDefault();

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