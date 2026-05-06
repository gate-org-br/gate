import DataURL from './data-url.js';

function reject(response)
{
	return response.text().then(error =>
	{
		if (response.status === 401)
			window.top.window.location = "Gate";
		return Promise.reject(new Error(error));
	});
}

/**
 * A utility class for handling different types of responses from fetch requests.
 * @class
 */
export default class ResponseHandler
{
	/**
	 * Handles a generic response, resolving or rejecting the promise based on the response status.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<Response>} - A promise that resolves if the response is successful. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/data')
	 *   .then(response => ResponseHandler.response(response))
	 *   .then(data => console.log(data))
	 *   .catch(error => console.error(error));
	 */
	static response(response)
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
			return response;
		return reject(response);
	}

	/**
	 * Handles a JSON response, resolving or rejecting the promise based on the response status.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<Object>} - A promise that resolves to a parsed JSON object if the response is successful. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/json-data')
	 *   .then(response => ResponseHandler.json(response))
	 *   .then(jsonData => console.log(jsonData))
	 *   .catch(error => console.error(error));
	 */
	static json(response)
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
			return response.json();
		return reject(response);
	}

	/**
	 * Handles a text response, resolving or rejecting the promise based on the response status.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<string>} - A promise that resolves to the text content if the response is successful. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/text-data')
	 *   .then(response => ResponseHandler.text(response))
	 *   .then(textData => console.log(textData))
	 *   .catch(error => console.error(error));
	 */
	static text(response)
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
			return response.text();
		return reject(response);
	}

	/**
	 * Handles a binary data (Blob) response, resolving or rejecting the promise based on the response status.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<Blob>} - A promise that resolves to a Blob if the response is successful. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/image')
	 *   .then(response => ResponseHandler.blob(response))
	 *   .then(blobData => {
	 *     // Handle the Blob data (e.g., display an image)
	 *     const imageUrl = URL.createObjectURL(blobData);
	 *     document.getElementById('myImage').src = imageUrl;
	 *   })
	 *   .catch(error => console.error(error));
	 */
	static blob(response)
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
			return response.blob();
		return reject(response);
	}

	/**
	 * Handles a response with no content, resolving or rejecting the promise based on the response status.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<void>} - A promise that resolves if the response is successful and has no content. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/delete', { method: 'DELETE' })
	 *   .then(response => ResponseHandler.none(response))
	 *   .then(() => console.log('Deletion successful'))
	 *   .catch(error => console.error(error));
	 */
	static none(response)
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
			return Promise.resolve();
		return reject(response);
	}

	/**
	 * Automatically handles different response types based on the content type, resolving or rejecting the promise.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<string|Object|Blob>} - A promise that resolves to the appropriate response data based on content type. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/data')
	 *   .then(response => ResponseHandler.auto(response))
	 *   .then(data => console.log(data))
	 *   .catch(error => console.error(error));
	 */
	static auto(response)
	{
		if (!response)
			return Promise.resolve();
		if (response.ok)
		{
			let contentType = response.headers.get('content-type');
			if (contentType.startsWith("text/"))
				return response.text();
			else if (contentType === "application/json")
				return response.json();
			else
				return response.blob();
		}
		return reject(response);
	}

	/**
	 * Handles a response by generating a data URL from the response content.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @returns {Promise<string>} - A promise that resolves to a data URL if the response is successful. Otherwise, a rejected promise with an error is returned.
	 * @example
	 * // Usage with fetch:
	 * fetch('https://api.example.com/data')
	 *   .then(response => ResponseHandler.dataURL(response))
	 *   .then(dataURL => console.log(dataURL))
	 *   .catch(error => console.error(error));
	 */
	static dataURL(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
		{
			return Promise.all([response.blob(),
				response.headers.get('content-disposition')])
				.then(([blob, contentDisposition]) =>
				{
					return new Promise((resolve, reject) =>
					{
						const reader = new FileReader();
						reader.onloadend = () =>
						{
							const matcher = contentDisposition
								? contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
								: null;
							const filename = matcher ? matcher[1].trim() : null;

							if (!filename)
								return resolve(reader.result);

							const dataURL = DataURL.parse(reader.result);
							if (dataURL.parameters.name)
								return resolve(reader.result);

							dataURL.parameters.name = filename;
							resolve(dataURL.toString());
						};
						reader.readAsDataURL(blob);
					});
				});
		}
		return reject(response);
	}

	/**
	 * Handles a Server-Sent Events (SSE) response, parsing each message and passing it to the handler.
	 * @static
	 * @param {Response} response - The response object received from a fetch request.
	 * @param {Function} handler - A callback invoked for each parsed SSE message, receiving an object with fields: id, event, data, retry.
	 * @returns {Promise<void>} - A promise that resolves when the stream ends normally. Rejects with an HTTP error if the response is not ok, or with a network error (status 0) if the connection is lost mid-stream.
	 * @example
	 * fetch('https://api.example.com/events')
	 *   .then(response => ResponseHandler.sse(response, message => console.log(message.event, message.data)))
	 *   .catch(error => console.error(error.status, error.message));
	 */
	static async sse(response, handler)
	{
		if (!response)
			return Promise.resolve();
		if (!response.ok)
			return reject(response);

		const reader = response.body.getReader();
		const decoder = new TextDecoder();
		let chunk = '';

		const parse = text =>
		{
			const message = {id: null, retry: null, data: null, event: null};
			text.split(/\n|\r\n|\r/).forEach(line =>
			{
				const index = line.indexOf(':');
				if (index === 0)
					return;

				const field = index > 0 ? line.substring(0, index) : line;
				const value = index > 0
					? line.substring(index + (line[index + 1] === ' ' ? 2 : 1))
					: '';

				if (!(field in message))
					return;

				if (field === 'data' && message[field] !== null)
					message['data'] += "\n" + value;
				else
					message[field] = value;
			});
			return message;
		};

		const dispatch = text =>
		{
			if (text.trim().length > 0)
				handler(parse(text));
		};

		try
		{
			while (true)
			{
				const {done, value} = await reader.read();
				if (done)
					break;

				const data = decoder.decode(value, {stream: true});
				const parts = (chunk + data).split(/(\r\n\r\n|\r\r|\n\n)/g);
				chunk = parts.pop();
				parts.forEach(p => dispatch(p));
			}
		} catch (err)
		{
			const networkError = new Error(err.message);
			networkError.status = 0;
			return Promise.reject(networkError);
		}

		dispatch(chunk);
	}
}