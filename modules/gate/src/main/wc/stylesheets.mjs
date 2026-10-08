export default function stylesheets(...filenames)
{
	return Array.from(filenames).map(filename =>
	{
		let link = document.createElement("link");
		link.setAttribute("rel", "stylesheet");
		link.setAttribute("type", "text/css");
			link.setAttribute("href", String(new URL(`./${filename}`, import.meta.url)));
		return link;
	});
}