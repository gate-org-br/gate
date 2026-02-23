export default class Clipboard
{
	static copy(data)
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
	}
}