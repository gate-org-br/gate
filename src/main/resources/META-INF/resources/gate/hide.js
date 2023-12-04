import GLoading from './g-loading.js';

export default function hide(link)
{
	GLoading.show();
	setTimeout(() =>
	{
		let type = link.getAttribute("data-on-hide");
		if (type === "reload")
			window.location.reload();
		else if (type === "submit")
			return link.closest("form").submit();
		else if (type.match(/submit\([^)]+\)/))
			document.getElementById(/submit\(([^)]+)\)/.exec(type)[1]).submit();
	}, 0);
}

window.addEventListener("click", function (event)
{
	event = event || window.event;
	let action = event.target || event.srcElement;
	action = action.closest("a.Hide");
	if (action)
		hide(action);
});

window.addEventListener("trigger", function (event)
{
	if (event.detail.target === "@hide")
	{
		event.preventDefault();
		if (window.frameElement
			&& window.frameElement.dialog
			&& window.frameElement.dialog.hide)
			window.frameElement.dialog.hide();
		else
			window.close();
	}
});
