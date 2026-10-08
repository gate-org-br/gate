window.addEventListener("connected", function (event)
{
	if (event.target.hasAttribute("autofocus"))
		return event.target.focus();

	if (!event.target.hasAttribute("tabindex"))
		return;

	let target = parseInt(event.target.getAttribute("tabindex"), 10);
	if (target < 0)
		return;

	if (!document.activeElement)
		return event.target.focus();

	if (!document.activeElement.hasAttribute("tabindex"))
		return event.target.focus();

	let current = parseInt(document.activeElement.getAttribute("tabindex"), 10);
	if (current < 0)
		return event.target.focus();

	if (target < current)
		event.target.focus();
});
