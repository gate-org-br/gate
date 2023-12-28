import DOM from './dom.js';
import Objects from './objects.js';
import colorize from './colorize.js';

export default function sort(element, index, order)
{
	let children = Array.from(element.children);
	children.sort(function (e1, e2)
	{
		e1 = e1.children[index];
		let s1 = e1.innerHTML.trim();
		if (e1.hasAttribute("data-value"))
			s1 = e1.getAttribute("data-value").trim() ?
				Number(e1.getAttribute("data-value")) : null;

		e2 = e2.children[index];
		let s2 = e2.innerHTML.trim();
		if (e2.hasAttribute("data-value"))
			s2 = e2.getAttribute("data-value").trim() ?
				Number(e2.getAttribute("data-value")) : null;

		if (order === "A")
			return Objects.compare(s1, s2);
		else if (order === "D")
			return Objects.compare(s2, s1);
	});
	for (let i = 0; i < children.length; i++)
		element.appendChild(children[i]);
	colorize(element);
}

function update()
{
	DOM.traverse(document,
		element => element.hasAttribute("data-sorted")
			&& !element.hasAttribute("data-sorting"),
		element =>
	{
		element.setAttribute("data-sorting", "true");

		let replacement = document.createElement("div");
		element.replaceWith(replacement);

		let value = element.getAttribute("data-sorted").split(":");
		let index = Number(value[0]);
		let order = value[1];

		sort(element, index, order);

		replacement.replaceWith(element);
		setTimeout(() => element.removeAttribute("data-sorting", 0));
	});
}

let observer = new MutationObserver(function (mutations)
{
	if (mutations.addedNodes)
		mutations.addedNodes
			.filter(node => node instanceof Element && node.shadowRoot)
			.forEach(node => observer.observe(node.shadowRoot,
					{childList: true,
						subtree: true,
						attributes: true,
						attributeFilter: ['data-sorted']}));
	update();
});

observer.observe(document,
	{childList: true,
		subtree: true,
		attributes: true,
		attributeFilter: ['data-sorted']});

update();

window.addEventListener("click", function (event)
{
	let target = event.composedPath()[0] || event.target;
	target = event.target.closest("th");

	if (target && target.hasAttribute("data-sortable"))
	{
		Array.from(target.parentNode.children)
			.filter(e => e.hasAttribute("data-sortable"))
			.forEach(e => e.dataset.sortable = "_");

		let position = Array.prototype
			.indexOf.call(target.parentNode.children, target);

		let tbody = target.closest("table").querySelector("tbody");
		if (tbody.hasAttribute("data-sorted") &&
			tbody.getAttribute("data-sorted").endsWith(":A"))
		{
			target.setAttribute("data-sortable", "D");
			tbody.setAttribute("data-sorted", `${position}:D`);
		} else
		{
			target.setAttribute("data-sortable", "A");
			tbody.setAttribute("data-sorted", `${position}:A`);
		}
	}
});