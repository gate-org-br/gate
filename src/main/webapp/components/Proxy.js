class Proxy
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
}