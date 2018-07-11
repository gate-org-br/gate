if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';


///////////////////////////////////////////////////////////////////////////////////////////////////
// Gate
///////////////////////////////////////////////////////////////////////////////////////////////////

function select(id)
{
	return document
			.getElementById(id);
}

function search(css)
{
	return Array.from(document
			.querySelectorAll(css));
}

function $(e)
{
	return new Gate(e);
}

function resolve(string)
{
	var result = string;
	var parameters = string.match(/([?][{][^}]*[}])/g);
	if (parameters)
		parameters.forEach(function (e)
		{
			var parameter = prompt(e.substring(2, e.length - 1));
			result = result.replace(e, parameter && parameter.length !== 0 ?
					encodeURIComponent(parameter) : "!{null}");
		});

	var parameters = string.match(/([@][{][^}]*[}])/g);
	if (parameters)
		parameters.forEach(function (e)
		{
			var parameter = select(e.substring(2, e.length - 1));
			result = result.replace(e, parameter ? encodeURIComponent(parameter.value) : "!{null}");
		});

	return result;
}

function Gate(e)
{
	if (typeof e === 'string')
		e = document.querySelectorAll(e);
	if (e instanceof NodeList)
		e = Array.from(e);
	if (e.tagName)
	{
		this.children = function ()
		{
			var result = [e];
			for (var i = 0; i < arguments.length; i++)
				result = children(result, arguments[i]);
			return result;

			function children(array, tagName)
			{
				var result = [];
				for (var i = 0; i < array.length; i++)
					for (var j = 0; j < array[i].childNodes.length; j++)
						if (array[i].childNodes[j].tagName && array[i].childNodes[j].tagName.toLowerCase() === tagName.toLowerCase())
							result.push(array[i].childNodes[j]);
				return result;
			}
		};

		this.siblings = function (selector)
		{
			return selector ? Array.from(e.parentNode.childNodes)
					.filter(function (node)
					{
						return node.matches
								&& node.matches(selector);
					}) : Array.from(e.parentNode.childNodes);
		};
		this.get = function (name)
		{
			return e[name];
		};
		this.set = function (name, value)
		{
			e[name] = value;
		};
		this.def = function (name, value)
		{
			if (!e[name])
				e[name] = value;
		};
		this.addEventListener = function (name, value)
		{
			e.addEventListener(name, value);
		};
		this.getAttribute = function (name)
		{
			return e.getAttribute(name);
		};
		this.setAttribute = function (name, value)
		{
			e.setAttribute(name, value);
		};
		this.getForm = function ()
		{
			var form = e.parentNode;
			while (form
					&& form.tagName.toLowerCase()
					!== 'form')
				form = form.parentNode;
			return form;
		};
		this.getNext = function ()
		{
			var next = e.nextSibling;
			while (next && next.nodeType !== 1)
				next = next.nextSibling;
			return next;
		};
		this.getPrev = function ()
		{
			var prev = e.previousSibling;
			while (prev && prev.nodeType !== 1)
				prev = prev.previousSibling;
			return prev;
		};
		this.search = function (css)
		{
			return Array.from(e.querySelectorAll(css));
		};
	} else if (e instanceof Array)
	{
		this.get = function (name)
		{
			return e.map(function (node)
			{
				return node[name];
			});
		};
		this.set = function (name, value)
		{
			e.forEach(function (node)
			{
				node[name] = value;
			});
		};
		this.def = function (name, value)
		{
			e.forEach(function (node)
			{
				if (node[name])
					node[name] = value;
			});
		};
		this.addEventListener = function (name, value)
		{
			e.forEach(function (node)
			{
				node.addEventListener(name, value);
			});
		};
		this.getAttribute = function (name)
		{
			return e.map(function (node)
			{
				return node.getAttribute(name);
			});
		};
		this.setAttribute = function (name, value)
		{
			e.forEach(function (node)
			{
				node.setAttribute(name, value);
			});
		};
		this.children = function ()
		{
			var result = e;
			for (var i = 0; i < arguments.length; i++)
				result = children(result, arguments[i]);
			return result;

			function children(array, tagName)
			{
				var result = [];
				for (var i = 0; i < array.length; i++)
					for (var j = 0; j < array[i].childNodes.length; j++)
						if (array[i].childNodes[j].tagName && array[i].childNodes[j].tagName.toLowerCase() === tagName.toLowerCase())
							result.push(array[i].childNodes[j]);
				return result;
			}
		};
		this.filter = function (selector)
		{
			switch (typeof selector)
			{
				case "string":
					return e.filter(function (node)
					{
						return node && node.matches
								&& node.matches(selector);
					});
				case "boolean":
					return e.filter(function (node)
					{
						return selector ===
								(node !== null && node !== undefined);
					});
				case "function":
					return e.filter(selector);
			}
		};

		this.toArray = function ()
		{
			return e;
		};
	}
}

///////////////////////////////////////////////////////////////////////////////////////////////////
// Loading
///////////////////////////////////////////////////////////////////////////////////////////////////

window.addEventListener("load", function ()
{
	$("form").addEventListener("keydown", function (e)
	{
		if (document.activeElement.tagName.toLowerCase() === 'input'
				|| document.activeElement.tagName.toLowerCase() === 'select')
		{
			e = e ? e : window.event;
			if (e.keyCode === ENTER)
			{
				e.preventDefault();
				var button = document.createElement("button");
				button.style.display = 'none';
				this.appendChild(button);
				button.click();
				this.removeChild(button);
			}
		}
	});
	$('select').set("onclick", function (e)
	{
		e = e ? e : window.event;
		if (e.stopPropagation)
			e.stopPropagation();
		else
			e.cancelBubble = true;
	});
	$('input.SELECTOR').set("onchange", function ()
	{
		$('input[type="checkbox"][name="' + this.getAttribute('data-target') + '"]').set("checked", this.checked);
	});

	search("td > label > span, td > label > i").forEach(function (e)
	{
		e.parentNode.style.position = 'relative';
	});
	search("fieldset > label > span > span + input, fieldset > label > span > span + input").forEach(function (e)
	{
		e.style.paddingRight = "20px";
	});
});

	