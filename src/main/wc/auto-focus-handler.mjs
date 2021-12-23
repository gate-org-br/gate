var elements = Array.from(document.querySelectorAll('[autofocus]'));
if (!elements.length)
{
	let elements = Array.from(document.querySelectorAll('[tabindex]'))
		.filter(e => Number(e.getAttribute("tabindex")) > 0);
	if (elements.length)
	{
		let element = elements.reverse()
			.reduce((a, b) => Number(a.getAttribute("tabindex")) < Number(b.getAttribute("tabindex")) ? a : b);
		if (element)
		{
			element.focus();
			if (element.onfocus)
				element.onfocus();
		}
	}
} else
	elements[0].focus();