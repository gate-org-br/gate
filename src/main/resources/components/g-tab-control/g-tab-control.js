/* global customElements */

window.addEventListener("load", () => customElements.define('g-tab-control', class extends HTMLElement
	{
		constructor()
		{
			super();
			this.attachShadow({mode: 'open'});

			let head = this.shadowRoot.appendChild(document.createElement("div"));
			head.style = "display: flex; align-items: center; justify-content: flex-start; flex-wrap: wrap";
			head.appendChild(document.createElement("slot")).name = "head";

			let body = this.shadowRoot.appendChild(document.createElement("div"));
			body.appendChild(document.createElement("slot")).name = "body";
		}

		get type()
		{
			return this.getAttribute("type") || "frame";
		}

		set type(type)
		{
			this.setAttribute("type", type);
		}

		connectedCallback()
		{
			if (this.type !== "dummy")
			{
				var links = Array.from(this.children).filter(e => e.tagName === "A"
						|| e.tagName === "BUTTON");

				this.setAttribute("size", links.length);

				links.filter(e => !e.nextElementSibling || e.nextElementSibling.tagName !== "DIV")
					.forEach(e => this.insertBefore(document.createElement("div"), e.nextElementSibling));

				var pages = Array.from(this.children).filter(e => e.tagName === "DIV");
				pages.forEach(e => e.setAttribute("slot", "body"));

				links.forEach(link =>
				{
					links.forEach(e => e.setAttribute("slot", "head"));
					let type = link.getAttribute("data-type") || this.type;
					let reload = link.getAttribute("data-reload") || this.getAttribute("reload");

					link.addEventListener("click", event =>
					{
						pages.forEach(e => e.style.display = "none");
						links.forEach(e => e.setAttribute("data-selected", "false"));
						link.nextElementSibling.style.display = "block";
						link.setAttribute("data-selected", "true");

						if (reload === "always")
							while (link.nextElementSibling.firstChild)
								link.nextElementSibling.removeChild(link.nextElementSibling.firstChild);

						if (!link.nextElementSibling.childNodes.length)
						{
							switch (type)
							{
								case "fetch":
									new URL(link.getAttribute('href')
										|| link.getAttribute('formaction'))
										.get(text => link.nextElementSibling.innerHTML = text);
									break;
								case "frame":
									let iframe = document.createElement("iframe");
									iframe.scrolling = "no";
									iframe.setAttribute("allowfullscreen", "true");
									let name = Math.random().toString(36).substr(2);
									iframe.setAttribute("id", name);
									iframe.setAttribute("name", name);
									link.nextElementSibling.appendChild(iframe);

									iframe.onload = () =>
									{
										if (iframe.contentWindow
											&& iframe.contentWindow.document
											&& iframe.contentWindow.document.body
											&& iframe.contentWindow.document.body.scrollHeight)
										{
											var height = iframe.contentWindow
												.document.body.scrollHeight + "px";
											if (iframe.height !== height)
											{
												iframe.height = "0";
												iframe.height = height;
											}
										}

										iframe.style.backgroundImage = "none";
									};

									if (link.tagName === "A")
										link.setAttribute("target", name);
									else
										link.setAttribute("formtarget", name);

									return;
							}
						}

						event.preventDefault();
						event.stopPropagation();
					});

					if (link.getAttribute("data-selected") &&
						link.getAttribute("data-selected").toLowerCase() === "true")
						link.click();
				});



				if (links.length && links.every(e => !e.hasAttribute("data-selected")
						|| e.getAttribute("data-selected").toLowerCase() === "false"))
					links[0].click();
			} else
			{
				var links = Array.from(this.children).filter(e => e.tagName === "A" || e.tagName === "BUTTON");
				this.setAttribute("size", links.length);
				links.forEach(e => e.setAttribute("slot", "head"));
				Array.from(this.children).filter(e => e.tagName === "DIV").forEach(e => e.setAttribute("slot", "body"));
			}

			this.style.visibility = "visible";
		}
	}));

function PageControl(pageControl)
{
	if (!pageControl.getAttribute("data-type"))
		pageControl.setAttribute("data-type", "Frame");

	var pages = Array.from(pageControl.children)
		.filter(e => e.tagName.toLowerCase() === "ul")
		.flatMap(e => Array.from(e.children));

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
			iframe.scrolling = "no";
			iframe.setAttribute("allowfullscreen", "true");

			var resize = function ()
			{
				if (iframe.contentWindow
					&& iframe.contentWindow.document
					&& iframe.contentWindow.document.body
					&& iframe.contentWindow.document.body.scrollHeight)
				{
					var height = iframe.contentWindow.document.body.scrollHeight + "px";
					if (iframe.height !== height)
					{
						iframe.height = "0";
						iframe.height = height;
					}
				}
			};

			iframe.onload = function ()
			{
				resize();
				window.addEventListener("refresh_size", resize);
				iframe.backgroundImage = "none";
			};

			iframe.refresh = function ()
			{
				var divs = Array.from(this.parentNode.parentNode.children).filter(e => e.tagName.toLowerCase() === "div");
				for (i = 0; i < divs.length; i++)
					if (divs[i].childNodes[0] !== this)
						if (divs[i] !== this.parenNode)
							divs[i].innerHTML = '';
			};

			iframe.setAttribute("src", link.getAttribute('href'));
		}

	}
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('div.PageControl'))
		.forEach(element => new PageControl(element));
});


function LinkControl(linkControl)
{
	var links = [];
	Array.from(linkControl.children).forEach(function (ul)
	{
		if (ul.tagName.toLowerCase() === "ul")
			Array.from(ul.children).forEach(function (li)
			{
				if (li.tagName.toLowerCase() === "li")
					links.push(li);
			});
	});

	if (links.length > 0 && links.every(e => !e.getAttribute("data-selected")
			|| e.getAttribute("data-selected").toLowerCase() !== "true"))
		links[0].setAttribute("data-selected", "true");
}

window.addEventListener("load", function ()
{
	Array.from(document.querySelectorAll('div.LinkControl')).forEach(function (e)
	{
		new LinkControl(e);
	});
});