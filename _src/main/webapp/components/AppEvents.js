class AppEvents
{
	static listen()
	{
		var url = "ws://" + /.*:\/\/(.*\/.*)\//.exec(window.location.href)[1] + "/AppEvents";

		if (!AppEvents.connection || AppEvents.connection.readyState === 3)
		{
			AppEvents.connection = new WebSocket(url);

			AppEvents.connection.onmessage = function (event)
			{
				event = JSON.parse(event.data);
				window.dispatchEvent(new CustomEvent(event.type, {detail: event.detail}));
			};

			AppEvents.connection.onopen = function ()
			{
			};

			AppEvents.connection.onclose = function ()
			{
				AppEvents.connection = new WebSocket(url);
			};
		}
	}
}