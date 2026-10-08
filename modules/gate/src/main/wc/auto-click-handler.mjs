Array.from(document.querySelectorAll("*[data-auto-click]"))
	.forEach(e => setTimeout(() => e.click(), 0));

Array.from(document.querySelectorAll("*[data-auto-click-when-unique]"))
	.filter(e => e.parentNode.children.length === 1)
	.forEach(e => setTimeout(() => e.click(), 0));
