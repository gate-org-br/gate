export function master(select)
{
	const id = select.id;
	const idx = select.selectedIndex >= 0 ? select.selectedIndex : 0;
	document.querySelectorAll(`[data-master="${id}"]`).forEach(e =>
	{
		const ondisabled = `data-disabled-on-${idx}`;
		if (e.hasAttribute(ondisabled))
			e.disabled = e.getAttribute(ondisabled) === 'true';
		else if (e.hasAttribute("data-disabled-on"))
			e.disabled = e.getAttribute('data-disabled-on') === 'true';
		else
			e.disabled = false;

		const onhidden = `data-hidden-on-${idx}`;
		if (e.hasAttribute(onhidden))
			e.hidden = e.getAttribute(onhidden) === 'true';
		else if (e.hasAttribute("data-hidden-on"))
			e.hidden = e.getAttribute('data-hidden-on') === 'true';
		else
			e.hidden = false;
	});
}

window.addEventListener("change", event =>
{
	if (event.target.tagName === "SELECT"
		&& event.target.id)
		master(event.target);
});

window.addEventListener("connected", event =>
{
	if (event.target.tagName === "SELECT"
		&& event.target.id)
		master(event.target);
});