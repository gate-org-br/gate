/* global Colorizer */

function registerTreeView(table)
{
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

						Colorizer.colorize(table);
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
		Colorizer.colorize(table);
	};

	Colorizer.colorize(table);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('table.TreeView, table.TREEVIEW'))
		.forEach(e => registerTreeView(e));
});