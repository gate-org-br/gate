window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("a.Return")).forEach(function (e)
	{
		e.onclick = function ()
		{
			window.history.go(-1);
		};
	});
});