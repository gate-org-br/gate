export function master(select)
{
	const id = select.id;
	const idx = select.selectedIndex >= 0 ? select.selectedIndex : 0;
	document.querySelectorAll(`[data-master="${id}"]`).forEach(e =>
	{
		const ondisabled = `data-on-${idx}:disabled`;
		if (e.hasAttribute(ondisabled))
			e.disabled = e.getAttribute(ondisabled) === 'true';
		else if (e.hasAttribute("data-on-x:disabled"))
			e.disabled = e.getAttribute('data-on-x:disabled') === 'true';
		else
			e.disabled = false;

		const onhidden = `data-on-${idx}:hidden`;
		if (e.hasAttribute(onhidden))
			e.hidden = e.getAttribute(onhidden) === 'true';
		else if (e.hasAttribute("data-on-x:hidden"))
			e.hidden = e.getAttribute('data-on-x:hidden') === 'true';
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