/* global Message, Block, ENTER, ESC, Commands, link */

class Button
{
	constructor(button, creator)
	{
		this.button = button;
		this.creator = creator;

		this.button.addEventListener("click", function (event)
		{
			if (this.hasAttribute("data-cancel"))
			{
				Message.error(this.getAttribute("data-cancel"), 2000);
				event.preventDefault();
				event.stopImmediatePropagation();
			}
		});

		this.button.addEventListener("click", function (event)
		{
			if (this.hasAttribute("data-disabled"))
			{
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
			}
		});

		this.button.addEventListener("click", function (event)
		{
			if (this.hasAttribute("data-confirm")
				&& !confirm(this.getAttribute("data-confirm")))
			{
				event.preventDefault();
				event.stopImmediatePropagation();
			}
		});

		this.button.addEventListener("click", function ()
		{
			if (this.hasAttribute("data-alert"))
				alert(this.getAttribute("data-alert"));
		});


		this.button.addEventListener("click", function (event)
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

		this.button.addEventListener("click", function (event)
		{
			if (this.getAttribute("formtarget"))
			{
				switch (this.getAttribute("formtarget").toLowerCase())
				{
					case "_dialog":
						if (this.form.checkValidity())
						{
							if (event.ctrlKey)
							{
								event.preventDefault();
								event.stopPropagation();
								this.setAttribute("formtarget", "_blank");
								this.click();
								this.setAttribute("formtarget", "_dialog");
							} else if (this.form.getAttribute("target") !== "_dialog")
							{
								let dialog = GDialog.create();
								dialog.creator = creator || this;
								dialog.caption = this.getAttribute("title");
								dialog.blocked = Boolean(this.getAttribute("data-blocked"));
								dialog.show();
							}
						}
						break;
					case "_message":
						event.preventDefault();
						event.stopPropagation();

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

						if (this.form.reportValidity())
						{
							this.disabled = true;
							new URL(this.getAttribute("formaction"))
								.post(new FormData(this.form), function (status)
								{
									alert(status);
									button.disabled = false;
								});
						}
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

					case "_progress-dialog":
						event.preventDefault();
						event.stopPropagation();

						if (this.form.reportValidity())
						{
							this.disabled = true;
							new URL(this.getAttribute("formaction"))
								.post(new FormData(this.form), process =>
								{
									process = JSON.parse(process);
									new ProgressDialog(process,
										{title: this.getAttribute("title")}).show();
									button.disabled = false;
								});
						}

						break;

					case "_progress-window":
						event.preventDefault();
						event.stopPropagation();

						if (this.form.reportValidity())
						{
							new URL(this.getAttribute("formaction"))
								.post(new FormData(this.form), function (process)
								{
									process = JSON.parse(process);
									document.body.appendChild(new ProgressWindow(process));
								});
						}

						break;

					case "_report":
					case "_report-dialog":
						event.preventDefault();
						event.stopPropagation();

						if (this.form.reportValidity())
						{
							new ReportDialog({method: "POST",
								blocked: true,
								url: this.getAttribute("formaction") || this.form.action,
								title: this.getAttribute("title"),
								data: new FormData(this.form)}).show();
							this.disabled = false;
						}

						break;
				}
			}
		});
	}

	setAlert(value)
	{
		if (value)
			this.button.setAttribute("data-alert", value);
		else if (this.button.getAttribute("data-alert"))
			this.button.removeAttribute("data-alert");
		return this;
	}

	setConfirm(value)
	{
		if (value)
			this.button.setAttribute("data-confirm", value);
		else if (this.button.getAttribute("data-confirm"))
			this.button.removeAttribute("data-confirm");
		return this;
	}

	setBlock(value)
	{
		if (value)
			this.button.setAttribute("data-block", value);
		else if (this.button.getAttribute("data-block"))
			this.button.removeAttribute("data-block");
		return this;
	}

	setAction(value)
	{
		if (value)
			this.button.setAttribute("formaction", value);
		else if (this.button.getAttribute("formaction"))
			this.button.removeAttribute("formaction");
		return this;
	}

	setTarget(value)
	{
		if (value)
			this.button.setAttribute("formtarget", value);
		else if (this.button.getAttribute("formtarget"))
			this.button.removeAttribute("formtarget");
		return this;
	}

	setTitle(value)
	{
		if (value)
			this.button.setAttribute("title", value);
		else if (this.button.getAttribute("title"))
			this.button.removeAttribute("title");
		return this;
	}

	get()
	{
		return this.button;
	}

	execute()
	{
		if (!this.button.parentNode)
		{
			if (!this.creator)
				throw "Attempt to trigger a button without a form";
			var form = this.creator.closest("form");
			form.appendChild(this.button);
			this.button.click();
			form.removeChild(this.button);
		} else
			this.button.click();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("button")).forEach(button => new Button(button));

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
