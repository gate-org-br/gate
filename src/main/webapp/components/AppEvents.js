class AppEvents
{
	static listen()
	{
		let protocol = location.protocol === 'https:' ? "wss://" : "ws://";
		var url = protocol + /.*:\/\/(.*\/.*)\//.exec(location.href)[1] + "/AppEvents";

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