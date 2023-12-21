window.addEventListener("keydown", function (event)
{
	if (event.target.tagName === 'BUTTON' && event.keyCode === 32)
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
		event.target.target.click();
	}
});