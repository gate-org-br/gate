window.setInterval(() =>
{
	Array.from(document.querySelectorAll("*[data-switch]")).forEach(node =>
	{
		let innerHTML = node.innerHTML;
		let dataSwitch = node.getAttribute('data-switch');
		node.innerHTML = dataSwitch;
		node.setAttribute('data-switch', innerHTML);
	});
}, 500);