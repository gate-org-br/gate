window.addEventListener("load", () => Array.from(document.all)
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true")));

window.addEventListener("resize", () => {
	Array.from(document.all)
		.filter(e => e.hasAtribute("data-overflow"))
		.forEach(e => e.removeAttribute("data-overflow"));

	Array.from(document.all)
		.filter(e => e.scrollWidth > e.clientWidth
				|| e.scrollHeight > e.clientHeight)
		.forEach(e => e.setAttribute("data-overflow", "true"));
});
