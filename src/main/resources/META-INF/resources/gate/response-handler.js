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
					.then(([blob, contentDisposition]) => {
						return new Promise((resolve, reject) => {
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
}