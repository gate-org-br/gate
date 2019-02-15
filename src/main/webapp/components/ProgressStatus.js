class ProgressStatus extends HTMLElement
{
	constructor()
	{
		super();

		var shadow = this.attachShadow({mode: 'open'});

		var title = shadow.appendChild(document.createElement("label"));
		title.innerHTML = "Conectando ao servidor";
		title.style.padding = "2px";
		title.style.display = "flex";
		title.style.marginTop = "10xp";
		title.style.justifyContent = "space-between";
		title.style.height = "20px";
		title.style.fontSize = "20px";

		var progress = shadow.appendChild(document.createElement("progress"));
		progress.style.height = "40px";
		progress.style.marginTop = "10px";
		progress.style.marginBottom = "10px";

		var div = shadow.appendChild(document.createElement("div"));
		div.style.display = "flex";
		div.style.alignItems = "stretch";

		var clock = div.appendChild(document.createElement("label"));
		clock.innerHTML = "00:00:00";
		clock.style.flexGrow = "1";
		clock.style.fontSize = "12px";
		clock.style.textAlign = "left";

		var counter = div.appendChild(document.createElement("label"));
		counter.innerHTML = "1/1000";
		counter.style.flexGrow = "1";
		counter.style.fontSize = "12px";
		counter.style.textAlign = "right";

		var div = shadow.appendChild(document.createElement("div"));
		div.style.padding = "4px";
		div.style.height = "100px";
		div.style.display = "flex";
		div.style.overflow = "auto";
		div.style.marginTop = "10px";
		div.style.borderRadius = "2px";
		div.style.alignItems = "stretch";
		div.style.backgroundColor = "white";

		var logger = div.appendChild(document.createElement("ul"));
		logger.style.margin = "0";
		logger.style.padding = "0";
		logger.style.flexGrow = "1";
		logger.style.listStyleType = "none";
	}

	connectedCallback()
	{
		var colors = new Object();
		colors["PENDING"] = '#000000';
		colors["COMMITED"] = '#006600';
		colors["CANCELED"] = '#660000';

		var title = this.shadowRoot.children[0];
		var progress = this.shadowRoot.children[1];
		var clock = this.shadowRoot.children[2].children[0];
		var counter = this.shadowRoot.children[2].children[1];
		var logger = this.shadowRoot.children[3].children[0];

		var time = 0;
		this.onClockTick = () => clock.innerHTML = new Duration(++time).toString();

		var ws = new WebSocket("ws://" + window.location.host + "/" +
			window.location.pathname.split("/")[1] + "/Progress/" + this.getAttribute('process'));
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
						title.innerHTML = event.text;

						var log = logger.firstElementChild ?
							logger.insertBefore(document.createElement("li"),
								logger.firstElementChild)
							: logger.appendChild(document.createElement("li"));
						log.innerHTML = event.text;
						log.style.height = "16px";
						log.style.display = "flex";
						log.style.alignItems = "center";
					}

					counter.innerHTML = event.toString();

					title.style.color = colors[event.status];
					clock.style.color = colors[event.status];
					counter.style.color = colors[event.status];

					switch (event.status)
					{
						case "COMMITED":
							if (!progress.max)
								this.this.max = 100;
							if (!progress.value)
								this.progress.value = 100;

							this.dispatchEvent(new CustomEvent('commited'));

							this.onClockTick = null;
							break;
						case "CANCELED":
							if (!progress.max)
								this.progress.max = 100;
							if (!progress.value)
								progress.value = 0;

							this.dispatchEvent(new CustomEvent('canceled'));

							this.onClockTick = null;
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

window.addEventListener("load", () =>
	customElements.define('progress-status', ProgressStatus));


