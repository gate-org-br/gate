window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("form")).forEach(function (form)
	{
		form.addEventListener("submit", function (e)
		{
			if (this.getAttribute("data-cancel"))
			{
				alert(this.getAttribute("data-cancel"));
				e.preventDefault();
				e.stopImmediatePropagation();
			} else if (this.target === "_dialog")
			{
				new Dialog()
					.setTitle(this.getAttribute("title"))
					.setOnHide(this.getAttribute("data-onHide"))
					.setNavigator(this.getAttribute("data-navigator") ?
						eval(this.getAttribute("data-navigator")) : null)
					.show();
			}
		});

		form.addEventListener("keydown", function (e)
		{
			if (this.getAttribute("action")
				&& (document.activeElement.tagName.toLowerCase() === 'input'
					|| document.activeElement.tagName.toLowerCase() === 'select'))
			{
				e = e ? e : window.event;
				if (e.keyCode === ENTER)
				{
					e.preventDefault();
					var button = document.createElement("button");
					button.style.display = 'none';
					this.appendChild(button);
					button.click();
					this.removeChild(button);
				}
			}
		});
	});
});