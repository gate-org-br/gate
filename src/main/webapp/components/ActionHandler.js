/* global ENTER, HOME, END, DOWN, UP */

function ActionHandler(element)
{
	element.setAttribute("tabindex", 1);
	element.onmouseover = () => element.focus();

	element.onkeydown = function (event)
	{
		event = event ? event : window.event;
		switch (event.keyCode)
		{
			case ENTER:
				this.onclick(event);
				return false;

			case HOME:
				var siblings = Array.from(this.parentNode.childNodes)
					.filter(node => node.tagName.toLowerCase() === "tr");
				if (siblings.length !== 0
					&& siblings[0].getAttribute("tabindex"))
					siblings[0].focus();
				return false;

			case END:
				var siblings = Array.from(this.parentNode.childNodes)
					.filter(node => node.tagName.toLowerCase() === "tr");
				if (siblings.length !== 0
					&& siblings[siblings.length - 1].getAttribute("tabindex"))
					siblings[siblings.length - 1].focus();
				return false;

			case UP:
				if (this.previousElementSibling &&
					this.previousElementSibling.getAttribute("tabindex"))
					this.previousElementSibling.focus();
				return false;

			case DOWN:
				if (this.nextElementSibling &&
					this.nextElementSibling.getAttribute("tabindex"))
					this.nextElementSibling.focus();
				return false;

			default:
				return true;
		}
	};

	if (!element.onclick)
		element.onclick = function (event)
		{
			this.blur();
			event = event || window.event;
			for (var parent = event.target || event.srcElement;
				parent !== this;
				parent = parent.parentNode)
				if (parent.onclick
					|| parent.tagName.toLowerCase() === 'a'
					|| parent.tagName.toLowerCase() === 'input'
					|| parent.tagName.toLowerCase() === 'select'
					|| parent.tagName.toLowerCase() === 'textarea'
					|| parent.tagName.toLowerCase() === 'button')
					return;
			switch (this.getAttribute("data-method") ?
				this.getAttribute("data-method")
				.toLowerCase() : "get")
			{
				case "get":

					if (!event.ctrlKey && this.getAttribute("data-target"))
						var navigator = Array.from(this.parentNode.children)
							.map(e => e.getAttribute("data-action"))
							.filter(e => e);

					var link = new Link(document.createElement("a"), element)
						.setAction(this.getAttribute("data-action"))
						.setTarget(event.ctrlKey ? "_blank" : this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
						.setNavigator(navigator)
						.get();
					document.body.appendChild(link);

					link.click();
					document.body.removeChild(link);
					break;
				case "post":
					var form = this.parentNode;
					while (form
						&& form.tagName.toLowerCase()
						!== 'form')
						form = form.parentNode;

					var button = new Button(document.createElement("button"), element)
						.setAction(this.getAttribute("data-action"))
						.setTarget(event.ctrlKey ? "_blank" : this.getAttribute("data-target"))
						.setTitle(this.getAttribute("title"))
						.setBlock(this.getAttribute("data-block"))
						.setAlert(this.getAttribute("data-alert"))
						.setConfirm(this.getAttribute("data-confirm"))
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
	Array.from(document.querySelectorAll('tr[data-action], td[data-action], li[data-action]')).forEach(element => new ActionHandler(element));
});