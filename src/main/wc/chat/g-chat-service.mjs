/* global customElements, fetch */
import Message from './g-message.mjs';

function path(path)
{
	return location.pathname.replace("/Gate", path);
}

export default class GChatService
{
	static host()
	{
		let url = path(`/gate/chat/host`);
		return fetch(url).then(response =>
		{
			return response.ok ?
				response.json()
				: response.text().then(message =>
			{
				throw new Error(message);
			});
		});
	}

	static peers()
	{
		let url = path(`/gate/chat/peers`);
		return fetch(url).then(response =>
		{
			return response.ok ?
				response.json()
				: response.text().then(message =>
			{
				throw new Error(message);
			});
		});
	}

	static peer(peerId)
	{
		let url = path(`/gate/chat/peers/${peerId}`);
		return fetch(url).then(response =>
		{
			return response.ok ?
				response.json()
				: response.text().then(message =>
			{
				throw new Error(message);
			});
		});
	}

	static messages(peerId)
	{
		let url = path(`/gate/chat/peers/${peerId}/messages`);
		return fetch(url).then(response =>
		{
			return response.ok ?
				response.json()
				: response.text().then(message =>
			{
				throw new Error(message);
			});
		});
	}

	static post(peerId, message)
	{
		let url = path(`/gate/chat/peers/${peerId}/messages`);
		return fetch(url, {body: message, method: "post"}).then(response =>
		{
			return response.ok ?
				response.text()
				: response.text().then(message =>
			{
				throw new Error(message);
			});
		});
	}

	static received(peerId)
	{
		let url = path(`/gate/chat/peers/${peerId}/messages`);
		return fetch(url, {method: "patch"}).then(response =>
		{
			return response.ok ?
				response.text()
				: response.text().then(message =>
			{
				throw new Error(message);
			});
		});
	}
}
