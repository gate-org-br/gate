export default function submit(form, method, action, target)
{
	let button = document.createElement("button");
	button.style.displey = "none";
	if (method)
		button.setAttribute("formmethod", method);
	if (action)
		button.setAttribute("formaction", action);
	if (target)
		button.setAttribute("formtarget", target);
	form.appendChild(button);
	button.click();
	button.remove();
}
