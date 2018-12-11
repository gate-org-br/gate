if (!document.querySelectorAll)
	window.location = '../gate/NAVI.jsp';

function resolve(string)
{
	var result = string;
	var parameters = string.match(/([?][{][^}]*[}])/g);
	if (parameters)
		for (var i = 0; i < parameters.length; i++)
		{
			var parameter = prompt(decodeURIComponent(parameters[i]
				.substring(2, parameters[i].length - 1)));
			if (parameter === null)
				return null;
			result = result.replace(parameters[i], encodeURIComponent(parameter));
		}

	var parameters = string.match(/([@][{][^}]*[}])/g);
	if (parameters)
		parameters.forEach(function (e)
		{
			var parameter = document.getElementById(e.substring(2, e.length - 1));
			result = result.replace(e, parameter ? encodeURIComponent(parameter.value) : "");
		});

	return result;
}

window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("select")).forEach(function (element)
	{
		element.onclick = function (e)
		{
			e = e ? e : window.event;
			if (e.stopPropagation)
				e.stopPropagation();
			else
				e.cancelBubble = true;
		};
	});

	Array.from(document.querySelectorAll("input.SELECTOR")).forEach(function (element)
	{
		element.onchange = function ()
		{
			var selector = 'input[type="checkbox"][name="' + this.getAttribute('data-target') + '"]';
			Array.from(document.querySelectorAll(selector)).forEach(target => target.checked = element.checked);
		};
	});

	Array.from(document.querySelectorAll("td > label > span, td > label > i")).forEach(function (e)
	{
		e.parentNode.style.position = 'relative';
	});

	Array.from(document.querySelectorAll("fieldset > label > span > span + input, fieldset > label > span > span + input")).forEach(function (e)
	{
		e.style.paddingRight = "20px";
	});
});