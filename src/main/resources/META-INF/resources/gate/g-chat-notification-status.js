/* global customElements, fetch */

function getKey(peerId)
{
	return peerId ?
		'g-chat-notification-status' + peerId
		: 'g-chat-notification-status';
}

export default class GChatNotificatorStatus
{
	static get(peerId)
	{
		let key = getKey(peerId);
		let value = localStorage.getItem(key);
		let defaultStatus = peerId ? 'enabled' : 'disabled';
		return value || defaultStatus;
	}

	static set(peerId, value)
	{
		let key = getKey(peerId);
		return localStorage.setItem(key, value)
	}
}
