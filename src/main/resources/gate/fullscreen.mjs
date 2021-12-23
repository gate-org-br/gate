export default class FullScreen
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
		if (FullScreen.status())
			FullScreen.exit();
		else
			FullScreen.enter(element);
		return FullScreen.status();
	}

	static enter(element)
	{
		if (element.requestFullscreen)
			element.requestFullscreen();
		else if (element.mozRequestFullScreen)
			element.mozRequestFullScreen();
		else if (element.webkitRequestFullScreen)
			element.webkitRequestFullScreen();
	}

	static exit()
	{
		if (document.exitFullscreen)
			document.exitFullscreen();
		else if (document.webkitExitFullscreen)
			document.webkitExitFullscreen();
		else if (document.mozCancelFullScreen)
			document.mozCancelFullScreen();
	}
}