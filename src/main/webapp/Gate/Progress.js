function Progress(process)
{
	var ws = new WebSocket("ws://" + window.location.host + "/" +
		window.location.pathname.split("/")[1] + "/Progress/" + process);
	ws.onmessage = function (event)
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
				for (var i = 0; i < document.all.length; i++)
					if (document.all[i].onProgressEvent)
						document.all[i].onProgressEvent(event);
				break;

			case "Redirect":
				window.location.href = event.url;
				break;
		}
	};
}