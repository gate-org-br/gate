export default class TriggerExtractor
{
	static method(element)
	{
		return (element.getAttribute("formmethod")
				|| element.getAttribute("data-method")
				|| element.getAttribute("method")
				|| element.form?.method
				|| "get").toLowerCase();
	}

	static action(element)
	{
		return element.getAttribute("href")
				|| element.getAttribute("formaction")
				|| element.getAttribute("data-action")
				|| element.getAttribute("action")
				|| element.form?.action
				|| "";
	}

	static target(element)
	{
		return element.getAttribute("target")
				|| element.getAttribute("formtarget")
				|| element.getAttribute("data-target")
				|| element.form?.target
				|| "_self";
	}
}
