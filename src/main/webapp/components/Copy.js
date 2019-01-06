function copy(data)
{
	const textarea = document.createElement('textarea');
	textarea.value = data;
	textarea.removeAttribute('readonly');
	textarea.style.position = 'absolute';
	textarea.style.left = '-9999px';
	document.body.appendChild(textarea);
	textarea.select();
	document.execCommand('copy');
	document.body.removeChild(textarea);
	Message.success("O texto " + data + " foi copiado com sucesso para a área de transferência.", 1000);
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("[data-copy-onclick]")).forEach(function (element)
	{
		element.addEventListener("click", () => copy(element.getAttribute("data-copy-onclick")));
	});
});