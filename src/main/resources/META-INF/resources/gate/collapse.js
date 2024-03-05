import DOM from './dom.js';

function checkOverflow(table)
{
	table.removeAttribute('data-overflowing');

	let style = window.getComputedStyle(table.parentNode);
	let width = table.offsetWidth + parseInt(style.paddingLeft)
		+ parseInt(style.paddingRight);

	if (width > table.parentNode.offsetWidth)
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
