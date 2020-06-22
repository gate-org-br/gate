class FullScreen
{
	static status()
	{
		return false
			|| document.fullscreenElement
			|| document.mozFullScreenElement
			|| document.webkitFullscreenElement;
	}

	static switch (element)
	{
		return FullScreen.status()
			? FullScreen.exit()
			: FullScreen.enter(element);
	}

	static enter(element)
	{
		if (element.requestFullscreen)
			element.requestFullscreen();
		else if (element.mozRequestFullScreen)
			element.mozRequestFullScreen();
		else if (element.webkitRequestFullScreen)
			element.webkitRequestFullScreen();
		return true;
	}

	static exit()
	{
		if (document.exitFullscreen)
			document.exitFullscreen();
		else if (document.webkitExitFullscreen)
			document.webkitExitFullscreen();
		else if (document.mozCancelFullScreen)
			document.mozCancelFullScreen();
		return false;
	}
}