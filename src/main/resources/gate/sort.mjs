import Objects from './objects.mjs';
import colorize from './colorize.mjs';

export default function sort(parent, index, mode)
{
	var children = Array.from(parent.children);
	children.sort(function (e1, e2)
	{
		e1 = e1.children[index];
		var s1 = e1.innerHTML.trim();
		if (e1.hasAttribute("data-value"))
			s1 = e1.getAttribute("data-value").trim() ?
				Number(e1.getAttribute("data-value")) : null;

		e2 = e2.children[index];
		var s2 = e2.innerHTML.trim();
		if (e2.hasAttribute("data-value"))
			s2 = e2.getAttribute("data-value").trim() ?
				Number(e2.getAttribute("data-value")) : null;

		if (mode === "A")
			return Objects.compare(s1, s2);
		else if (mode === "D")
			return Objects.compare(s2, s1);
	});
	for (var i = 0; i < children.length; i++)
		parent.appendChild(children[i]);
}

window.addEventListener("click", event =>
{
	let link = event.target.closest("th");
	if (link && link.hasAttribute("data-sortable"))
	{
		if (!link.getAttribute("data-sortable"))
			link.setAttribute("data-sortable", "N");

		switch (link.getAttribute("data-sortable"))
		{
			case "N":
				Array.from(link.parentNode.children)
					.filter(e => e.hasAttribute("data-sortable"))
					.forEach(e => e.setAttribute("data-sortable", "N"));
				link.setAttribute("data-sortable", "A");
				break;
			case "A":
				link.setAttribute("data-sortable", "D");
				break;
			case "D":
				link.setAttribute("data-sortable", "A");
				break;
		}

		var table = link.closest("TABLE");
		Array.from(table.children)
			.filter(e => e.tagName.toUpperCase() === "TBODY")
			.forEach(e => sort(e, Array.prototype.indexOf
					.call(link.parentNode.children, link),
					link.getAttribute("data-sortable")));
		colorize(table);
	}
});
