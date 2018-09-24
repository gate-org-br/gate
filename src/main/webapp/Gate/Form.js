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
				if (this.hasAttribute("action") && event.ctrlKey)
				{
					var button = document.createElement("button");
					button.style.display = "none";
					this.appendChild(button);
					button.click();
					this.removeChild(button);
					event.preventDefault();
					event.stopImmediatePropagation();
				} else
				{
					var input = document.activeElement;
					if (input.hasAttribute("tabindex")
						&& input.tagName.toLowerCase() !== "textarea")
					{
						input = next(input, event.shiftKey);
						if (input)
							input.focus();
						event.preventDefault();
						event.stopImmediatePropagation();
					}
				}
			}

			function next(input, shift)
			{
				var form = input.parentNode;
				while (form.tagName.toLowerCase() !== "form")
					form = form.parentNode;

				var inputs = Array.from(form.getElementsByTagName("*")).filter(function (e)
				{
					return e.tagName
						&& e.offsetParent
						&& e.hasAttribute("tabindex")
						&& (e.tagName.toLowerCase() === "input"
							|| e.tagName.toLowerCase() === "select"
							|| e.tagName.toLowerCase() === "textarea"
							|| e.tagName.toLowerCase() === "a"
							|| e.tagName.toLowerCase() === "button");
				}).map(function (e, index)
				{
					return {obj: e, idx: index};
				});

				inputs.sort(function (e1, e2)
				{
					var tabindex1 = e1.obj.getAttribute("tabindex");
					var tabindex2 = e2.obj.getAttribute("tabindex");
					if (tabindex1 === tabindex2)
						return e1.idx - e2.idx;
					return Number(tabindex1) - Number(tabindex2);
				});

				if (shift)
					inputs = inputs.reverse();

				inputs = inputs.map(function (e)
				{
					return e.obj;
				});
				for (var i = 0; i < inputs.length; i++)
					if (inputs[i] === input && inputs.length > i + 1)
						return inputs[i + 1];

				return inputs.length > 0 ? inputs[0] : null;
			}
		});
	});
});


