import DOM from './dom.js';
import colorize from './colorize.js';

export default function filter(elements, value)
{
	if (!Array.isArray(elements))
		elements = Array.from(elements);
	value = value ? value.toUpperCase() : "";
	elements.forEach(row => row.style.display =
			row.innerHTML.toUpperCase().indexOf(value) !== -1 ? "" : "none");
}

function update()
{
	DOM.traverse(document,
		element => element.tagName === "TBODY"
			&& element.hasAttribute("data-filter"),
		element =>
	{
		filter(element.children,
			element.getAttribute("data-filter"));
		colorize(element);
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
						attributeFilter: ['data-filter']}));
	update();
});

observer.observe(document,
	{childList: true,
		subtree: true,
		attributes: true,
		attributeFilter: ['data-filter']});

update();

window.addEventListener("input", function (event)
{
	if (event.target.tagName === "INPUT"
		&& event.target.hasAttribute("data-filter"))
	{
		let tbody = event.target.getAttribute("data-filter")
			? document.getElementById(event.target.dataset.filter)
			: event.target.closest("TABLE").querySelector("TBODY");
		tbody.setAttribute("data-filter", event.target.value);
	}

});

window.addEventListener("changed", function (event)
{
	if (event.target.tagName === "INPUT"
		&& event.target.hasAttribute("data-filter"))
	{
		let tbody = event.target.getAttribute("data-filter")
			? document.getElementById(event.target.dataset.filter)
			: event.target.closest("TABLE").querySelector("TBODY");
		tbody.setAttribute("data-filter", event.target.value);
	}
});