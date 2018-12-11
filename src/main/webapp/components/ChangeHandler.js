function ChangeHandler(e)
{
	e.onchange = function ()
	{
		switch (this.getAttribute("data-method") ?
			this.getAttribute("data-method")
			.toLowerCase() : "get")
		{
			case "get":
				var a = new Link(document.createElement("a"))
					.setAction(this.getAttribute("data-action"))
					.setTarget(this.getAttribute("data-target"))
					.setTitle(this.getAttribute("title"))
					.setOnHide(this.getAttribute("data-onHide"))
					.setBlock(this.getAttribute("data-block"))
					.setAlert(this.getAttribute("data-alert"))
					.setConfirm(this.getAttribute("data-confirm"))
					.setNavigator(this.getAttribute("data-navigator"))
					.get();
				document.body.appendChild(a);
				a.click();
				document.body.removeChild(a);
				break;
			case "post":
				var form = this.parentNode;
				while (form
					&& form.tagName.toLowerCase()
					!== 'form')
					form = form.parentNode;

				var button = new Button(document.createElement("button"))
					.setAction(this.getAttribute("data-action"))
					.setTarget(this.getAttribute("data-target"))
					.setTitle(this.getAttribute("title"))
					.setOnHide(this.getAttribute("data-onHide"))
					.setBlock(this.getAttribute("data-block"))
					.setAlert(this.getAttribute("data-alert"))
					.setConfirm(this.getAttribute("data-confirm"))
					.setNavigator(this.getAttribute("data-navigator"))
					.get();
				form.appendChild(button);
				button.click();
				form.removeChild(button);
				break;
		}
	};
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('input[data-method], input[data-action], input[data-target]')).forEach(function (e)
	{
		new ChangeHandler(e);
	});
	Array.from(document.querySelectorAll('select[data-method], select[data-action], select[data-target]')).forEach(function (e)
	{
		new ChangeHandler(e);
	});
});
