/* global customElements, Proxy, GSelection */

class GOverflow extends HTMLElement
{
	constructor()
	{
		super();
		this._private = {more: new GMore(),
			container: document.createElement("div")};

		this.attachShadow({mode: 'open'});
		this.container.style.width = "auto";
		this.container.style.flexGrow = "1";
		this.container.style.display = "flex";
		this.container.style.whiteSpace = "nowrap";
		this.shadowRoot.appendChild(this.container);
		this.container.appendChild(document.createElement("slot"));
	}

	get more()
	{
		return this._private.more;
	}

	get container()
	{
		return this._private.container;
	}

	connectedCallback()
	{
		window.addEventListener("load", () => this.appendChild(this.more));
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

customElements.define('g-overflow', GOverflow);

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