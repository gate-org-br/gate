/* global customElements */

import GOverflow from "./g-overflow.mjs";

customElements.define('g-coolbar', class extends GOverflow
{
});

Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
	.filter(e => e.scrollWidth > e.clientWidth
			|| e.scrollHeight > e.clientHeight)
	.forEach(e => e.setAttribute("data-overflow", "true"));

window.addEventListener("resize", () => {
	Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.hasAttribute("data-overflow"))
		.forEach(e => e.removeAttribute("data-overflow"));

	Array.from(document.querySelectorAll("div.Coolbar, div.COOLBAR"))
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true"));
});