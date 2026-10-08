export default class Proxy
{
	static create(element)
	{
		let clone = element.cloneNode(true);
		clone.onclick = event =>
		{
			element.click();
			event.preventDefault();
		};
		return clone;
	}

	static	copyStyle(source, target)
	{
		target = target.style;
		source = getComputedStyle(source);
		for (var i = source.length; i-- > 0; ) {
			var name = source[i];
			alert(name);
			target.setProperty(name, source.getPropertyValue(name));
		}
	}
}