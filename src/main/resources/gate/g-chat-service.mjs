/* global customElements, fetch */

function path(path)
{
	return location.pathname.replace("/Gate", path);
}

export default class ChatService
{
	static users()
	{
		let url = path("/gate/chat/users");
		return fetch(url)
			.then(response => {
				return response.json();
			}
			);
	}

	static post(peerId, message)
	{
		let url = path(`/gate/chat/post/${peerId}/${encodeURIComponent(message)}`);
		return fetch(url)
			.then(response => response.json());
	}

	static messages(peerId)
	{
		let url = path(`/gate/chat/messages/${peerId}`);
		return fetch(url)
			.then(response => response.json());
	}
}
