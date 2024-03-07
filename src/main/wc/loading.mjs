const id = 'gate-loading-style';

const style =
	`@keyframes loading
	{
		0% { width: 0 }
		100% { width: 100% }
	}`;

document.head.insertAdjacentHTML('beforeend', `<style id='${id}'>${style}</style>`);

export default function loading(element)
{
	if (element?.parentNode?.shadowRoot)
	{
		if (!element.parentNode.shadowRoot.getElementById(id))
		{
			const styleElement = document.createElement('style');
			styleElement.id = id;
			styleElement.innerHTML = style;
			element.parentNode.shadowRoot.appendChild(styleElement);
		}
	}
}
