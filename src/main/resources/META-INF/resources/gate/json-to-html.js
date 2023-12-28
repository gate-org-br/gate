export default function JSONtoHTML(json)
{
	switch (typeof json)
	{
		case "string":
			return json;
		case "number":
			return String(json);
		case "boolean":
			return String(json);
		case "object":
			if (Array.isArray(json))
				return array(json);
			else
				return object(json);
	}
}

function object(json)
{
	let result = "";
	for (let key in json)
		if (json.hasOwnProperty(key) && json[key])
			result += `<dt>${key}</dt><dd>${JSONtoHTML(json[key])}</dd>`;
	return `<dl>${result}</dl>`;
}

function array(json)
{
	let result = "";
	for (let value of json)
		if (value)
			result += `<li>${JSONtoHTML(value)}</li>`;
	return `<ul>${result}</ul>`;
}

