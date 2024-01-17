import GMessageDialog from './g-message-dialog.js';

const ESC = 27;
const ENTER = 13;

Array.from(document.getElementsByTagName("form")).forEach(function (form)
{
	form.addEventListener("submit", function (e)
	{
		if (this.hasAttribute("data-cancel"))
		{
			GMessageDialog.error(this.getAttribute("data-cancel"), 2000);
			e.preventDefault();
			e.stopImmediatePropagation();

		} else if (this.hasAttribute("data-confirm") && !confirm(this.getAttribute("data-confirm")))
		{
			e.preventDefault();
			e.stopImmediatePropagation();
		}
	});

	form.addEventListener("keydown", function (event)
	{
		event = event ? event : window.event;

		if (!event.ctrlKey && event.keyCode === ENTER)
		{
			var element = document.activeElement;
			switch (element.tagName.toLowerCase())
			{
				case "select":
				case "textarea":
					break;
				case "a":
				case "button":
					element.click();
					event.preventDefault();
					event.stopImmediatePropagation();
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
		}
	});
});