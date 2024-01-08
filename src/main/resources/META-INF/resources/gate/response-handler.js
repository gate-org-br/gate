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
		return response.text().then(error => Promise.reject(new Error(error)));
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
		return response.text().then(error => Promise.reject(new Error(error)));
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
		return response.text().then(error => Promise.reject(new Error(error)));
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
		return response.text().then(error => Promise.reject(new Error(error)));
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
		return response.text().then(error => Promise.reject(new Error(error)));
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
		return response.text().then(error => Promise.reject(new Error(error)));
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
				response.headers.get('content-type'),
				response.headers.get('content-disposition')])
				.then(([blob, contentType, contentDisposition]) => {
					return new Promise((resolve, reject) => {
						const reader = new FileReader();
						reader.onloadend = () => {
							const base64data = reader.result.split(',')[1];
							const filenameMatch = contentDisposition && contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
							const filename = filenameMatch ? filenameMatch[1].trim() : null;
							resolve(filename
								? `data:${contentType || 'application/octet-stream'};filename=${encodeURIComponent(filename)};base64,${base64data}`
								: `data:${contentType || 'application/octet-stream'};base64,${base64data}`);
						};
						reader.readAsDataURL(blob);
					});
				});
		}

		return response.text().then(error => Promise.reject(new Error(error)));
	}

}
