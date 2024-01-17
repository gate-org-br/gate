/* global Objects */

import Objects from './objects.js';
import colorize from './colorize.js';

function sort(element, index, order)
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
}

function filter(elements, value)
{
	if (!Array.isArray(elements))
		elements = Array.from(elements);
	value = value ? value.toUpperCase() : "";
	elements.forEach(row => row.style.display =
			row.innerHTML.toUpperCase().indexOf(value) !== -1 ? "" : "none");
}

customElements.define('g-table', class extends HTMLTableElement
{
	#observer;

	constructor()
	{
		super();
		this.#observer = new MutationObserver(() => this.update(this));
	}

	update()
	{
		this.#observer.disconnect();

		let body = this.querySelector("tbody");
		const size = body ? body.children.length : 0;
		this.setAttribute("data-table-size", size);
		this.querySelectorAll("[data-table-size]").forEach(e => e.innerText = size);

		if (body.hasAttribute("data-sorted"))
		{
			let replacement = document.createElement("div");
			body.replaceWith(replacement);

			let value = body.getAttribute("data-sorted").split(":");
			let index = Number(value[0]);
			let order = value[1];

			sort(body, index, order);
			replacement.replaceWith(body);
		}


		if (body.hasAttribute("data-filter"))
			filter(body.children, body.getAttribute("data-filter"));

		colorize(body.children);

		this.#observer.observe(body, {childList: true, subtree: true,
			attributes: true,
			attributeFilter: ['data-sorted', 'data-filter']});
	}

	connectedCallback()
	{
		let body = this.querySelector("tbody");
		Array.from(this.querySelectorAll("th[data-sortable]")).forEach(target =>
		{
			target.addEventListener("click", () =>
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

			});
		});

		Array.from(this.querySelectorAll("input[data-filter]"))
			.forEach(target => target.addEventListener("input",
					() => body.setAttribute("data-filter", target.value)));

		this.#observer.observe(body, {childList: true, subtree: true,
			attributes: true,
			attributeFilter: ['data-sorted', 'data-filter']});
		this.update(this);
	}
}, {extends: 'table'});


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
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				break;

			case "Home":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.parentNode.firstElementChild.hasAttribute("tabindex"))
					target.parentNode.firstElementChild.focus();

				break;
			case "End":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.parentNode.lastElementChild.hasAttribute("tabindex"))
					target.parentNode.lastElementChild.focus();
				break;
			case "ArrowUp":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.previousElementSibling &&
					target.previousElementSibling.hasAttribute("tabindex"))
					target.previousElementSibling.focus();
				break;
			case "ArrowDown":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.nextElementSibling &&
					target.nextElementSibling.hasAttribute("tabindex"))
					target.nextElementSibling.focus();
				break;
		}
	}
});