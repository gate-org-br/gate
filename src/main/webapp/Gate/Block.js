function Block(text)
{
	var modal = new Modal();
	modal.style.display = "flex";
	modal.style.alignItems = "center";
	modal.style.justifyContent = "center";

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.style.width = "50%";
	dialog.style.display = "flex";
	dialog.style.flexWrap = "wrap";
	dialog.style.borderRadius = "5px";
	dialog.style.alignItems = "center";
	dialog.style.justifyContent = "center";
	dialog.style.border = "4px solid #767d90";

	var head = dialog.appendChild(window.top.document.createElement('div'));
	head.style.width = "100%";
	head.style.height = "40px";
	head.style.backgroundImage = "linear-gradient(to bottom, #767D90 100%, #AAB3BD 100%)";
	head.setAttribute("tabindex", "1");
	head.focus();

	var caption = head.appendChild(window.top.document.createElement('label'));
	caption.style.color = "white";
	caption.style.padding = "10px";
	caption.style.fontSize = "18px";
	caption.style['float'] = "left";
	caption.innerHTML = "Aguarde";

	var body = dialog.appendChild(window.top.document.createElement('div'));
	body.style.width = "100%";
	body.style.display = "flex";
	body.style.height = "180px";
	body.style.flexWrap = "wrap";
	body.style.alignItems = "center";
	body.style.justifyContent = "center";
	body.style.background = "linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%)";

	var label = body.appendChild(window.top.document.createElement('label'));
	label.style.width = "calc(100% - 20px)";
	label.style.height = "50px";
	label.style.fontSize = "20px";
	label.style.display = "flex";
	label.style.alignItems = "center";
	label.style.justifyContent = "center";
	label.innerHTML = text;

	var progress = body.appendChild(window.top.document.createElement('progress'));
	progress.style.width = "calc(100% - 20px)";
	progress.style.height = "50px";

	var foot = body.appendChild(window.top.document.createElement('label'));
	foot.style.width = "calc(100% - 20px)";
	foot.style.height = "40px";
	foot.style.fontSize = "20px";
	foot.style.display = "flex";
	foot.style.alignItems = "center";
	foot.style.justifyContent = "right";
	foot.innerHTML = "00:00:00";
	foot.setAttribute("data-clock", '0');

	modal.show();
	return modal;
}

Block.show = function (text)
{
	if (!window.top._block)
		window.top._block = new Block(text);
};

Block.hide = function ()
{
	if (window.top._block)
	{
		window.top._block.hide();
		window.top._block = null;
	}
};

window.addEventListener("load", function ()
{
	Block.hide();
	search("form[data-block]").forEach(function (e)
	{
		e.addEventListener("submit", function ()
		{
			Block.show(this.getAttribute("data-block"));
		});
	});
});