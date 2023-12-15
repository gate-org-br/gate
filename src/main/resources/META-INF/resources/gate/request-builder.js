export default class RequestBuilder
{
	static build(action, method, form)
	{
		if (method === "get")
			return new Request(action);
		else if (method === "delete"
			|| method === "head"
			|| method === "options")
			return new Request(action, {method});
		else if (method === "post"
			|| method === "put"
			|| method === "patch")
			return new Request(action, {method,
				body: new FormData(form),
				headers: {'Content-Type': 'application/x-www-form-urlencoded'}});

	}
}
