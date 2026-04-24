Array.from(document.querySelectorAll("input[type=password]")).forEach(function (input)
{
	var link = input.parentNode.appendChild(document.createElement("a"));
	link.href = "#";
	link.setAttribute("tabindex", input.getAttribute('tabindex'));
	link.appendChild(document.createElement("i")).innerHTML = "&#x2055;";
	link.addEventListener("click", () => input.setAttribute("type",
			input.getAttribute("type") === 'password' ? "text" : "password"));
});