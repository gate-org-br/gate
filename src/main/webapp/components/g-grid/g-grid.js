/* global customElements */

class GGrid extends HTMLElement
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		const create = (cols, data) =>
		{
			return data.map(row =>
			{
				let tr = document.createElement("tr");
				if (this.action)
				{
					let action = this.action;
					var parameters = action.match(/([@][0-9]+)/g);
					if (parameters)
						parameters.forEach(e => action = action.replace(e, row[Number(e.substring(1))]));
					tr.setAttribute("data-action", action);
				}

				if (this.method)
					tr.setAttribute("data-method", this.method);
				if (this.target)
					tr.setAttribute("data-target", this.target);
				row.filter((e, i) => cols[i]).forEach(column => tr.appendChild(document.createElement("td")).innerText = column);
				return tr;
			});
		};

		const update = data =>
		{
			let cols = JSON.parse(this.cols);

			let table = this.appendChild(document.createElement("table"));
			table.style = this.style;
			table.className = this.className;

			let caption = table.appendChild(document.createElement("caption"));
			caption.innerText = `REGISTROS: ${data.length}`;
			let thead = table.appendChild(document.createElement("thead"));

			let tr = thead.appendChild(document.createElement("tr"));
			cols.filter(e => e).forEach(column =>
			{
				let th = document.createElement("th");

				if (this.sortable)
					th.setAttribute("data-sortable", "");

				th.innerText = column;

				if (column.style)
					th.style = column.style;

				th.innerText = column.head || column;

				tr.appendChild(th);
			});

			if (this.filterable)
			{
				let td = thead.appendChild(document.createElement("tr"))
					.appendChild(document.createElement("td"));
				td.setAttribute("colspan", cols.length);
				let filter = td.appendChild(document.createElement("input"));
				filter.setAttribute("type", "text");
				filter.setAttribute("data-filter", "");
			}

			let tbody = table.appendChild(document.createElement("tbody"));
			create(cols, data).forEach(e => tbody.appendChild(e));

			if (this.more)
			{
				let tfoot = table.appendChild(document.createElement("tfoot"))
					.appendChild(document.createElement("tr"))
					.appendChild(document.createElement("td"));
				tfoot.setAttribute("colspan", cols.length);

				let more = tfoot.appendChild(document.createElement("a"));
				more.innerText = "<< Mais >>";
				more.href = "#";
				more.onclick = () =>
				{
					let url = this.more.replace("@size", tbody.children.length);

					const process = data =>
					{
						data = JSON.parse(data);

						if (data.length)
						{
							create(cols, data).forEach(e => tbody.appendChild(e));
							caption.innerText = `REGISTROS: ${tbody.children.length}`;
						} else {
							more.parentNode.removeChild(more);
							alert("Não há mais registros a carregar");
						}

						tfoot.scrollIntoView({behavior: "smooth", block: "end", inline: "end"});
					};


					let form = this.closest("form");
					if (form)
						new URL(url).post(new FormData(form),
							data => process(data));
					else
						new URL(url).get(data => process(data));
				};
			}

		};
		try {
			update(JSON.parse(this.data));
		} catch (ex)
		{
			let data = this.data.replace("@size", 0);
			var parameters = data.match(/([@][0-9]+)/g);
			if (parameters)
				parameters.forEach(e => action = action.replace(e, row[Number(e.substring(1))]));
			new URL(data).get(e => update(JSON.parse(e)));
		}
	}

	head(index, name)
	{
		index = index.toString();
		if (!this._private.cols.has(index))
			this._private.cols.set(index, {});
		if (!name)
			return this._private.cols.get(index).name;
		this._private.cols.get(index).name = name;
	}

	size(index, size)
	{
		index = index.toString();
		if (!this._private.cols.has(index))
			this._private.cols.set(index, {});
		if (!size)
			return this._private.cols.get(index).name;
		this._private.cols.get(index).size = size;
	}

	get cols()
	{
		return this.getAttribute("cols");
	}

	set cols(cols)
	{
		this.setAttribute("cols", cols);
	}

	get data()
	{
		return this.getAttribute("data");
	}
	set data(data)
	{
		this.setAttribute("data", data);
		if (this.parentNode)
			this.connectedCallback();
	}
	get action()
	{
		return this.getAttribute("action");
	}
	set action(action)
	{
		this.setAttribute("action", action);
	}

	get method()
	{
		return this.getAttribute("method");
	}

	set method(method)
	{
		this.setAttribute("method", method);
	}

	get sortable()
	{
		if (this.more)
			return false;
		return this.hasAttribute("sortable")
			? this.getAttribute("sortable") === "true"
			: true;
	}

	set sortable(sortable)
	{
		this.setAttribute("sortable", sortable);
	}

	get filterable()
	{
		if (this.more)
			return false;
		return this.hasAttribute("filterable")
			? this.getAttribute("filterable") === "true"
			: true;
	}

	set filterable(filterable)
	{
		this.setAttribute("filterable", filterable);
	}

	get target()
	{
		return this.getAttribute("target");
	}

	set target(target)
	{
		this.setAttribute("target", target);
	}

	get more()
	{
		return this.getAttribute("more");
	}

	set more(more)
	{
		this.setAttribute("more", more);
	}

	attributeChangedCallback(name)
	{
		if (this.parentNode)
		{
			switch (name)
			{
				case "cols":
				case "data":
			}
			this.connectedCallback();
		}
	}

	static get observedAttributes()
	{
		return ["cols", "data", "action", "method", "target"];
	}
}

customElements.define('g-grid', GGrid);