window.addEventListener("mouseover", function (event)
{
	let target = event.target.closest("tr[tabindex]");
	if (target)
		target.focus();
});

window.addEventListener("keydown", function (event)
{
	let target = event.target.closest("tr[tabindex]");
	if (target)
	{
		switch (event.key)
		{
			case "Enter":
				target.click();
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();
				break;

			case "Home":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.parentNode.firstElementChild.hasAttribute("tabindex"))
					target.parentNode.firstElementChild.focus();

				break;
			case "End":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.parentNode.lastElementChild.hasAttribute("tabindex"))
					target.parentNode.lastElementChild.focus();
				break;
			case "ArrowUp":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.previousElementSibling &&
					target.previousElementSibling.hasAttribute("tabindex"))
					target.previousElementSibling.focus();
				break;
			case "ArrowDown":
				event.preventDefault();
				event.stopPropagation();
				event.stopImmediatePropagation();

				if (target.nextElementSibling &&
					target.nextElementSibling.hasAttribute("tabindex"))
					target.nextElementSibling.focus();
				break;
		}
	}
});