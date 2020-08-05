class GList
{
	static of(entries)
	{
		var ul = document.createElement("ul");
		ul.classList.add("unordered-list");
		entries.forEach(e => ul.appendChild(document.createElement("li")).innerHTML = e);
		return ul;
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("ul[data-entries]")).forEach(function (ul)
	{
		entries = JSON.parse(ul.getAttribute("data-entries"));
		entries.forEach(entry => ul.appendChild(document.createElement("li")).innerHTML = entry);
	});
});