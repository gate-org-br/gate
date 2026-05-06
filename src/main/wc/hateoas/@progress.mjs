import Job from './job.js';
import DataURL from "./data-url.js";

window.addEventListener("@progress", function (event)
{
	const path = event.composedPath();
	const trigger = path[0] || event.target;
	const dialog = document.createElement('g-progress-dialog');

	dialog.caption = trigger.title || "Progresso";
	dialog.show();

	const body = event.detail.form instanceof HTMLFormElement
		? new FormData(event.detail.form)
		: event.detail.form;

	const connect = (url, uuid = null) =>
		fetch(url, url === event.detail.action
			? {method: event.detail.method, body}
			: {})
			.then(response => Job.from(response))
			.then(job =>
			{
				job.addEventListener('Progress', e =>
				{
					const detail = JSON.parse(e.detail);
					dialog.dispatchEvent(new CustomEvent('Progress', {detail}));
				});
				job.addEventListener('Result', e =>
				{
					const result = JSON.parse(e.detail);
					event.success(path, new DataURL(result.contentType || 'text/plain;charset=utf-8',
						result.data, result.filename ? {name: result.filename} : {}).toString());
				});

				job.addEventListener('Redirect', e => dialog.dispatchEvent(new CustomEvent('Redirect', {detail: JSON.parse(e.detail)})));
				job.addEventListener('Finish', () => event.resolve(path));
				job.addEventListener('Failure', e => event.failure(path, JSON.parse(e.detail)));

				return job.start();
			})
			.catch(err =>
			{
				const reconnect = err.uuid || uuid;
				if (!reconnect)
					return event.failure(path, err);
				dialog.dispatchEvent(new CustomEvent('error', {detail: {text: "Reconnecting to server", uuid: reconnect}}));
				setTimeout(() => connect(`Progress?uuid=${encodeURIComponent(reconnect)}`, reconnect), 1000);
			});
	connect(event.detail.action);
});
