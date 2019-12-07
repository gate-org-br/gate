class DefinitionList
{
	static of(entries)
	{
		var dd = document.createElement("dd");
		dd.style.display = "grid";
		dd.style.gridTemplateColumns = "auto auto";
		dd.style.gridGap = "8px";


		for (var key in entries)
		{
			dd.appendChild(document.createElement("dt")).innerHTML = key;
			dd.appendChild(document.createElement("dd")).innerHTML = entries[key];


		}
		return dd;
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("dl[data-entries]")).forEach(function (e)
	{
		entries = JSON.parse(e.getAttribute("data-entries"));

		for (var key in entries)
		{
			e.appendChild(document.createElement("dt")).innerHTML = key;
			e.appendChild(document.createElement("dd")).innerHTML = entries[key];
		}
	});
});