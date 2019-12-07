window.addEventListener("load", function ()
{
	document.addEventListener("fullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.fullscreen ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	document.addEventListener("mozfullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.mozFullScreen ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	document.addEventListener("webkitfullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.webkitIsFullScreen ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	document.addEventListener("msfullscreenchange", function ()
	{
		Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
		{
			e.innerHTML = document.msFullscreenElement ? "<i >&#x2251;</i>" : "<i >&#x2250;</i>";
		});
	}, false);

	Array.from(document.querySelectorAll("a.FullScreen")).forEach(function (e)
	{
		e.innerHTML =
				!document.fullscreen && !document.mozFullScreen
				&& !document.webkitIsFullScreen && !document.msFullscreenElement
				? "<i >&#x2250;</i>" : "<i >&#x2251;</i>";

		e.addEventListener("click", function ()
		{
			if (!document.fullscreen && !document.mozFullScreen
					&& !document.webkitIsFullScreen && !document.msFullscreenElement)
			{
				if (document.documentElement.requestFullscreen)
					document.documentElement.requestFullscreen();
				else if (document.documentElement.mozRequestFullScreen)
					document.documentElement.mozRequestFullScreen();
				else if (document.documentElement.webkitRequestFullScreen)
					document.documentElement.webkitRequestFullScreen();
			} else
			{
				if (document.exitFullscreen)
					document.exitFullscreen();
				else if (document.webkitExitFullscreen)
					document.webkitExitFullscreen();
				else if (document.mozCancelFullScreen)
					document.mozCancelFullScreen();
			}
		});
	});
});
