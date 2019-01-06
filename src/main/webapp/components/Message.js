class Message extends Modal
{
	constructor(options)
	{
		super();

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.classList.add("Message");
		dialog.classList.add(options.type);

		var icon = dialog.appendChild(window.top.document.createElement('label'));
		switch (options.type)
		{
			case "SUCCESS":
				icon.innerHTML = "&#X1000;";
				break;
			case "WARNING":
				icon.innerHTML = "&#X1007;";
				break;
			case "ERROR":
				icon.innerHTML = "&#X1001;";
				break;
		}

		var message = dialog.appendChild(window.top.document.createElement('label'));
		message.innerHTML = options.message;

		if (options.timeout)
			window.top.setTimeout(() => this.hide(), options.timeout);

		this.show();
	}
}

Message.success = function (message, timeout)
{
	new Message({type: "SUCCESS", message: message, timeout: timeout});
};

Message.warning = function (message, timeout)
{
	new Message({type: "WARNING", message: message, timeout: timeout});
};

Message.error = function (message, timeout)
{
	new Message({type: "ERROR", message: message, timeout: timeout});
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
