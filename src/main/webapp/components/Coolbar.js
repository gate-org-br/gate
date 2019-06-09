
/* global customElements */

class Coolbar extends HTMLElement
{
	constructor()
	{
		super();
	}

	static resize()
	{
		Array.from(document.querySelectorAll('cool-bar, div.COOLBAR, div.Coolbar'))
			.forEach(function (element)
			{
				element.style.visibility = "hidden";
				element.removeAttribute("data-overflow");
				if (element.scrollWidth > element.clientWidth
					|| element.scrollHeight > element.clientHeight)
					element.setAttribute("data-overflow", "true");
				element.style.visibility = "visible";
			});
	}
}

customElements.define('cool-bar', Coolbar);

window.addEventListener("load", () => Coolbar.resize());
window.addEventListener("resize", () => Coolbar.resize());

