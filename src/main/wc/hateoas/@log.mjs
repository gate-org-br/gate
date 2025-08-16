/* global fetch */

import './trigger.js';
import DataURL from './data-url.js';

window.addEventListener("@log", function (event)
{
	const path = event.composedPath();
	const trigger = path[0] || event.target;
	const {method, action, form, parameters = [], context} = event.detail;
	const [label = "", level = "log"] = parameters;

	event.resolve(path);

	try {
		const logger = (console[level] || console.log).bind(console);
		const baseInfo = {
			'@': event.type,
			label,
			element: trigger?.tagName || '(node)',
			method,
			form: form ? (form.getAttribute?.('name') || form.tagName) : null,
			context
		};

		if (typeof action === 'string' && action.startsWith('data:')) {
			const parsed = DataURL.parse(action);
			logger("[Gate]@log", {...baseInfo, dataURL: parsed});
		} else {
			logger("[Gate]@log", {...baseInfo, action});
		}
	} catch (e) {
		console.warn("[Gate] @log error:", e);
	} finally {
		event.success(path);
	}
});
;