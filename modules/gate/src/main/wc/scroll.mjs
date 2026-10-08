const PREVENT_BODY_SCROLL = e => e.preventDefault();

export default class Scroll
{
	static disable(element)
	{
		element.setAttribute("data-scroll-disabled", "data-scroll-disabled");
		Array.from(element.children).forEach(e => Scroll.disable(e));
		window.top.document.documentElement.addEventListener("touchmove", PREVENT_BODY_SCROLL, false);
	}

	static enable(element)
	{
		element.removeAttribute("data-scroll-disabled");
		Array.from(element.children).forEach(e => Scroll.enable(e));
		window.top.document.documentElement.removeEventListener("touchmove", PREVENT_BODY_SCROLL, false);
	}
}