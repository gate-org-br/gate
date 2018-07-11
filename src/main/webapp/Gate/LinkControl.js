function LinkControl(linkControl)
{
	var links = $(linkControl).children("ul", "li", "a");

	if (links.length > 0 && links.every(function (e)
	{
		return !e.parentNode.getAttribute("data-selected")
				|| e.parentNode.getAttribute("data-selected")
				.toLowerCase() !== "true";
	}))
		links[0].parentNode.setAttribute("data-selected", "true");
}

window.addEventListener("load", function ()
{
	search('div.LinkControl').forEach(function (e)
	{
		new LinkControl(e);
	});
});