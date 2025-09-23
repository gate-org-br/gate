let template = document.createElement("template");
template.innerHTML = `
 <style data-element="g-table">table[data-table-size='0'][data-empty] {
	display: flex;
	padding: 12px;
	display: flex;
	font-size: 16px;
	border: 1px solid;
	text-align: justify;
	align-items: stretch;
	border-left: 6px solid;
	border-color: var(--main6);
	border-radius: 0 3px 3px 0;
	background-color: var(--main1);
}

table[data-table-size='0'][data-empty] * {
	display: none !important;
}

table[data-table-size='0'][data-empty]::before {
	content: attr(data-empty);
}

table>thead>tr>th[data-sortable] {
	cursor: pointer;
	color: var(--text1);
}

table>thead>tr>th[data-sortable]::before {
	font-size: 16px;
	content: "\\2195 ";
	font-family: monospace;
	color: rgba(0, 0, 0, 0.2)
}

table>thead>tr>th[data-sortable="A"]::before {
	content: "\\2191 ";
	color: var(--text1);
}

table>thead>tr>th[data-sortable="D"]::before {
	content: "\\2193 ";
	color: var(--text1);

}

table>tbody>tr[hidden] {
	display: none;
}</style>`;
/* global template */

import DOM from './dom.js';
import Objects from './objects.js';
import colorize from './colorize.js';
import EventHandler from './event-handler.js';

const sheet = new CSSStyleSheet();
sheet.replaceSync(template.content.querySelector("style").textContent);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

const observer = new MutationObserver(mutations =>
{
	observer.takeRecords();
	new Set(mutations.map(e => e.target.closest("table")).filter(Boolean))
		.forEach(update);
	observer.takeRecords();
});
observer.observe(document, {childList: true, subtree: true, attributes: true});

window.addEventListener("connected", event =>
{
	if (event.target.tagName === "TABLE"
		&& event.target.getRootNode() instanceof ShadowRoot)
		observer.observe(event.target.getRootNode(),
			{childList: true, subtree: true, attributes: true});
});

function update(table)
{
	if (table.hasAttribute("data-sorted"))
	{
		let value = table.getAttribute("data-sorted").split(":");
		let index = Number(value[0]);
		let order = value[1];

		Array.from(table.querySelectorAll("tbody")).forEach(body =>
		{
			let replacement = document.createElement("div");
			body.replaceWith(replacement);
			sort(body, index, order);
			replacement.replaceWith(body);
		});
	}

	let rows = Array.from(table.querySelectorAll("tbody > tr"));

	let criteria = table.hasAttribute("data-filter") ?
		table.getAttribute("data-filter") : null;
	let columns = Array.from(table.querySelectorAll("col"))
		.map(e => e.hasAttribute("data-filter") ? e.getAttribute("data-filter") : null);
	filter(rows, criteria, ...columns);

	colorize(rows);

	rows = rows.filter(e => !e.hasAttribute("hidden"));
	table.setAttribute("data-table-size", rows.length);
	table.querySelectorAll("[data-table-size]")
		.forEach(e => e.textContent = rows.length);
}


function sort(element, index, order)
{
	let children = Array.from(element.children);
	children.sort(function (e1, e2)
	{
		e1 = e1.children[index];
		let s1 = e1.textContent.trim();
		if (e1.hasAttribute("data-value"))
			s1 = e1.getAttribute("data-value").trim() ?
				Number(e1.getAttribute("data-value")) : null;

		e2 = e2.children[index];
		let s2 = e2.textContent.trim();
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
}

function filter(elements, value, ...columns)
{
	if (!Array.isArray(elements))
		elements = Array.from(elements);

	value = value ? value.toUpperCase().trim() : "";
	elements.forEach(element =>
	{
		if (value && element.textContent.toUpperCase().indexOf(value) === -1)
			return element.setAttribute("hidden", "hidden");

		for (let i = 0; i < columns.length; i++)
		{
			if (columns[i])
			{
				let column = columns[i].trim().toUpperCase();
				if (element.children[i].hasAttribute("data-filter:value"))
				{
					if (element.children[i].getAttribute("data-filter:value").trim().toUpperCase() !== column)
						return element.setAttribute("hidden", "hidden");

				} else if (element.children[i].textContent.trim().toUpperCase().indexOf(column) === -1)
					return element.setAttribute("hidden", "hidden");
			}
		}

		return element.removeAttribute("hidden");
	});
}

window.addEventListener("click", function (event)
{
	const target = event.composedPath()
		.find(e => e.matches && e.matches("table > thead > tr > th")
				&& e.hasAttribute("data-sortable"));
	if (target)
	{
		Array.from(target.parentNode.children)
			.filter(e => e.hasAttribute("data-sortable"))
			.forEach(e => e.dataset.sortable = "_");

		let position = Array.prototype
			.indexOf.call(target.parentNode.children, target);

		const table = target.closest("table");
		if (table.hasAttribute("data-sorted") &&
			table.getAttribute("data-sorted").endsWith(":A"))
		{
			target.setAttribute("data-sortable", "D");
			table.setAttribute("data-sorted", `${position}:D`);
		} else
		{
			target.setAttribute("data-sortable", "A");
			table.setAttribute("data-sorted", `${position}:A`);
		}
	}
});

window.addEventListener("mouseover", function (event)
{
	let target = event.target.closest("tr[tabindex]");
	if (target)
		target.focus();
});

window.addEventListener("keydown", function (event)
{
	let target = event.target.closest("tr[tabindex]");
	if (target)
	{
		switch (event.key)
		{
			case "Enter":
				target.click();
				EventHandler.cancel(event);
				break;
			case "Home":
				EventHandler.cancel(event);
				if (target.parentNode.firstElementChild.hasAttribute("tabindex"))
					target.parentNode.firstElementChild.focus();
				break;
			case "End":
				EventHandler.cancel(event);
				if (target.parentNode.lastElementChild.hasAttribute("tabindex"))
					target.parentNode.lastElementChild.focus();
				break;
			case "ArrowUp":
				EventHandler.cancel(event);
				if (target.previousElementSibling &&
					target.previousElementSibling.hasAttribute("tabindex"))
					target.previousElementSibling.focus();
				break;
			case "ArrowDown":
				EventHandler.cancel(event);
				if (target.nextElementSibling &&
					target.nextElementSibling.hasAttribute("tabindex"))
					target.nextElementSibling.focus();
				break;
		}
	}
});