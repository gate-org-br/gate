export default class RequestBuilder
{
	static build(method, action, body, contentType)
	{

		let headers = new Headers();
		if (contentType)
			headers.append("Content-Type", contentType);

		if (body instanceof HTMLFormElement)
			body = new FormData(body);
		else if (!body)
			body = "";

		if (method === "get")
			return new Request(action);
		else if (method === "delete"
			|| method === "head"
			|| method === "options")
			return new Request(action, {method});
		else if (method === "post"
			|| method === "put"
			|| method === "patch")
			return new Request(action, {method, headers, body});

	}
}
