export default class RequestBuilder
{
	static build(method, action, body, contentType)
	{
		if (!action || action === "#")
			action = "data:text/plain,";

		const headers = new Headers();
		headers.append("X-G-Fragment", "1");
		switch (method)
		{
			case "get":
				return new Request(action, {headers});
			case "delete":
			case "head":
			case "options":
				return new Request(action, {headers, method});
			case "post":
			case "put":
			case "patch":
				if (contentType)
					headers.append("Content-Type", contentType);

				if (body instanceof HTMLFormElement)
					body = new FormData(body);
				else if (!body)
					body = "";

				return new Request(action, {method, headers, body});
		}
	}
}
