/* global */

import Optional from './optional.js';

export default class GSelection
{
	static getSelectedLink(nodes)
	{
		let href = window.location.href;
		if (href[href.length - 1] === '#')
			href = href.slice(0, -1);
		let parameters = new URLSearchParams(new URL(href).search);
		let elements = Array.from(nodes)
			.filter(e => (e.href && e.href.includes('?'))
					|| (e.formaction && e.formaction.includes('?')));

		var q = elements.filter(e =>
		{
			const args = new URLSearchParams(new URL(e.href || e.formaction).search);
			return args.MODULE === parameters.MODULE
				&& args.SCREEN === parameters.SCREEN
				&& args.ACTION === parameters.ACTION;
		});

		if (q.length === 0)
		{
			var q = elements.filter(e =>
			{
				const args = new URLSearchParams(new URL(e.href || e.formaction).search);
				return args.MODULE === parameters.MODULE && args.SCREEN === parameters.SCREEN;
			});

			if (q.length === 0)
			{
				q = elements.filter(e =>
				{
					const args = new URLSearchParams(new URL(e.href || e.formaction).search);
					return args.MODULE === parameters.MODULE;
				});
			}
		}

		return Optional.of(q[0]);
	}
}