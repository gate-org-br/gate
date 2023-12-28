import DOM from './dom.js';

function update()
{
	DOM.traverse(document,
		element => element.hasAttribute("data-counter:target")
			|| element.hasAttribute("data-counter:position")
			|| element.hasAttribute("data-counter:zero-alert"),
		element => DOM.navigate(element, element.getAttribute("data-counter:target"))
			.then(target => element.setAttribute("data-count", target.children.length)));

}

let observer = new MutationObserver(function (mutations)
{
	if (mutations.addedNodes)
		mutations.addedNodes
			.filter(node => node instanceof Element && node.shadowRoot)
			.forEach(node => observer.observe(node.shadowRoot,
					{childList: true,
						subtree: true,
						attributes: true,
						attributeFilter: ['data-counter:target',
							'data-counter:position',
							'data-counter:zero-alert']}));
	update();
});


observer.observe(document,
	{childList: true,
		subtree: true,
		attributes: true,
		attributeFilter: ['data-counter:target',
			'data-counter:position',
			'data-counter:zero-alert']});
update();

