import GBlock from './g-block.js';


window.addEventListener("@hide", function (event)
{
	event.preventDefault();
	if (window.frameElement
		&& window.frameElement.dialog
		&& window.frameElement.dialog.hide)
		window.frameElement.dialog.hide();
	else
		window.close();
});