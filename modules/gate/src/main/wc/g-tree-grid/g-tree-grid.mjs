import colorize from './colorize.js';

function depth(tr)
{
	return parseInt(tr.getAttribute("data-depth"));
}

function expands(tr)
{
	if (tr.children[0].innerHTML === '+')
		tr.children[0].innerHTML = '-';
	for (var next = tr.nextElementSibling;
		next && depth(next) > depth(tr);
		next = next.nextElementSibling)
		if (depth(tr) === depth(next) - 1)
			next.style.display = 'table-row';
}

function colapse(tr)
{
	if (tr.children[0].innerHTML === '-')
		tr.children[0].innerHTML = '+';

	for (var next = tr.nextElementSibling;
		next && depth(next) > depth(tr);
		next = next.nextElementSibling)
	{
		next.style.display = 'none';
		if (next.children[0].innerHTML === '-')
			next.children[0].innerHTML = '+';
	}
}

function parent(tr)
{
	for (var p = tr; p; p = p.previousSibling)
		if (depth(p) < depth(tr))
			return p;
}

customElements.define('g-tree-grid', class extends HTMLTableElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		let rows = Array.from(this.children)
			.filter(e => e.tagName === "TBODY")
			.flatMap(e => Array.from(e.children));

		Array.from(this.children)
			.filter(e => e.tagName === "THEAD")
			.map(e => e.children[0])
			.map(e => e.insertBefore(document.createElement("th"), e.firstElementChild))
			.forEach(selector =>
			{
				selector.innerHTML = "+";
				selector.style.width = "50px";
				selector.style.fontSize = "12px";
				selector.style.cursor = "pointer";
				selector.style.fontWeight = "bold";
				selector.style.textAlign = "center";

				selector.onclick = function (event)
				{
					event.preventDefault();
					event.stopPropagation();
					if (this.innerHTML === '+')
					{
						this.innerHTML = '-';
						rows.forEach(expands);
					} else if (this.innerHTML === '-')
					{
						this.innerHTML = '+';
						rows.forEach(colapse);
					}

					colorize(rows);
				};
			});

		rows.forEach(tr =>
		{
			let selector = tr.insertBefore(document.createElement("td"), tr.firstElementChild);
			selector.innerHTML = '+';
			selector.style.width = "50px";
			selector.style.fontSize = "12px";
			selector.style.cursor = "pointer";
			selector.style.fontWeight = "bold";
			selector.style.textAlign = "center";


			if (depth(tr) !== 0)
				tr.style.display = 'none';

			if (tr.children[1])
				tr.children[1].style.paddingLeft = (depth(tr) * 40) + "px";

			if (tr.nextElementSibling && depth(tr) < depth(tr.nextElementSibling))
			{
				selector.onclick = function (event)
				{
					event.preventDefault();
					event.stopPropagation();

					if (this.innerHTML === '+')
						expands(this.parentNode);
					else if (this.innerHTML === '-')
						colapse(this.parentNode);

					colorize(rows);
				};
			} else
				tr.children[0].innerHTML = ' ';

			if (tr.getAttribute("data-expanded"))
			{
				for (var p = tr; p; p = parent(p))
					expands(p);
				setTimeout(() => tr.scrollIntoView(), 0);
			}
		});

		colorize(rows);
	}

}, {extends: 'table'});