export default [{"text": "function", "icon": "2008", "action": () => alert("Item 1")},
	{"text": "_blank", "icon": "2009", "action": "http://www.google.com", "target": "_blank"},
	{"text": "@dialog", "icon": "2006", "action": "message.txt", "target": "@dialog", "title": "Dialog"},
	{"text": "Submenu", "icon": "2207",
		"action": [{"text": "Action", "icon": "2008", "action": () => alert("Item 1")},
			{"text": "_blank", "icon": "2009", "action": "http://www.google.com", "target": "_blank"},
			{"text": "@dialog", "icon": "2006", "action": "http://www.google.com", "target": "@dialog"}]
	}]