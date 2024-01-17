import DOM from './dom.js';

DOM.forEveryElement(e => e.tagName === "UL" && e.hasAttribute("data-treeview"), function (ul)
{
	Array.from(ul.querySelectorAll("li")).forEach(li =>
	{
		if (li.querySelector("ul"))
		{
			li.addEventListener("click", event =>
			{
				event.stopPropagation();
				if (li.hasAttribute('data-expanded'))
				{
					li.removeAttribute("data-expanded");
					Array.from(li.getElementsByTagName("li"))
						.forEach(e => e.removeAttribute("data-expanded"));
				} else
					li.setAttribute("data-expanded", "data-expanded");
			});
		} else
		{
			li.setAttribute("data-empty", "data-empty");
			li.addEventListener("click", event => event.stopPropagation());
		}
	});
});
