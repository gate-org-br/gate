/**
 * EventHandler class provides utility methods for handling events.
 * @class
 */
export default class EventHandler extends HTMLElement
{
	/**
	 * Cancels the default action, propagation, and immediate propagation of an event.
	 * @static
	 * @param {Event} event - The event object to be cancelled.
	 * @returns {void}
	 * @example
	 * // Import the EventHandler class
	 * import EventHandler from './gate/event-handler.js';
	 *
	 * // Use the cancel method to prevent the default action, propagation, and immediate propagation of an event
	 * document.addEventListener('click', (event) => {
	 *     EventHandler.cancel(event);
	 * });
	 */
	static cancel(event)
	{
		event.preventDefault();
		event.stopPropagation();
		event.stopImmediatePropagation();
	}

	static dispatch(path, ...events)
	{
		let target = path.filter(e => e.isConnected && e.dispatchEvent)[0];
		if (target)
			events.forEach(event => target.dispatchEvent(event));
	}
}


