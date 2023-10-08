/**
 * Utility class for handling responses from HTTP requests.
 */
export default class ResponseHandler
{

	/**
	 * Returns a promise that resolves to the JSON body of the response,
	 * or rejects with an error message if the response is not successful.
	 *
	 * @param {Response} response - The response object to handle.
	 * @returns {Promise} A promise that resolves to the JSON body of the response,
	 *                    or rejects with an error message.
	 */
	static json(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return response.json();

		return response.text().then((error) => Promise.reject(new Error(error)));
	}

	/**
	 * Returns a promise that resolves to the text body of the response,
	 * or rejects with an error message if the response is not successful.
	 *
	 * @param {Response} response - The response object to handle.
	 * @returns {Promise} A promise that resolves to the text body of the response,
	 *                    or rejects with an error message.
	 */
	static text(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return response.text();

		return response.text().then((error) => Promise.reject(new Error(error)));
	}

	/**
	 * Returns a promise that resolves to the blob body of the response,
	 * or rejects with an error message if the response is not successful.
	 *
	 * @param {Response} response - The response object to handle.
	 * @returns {Promise} A promise that resolves to the blob body of the response,
	 *                    or rejects with an error message.
	 */
	static blob(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return response.blob();

		return response.text().then((error) => Promise.reject(new Error(error)));
	}

	/**
	 * Returns a promise that resolves with no value if the response is successful,
	 * or rejects with an error message if the response is not successful.
	 *
	 * @param {Response} response - The response object to handle.
	 * @returns {Promise} A promise that resolves with no value if the response is successful,
	 *                    or rejects with an error message.
	 */
	static none(response)
	{
		if (!response)
			return Promise.resolve();

		if (response.ok)
			return Promise.resolve();

		return response.text().then((error) => Promise.reject(new Error(error)));
	}
}
