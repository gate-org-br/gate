function TimeDialog(callback)
{
	var dialog = new Modal();
	dialog.addEventListener("click", function ()
	{
		this.close();
	});

	var hour = dialog.appendChild(document.createElement("table"));
	hour.appendChild(document.createElement("caption"))
			.appendChild(document.createTextNode("Selecione a Hora"));
	var columns = ["9%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "8%", "9%"];
	for (var i = 0; i < columns.length; i++)
	{
		var col = hour.appendChild(document.createElement("col"));
		col.style.width = columns[i];
	}

	var tbody = hour.appendChild(document.createElement("tbody"));
	for (var i = 0; i < 24; i += 12)
	{
		var tr = tbody.appendChild(document.createElement("tr"));
		for (var j = 0; j < 12; j++)
		{
			var td = tr.appendChild(document.createElement("td"));
			td.dialog = this;
			td.style.cursor = "pointer";
			td.style.textAlign = "center";
			td.title = "00".concat(new String(i + j)).slice(-2);
			td.appendChild(document.createTextNode(td.title));
			td.onmouseover = function ()
			{
				this.style.fontWeight = "bold";
				this.prev = this.style.backgroundColor;
				this.style.backgroundColor = "#CCCCCC";
			};
			td.onmouseout = function ()
			{
				this.style.fontWeight = "normal";
				this.style.backgroundColor = this.prev;
			};
			td.onclick = function ()
			{
				hour.minutes.innerHTML = "";
				var minutes = hour.minutes.appendChild(document.createElement("table"));
				minutes.appendChild(document.createElement("caption"))
						.appendChild(document.createTextNode("Selecione os Minutos"));
				var tbody = minutes.appendChild(document.createElement("tbody"));
				for (var i = 0; i < 60; i += 12)
				{
					var tr = tbody.appendChild(document.createElement("tr"));
					for (var j = 0; j < 12; j++)
					{
						var td = tr.appendChild(document.createElement("td"));
						td.dialog = this.dialog;
						td.style.cursor = "pointer";
						td.style.textAlign = "center";
						td.title = this.title + ":" + "00".concat(new String(i + j)).slice(-2);
						td.appendChild(document.createTextNode(td.title));
						td.onmouseover = function ()
						{
							this.prev = this.style.backgroundColor;
							this.style.backgroundColor = "#CCCCCC";
						};
						td.onmouseout = function ()
						{
							this.style.backgroundColor = this.prev;
						};
						td.onclick = function ()
						{
							dialog.hide();
							callback(this.title);
						};
					}
				}
			};
		}
	}

	hour.minutes = tbody.appendChild(document.createElement("tr"))
			.appendChild(document.createElement("td"));
	hour.minutes.style.padding = '06px';
	hour.minutes.style.height = '200px';
	hour.minutes.style.textAlign = 'center';
	hour.minutes.setAttribute("colspan", 12);
	hour.minutes.innerHTML = "Selecione a Hora";

	dialog.show();

	dialog.onclick = function (e)
	{
		if (e.target === this
				|| e.srcElement === this)
			this.hide();
	};
}

window.addEventListener("load", function ()
{
	search("input.Time").forEach(function (input)
	{
		input.style.width = "calc(100% - 32px)";
		var link = input.parentNode.appendChild(document.createElement("a"));
		link.href = "#";
		link.className = input.className;
		link.setAttribute("tabindex", input.getAttribute('tabindex'));
		link.appendChild(document.createElement("i")).innerHTML = "&#x2167;";
		link.onclick = function ()
		{
			if (input.value)
				input.value = '';
			else
				new TimeDialog(function (e)
				{
					input.value = e;
				});
			return false;
		};
	});
});