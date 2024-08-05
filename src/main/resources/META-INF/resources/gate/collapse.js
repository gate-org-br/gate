import DOM from './dom.js';

function checkOverflow(table)
{
	table.removeAttribute('data-overflowing');
	if (table.scrollWidth > table.clientWidth
		|| Array.from(table.querySelectorAll("td, th"))
		.some(e => e.scrollWidth > e.clientWidth))
		table.setAttribute('data-overflowing', 'true');
}

window.addEventListener("resize", () =>
	DOM.traverse(document, e => e.tagName === "TABLE"
			&& e.hasAttribute("data-collapse"), table => checkOverflow(table)));

window.addEventListener("connected", event =>
	{
		if (event.target.tagName === "TABLE"
			&& event.target.hasAttribute('data-collapse'))
			checkOverflow(event.target);
	});
