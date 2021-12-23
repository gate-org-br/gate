import Message from './g-message.mjs';

export default class Clipboard
{
	static copy(data, silent = false)
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
		if (!silent)
			Message.success("O texto " + data + " foi copiado com sucesso para a área de transferência.", 1000);
	}
}

Array.from(document.querySelectorAll("[data-copy-onclick]")).forEach(function (element)
{
	element.addEventListener("click", () => Clipboard.copy(element.getAttribute("data-copy-onclick")));
});