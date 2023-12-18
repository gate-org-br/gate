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

	static fetch(response)
	{
		let contentType = response.headers.get('content-type');
		let contentDisposition = response.headers.get('content-disposition') || "";
		let filename = contentDisposition.split('filename=')[1] || contentType.replace("/", ".");
		return response.blob().then(blob => GFilePicker.save(blob, filename));
	}

	static save(file, filename = "file.bin")
	{
		const reader = new FileReader();
		reader.onloadend = function ()
		{
			const link = document.createElement('a');
			link.download = filename;
			document.body.appendChild(link);
			link.href = reader.result;
			link.click();
			link.remove();
		};
		reader.readAsDataURL(file);
	}
};