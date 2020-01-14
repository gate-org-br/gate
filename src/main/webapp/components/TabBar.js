/* global customElements */

class TabBar extends HTMLElement
{
	constructor(element)
	{
		super(element);
	}

	connectedCallback()
	{
		this.parentNode.style.overflow = "hidden";
		if (!this.getElementsByTagName("g-overflow").length)
			this.appendChild(document.createElement("g-overflow"))
				.innerHTML = "Mais<i>&#X3017;</i>";
	}

	disconnectedCallback()
	{
		this.parentNode.style.overflow = "";
	}
}

window.addEventListener("load", function ()
{
	var parameters = URL.parse_query_string(window.location.href);
	var elements = Array.from(document.querySelectorAll("a, button"))
		.filter(e => (e.href && e.href.includes('?'))
				|| (e.formaction && e.formaction.includes('?')));

	var q = elements.filter(e =>
	{
		var arguments = URL.parse_query_string(e.href || e.formaction);
		return arguments.MODULE === parameters.MODULE
			&& arguments.SCREEN === parameters.SCREEN
			&& arguments.ACTION === parameters.ACTION;
	});

	if (q.length === 0)
	{
		var q = elements.filter(e =>
		{
			var arguments = URL.parse_query_string(e.href || e.formaction);
			return arguments.MODULE === parameters.MODULE
				&& arguments.SCREEN === parameters.SCREEN;
		});

		if (q.length === 0)
		{
			var q = elements.filter(e =>
			{
				var arguments = URL.parse_query_string(e.href || e.formaction);
				return arguments.MODULE === parameters.MODULE;
			});
		}
	}

	if (q.length !== 0)
		q[0].setAttribute("aria-selected", "true");
});

customElements.define('g-tabbar', TabBar);