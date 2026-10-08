export default class TriggerExtractor
{
	static form(element)
	{
		if (element.tagName === "FORM")
			return element;
		if (TriggerExtractor.isSubmit(element))
			return element.form;
		if (element.hasAttribute("data-form"))
			return element.getRootNode()
				.getElementById(element.getAttribute("data-form"));
		if (["post", "put", "patch"].includes((element.getAttribute("formmethod")
			|| element.getAttribute("data-method") || "none").toLowerCase()))
			return element.form || element.closest("form");
	}

	static isSubmit(element)
	{
		if (element.tagName === "BUTTON")
			return element.type === "submit"
				&& !!element.form;

		return element.tagName === 'INPUT'
			&& !!element.form
			&& (element.type === "image"
				|| element.type === "submit"
				|| element.type === "button");
	}

	static isNative(element)
	{
		if (element.tagName === "A"
			&& !(element.target || "_self").startsWith("@"))
			return true;
		return TriggerExtractor.isSubmit(element)
			&& !(element.getAttribute("formtarget")
				|| element.form.target || "_self")
				.startsWith("@");
	}

	static trigger(element)
	{
		if (element.hasAttribute("data-trigger"))
			return element.dataset.trigger;

		if (!element.hasAttribute("data-method")
			&& !element.hasAttribute("data-action")
			&& !element.hasAttribute("data-target"))
			return null;

		if (TriggerExtractor.isSubmit(element))
			return "click";

		switch (element.tagName)
		{
			case "DIV":
			case "SPAN":
			case "LABEL":
				return "load";

			case "TR":
			case "A":
			case "BUTTON":
			case "TD":
			case "LI":
				return "click";

			case "INPUT":
			case "SELECT":
			case "TEXTAREA":
				return "change";
		}
		return null;
	}

	static method(element)
	{
		return (element.getAttribute("formmethod")
			|| element.getAttribute("data-method")
			|| element.getAttribute("method")
			|| TriggerExtractor.form(element)?.method || "get").toLowerCase();
	}

	static action(element)
	{
		return element.getAttribute("href")
			|| element.getAttribute("formaction")
			|| element.getAttribute("data-action")
			|| element.getAttribute("action")
			|| TriggerExtractor.form(element)?.action
			|| "";
	}

	static target(element)
	{
		return element.getAttribute("target")
			|| element.getAttribute("formtarget")
			|| element.getAttribute("data-target")
			|| TriggerExtractor.form(element)?.target
			|| "_self";
	}
}