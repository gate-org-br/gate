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

	static dispatch(path, event)
	{
		for (let target of path)
			if (target.isConnected && target.dispatchEvent)
				return target.dispatchEvent(event);
	}
}


