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
		if (window.XMLHttpRequest)
			this.request = new XMLHttpRequest();
		else
			this.request = new ActiveXObject("Microsoft.XMLHTTP");

		if (callback)
		{
			this.request.onreadystatechange = function ()
			{
				switch (this.readyState)
				{
					case 4:
						if (this.status === 200)
							callback(this.responseText);
						else
							alert("Erro ao obter dados do servidor");
						break;
				}
			};

			this.request.open('GET', this.value, true);
			this.request.send(null);
		} else
		{
			this.request.open('GET', this.value, false);
			this.request.send(null);
			if (this.request.status === 200)
				return this.request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.post = function (data, callback)
	{
		if (window.XMLHttpRequest)
			this.request = new XMLHttpRequest();
		else
			this.request = new ActiveXObject("Microsoft.XMLHTTP");

		if (callback)
		{
			this.request.onreadystatechange = function ()
			{
				switch (this.readyState)
				{
					case 4:
						if (this.status === 200)
							callback(this.responseText);
						else
							alert("Erro ao obter dados do servidor");
						break;
				}
			};

			this.open("POST", this.value, true);
			this.setRequestHeader("Connection", "close");
			this.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			this.send(data);
		} else
		{
			this.request.open("POST", this.value, true);
			this.request.setRequestHeader("Connection", "close");
			this.request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			this.request.send(data);
			if (this.request.status === 200)
				return this.request.responseText;
			else
				alert("Erro ao obter dados do servidor");
		}

		return this;
	};

	this.populate = function (css)
	{
		var selects = Array.from(document.querySelectorAll(css));
		for (var i = 0; i < selects.length; i++)
		{
			selects[i].value = undefined;
			selects[i].innerHTML = "<option value=''></option>";
		}

		this.get(function (options)
		{
			if (options)
			{
				options = JSON.parse(options);
				for (var i = 0; i < selects.length; i++)
					for (var j = 0; j < options.length; j++)
					{
						var option = selects[i].appendChild(document.createElement("option"));
						option.innerHTML = options[j].label;
						option.setAttribute('value', options[j].value);
					}
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
						(prompt(parameters[i].substring(2, parameters[i].length - 1)));
				if (!parameter)
					return false;
				result = result.replace(parameters[i], encodeURIComponent(parameter));
			}

		var parameters = this.value.match(/([@][{][^}]*[}])/g);
		if (parameters)
			for (var i = 0; i < parameters.length; i++)
			{
				var parameter = select(parameters[i].substring(2, parameters[i].length - 1));
				if (!parameter || !parameter.value)
					return false;
				result = result.replace(parameters[i], encodeURIComponent(parameter.value));
			}

		this.value = result;
		return true;
	};
}