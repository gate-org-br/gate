export default [{text: "Show contextmenu.js",
		icon: "2217",
		action: "/component/contextmenu.js",
		target: "@inner-html(#code)"},
	{text: "Show context id", icon: "2009", action: context => alert(context.id)},
	{text: "Submenu", icon: "2004",
		action: [{text: "Item 1", icon: "2026", action: () => alert("Item 1")},
			{text: "Item 1", icon: "2031", action: () => alert("Item 2")},
			{text: "Item 2", icon: "2021", action: () => alert("Item 3")},
			{text: "Item 4", icon: "2020", action: () => alert("Item 4")},
			{text: "Item 5", icon: "2034",
				action: [{text: "Item 5.1", icon: "2051", action: () => alert("Item 5.1")},
					{text: "Item 5.2", icon: "2058", action: () => alert("Item 5.2")},
					{text: "Item 5.3", icon: "2059", action: () => alert("Item 5.3")},
					{text: "Item 5.4", icon: "2056", action: () => alert("Item 5.4")},
					{text: "Item 5.5", icon: "2053", action: () => alert("Item 5.5")}]}]
	}];