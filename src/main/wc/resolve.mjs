export default function resolve(string)
{
	var result = string;

	var parameters = string.match(/([@][{][^}]*[}])/g);
	if (parameters)
		parameters.forEach(function (e)
		{
			var parameter = document.getElementById(e.substring(2, e.length - 1));
			result = result.replace(e, parameter ? encodeURIComponent(parameter.value) : "");
		});

	var parameters = string.match(/([!][{][^}]*[}])/g);
	if (parameters)
		for (var i = 0; i < parameters.length; i++)
		{
			var parameter = confirm(decodeURIComponent(parameters[i]
				.substring(2, parameters[i].length - 1)));
			result = result.replace(parameters[i], parameter ? "true" : "false");
		}

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

	return result;
}