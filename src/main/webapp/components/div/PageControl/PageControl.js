function PageControl(pageControl)
{
	if (!pageControl.getAttribute("data-type"))
		pageControl.setAttribute("data-type", "Frame");

	var pages = [];
	Array.from(pageControl.children)
		.filter(e => e.tagName.toLowerCase() === "ul")
		.forEach(ul => Array.from(ul.children).forEach(li => pages.push(li)));


	if (pages.length > 0
		&& pages.every(e => !e.getAttribute("data-selected")
				|| e.getAttribute("data-selected").toLowerCase() !== "true"))
		pages[0].setAttribute("data-selected", "true");

	for (var i = 0; i < pages.length
		&& i < pages.length; i++)
		new Page(pages[i]);

	function reset()
	{
		for (var i = 0; i < pages.length; i++)
			pages[i].setAttribute("data-selected", "false");

		for (var i = 0; i < pageControl.children.length; i++)
			if (pageControl.children[i].tagName.toLowerCase() === 'div')
				pageControl.children[i].style.display = "";
	}

	function Page(page)
	{
		if (!page.getAttribute("data-type"))
			page.setAttribute("data-type",
				pageControl.getAttribute("data-type"));

		var link;
		for (var i = 0; i < page.children.length; i++)
			if (page.children[i].tagName.toLowerCase() === 'a')
				link = page.children[i];

		var body;
		for (var i = 0; i < page.children.length; i++)
			if (page.children[i].tagName.toLowerCase() === 'div')
				body = page.children[i];

		if (!body)
			body = document.createElement("div");
		pageControl.appendChild(body);

		link.onclick = function ()
		{
			reset();
			body.style.display = "block";
			page.setAttribute("data-selected", "true");

			if (!body.innerHTML.replace(/^\s+|\s+$/g, ''))
				switch (page.getAttribute("data-type"))
				{
					case 'Fetch':
						fetch();
						break;
					case 'Frame':
						frame();
						break;
				}

			return false;
		};


		if (page.getAttribute("data-selected") &&
			page.getAttribute("data-selected").toLowerCase() === "true")
			link.onclick();


		function fetch()
		{
			new URL(link.getAttribute('href')).get(text => body.innerHTML = text);
		}

		function frame()
		{
			var iframe = body.appendChild(document.createElement("iframe"));
			iframe.setAttribute("allowfullscreen", "true");
			iframe.style.backgroundPosition = "center";
			iframe.style.backgroundRepeat = "no-repeat";
			iframe.scrolling = "no";
			iframe.style.backgroundImage = "url('../gate/imge/back/LOADING.gif')";
			iframe.setAttribute("src", link.getAttribute('href'));

			var observer = new MutationObserver(function ()
			{
				iframe.height = 0;
				iframe.height = iframe.contentWindow.document
					.body.scrollHeight + "px";
			});

			iframe.onload = function ()
			{
				observer.disconnect();

				this.height = 0;
				this.height = this.contentWindow.document
					.body.scrollHeight + "px";

				this.style.backgroundImage = "";
				var elements = iframe.contentWindow.document.querySelectorAll("*");
				for (var i = 0; i < elements.length; i++)
					observer.observe(elements[i], {attributes: true, childList: true, characterData: true});
			};
			iframe.refresh = function ()
			{
				var divs = Array.from(this.parentNode.parentNode.children).filter(e => e.tagName.toLowerCase() === "div");
				for (i = 0; i < divs.length; i++)
					if (divs[i].childNodes[0] !== this)
						if (divs[i] !== this.parenNode)
							divs[i].innerHTML = '';
			};
		}
	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('div.PageControl'))
		.forEach(element => new PageControl(element));
});