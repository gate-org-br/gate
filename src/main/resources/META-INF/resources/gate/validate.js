export default function validate(element)
{
	if (element.hasAttribute("data-disabled"))
		return false;

	if (element.hasAttribute("data-cancel"))
		return alert(element.getAttribute("data-cancel"), 2000) && false;

	if (element.hasAttribute("data-confirm"))
		return confirm(element.getAttribute("data-confirm"));

	if (element.hasAttribute("data-alert"))
		return alert(element.getAttribute("data-alert")) || true;

	return true;
}