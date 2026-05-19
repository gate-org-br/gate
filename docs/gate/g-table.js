let template = document.createElement("template");
template.innerHTML = `
<style data-element="g-table">table[data-table-size="0"][data-empty] {
	display: flex;
	padding: 12px;
	font-size: 16px;
	border: 1px solid;
	text-align: justify;
	align-items: stretch;
	border-left: 6px solid;
	border-radius: 0 3px 3px 0;
	background-color: var(--main1);
	border-color: var(--main3, #DDDDDD);
}

table[data-table-size="0"][data-empty] * {
	display: none !important;
}

table[data-table-size="0"][data-empty]::before {
	content: attr(data-empty);
}

table > thead > tr > th[data-sortable] {
	cursor: pointer;
	color: var(--text1);
}

table > thead > tr > th[data-sortable]::before {
	opacity: 0.2;
	font-size: 16px;
	content: "\\2195 ";
	font-family: monospace;
	color: var(--text1, #000000);
}

table > thead > tr > th[data-sortable="A"]::before {
	opacity: 1;
	content: "\\2191 ";
}

table > thead > tr > th[data-sortable="D"]::before {
	opacity: 1;
	content: "\\2193 ";

}

table > tbody > tr[hidden] {
	display: none;
}</style>`;
/* global template */

import colorize from './colorize.js';
import EventHandler from './event-handler.js';
import MutationListener from './mutation-listener.js';
import Objects from './objects.js';

const sheet = new CSSStyleSheet();
sheet.replaceSync(template.content.querySelector("style").textContent);
document.adoptedStyleSheets = [...document.adoptedStyleSheets, sheet];

const listener = new MutationListener(mutations =>
{
	new Set(
		mutations
			.map(m => m.target.closest?.("table"))
			.filter(Boolean)
	).forEach(update);
});

listener.listen(document);

window.addEventListener("connected", event =>
{
	const table = event.target;
	if (table.tagName !== "TABLE")
		return;

	const root = table.getRootNode();
	if (root instanceof ShadowRoot)
		listener.listen(root);

	update(table);
});

function update(table)
{
	listener.pause();

	try
	{
		if (table.hasAttribute("data-sorted"))
		{
			let [index, order] = table.getAttribute("data-sorted").split(":");
			index = Number(index);

			Array.from(table.querySelectorAll("tbody")).forEach(body =>
			{
				const placeholder = document.createElement("div");
				body.replaceWith(placeholder);
				sort(body, index, order);
				placeholder.replaceWith(body);
			});
		}

		let rows = Array.from(table.querySelectorAll("tbody > tr"));

		let criteria = table.getAttribute("data-filter");
		let columns = Array.from(table.querySelectorAll("col"))
			.map(e => e.getAttribute("data-filter"));

		filter(rows, criteria, ...columns);
		colorize(rows);

		rows = rows.filter(e => !e.hasAttribute("hidden"));
		table.setAttribute("data-table-size", rows.length);
		table.querySelectorAll("[data-table-size]")
			.forEach(e => e.textContent = rows.length);
	} finally
	{
		listener.resume();
	}
}

function sort(element, index, order)
{
	const children = Array.from(element.children);

	children.sort((e1, e2) =>
	{
		const c1 = e1.children[index];
		const c2 = e2.children[index];

		const s1 = c1.hasAttribute("data-value")
			? Number(c1.getAttribute("data-value"))
			: c1.textContent.trim();

		const s2 = c2.hasAttribute("data-value")
			? Number(c2.getAttribute("data-value"))
			: c2.textContent.trim();

		return order === "A"
			? Objects.compare(s1, s2)
			: Objects.compare(s2, s1);
	});

	children.forEach(e => element.appendChild(e));
}

function filter(elements, value, ...columns)
{
	value = value ? value.toUpperCase().trim() : "";

	elements.forEach(element =>
	{
		if (value && !element.textContent.toUpperCase().includes(value))
			return element.setAttribute("hidden", "hidden");

		for (let i = 0; i < columns.length; i++)
		{
			if (!columns[i])
				continue;

			const column = columns[i].trim().toUpperCase();
			const cell = element.children[i];

			if (cell.hasAttribute("data-filter:value"))
			{
				if (cell.getAttribute("data-filter:value").trim().toUpperCase() !== column)
					return element.setAttribute("hidden", "hidden");
			} else if (!cell.textContent.trim().toUpperCase().includes(column))
				return element.setAttribute("hidden", "hidden");
		}

		element.removeAttribute("hidden");
	});
}

window.addEventListener("click", event =>
{
	const target = event.composedPath()
		.find(e => e.matches?.("table > thead > tr > th[data-sortable]"));

	if (!target)
		return;

	Array.from(target.parentNode.children)
		.filter(e => e.hasAttribute("data-sortable"))
		.forEach(e => e.dataset.sortable = "_");

	const position = Array.prototype.indexOf.call(target.parentNode.children, target);
	const table = target.closest("table");

	if (table.getAttribute("data-sorted")?.endsWith(":A"))
	{
		target.setAttribute("data-sortable", "D");
		table.setAttribute("data-sorted", `${position}:D`);
	} else
	{
		target.setAttribute("data-sortable", "A");
		table.setAttribute("data-sorted", `${position}:A`);
	}
});

window.addEventListener("mouseover", event =>
{
	const target = event.target.closest("tr[tabindex]");
	if (target)
		target.focus();
});

window.addEventListener("keydown", event =>
{
	const target = event.target.closest("tr[tabindex]");
	if (!target)
		return;

	switch (event.key)
	{
		case "Enter":
			target.click();
			EventHandler.cancel(event);
			break;
		case "Home":
			EventHandler.cancel(event);
			target.parentNode.firstElementChild?.focus();
			break;
		case "End":
			EventHandler.cancel(event);
			target.parentNode.lastElementChild?.focus();
			break;
		case "ArrowUp":
			EventHandler.cancel(event);
			target.previousElementSibling?.focus();
			break;
		case "ArrowDown":
			EventHandler.cancel(event);
			target.nextElementSibling?.focus();
			break;
	}
});