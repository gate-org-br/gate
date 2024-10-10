import DataURL from './data-url.js';

export default class GFilePicker
{
	static pick(type = "*")
	{
		const input = document.createElement("input");
		input.type = "file";
		input.setAttribute("accept", type);
		input.multiple = false;
		input.style.display = "none";
		document.body.appendChild(input);

		return	new Promise((resolve, reject) =>
		{
			input.onchange = () => resolve({
					bytes: () => new Promise(resolve => {
							const reader = new FileReader();
							reader.addEventListener("load", () => resolve(new Uint8Array(reader.result)));
							reader.readAsArrayBuffer(input.files[0]);
						}),
					dataURL: () => new Promise((resolve) => {
							const reader = new FileReader();
							reader.addEventListener("load", () => resolve(reader.result));
							reader.readAsDataURL(input.files[0]);
						}),
					file: () => input.files[0]});

			window.addEventListener('focus', () => !input.files.length && reject(new Error('Cancel')), {once: true})

			input.click();
		}).finally(() => input.remove());
	}

	static saveDataURL(url, filename)
	{
		let dataURL = DataURL.parse(url);
		filename = filename || dataURL.parameters.name
			|| dataURL.contentType.replace("/", ".");
		let anchor = document.createElement("a");
		anchor.href = url;
		anchor.download = filename;
		anchor.click();
		return url;
	}
};