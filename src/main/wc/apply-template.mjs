export default function applyTemplate(element, template)
{
	for (let parent = element.parentNode; parent;
		parent = parent.parentNode)
		if (parent === document)
		{
			document.head.insertAdjacentHTML('beforeend', template.innerHTML);
			return;
		} else if (parent instanceof ShadowRoot)
		{
			if (!parent.querySelector(`[data-element='${element.getAttribute("is") || element.tagName}]'`))
				parent.appendChild(template.content.cloneNode(true));
			return;
		}

}