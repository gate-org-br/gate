/* global customElements, fetch */
import GMessageDialog from './g-message-dialog.mjs';
import ResponseHandler from './response-handler.mjs';

function path(path)
{
	return location.pathname.replace("/Gate", path);
}

export default class GChatService
{
	static host()
	{
		let url = path(`/gate/chat/host`);
		return fetch(url).then(response => ResponseHandler.json(response));
	}

	static peers()
	{
		let url = path(`/gate/chat/peers`);
		return fetch(url).then(response => ResponseHandler.json(response));
	}

	static peer(peerId)
	{
		let url = path(`/gate/chat/peers/${peerId}`);
		return fetch(url).then(response => ResponseHandler.json(response));
	}

	static messages(peerId)
	{
		let url = path(`/gate/chat/peers/${peerId}/messages`);
		return fetch(url).then(response => ResponseHandler.json(response));
	}

	static post(peerId, message)
	{
		let url = path(`/gate/chat/peers/${peerId}/messages`);
		return fetch(url, {body: message, method: "post"}).then(response => ResponseHandler.text(response));
	}

	static received(peerId)
	{
		let url = path(`/gate/chat/peers/${peerId}/messages`);
		return fetch(url, {method: "patch"}).then(response => ResponseHandler.text(response));
	}
}
