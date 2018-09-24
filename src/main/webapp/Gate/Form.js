window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("form")).forEach(function (form)
	{
		form.addEventListener("submit", function (e)
		{
			if (this.getAttribute("data-cancel"))
			{
				alert(this.getAttribute("data-cancel"));
				e.preventDefault();
				e.stopImmediatePropagation();
				return false;
			}

			if (this.getAttribute("data-confirm")
				&& !prompt(this.getAttribute("data-confirm")))
			{
				e.preventDefault();
				e.stopImmediatePropagation();
				return false;
			}

			if (this.target === "_dialog")
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
			if (event.keyCode === 13)
			{
				var input = document.activeElement;
				switch (input.tagName.toLowerCase())
				{
					case "input":
						if (input.hasAttribute("type"))
							switch (input.getAttribute("type").toLowerCase())
							{
								case "color":
								case "date":
								case "datetime-local":
								case "email":
								case "month":
								case "number":
								case "file":
								case "range":
								case "search":
								case "tel":
								case "time":
								case "url":
								case "week":
								case "text":
								case "password":
									input = next(input);
									if (input)
										input.focus();
									break;
								case "submit":
									input.click();
									break;
							}
						event.preventDefault();
						event.stopImmediatePropagation();
						break;
					case "select":
						input = next(input);
						if (input)
							input.focus();
						event.preventDefault();
						event.stopImmediatePropagation();
						break;
					case "button":
						input.click();
						event.preventDefault();
						event.stopImmediatePropagation();
						break;
					case "textarea":
						if (event.ctrlKey)
						{
							input = next(input);
							if (input)
								input.focus();
							event.preventDefault();
							event.stopImmediatePropagation();
						}
						break;
				}
			}

			function next(input)
			{
				var inputs = Array.from(input.form.elements)
					.filter(function (e)
					{
						return e.offsetParent;
					})
					.map(function (e, index)
					{
						return {obj: e, idx: index};
					});
				inputs.sort(function (e1, e2)
				{
					var tabindex1 = e1.obj.getAttribute("tabindex");
					var tabindex2 = e2.obj.getAttribute("tabindex");
					if (tabindex1 === tabindex2)
						return e1.idx - e2.idx;
					if (tabindex1 && !tabindex2)
						return -1;
					if (!tabindex1 && tabindex2)
						return 1;
					return Number(tabindex1) - Number(tabindex2);
				});
				inputs = inputs.map(function (e)
				{
					return e.obj;
				});
				for (var i = 0; i < inputs.length; i++)
					if (inputs[i] === input && inputs.length >= i + 1)
						return inputs[i + 1];
			}
		});
	}
	);
});


