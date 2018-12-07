window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll("select.Editable")).forEach(function (select)
	{
		var datalist = select.parentNode.appendChild(document.createElement("datalist"));
		datalist.setAttribute("id", datalist + select.getAttribute("name"));
		Array.from(select.children)
			.forEach(option => datalist.appendChild(document.createElement("option")).innerHTML = option.innerHTML);

		var input = select.parentNode.appendChild(document.createElement("input"));
		input.setAttribute("list", datalist.getAttribute("id"));
		input.value = select.options[select.selectedIndex].innerHTML;

		if (select.hasAttribute("required"))
			input.setAttribute("required", "required");
		if (select.hasAttribute("tabindex"))
			input.setAttribute("tabindex", select.getAttribute("tabindex"));

		input.addEventListener("change", function ()
		{
			for (var i = 0; i < select.children.length; i++)
			{
				if (select.children[i].innerHTML === this.value)
				{
					select.selectedIndex = i;
					return;
				}
			}

			alert("Tentativa de selecionar item inexistente");
			input.value = select.options[select.selectedIndex].innerHTML;
		});

		select.form.appendChild(select);
	});
});