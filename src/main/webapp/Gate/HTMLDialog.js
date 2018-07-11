function HTMLDialog(e)
{
	var modal = new Modal();
	modal.style.display = "none";
	e.parentNode.appendChild(modal);
	modal.setAttribute("id", e.getAttribute("id"));
	e.removeAttribute("id");

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.className = "Dialog";

	var head = dialog.appendChild(window.top.document.createElement('div'));
	head.setAttribute("tabindex", "1");

	head.onmouseenter = function ()
	{
		head.focus();
	};

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.innerHTML = e.getAttribute("title");
	caption.style['float'] = "left";

	var body = dialog.appendChild(window.top.document.createElement("div"));
	body.style.overflow = "auto";
	body.appendChild(e);


	e.style.display = "block";
	var close = head.appendChild(window.top.document.createElement('a'));
	close.style['float'] = "right";
	close.innerHTML = "&#x1011;";
	close.onclick = function ()
	{
		modal.hide();
	};
	modal.show = function ()
	{
		caption.innerHTML = e.getAttribute("title");
		modal.style.display = "block";
		return this;
	};
	modal.hide = function ()
	{
		modal.style.display = "none";
		return this;
	};
}

window.addEventListener("load", function ()
{
	search("dialog").forEach(function (e)
	{
		new HTMLDialog(e);
	});

	$('a[data-show]').set("onclick", function ()
	{
		select(this.getAttribute("data-show")).show();
	});

	$('a[data-hide]').set("onclick", function ()
	{
		window.frameElement.dialog.hide();
	});
});

