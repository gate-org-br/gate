function Button(button)
{
	button.addEventListener("click", function (e)
	{
		if (this.hasAttribute("data-cancel"))
		{
			Message.error(this.getAttribute("data-cancel"));
			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (e)
	{
		if (this.hasAttribute("data-confirm")
			&& !confirm(this.getAttribute("data-confirm")))
		{
			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function ()
	{
		if (this.hasAttribute("data-alert"))
			alert(this.getAttribute("data-alert"));
	});

	button.addEventListener("click", function (e)
	{
		if (this.getAttribute("formaction") &&
			(this.getAttribute("formaction").match(/([?][{][^}]*[}])/g)
				|| this.getAttribute("formaction").match(/([@][{][^}]*[}])/g)))
		{
			var resolved = resolve(this.getAttribute("formaction"));

			if (resolved !== null)
			{
				var formaction = this.getAttribute("formaction");
				this.setAttribute("formaction", resolved);
				this.click();
				this.setAttribute("formaction", formaction);
			}

			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	button.addEventListener("click", function (e)
	{
		if (this.getAttribute("formtarget"))
		{
			switch (this.getAttribute("formtarget").toLowerCase())
			{
				case "_dialog":
					if (e.ctrlKey)
					{
						e.preventDefault();
						e.stopPropagation();
						e.stopImmediatePropagation();
						e.setAttribute("formtarget", "_blank");
						this.click();
						e.setAttribute("formtarget", "_dialog");
					} else if (this.form.getAttribute("target") !== "_dialog")
						new Dialog()
							.setTitle(this.getAttribute("title"))
							.setOnHide(this.getAttribute("data-onHide"))
							.setNavigator(this.getAttribute("data-navigator") ?
								eval(this.getAttribute("data-navigator")) : null)
							.setCloseable(!this.hasAttribute("data-closeable")
								|| JSON.parse(this.getAttribute("data-closeable")))
							.show();
					break;
				case "_message":
					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
					var message = JSON.parse(new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form)));
					Message.show(message, 2000);
					break;
				case "_void":
					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
					var status = JSON.parse(new URL(this.getAttribute("formaction"))
						.post(new FormData(this.form)));
					if (status.type !== "SUCCESS")
						Message.show(status, 2000);
					break;
				case "_this":
					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
					var status = JSON.parse(new URL(this.getAttribute("formaction").post(new FormData(this.form))));
					if (status.type === "SUCCESS")
						this.innerHTML = status.value;
					else
						Message.show(status, 2000);
					break;
				case "_alert":
					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
					alert(new URL(this.getAttribute("formaction")).post(new FormData(this.form)));
					break;
				case "_hide":
					e.preventDefault();
					e.stopPropagation();
					e.stopImmediatePropagation();
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

	button.addEventListener("click", function ()
	{
		if (this.getAttribute("data-block"))
		{
			this.form.addEventListener("submit", function (e)
			{
				if (!this.getAttribute("data-block"))
					Block.show(button.getAttribute("data-block"));
				e.target.removeEventListener(e.type, arguments.callee);
			});
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

	this.setNavigator = function (value)
	{
		if (value)
			button.setAttribute("data-navigator", value);
		else if (button.getAttribute("data-navigator"))
			button.removeAttribute("data-navigator");
		return this;
	};

	this.setOnHide = function (value)
	{
		if (value)
			button.setAttribute("data-onHide", value);
		else if (button.getAttribute("data-onHide"))
			button.removeAttribute("data-onHide");
		return this;
	};

	this.get = function ()
	{
		return button;
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("button")).forEach(function (button)
	{
		new Button(button);
	});
});
