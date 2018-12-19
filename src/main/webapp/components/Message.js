function Message(type, message, timeout)
{
	var modal = new Modal();
	modal.style.display = "flex";
	modal.style.alignItems = "center";
	modal.style.justifyContent = "center";

	var dialog = modal.appendChild(window.top.document.createElement('div'));
	dialog.style.width = "800px";
	dialog.style.height = "260px";
	dialog.style.display = "flex";
	dialog.style.borderRadius = "5px";
	dialog.style.alignItems = "center";
	dialog.style.justifyContent = "center";
	dialog.style.border = "4px solid #767d90";
	dialog.style.boxShadow = "3px 10px 5px 0px rgba(0,0,0,0.75)";

	var icon = dialog.appendChild(window.top.document.createElement('div'));
	icon.style.width = "240px";
	icon.style.height = "100%";
	icon.style.fontFamily = "gate";
	icon.style.fontSize = "120px";
	icon.style.display = "flex";
	icon.style.alignItems = "center";
	icon.style.justifyContent = "center";
	icon.style.background = "linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%)";

	var body = dialog.appendChild(window.top.document.createElement('div'));
	body.innerHTML = message;
	body.style.width = "100%";
	body.style.height = "100%";
	body.style.padding = "8px";
	body.style.display = "flex";
	body.style.flexWrap = "wrap";
	body.style.fontSize = "20px";
	body.style.alignItems = "center";
	body.style.justifyContent = "center";
	body.style.backgroundColor = "white";

	modal.onclick = dialog.onclick = icon.onclick = body.onclick = function ()
	{
		modal.hide();
	};

	switch (type)
	{
		case "SUCCESS":
			icon.innerHTML = "&#X1000";
			icon.style.color = "@commit";
			body.style.color = "@commit";
			break;
		case "WARNING":
			icon.innerHTML = "&#X1007";
			icon.style.color = "#666600";
			body.style.color = "#666600";
			break;
		case "ERROR":
			icon.innerHTML = "&#X1001";
			icon.style.color = "@cancel";
			body.style.color = "@cancel";
			break;
	}

	if (timeout)
		window.top.setTimeout(function ()
		{
			modal.hide();
		}, timeout);


	modal.show();
}

Message.success = function (message, timeout)
{
	Message("SUCCESS", message, timeout);
};

Message.warning = function (message, timeout)
{
	Message("WARNING", message, timeout);
};

Message.error = function (message, timeout)
{
	Message("ERROR", message, timeout);
};

Message.show = function (status, timeout)
{
	switch (status.type)
	{
		case "SUCCESS":
			Message.success(status.message, timeout);
			break;
		case "WARNING":
			Message.warning(status.message, timeout);
			break;
		case "ERROR":
			Message.error(status.message, timeout);
			break;
	}

};
