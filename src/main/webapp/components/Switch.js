window.addEventListener("load", function ()
{
	window.setInterval(function ()
	{
		search("*[data-switch]").forEach(function (node)
		{
			var innerHTML = node.innerHTML;
			var dataSwitch = node.getAttribute('data-switch');
			node.innerHTML = dataSwitch;
			node.setAttribute('data-switch', innerHTML);
		});
	}, 500);
});
