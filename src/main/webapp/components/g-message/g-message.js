/* global customElements */

class Message extends GWindow
{
	constructor()
	{
		super();
	}

	connectedCallback()
	{
		super.connectedCallback();

		this.classList.add("g-message");

		let close = document.createElement("a");
		close.href = "#";
		close.onclick = () => this.hide();
		close.innerHTML = "<i>&#x1011</i>";
		this.head.appendChild(close);

		this.caption = this.title || "";
		this.body.appendChild(window.top.document.createElement("label")).innerHTML = this.text;
	}

	set type(type)
	{
		this.setAttribute("type", type);
	}

	get type()
	{
		return this.getAttribute("type");
	}

	set text(text)
	{
		this.setAttribute("text", text);
	}

	get text()
	{
		return this.getAttribute("text");
	}

	get title()
	{
		return this.getAttribute("title");
	}

	set title(title)
	{
		return this.setAttribute("title", title);
	}

	attributeChangedCallback()
	{
		if (this.parentNode)
		{
			this.caption = this.title || "";
			this.body.firstChild.innerHTML = this.text;
		}
	}

	static get observedAttributes() {
		return ['type', 'text', "timeout", "title"];
	}
}

customElements.define('g-message', Message);

Message.success = function (text, timeout)
{
	var message = window.top.document.createElement("g-message");
	message.type = "SUCCESS";
	message.text = text;
	message.title = "Sucesso";
	message.timeout = timeout;
	message.show();

	if (timeout)
		window.top.setTimeout(() => message.hide(), timeout);
};

Message.warning = function (text, timeout)
{
	var message = window.top.document.createElement("g-message");
	message.type = "WARNING";
	message.text = text;
	message.title = "Alerta";
	message.timeout = timeout;
	message.show();

	if (timeout)
		window.top.setTimeout(() => message.hide(), timeout);
};

Message.error = function (text, timeout)
{
	var message = window.top.document.createElement("g-message");
	message.type = "ERROR";
	message.text = text;
	message.title = "Erro";
	message.timeout = timeout;
	message.show();

	if (timeout)
		window.top.setTimeout(() => message.hide(), timeout);
};

Message.info = function (text, timeout)
{
	var message = window.top.document.createElement("g-message");
	message.type = "INFO";
	message.text = text;
	message.title = "Informação";
	message.timeout = timeout;
	message.show();

	if (timeout)
		window.top.setTimeout(() => message.hide(), timeout);
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
