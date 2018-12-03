function TreeView(table)
{
	var trs = $($(table).children("tbody")).children("tr");
	var sel = $($($(table).children("thead")).children("tr")).children("th")[0];

	trs.forEach(function (tr)
	{
		tr.selector = $(tr).children("td")[0];
		if (parseInt(tr.getAttribute('data-depth')) === 0)
			tr.style.display = 'table-row';

		if ($(tr).getNext() && parseInt(tr.getAttribute('data-depth')) < parseInt($(tr).getNext().getAttribute('data-depth')))
		{
			tr.parent = function ()
			{
				var parent = this;
				while (parent && parent.getAttribute("data-depth")
						>= this.getAttribute("data-depth"))
					parent = $(parent).getPrev();
				return parent;
			};
			tr.expands = function ()
			{
				this.selector.innerHTML = '-';
				for (var next = $(this).getNext();
						next && parseInt(next.getAttribute('data-depth')) > parseInt(this.getAttribute('data-depth'));
						next = $(next).getNext())
				{
					if (parseInt(this.getAttribute('data-depth')) === parseInt(next.getAttribute('data-depth')) - 1)
					{
						next.style.display = 'table-row';
					}
				}
			};
			tr.colapse = function ()
			{
				this.selector.innerHTML = '+';
				for (var next = $(this).getNext();
						next && parseInt(next.getAttribute('data-depth')) > parseInt(this.getAttribute('data-depth'));
						next = $(next).getNext())
				{
					next.style.display = 'none';
					if (next.selector.innerHTML === '-')
						next.selector.innerHTML = '+';
				}
			};
			tr.selector.innerHTML = '+';
			tr.selector.style.cursor = 'pointer';
			tr.selector.onclick = function (e)
			{
				switch (this.innerHTML)
				{
					case '+':
						this.parentNode.expands();
						break;
					case '-':
						this.parentNode.colapse();
						break;
				}

				this.parentNode.parentNode.parentNode.colorize();
				e = e ? e : window.event;
				if (e.stopPropagation)
					e.stopPropagation();
				else
					e.cancelBubble = true;
			};
		} else
			tr.selector.innerHTML = ' ';

		if (tr.getAttribute("data-expanded"))
		{
			var parent = tr;
			do
			{
				if (parent.expands)
					parent.expands();
				if (parent.parent)
					parent = parent.parent();
			} while (parent && parent.parent);

			setTimeout(function ()
			{
				tr.scrollIntoView();
			}, 0);
		}
	});

	sel.innerHTML = "+";
	table.style.cursor = "pointer";
	sel.onclick = function (e)
	{
		switch (this.innerHTML)
		{
			case '+':
				this.innerHTML = '-';
				trs.forEach(function (e)
				{
					if (e.expands)
						e.expands();
				});
				break;
			case '-':
				this.innerHTML = '+';
				trs.forEach(function (e)
				{
					if (e.colapse)
						e.colapse();
				});
				break;
		}
		this.parentNode.parentNode.parentNode.colorize();
		e = e ? e : window.event;
		if (e.stopPropagation)
			e.stopPropagation();
		else
			e.cancelBubble = true;
	};

	table.colorize = function ()
	{
		var rows = trs.filter(function (e)
		{
			return e.style.display === 'table-row';
		});
		for (var i = 0; i < rows.length; i++)
			if (rows[i].style.display === 'table-row')
				for (var j = 0; j < rows[i].children.length; j++)
					rows[i].children[j].style.backgroundColor = i % 2 === 0 ? "#FFFFFF" : "#FDFAE9";
	};

	table.colorize();
}

window.addEventListener("load", function ()
{
	search('table.TREEVIEW').forEach(function (e)
	{
		new TreeView(e);
	});
});

