/* global Message, Block, ENTER, ESC */

function Link(link, creator)
{
	var navigator;

	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-cancel"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();

			Message.error(this.getAttribute("data-cancel"), 2000);
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-confirm")
			&& !confirm(this.getAttribute("data-confirm")))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	link.addEventListener("click", function ()
	{
		if (this.hasAttribute("data-alert"))
			alert(this.getAttribute("data-alert"));
	});

	link.addEventListener("click", function (event)
	{
		if (this.href.match(/([@][{][^}]*[}])/g)
			|| this.href.match(/([!][{][^}]*[}])/g)
			|| this.href.match(/([?][{][^}]*[}])/g))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();

			var resolved = resolve(this.href);
			if (resolved !== null)
			{
				var href = this.href;
				this.href = resolved;
				this.click();
				this.href = href;
			}
		}
	});

	link.addEventListener("click", function (event)
	{
		if (this.getAttribute("target"))
		{
			switch (this.getAttribute("target").toLowerCase())
			{
				case "_dialog":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					if (event.ctrlKey)
					{
						this.setAttribute("target", "_blank");
						this.click();
						this.setAttribute("target", "_dialog");
					} else
					{
						new Dialog({creator: creator || this,
							title: this.getAttribute("title"),
							target: this.getAttribute("href"),
							blocked: Boolean(this.getAttribute("data-blocked")),
							navigator: navigator})
							.show();
					}
					break;
				case "_message":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							Message.show(status, 2000);
						} finally
						{
							link.removeAttribute("data-cancel");
						}
					});

					break;
				case "_none":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type !== "SUCCESS")
								Message.show(status, 2000);
						} finally
						{
							link.removeAttribute("data-cancel");
						}
					});

					break;
				case "_this":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						try
						{
							status = JSON.parse(status);
							if (status.type === "SUCCESS")
								this.innerHTML = status.value;
							else
								Message.show(status, 2000);
						} finally
						{
							link.removeAttribute("data-cancel");
						}
					});
					break;
				case "_alert":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					link.setAttribute("data-cancel", "Processando");
					new URL(this.href).get(function (status)
					{
						alert(status);
						link.removeAttribute("data-cancel");
					});

					break;
				case "_hide":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();
					if (window.frameElement
						&& window.frameElement.dialog
						&& window.frameElement.dialog.hide)
						window.frameElement.dialog.hide();
					else
						window.close();
					break;

				case "_popup":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();
					Array.from(this.children)
						.filter(e => e.tagName.toLowerCase() === "div")
						.forEach(e => new Popup(e));
					break;

				case "_progress-dialog":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					new URL(this.href).get(function (process)
					{
						process = JSON.parse(process);
						new ProgressDialog(process,
							{title: link.getAttribute("title")}).show();
					});

					break;

				case "_progress-window":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					new URL(this.href).get(function (process)
					{
						process = JSON.parse(process);
						document.body.appendChild(new ProgressWindow(process));
					});

					break;

				case "_report":
				case "_report-dialog":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					new ReportDialog({method: "GET",
						blocked: true,
						url: link.href,
						title: link.getAttribute("title")}).show();

					break;
			}
		}
	});

	link.addEventListener("keydown", function (event)
	{
		if (event.keyCode === 32)
		{
			this.click();
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	this.setAlert = function (value)
	{
		if (value)
			link.setAttribute("data-alert", value);
		else if (link.getAttribute("data-alert"))
			link.removeAttribute("data-alert");
		return this;
	};

	this.setConfirm = function (value)
	{
		if (value)
			link.setAttribute("data-confirm", value);
		else if (link.getAttribute("data-confirm"))
			link.removeAttribute("data-confirm");
		return this;
	};

	this.setBlock = function (value)
	{
		if (value)
			link.setAttribute("data-block", value);
		else if (link.getAttribute("data-block"))
			link.removeAttribute("data-block");
		return this;
	};

	this.setAction = function (value)
	{
		if (value)
			link.setAttribute("href", value);
		else if (link.getAttribute("href"))
			link.removeAttribute("href");
		return this;
	};

	this.setTarget = function (value)
	{
		if (value)
			link.setAttribute("target", value);
		else if (link.getAttribute("target"))
			link.removeAttribute("target");
		return this;
	};

	this.setTitle = function (value)
	{
		if (value)
			link.setAttribute("title", value);
		else if (link.getAttribute("title"))
			link.removeAttribute("title");
		return this;
	};

	this.setNavigator = function (value)
	{
		navigator = value;
		return this;
	};

	this.get = function ()
	{
		return link;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a"))
		.forEach(a => new Link(a));

	document.documentElement.addEventListener("keydown", function (event)
	{
		switch (event.keyCode)
		{
			case ENTER:
				Array.from(document.querySelectorAll("a.Action")).forEach(e => e.click());
				break;
			case ESC:
				Array.from(document.querySelectorAll("a.Cancel")).forEach(e => e.click());
				break;

		}
	});
});
