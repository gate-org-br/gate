export default function sanitize(html) {
	const template = document.createElement('template');
	template.innerHTML = html;


	template.content.querySelectorAll('script, style')
		.forEach(e => e.remove());


	template.content.querySelectorAll('*').forEach(el => {
		[...el.attributes].forEach(a => {
			const name = a.name.toLowerCase();
			const value = a.value;
			if (name.startsWith('on'))
				el.removeAttribute(a.name);
			if ((name === 'href' || name === 'src') && /^javascript:/i.test(value))
				el.removeAttribute(a.name);
		});
	});

	return template.innerHTML;
}