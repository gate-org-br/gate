class Popup extends Modal
{
	constructor(element)
	{
		super(false);

		var dialog = this.element().appendChild(window.top.document.createElement('div'));
		dialog.className = "Popup";

		var head = dialog.appendChild(window.top.document.createElement('div'));
		head.setAttribute("tabindex", "1");
		head.focus();

		var caption = head.appendChild(window.top.document.createElement('label'));
		if (element.hasAttribute("title"))
			caption.innerHTML = element.getAttribute("title");

		var close = head.appendChild(window.top.document.createElement("a"));
		close.title = 'Fechar janela';
		close.innerHTML = "&#x1011;";
		close.onclick = () => this.hide();

		var body = dialog.appendChild(window.top.document.createElement('div'));
		body.appendChild(element);
		element.style.display = "block";

		this.show();
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("*[popup]")).forEach(element => new Popup(element));
});