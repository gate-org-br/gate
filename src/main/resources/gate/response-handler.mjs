/* global Promise */

export default class ResponseHandler
{
	static json(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return response.json();

		return response.text()
			.then(error => Promise.reject(new Error(error)));
	}

	static text(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return response.text();

		return response.text()
			.then(error => Promise.reject(new Error(error)));
	}

	static 	blob(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return response.blob();

		return response.text()
			.then(error => Promise.reject(new Error(error)));
	}

	static 	none(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return Promise.resolve();

		return response.text()
			.then(error => Promise.reject(new Error(error)));
	}
}