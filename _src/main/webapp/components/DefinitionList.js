window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("dl[data-entries]")).forEach(function (e)
	{
		entries = JSON.parse(e.getAttribute("data-entries"));

		for (var key in entries)
		{
			e.appendChild(document.createElement("dt")).innerHTML = key;
			e.appendChild(document.createElement("dd")).innerHTML = entries[key];
		}
	});
});