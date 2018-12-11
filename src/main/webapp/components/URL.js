function URL(value)
{
	this.value = value;

	this.setParameter = function (name, value)
	{
		if (this.value.indexOf("?") === -1)
			this.value += "?";
		if (this.value[this.value.length - 1] !== '?')
			this.value += "&";
		this.value += name + "=" + value;
		return this;
	};

	this.setModule = function (module)
	{
		this.setParameter("MODULE", module);
		return this;
	};

	this.setScreen = function (screen)
	{
		this.setParameter("SCREEN", screen);
		return this;
	};

	this.setAction = function (action)
	{
		this.setParameter("ACTION", action);
		return this;
	};

	this.get = function (callback)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");

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

			request.open('GET', this.value, true);
			request.send(null);
		} else
		{
			request.open('GET', this.value, false);
			request.send(null);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.post = function (data, callback)
	{
		var request =
			window.XMLHttpRequest ?
			new XMLHttpRequest() :
			new ActiveXObject("Microsoft.XMLHTTP");
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

			request.open("POST", this.value, true);
			request.send(data);
		} else
		{
			request.open("POST", this.value, false);
			request.send(data);
			if (request.status === 200)
				return request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.go = function ()
	{
		window.location.href = this.toString();
		return this;
	};

	this.toString = function ()
	{
		return this.value;
	};

	this.populate = function (css)
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
	};

	this.check = function ()
	{
		return !this.value.match(/([?][{][^}]*[}])/g)
			&& !this.value.match(/([@][{][^}]*[}])/g);
	};

	this.resolve = function ()
	{
		var result = this.value;
		var parameters = this.value.match(/([?][{][^}]*[}])/g);
		if (parameters)
			for (var i = 0; i < parameters.length; i++)
			{
				var parameter = decodeURIComponent
					(prompt(decodeURIComponent(parameters[i].substring(2, parameters[i].length - 1))));
				if (!parameter)
					return false;
				result = result.replace(parameters[i], encodeURIComponent(parameter));
			}

		var parameters = this.value.match(/([@][{][^}]*[}])/g);
		if (parameters)
			for (var i = 0; i < parameters.length; i++)
			{
				var parameter = document.getElementById(parameters[i].substring(2, parameters[i].length - 1));
				if (!parameter || !parameter.value)
					return false;
				result = result.replace(parameters[i], encodeURIComponent(parameter.value));
			}

		this.value = result;
		return true;
	};
}