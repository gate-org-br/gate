/* global customElements, Overflow */

class GScrollTabBar extends HTMLElement
{
	constructor()
	{
		super();
		this.addEventListener("mouseenter", () => this.style.overflowX = "auto");
		this.addEventListener("mouseleave", () => this.style.overflowX = "hidden");
		this.addEventListener("touchstart", () => this.style.overflowX = "auto");
		this.addEventListener("touchend", () => this.style.overflowX = "hidden");
		this.addEventListener("touchmove", e => this.style.overflowX = this.contains(e.target) ? "auto" : "hidden");

		this.addEventListener("scroll", () => this.setAttribute("data-overflowing",
				Overflow.determineOverflow(this)));
	}

	connectedCallback()
	{
		var parameters = URL.parse_query_string(window.location.href);
		var elements = Array.from(this.children).filter(e => (e.href && e.href.includes('?'))
				|| (e.formaction && e.formaction.includes('?')));

		var q = elements.filter(e =>
		{
			var args = URL.parse_query_string(e.href || e.formaction);
			return args.MODULE === parameters.MODULE
				&& args.SCREEN === parameters.SCREEN
				&& args.ACTION === parameters.ACTION;
		});

		if (q.length === 0)
		{
			var q = elements.filter(e =>
			{
				var args = URL.parse_query_string(e.href || e.formaction);
				return args.MODULE === parameters.MODULE
					&& args.SCREEN === parameters.SCREEN;
			});

			if (q.length === 0)
			{
				var q = elements.filter(e =>
				{
					var args = URL.parse_query_string(e.href || e.formaction);
					return args.MODULE === parameters.MODULE;
				});
			}
		}

		if (q.length !== 0)
			q[0].setAttribute("aria-selected", "true");

		window.addEventListener("load", () =>
		{
			this.setAttribute("data-overflowing", Overflow.determineOverflow(this));
			Array.from(this.children).filter(e => e.getAttribute("aria-selected"))
				.forEach(e => e.scrollIntoView({inline: "center", block: "nearest"}));
		});
	}
}

customElements.define("g-scroll-tabbar", GScrollTabBar);