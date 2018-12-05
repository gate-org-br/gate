function ActionHandler(e)
{
	e.setAttribute("tabindex", 1);
	e.onmouseover = function ()
	{
		this.focus();
	};

	e.onkeydown = function (e)
	{
		e = e ? e : window.event;
		switch (e.keyCode)
		{
			case ENTER:
				this.onclick(e);
				return false;

			case HOME:
				var siblings = $(this).siblings("tr");
				if (siblings.length !== 0
					&& siblings[0].getAttribute("tabindex"))
					siblings[0].focus();
				return false;

			case END:
				var siblings = $(this).siblings("tr");
				if (siblings.length !== 0
					&& siblings[siblings.length - 1].getAttribute("tabindex"))
					siblings[siblings.length - 1].focus();
				return false;

			case UP:
				var prev = $(this).getPrev();
				if (prev &&
					prev.getAttribute("tabindex"))
					prev.focus();
				return false;

			case DOWN:
				var next = $(this).getNext();
				if (next &&
					next.getAttribute("tabindex"))
					next.focus();
				return false;

			default:
				return true;
		}
	};

	if (!e.onclick)
		e.onclick = function (e)
		{
			this.blur();
			e = e || window.event;
			for (var parent = e.target || e.srcElement;
				parent !== this;
				parent = parent.parentNode)
				if (parent.onclick
					|| parent.tagName.toLowerCase() === 'a'
					|| parent.tagName.toLowerCase() === 'button')
					return;
			switch (this.getAttribute("data-method") ?
				this.getAttribute("data-method")
				.toLowerCase() : "get")
			{
				case "get":
					var a = new Link(document.createElement("a"))
						.setAction(this.getAttribute("data-action"))
						.setTarget(e.ctrlKey ? "_blank" : this.getAttribute("data-target"))
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
					var form = $(this).getForm();
					var button = new Button(document.createElement("button"))
						.setAction(this.getAttribute("data-action"))
						.setTarget(e.ctrlKey ? "_blank" : this.getAttribute("data-target"))
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

			return false;
		};
}

window.addEventListener("load", function ()
{
	search('*[data-action]').forEach(function (e)
	{
		new ActionHandler(e);
	});
});