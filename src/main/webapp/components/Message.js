class Message extends Modal
{
	constructor(options)
	{
		super();

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.className = "Message";

		var body = dialog.appendChild(window.top.document.createElement('div'));
		body.className = options.type;
		body.innerHTML = options.message;

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
