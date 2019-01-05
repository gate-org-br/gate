/* global Message, Block, ENTER, ESC */

function Button(button, creator)
{
	button.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-cancel"))
		{
			Message.error(this.getAttribute("data-cancel"), 2000);
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-disabled"))
		{
			event.preventDefault();
			event.stopPropagation();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (event)
	{
		if (this.hasAttribute("data-confirm")
			&& !confirm(this.getAttribute("data-confirm")))
		{
			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function ()
	{
		if (this.hasAttribute("data-alert"))
			alert(this.getAttribute("data-alert"));
	});


	button.addEventListener("click", function (event)
	{
		if (this.getAttribute("formaction") &&
			(this.getAttribute("formaction").match(/([@][{][^}]*[}])/g)
				|| this.getAttribute("formaction").match(/([!][{][^}]*[}])/g)
				|| this.getAttribute("formaction").match(/([?][{][^}]*[}])/g)))
		{
			var resolved = resolve(this.getAttribute("formaction"));

			if (resolved !== null)
			{
				var formaction = this.getAttribute("formaction");
				this.setAttribute("formaction", resolved);
				this.click();
				this.setAttribute("formaction", formaction);
			}

			event.preventDefault();
			event.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (event)
	{
		if (this.getAttribute("formtarget"))
		{
			switch (this.getAttribute("formtarget").toLowerCase())
			{
				case "_dialog":
					if (event.ctrlKey)
					{
						event.preventDefault();
						event.stopPropagation();
						event.stopImmediatePropagation();
						this.setAttribute("formtarget", "_blank");
						this.click();
						this.setAttribute("formtarget", "_dialog");
					} else if (this.form.getAttribute("target") !== "_dialog")
					{
						new Dialog({creator: creator || this,
							title: this.getAttribute("title"),
							blocked: Boolean(this.getAttribute("data-blocked"))})
							.show();
					}

					break;
				case "_message":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							try
							{
								status = JSON.parse(status);
								Message.show(status, 2000);
							} finally
							{
								button.disabled = false;
							}
						});
					break;
				case "_none":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							try
							{
								status = JSON.parse(status);
								if (status.type !== "SUCCESS")
									Message.show(status, 2000);
							} finally
							{
								button.disabled = false;
							}
						});
					break;
				case "_this":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							try
							{
								status = JSON.parse(status);
								if (status.type === "SUCCESS")
									button.innerHTML = status.value;
								else
									Message.show(status, 2000);
							} finally
							{
								button.disabled = false;
							}
						});
					break;
				case "_alert":
					event.preventDefault();
					event.stopPropagation();
					event.stopImmediatePropagation();

					this.disabled = true;
					new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form), function (status)
						{
							alert(status);
							button.disabled = false;
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
			}
		}
	});

	this.setAlert = function (value)
	{
		if (value)
			button.setAttribute("data-alert", value);
		else if (button.getAttribute("data-alert"))
			button.removeAttribute("data-alert");
		return this;
	};

	this.setConfirm = function (value)
	{
		if (value)
			button.setAttribute("data-confirm", value);
		else if (button.getAttribute("data-confirm"))
			button.removeAttribute("data-confirm");
		return this;
	};

	this.setBlock = function (value)
	{
		if (value)
			button.setAttribute("data-block", value);
		else if (button.getAttribute("data-block"))
			button.removeAttribute("data-block");
		return this;
	};

	this.setAction = function (value)
	{
		if (value)
			button.setAttribute("formaction", value);
		else if (button.getAttribute("formaction"))
			button.removeAttribute("formaction");
		return this;
	};

	this.setTarget = function (value)
	{
		if (value)
			button.setAttribute("formtarget", value);
		else if (button.getAttribute("formtarget"))
			button.removeAttribute("formtarget");
		return this;
	};

	this.setTitle = function (value)
	{
		if (value)
			button.setAttribute("title", value);
		else if (button.getAttribute("title"))
			button.removeAttribute("title");
		return this;
	};

	this.get = function ()
	{
		return button;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("button"))
		.forEach(button => new Button(button));

	document.documentElement.addEventListener("keydown", function (event)
	{
		switch (event.keyCode)
		{
			case ENTER:
				Array.from(document.querySelectorAll("button.Action")).forEach(e => e.click());
				break;
			case ESC:
				Array.from(document.querySelectorAll("button.Cancel")).forEach(e => e.click());
				break;

		}
	});
});
