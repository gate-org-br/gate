window.addEventListener("load", function ()
{
	Array.from(document.getElementsByTagName("form"))
		.forEach(function (form)
		{
			form.addEventListener("submit", function (e)
			{
				if (this.target === "_dialog")
				{
					new Dialog()
						.setTitle(this.getAttribute("title"))
						.setOnHide(this.getAttribute("data-onHide"))
						.setNavigator(this.getAttribute("data-navigator") ?
							eval(this.getAttribute("data-navigator")) : null)
						.show();
				}
			});
		});
});