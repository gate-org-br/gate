/* global customElements, fetch */
import Message from './g-message.mjs';

function path(path)
{
	return location.pathname.replace("/Gate", path);
}

export default class GChatService
{
	static peers()
	{
		let url = path(`/gate/chat/peers`);
		return fetch(url).then(response => response.json());
	}

	static messages(peerId)
	{
		let url = path(`/gate/chat/messages/${peerId}`);
		return fetch(url)
			.then(response => response.json());
	}

	static post(peerId, message)
	{
		let url = path(`/gate/chat/post/${peerId}/${encodeURIComponent(message)}`);
		return fetch(url)
			.then(response => response.json());
	}

	static received(peerId)
	{
		let url = path(`/gate/chat/received/${peerId}`);
		fetch(url).then(response => response.json()).then(response =>
		{
			if (response.status === 'error')
				Message.error(response.message);
		});
	}

	static summary(peerId)
	{
		let url = path(peerId ? `/gate/chat/summary/${peerId}`
			: "/gate/chat/summary");
		return fetch(url)
			.then(response => response.json());
	}
}
