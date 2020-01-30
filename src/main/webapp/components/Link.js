/* global Message, Block, ENTER, ESC, Commands, GDialog */

class Link
{
	constructor(link, creator)
	{
		this.link = link;
		this.creator = creator;


		this.link.addEventListener("click", function (event)
		{
			if (this.hasAttribute("data-cancel"))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				Message.error(this.getAttribute("data-cancel"), 2000);
			}
		});
		this.link.addEventListener("click", function (event)
		{
			if (this.hasAttribute("data-disabled"))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
			}
		});
		this.link.addEventListener("click", function (event)
		{
			if (this.hasAttribute("data-confirm")
				&& !confirm(this.getAttribute("data-confirm")))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
			}
		});
		this.link.addEventListener("click", function ()
		{
			if (this.hasAttribute("data-alert"))
				alert(this.getAttribute("data-alert"));
		});
		this.link.addEventListener("click", function (event)
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
		this.link.addEventListener("click", function (event)
		{
			if (this.getAttribute("target"))
			{
				switch (this.getAttribute("target").toLowerCase())
				{
					case "_dialog":
						event.preventDefault();
						event.stopPropagation();
						if (event.ctrlKey)
						{
							this.setAttribute("target", "_blank");
							this.click();
							this.setAttribute("target", "_dialog");
						} else
						{
							let dialog = GDialog.create();
							dialog.navigator = this.navigator;
							dialog.creator = this.creator || this;
							dialog.target = this.getAttribute("href");
							dialog.caption = this.getAttribute("title");
							dialog.blocked = Boolean(this.getAttribute("data-blocked"));
							dialog.show();
						}
						break;
					case "_message":
						event.preventDefault();
						event.stopPropagation();
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
						Array.from(this.children)
							.filter(e => e.tagName.toLowerCase() === "div")
							.forEach(e => new Popup(e));
						break;
					case "_progress-dialog":
						event.preventDefault();
						event.stopPropagation();
						new URL(this.href).get(process =>
						{
							process = JSON.parse(process);
							new ProgressDialog(process,
								{title: this.getAttribute("title")}).show();
						});
						break;
					case "_progress-window":
						event.preventDefault();
						event.stopPropagation();
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
						new ReportDialog({method: "GET",
							blocked: true,
							url: this.href,
							title: this.getAttribute("title")}).show();
						break;
				}
			}
		});
		this.link.addEventListener("keydown", function (event)
		{
			if (event.keyCode === 32)
			{
				this.click();
				event.preventDefault();
				event.stopImmediatePropagation();
			}
		});
	}

	setAlert(value)
	{
		if (value)
			this.link.setAttribute("data-alert", value);
		else if (this.link.getAttribute("data-alert"))
			this.link.removeAttribute("data-alert");
		return this;
	}

	setConfirm(value)
	{
		if (value)
			this.link.setAttribute("data-confirm", value);
		else if (this.link.getAttribute("data-confirm"))
			this.link.removeAttribute("data-confirm");
		return this;
	}

	setBlock(value)
	{
		if (value)
			this.link.setAttribute("data-block", value);
		else if (this.link.getAttribute("data-block"))
			this.link.removeAttribute("data-block");
		return this;
	}

	setAction(value)
	{
		if (value)
			this.link.setAttribute("href", value);
		else if (this.link.getAttribute("href"))
			this.link.removeAttribute("href");
		return this;
	}
	setTarget(value)
	{
		if (value)
			this.link.setAttribute("target", value);
		else if (this.link.getAttribute("target"))
			this.link.removeAttribute("target");
		return this;
	}

	setTitle(value)
	{
		if (value)
			this.link.setAttribute("title", value);
		else if (this.link.getAttribute("title"))
			this.link.removeAttribute("title");
		return this;
	}

	setNavigator(value)
	{
		this.link.navigator = value;
		return this;
	}

	get()
	{
		return this.link;
	}

	execute()
	{
		if (!this.link.parentNode)
		{
			document.body.appendChild(this.link);
			this.link.click();
			document.body.removeChild(this.link);
		} else
			this.link.click();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a")).forEach(a => new Link(a));
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
