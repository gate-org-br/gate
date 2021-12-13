/* global customElements */


customElements.define('g-coolbar', class extends GOverflow
{
	constructor()
	{
		super();
		this.container.style.flexDirection = "row-reverse";
	}
});

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