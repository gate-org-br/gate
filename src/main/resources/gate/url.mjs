import resolve from './resolve.mjs';
import Populator from './populator.mjs';

export default class URL
{
	constructor(value)
	{
		this.value = value;
	}

	setParameter(name, value)
	{
		if (this.value.indexOf("?") === -1)
			this.value += "?";
		if (this.value[this.value.length - 1] !== '?')
			this.value += "&";
		this.value += name + "=" + value;
		return this;
	}

	setModule(module)
	{
		this.setParameter("MODULE", module);
		return this;
	}

	setScreen(screen)
	{
		this.setParameter("SCREEN", screen);
		return this;
	}

	setAction(action)
	{
		this.setParameter("ACTION", action);
		return this;
	}

	setContentType(contentType)
	{
		this.contentType = contentType;
		return this;
	}

	get(callback, dwload)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");

		if (dwload)
			request.addEventListener("progress", dwload, false);

		if (this.contentType)
			request.setRequestHeader('Content-type', contentType);

		if (callback)
		{
			request.onreadystatechange = function ()
			{
				switch (this.readyState)
				{
					case 4:
						switch (this.status)
						{
							case 200:
								callback(this.responseText);
								break;
							case 420:
								alert(this.responseText);
								break;
							default:
								alert("Erro ao obter dados do servidor");
								break;
						}
						break;
				}
			};

			request.open('GET', resolve(this.value), true);
			request.send(null);
		} else
		{
			request.open('GET', resolve(this.value), false);
			request.send(null);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	}

	post(data, callback, upload, dwload)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");

		if (dwload)
			request.addEventListener("progress", dwload, false);
		if (upload)
			request.upload.addEventListener("progress", upload, false);

		if (this.contentType)
			request.setRequestHeader('Content-type', contentType);

		if (callback)
		{
			request.onreadystatechange = function ()
			{
				switch (this.readyState)
				{
					case 4:
						switch (this.status)
						{
							case 200:
								callback(this.responseText);
								break;
							case 420:
								alert(this.statusText);
								break;
							default:
								alert("Erro ao obter dados do servidor");
								break;
						}
						break;
				}
			};

			request.open("POST", resolve(this.value), true);
			request.send(data);
		} else
		{
			request.open("POST", resolve(this.value), false);
			request.send(data);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	}

	go()
	{
		window.location.href = resolve(this.value);
		return this;
	}

	toString()
	{
		return this.value;
	}

	populate(css)
	{
		this.get(function (options)
		{
			if (options)
			{
				options = JSON.parse(options);
				var populator = new Populator(options);
				Array.from(document.querySelectorAll(css))
					.forEach(element => populator.populate(element));
			}
		});
		return this;
	}

	static parse_query_string(query)
	{
		var query_string = {};
		if (query.includes('?'))
			query = query.split('?')[1];
		var vars = query.split("&");
		for (var i = 0; i < vars.length; i++)
		{
			var pair = vars[i].split("=");
			var key = decodeURIComponent(pair[0]);
			var value = decodeURIComponent(pair[1]);
			if (typeof query_string[key] === "undefined")
				query_string[key] = decodeURIComponent(value);
			else if (typeof query_string[key] === "string")
				query_string[key] = [query_string[key], decodeURIComponent(value)];
			else
				query_string[key].push(decodeURIComponent(value));
		}
		return query_string;
	}
}