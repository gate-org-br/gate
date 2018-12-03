function clock()
{
	for (var i = 0; i < document.all.length; i++)
	{
		var node = document.all[i];
		if (node.getAttribute("data-clock")
				&& node.getAttribute("data-paused") !== 'true')
		{
			var time = Number(node.getAttribute("data-clock"));
			node.setAttribute("data-clock", time + 1);
			node.innerHTML = new Duration(time).toString();
		}

		if (node.onClockTick)
			node.onClockTick();
	}
}

window.addEventListener("load", function ()
{
	clock();
	window.setInterval(clock, 1000);
});

