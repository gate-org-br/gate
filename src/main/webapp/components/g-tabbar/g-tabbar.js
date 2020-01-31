/* global customElements, Overflow, Proxy */

customElements.define('g-tabbar', class extends HTMLElement
{
	constructor()
	{
		super();

		let overflow = new Command();
		overflow.innerText = "Mais";
		overflow.addEventListener("click", () =>
		{
			var elements = Array.from(this.children)
				.filter(e => e !== overflow)
				.filter(e => e.style.display === "none")
				.map(element => Proxy.create(element));
			elements.forEach(e => e.style.display = "");
			document.documentElement.appendChild(new SideMenu(elements))
				.show(overflow);
		});

		overflow.update = () =>
		{
			Array.from(this.children).forEach(e => e.style.display = "flex");

			overflow.style.display = "none";
			if (Overflow.isOverflowed(this))
				overflow.style.display = "flex";

			for (let element = overflow.previousElementSibling;
				element;
				element = element.previousElementSibling)
				if (Overflow.isOverflowed(this))
					if (!element.hasAttribute("aria-selected"))
						element.style.display = "none";
		};

		window.addEventListener("load", () =>
		{
			this.parentNode.style.overflow = "hidden";

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

			if (this.querySelector("* > i"))
				overflow.appendChild(document.createElement("i"))
					.innerHTML = "&#X3017;";
			this.appendChild(overflow);
			overflow.update();
		});

		window.top.addEventListener("resize", () => overflow.update());
	}
});