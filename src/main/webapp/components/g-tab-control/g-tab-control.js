/* global customElements */

class GTabControl extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: 'open'});
		let head = this.shadowRoot.appendChild(document.createElement("div"));
		head.style = "display: flex; align-items: center; justify-content: flex-start; flex-wrap: wrap";

		head.appendChild(document.createElement("slot"));

		this.shadowRoot.appendChild(document.createElement("div"))
			.appendChild(document.createElement("slot")).name = "body";
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
		window.setTimeout(() =>
		{
			let slot = this.shadowRoot.firstChild.firstChild;

			Array.from(slot.assignedElements())
				.filter(e => e.tagName === "A" || e.tagName === "BUTTON")
				.forEach(link =>
				{
					let tab = document.createElement("div");
					tab.setAttribute("slot", "body");
					this.appendChild(tab);
					tab.style = "display: none; padding : 10px; overflow: hidden";
					let type = link.getAttribute("data-type") || this.type;

					link.addEventListener("click", event =>
					{
						event.preventDefault();
						event.stopPropagation();
						Array.from(this.children).filter(e => e.tagName === "DIV").forEach(e => e.style.display = "none");
						Array.from(this.children).filter(e => e.tagName !== "DIV").forEach(e => e.setAttribute("data-selected", "false"));
						tab.style.display = "block";
						link.setAttribute("data-selected", "true");

						if (!tab.childNodes.length)
						{
							switch (type)
							{
								case "frame":

									let iframe = tab.appendChild(document.createElement("iframe"));
									iframe.scrolling = "no";
									iframe.setAttribute("allowfullscreen", "true");
									iframe.style = "margin: 0; width : 100%; border: none; overflow: hiddden";

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

									iframe.src = link.href || link.getAttribute("formaction");
									break;
								case "fetch":
									if (!link.form)
										new URL(link.getAttribute('href'))
											.get(text => tab.innerHTML = text);
									else if (link.form.checkValidity())
										new URL(link.getAttribute('href'))
											.post(new FormData(link.form),
												text => tab.innerHTML = text);
									break;
							}

						} else
							event.preventDefault();
					});

					if (link.getAttribute("data-selected") &&
						link.getAttribute("data-selected").toLowerCase() === "true")
						link.click();
				});


			Array.from(slot.assignedElements())
				.filter(e => e.tagName === "DIV")
				.forEach(tab =>
				{
					let link = this.appendChild(document.createElement("a"));
					link.innerText = tab.getAttribute("data-name");
					if (tab.hasAttribute("data-icon"))
						link.appendChild(document.createElement("i")).innerHTML = "&#X" + tab.getAttribute("data-icon") + ";";
					tab.setAttribute("slot", "body");
					tab.style = "display: none; padding : 10px; overflow: hidden";
					let type = link.getAttribute("data-type") || this.type;

					link.addEventListener("click", event =>
					{
						event.stopPropagation();
						Array.from(this.children).filter(e => e.tagName === "DIV").forEach(e => e.style.display = "none");
						Array.from(this.children).filter(e => e.tagName !== "DIV").forEach(e => e.setAttribute("data-selected", "false"));
						tab.style.display = "block";
						link.setAttribute("data-selected", "true");
					});

					if (link.getAttribute("data-selected") &&
						link.getAttribute("data-selected").toLowerCase() === "true")
						link.click();
				});

			if (slot.assignedElements().length
				&& Array.from(slot.assignedElements())
				.every(e => !e.hasAttribute("data-selected") ||
						e.getAttribute("data-selected")
						.toLowerCase() === "false"))
				slot.assignedElements()[0].click();
		}, 0);
	}
}

customElements.define('g-tab-control', GTabControl);


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