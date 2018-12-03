window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("form")).forEach(function (form)
	{
		form.addEventListener("submit", function (e)
		{
			if (this.hasAttribute("data-cancel"))
			{
				Message.error(this.getAttribute("data-cancel"), 2000);
				e.preventDefault();
				e.stopImmediatePropagation();

			} else if (this.hasAttribute("data-confirm") && !confirm(this.getAttribute("data-confirm")))
			{
				e.preventDefault();
				e.stopImmediatePropagation();
			} else if (this.target === "_dialog")
			{
				new Dialog()
					.setTitle(this.getAttribute("title"))
					.setOnHide(this.getAttribute("data-onHide"))
					.setNavigator(this.getAttribute("data-navigator") ?
						eval(this.getAttribute("data-navigator")) : null)
					.show();
			}
		});

		form.addEventListener("keydown", function (event)
		{
			event = event ? event : window.event;

			switch (event.keyCode)
			{
				case ENTER:
					var element = document.activeElement;
					switch (element.tagName.toLowerCase())
					{
						case "a":
						case "button":
							element.click();
							event.preventDefault();
							event.stopImmediatePropagation();
							break;
						case "select":
						case "textarea":
							if (!event.ctrlKey)
								break;
						case "input":
							var commit = this.querySelector(".Commit");
							if (commit)
							{
								commit.focus();
								commit.click();
								event.preventDefault();
								event.stopImmediatePropagation();
							} else if (this.hasAttribute("action"))
							{
								let button = document.createElement("button");
								button.style.display = "none";
								this.appendChild(button);
								button.click();
								this.removeChild(button);
								event.preventDefault();
								event.stopImmediatePropagation();
							}
							break;
					}
					break;
				case ESC:
					var element = document.activeElement;
					switch (element.tagName.toLowerCase())
					{
						case "select":
							if (!event.ctrlKey)
								break;
						case "input":
						case "a":
						case "button":
						case "textarea":
							var cancel = this.querySelector(".Cancel");
							if (cancel)
							{
								cancel.focus();
								cancel.click();
								event.preventDefault();
								event.stopImmediatePropagation();
							}
							break;
					}
					break;

			}
		});
	});
});


