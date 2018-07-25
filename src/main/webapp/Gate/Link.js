function Link(a)
{
	a.addEventListener("click", function (e)
	{
		if (this.getAttribute("data-cancel"))
		{
			alert(this.getAttribute("data-cancel"));
			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	a.addEventListener("click", function (e)
	{
		if (this.getAttribute("data-confirm")
			&& !confirm(this.getAttribute("data-confirm")))
		{
			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	a.addEventListener("click", function (e)
	{
		if (this.getAttribute("data-alert"))
			alert(this.getAttribute("data-alert"));
	});

	a.addEventListener("click", function (e)
	{
		if (this.href.match(/([?][{][^}]*[}])/g)
			|| this.href.match(/([@][{][^}]*[}])/g))
		{
			var resolved = resolve(this.href);
			if (resolved !== null)
			{
				var href = this.href;
				this.href = resolved;
				this.click();
				this.href = href;
			}

			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	a.addEventListener("click", function (e)
	{
		if (this.getAttribute("target"))
		{
			switch (this.getAttribute("target").toLowerCase())
			{
				case "_dialog":
					if (e.ctrlKey)
					{
						e.setAttribute("target", "_blank");
						this.click();
						e.setAttribute("target", "_dialog");
					} else
						new Dialog()
							.setTitle(this.getAttribute("title"))
							.setOnHide(this.getAttribute("data-onHide"))
							.setNavigator(this.getAttribute("data-navigator") ?
								eval(this.getAttribute("data-navigator")) : null)
							.setTarget(this.getAttribute("href"))
							.setCloseable(!this.hasAttribute("data-closeable")
								|| JSON.parse(this.getAttribute("data-closeable")))
							.show();

					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
					break;
				case "_alert":

					new URL(this.getAttribute("href")).get(function (response)
					{
						alert(response);
					});

					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
					break;
			}
		}
	});

	a.addEventListener("click", function (e)
	{
		if (this.getAttribute("data-block"))
			Block.show(this.getAttribute("data-block"));
	});

	this.setAlert = function (value)
	{
		if (value)
			a.setAttribute("data-alert", value);
		else if (a.getAttribute("data-alert"))
			a.removeAttribute("data-alert");
		return this;
	};

	this.setConfirm = function (value)
	{
		if (value)
			a.setAttribute("data-confirm", value);
		else if (a.getAttribute("data-confirm"))
			a.removeAttribute("data-confirm");
		return this;
	};

	this.setBlock = function (value)
	{
		if (value)
			a.setAttribute("data-block", value);
		else if (a.getAttribute("data-block"))
			a.removeAttribute("data-block");
		return this;
	};

	this.setAction = function (value)
	{
		if (value)
			a.setAttribute("href", value);
		else if (a.getAttribute("href"))
			a.removeAttribute("href");
		return this;
	};

	this.setTarget = function (value)
	{
		if (value)
			a.setAttribute("target", value);
		else if (a.getAttribute("target"))
			a.removeAttribute("target");
		return this;
	};

	this.setTitle = function (value)
	{
		if (value)
			a.setAttribute("title", value);
		else if (a.getAttribute("title"))
			a.removeAttribute("title");
		return this;
	};

	this.setNavigator = function (value)
	{
		if (value)
			a.setAttribute("data-navigator", value);
		else if (a.getAttribute("data-navigator"))
			a.removeAttribute("data-navigator");
		return this;
	};

	this.setOnHide = function (value)
	{
		if (value)
			a.setAttribute("data-onHide", value);
		else if (a.getAttribute("data-onHide"))
			a.removeAttribute("data-onHide");
		return this;
	};

	this.get = function ()
	{
		return a;
	};
}

window.addEventListener("load", function ()
{
	search('a').forEach(function (a)
	{
		new Link(a);
	});
});
