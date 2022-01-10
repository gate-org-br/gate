import colorize from './colorize.mjs';

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

function register(table)
{
	Array.from(table.children)
		.filter(e => e.tagName.toLowerCase() === "tbody")
		.forEach(function (tbody)
		{
			Array.from(tbody.children).forEach(function (tr)
			{
				if (depth(tr) === 0)
					tr.style.display = 'table-row';
				if (tr.nextElementSibling && depth(tr) < depth(tr.nextElementSibling))
				{
					tr.children[0].innerHTML = '+';
					tr.children[0].style.cursor = 'pointer';
					tr.children[0].onclick = function (e)
					{
						e = e ? e : window.event;
						if (e.stopPropagation)
							e.stopPropagation();
						else
							e.cancelBubble = true;

						if (this.innerHTML === '+')
							expands(this.parentNode);
						else if (this.innerHTML === '-')
							colapse(this.parentNode);

						colorize(table);
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
		});

	var selector = Array.from(table.children)
		.filter(e => e.tagName.toLowerCase() === "thead")
		.map(e => e.children[0])
		.map(e => e.children[0])[0];
	selector.innerHTML = "+";
	selector.style.cursor = "pointer";

	selector.onclick = function (e)
	{
		e = e ? e : window.event;
		if (e.stopPropagation)
			e.stopPropagation();
		else
			e.cancelBubble = true;

		switch (this.innerHTML)
		{
			case '+':
				this.innerHTML = '-';
				Array.from(table.children).filter(e => e.tagName.toLowerCase() === "tbody").forEach(function (tbody)
				{
					Array.from(tbody.children).forEach(e => expands(e));
				});

				break;
			case '-':
				this.innerHTML = '+';
				Array.from(table.children).filter(e => e.tagName.toLowerCase() === "tbody").forEach(function (tbody)
				{
					Array.from(tbody.children).forEach(e => colapse(e));
				});

				break;
		}
		colorize(table);
	};

	colorize(table);
}

Array.from(document.querySelectorAll('table.TreeView, table.TREEVIEW')).forEach(e => register(e));

Array.from(document.querySelectorAll("ul.TreeView li")).forEach(li =>
	{
		if (li.querySelector("ul"))
		{
			li.addEventListener("click", event =>
			{
				event.stopPropagation();
				if (li.hasAttribute('data-expanded'))
				{
					li.removeAttribute("data-expanded")
					Array.from(li.getElementsByTagName("li"))
						.forEach(e => e.removeAttribute("data-expanded"));
				} else
					li.setAttribute("data-expanded", "data-expanded");
			});
		} else
		{
			li.setAttribute("data-empty", "data-empty");
			li.addEventListener("click", event => event.stopPropagation());
		}
	});