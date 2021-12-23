/* global Colorizer */

import colorize from './colorize.mjs';

export default function filter(elements, value)
{
	elements.forEach(row => row.style.display = !value
			|| row.innerHTML.toUpperCase().indexOf(value.toUpperCase()) !== -1 ? "" : "none");
}

function process(input)
{

	let table = input.getAttribute("data-filter")
		? document.getElementById(input.getAttribute("data-filter"))
		: input.closest("TABLE");
	let elements = Array.from(table.children)
		.filter(e => e.tagName === "TBODY")
		.flatMap(e => Array.from(e.children));

	filter(elements, input.value);
	colorize(table);
}

window.addEventListener("input", function (event)
{
	if (event.target.tagName === "INPUT"
		&& event.target.hasAttribute("data-filter"))
		process(event.target);
});

window.addEventListener("changed", function (event)
{
	if (event.target.tagName === "INPUT"
		&& event.target.hasAttribute("data-filter"))
		process(event.target);
});

Array.from(document.querySelectorAll("input[data-filter]")).forEach(e => process(e));