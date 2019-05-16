/* global Message */

class ProgressStatus extends HTMLElement
{
	constructor(process)
	{
		super();
		if (process)
			this.setAttribute("process", process);
	}

	connectedCallback()
	{
		var colors = new Object();
		colors["PENDING"] = '#000000';
		colors["COMMITED"] = '#006600';
		colors["CANCELED"] = '#660000';
		colors["CLOSED"] = '#666666';

		var title = this.appendChild(document.createElement("label"));
		title.style.fontSize = "20px";
		title.style.flexBasis = "40px";
		title.innerHTML = "Conectando ao servidor";

		var progress = this.appendChild(document.createElement("progress"));
		progress.style.width = "100%";
		progress.style.flexBasis = "40px";

		var div = this.appendChild(document.createElement("div"));
		div.style.flexBasis = "40px";
		div.style.display = "flex";
		div.style.alignItems = "center";

		var clock = div.appendChild(document.createElement("digital-clock"));
		clock.style.flexGrow = "1";
		clock.style.fontSize = "12px";
		clock.style.display = "flex";
		clock.style.alignItems = "center";
		clock.style.justifyContent = "flex-start";
		clock.innerHTML = "00:00:00";

		var counter = div.appendChild(document.createElement("label"));
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.display = "flex";
		counter.style.alignItems = "center";
		counter.style.justifyContent = "flex-end";
		counter.innerHTML = "...";

		var div = this.appendChild(document.createElement("div"));
		div.style.padding = "4px";
		div.style.display = "flex";
		div.style.overflow = "auto";
		div.style.flexBasis = "120px";
		div.style.borderRadius = "5px";
		div.style.alignItems = "stretch";
		div.style.justifyContent = "center";
		div.style.backgroundColor = "white";

		var logger = div.appendChild(document.createElement("ul"));
		logger.style.flexGrow = "1";
		logger.style.margin = "0";
		logger.style.padding = "0";
		logger.style.listStyleType = "none";

		function log(message)
		{
			var log = logger.firstElementChild ?
				logger.insertBefore(document.createElement("li"),
					logger.firstElementChild)
				: logger.appendChild(document.createElement("li"));
			log.innerHTML = message;
			log.style.height = "16px";
			log.style.display = "flex";
			log.style.alignItems = "center";
		}

		var ws = new WebSocket("ws://" + window.location.host + "/" +
			window.location.pathname.split("/")[1] + "/Progress/" + this.getAttribute('process'));

		ws.onerror = e => log(e.data);

		ws.onclose = e =>
		{
			if (e.code !== 1000)
			{
				title.style.color = colors["CLOSED"];
				clock.style.color = colors["CLOSED"];
				counter.style.color = colors["CLOSED"];
				log(e.reason ? e.reason : "Conexão perdida com o servidor");
			}
		};

		ws.onmessage = (event) =>
		{
			event = JSON.parse(event.data);

			event.toString = function ()
			{
				if (this.done && this.done !== -1)
					if (this.todo && this.todo !== -1)
						return this.done + "/" + this.todo;
					else
						return this.done.toString();
				else
					return "...";
			};

			switch (event.event)
			{
				case "Progress":

					if (event.text !== title.innerHTML)
					{
						log(event.text);
						title.innerHTML = event.text;
					}

					counter.innerHTML = event.toString();

					title.style.color = colors[event.status];
					clock.style.color = colors[event.status];
					counter.style.color = colors[event.status];

					switch (event.status)
					{
						case "COMMITED":
							if (!progress.max)
								progress.max = 100;
							if (!progress.value)
								progress.value = 100;

							this.dispatchEvent(new CustomEvent('commited'));

							clock.setAttribute("paused", "paused");
							break;
						case "CANCELED":
							if (!progress.max)
								progress.max = 100;
							if (!progress.value)
								progress.value = 0;

							this.dispatchEvent(new CustomEvent('canceled'));

							clock.setAttribute("paused", "paused");
							break;
					}

					if (event.todo !== -1)
					{
						progress.max = event.todo;
						if (event.done !== -1)
							progress.value = event.done;
					}

					break;

				case "Redirect":
					this.dispatchEvent(new CustomEvent('redirected', {detail: event.url}));
					break;
			}
		};
	}
}

customElements.define('progress-status', ProgressStatus);


