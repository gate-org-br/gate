/* global customElements, Modal */

class Message extends Window
{
	constructor(options)
	{
		super();
		this.classList.add("g-message");
		this.classList.add(options.type);
		this.caption = options.title || "";

		let close = new GCommand();
		close.action = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);

		this.body.appendChild(window.top.document.createElement('label')).innerHTML = options.message;

		if (options.timeout)
			window.top.setTimeout(() => this.hide(), options.timeout);

		this.show();
	}
}

customElements.define('g-message', Message);

Message.success = function (message, timeout)
{
	new Message({type: "SUCCESS", title: "Sucesso", message: message, timeout: timeout});
};

Message.warning = function (message, timeout)
{
	new Message({type: "WARNING", title: "Alerta", message: message, timeout: timeout});
};

Message.error = function (message, timeout)
{
	new Message({type: "ERROR", title: "Erro", message: message, timeout: timeout});
};

Message.info = function (message, timeout)
{
	new Message({type: "INFO", title: "Informação", message: message, timeout: timeout});
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
		case "INFO":
			Message.info(status.message, timeout);
			break;
	}

};
