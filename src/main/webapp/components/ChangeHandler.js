window.addEventListener("load", function ()
{
	var change = function (event)
	{
		switch (this.getAttribute("data-method") ?
			this.getAttribute("data-method")
			.toLowerCase() : "get")
		{
			case "get":
				new Link(document.createElement("a"), event.target)
					.setAction(this.getAttribute("data-action"))
					.setTarget(this.getAttribute("data-target"))
					.setTitle(this.getAttribute("title"))
					.setBlock(this.getAttribute("data-block"))
					.setAlert(this.getAttribute("data-alert"))
					.setConfirm(this.getAttribute("data-confirm"))
					.execute();
				break;
			case "post":
				new Button(document.createElement("button"), event.target)
					.setAction(this.getAttribute("data-action"))
					.setTarget(this.getAttribute("data-target"))
					.setTitle(this.getAttribute("title"))
					.setBlock(this.getAttribute("data-block"))
					.setAlert(this.getAttribute("data-alert"))
					.setConfirm(this.getAttribute("data-confirm"))
					.execute();
				break;
		}
	};

	Array.from(document.querySelectorAll('input[data-method], input[data-action], input[data-target], select[data-method], select[data-action], select[data-target]')).forEach(element =>
	{
		element.addEventListener("change", change);
		element.addEventListener("changed", change);
	});
});

