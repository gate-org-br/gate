const WebSocket = require('ws');
const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');
const querystring = require('querystring');

const server = http.createServer((req, res) => {
	const parsedUrl = url.parse(req.url);
	const query = querystring.parse(parsedUrl.query);
	const filePath = path.join(__dirname, parsedUrl.pathname);
	const stream = fs.createReadStream(filePath);

	stream.on('error', (err) => {
		res.writeHead(404, {'Content-Type': 'text/plain'});
		res.end('Not Found');
	});

	const ext = path.extname(filePath).toLowerCase();
	const contentType = getContentType(ext);
	res.writeHead(200, {'Content-Type': contentType});

	if (req.method === 'POST')
	{
		let rawData = '';
		req.on('data', chunk => rawData += chunk);

		req.on('end', () => {
			if (rawData.length > 0)
				res.end(rawData);
			else
				stream.pipe(res);
		});
	} else if (Object.keys(query).length > 0
		&& Object.keys(query)[0] !== "type")
	{
		let rawData = '';
		stream.on('data', chunk => rawData += chunk);

		stream.on('end', () =>
		{
			try
			{
				const jsonData = JSON.parse(rawData);

				const filteredData = jsonData.filter(item => {
					for (const key in query)
					{
						let value = String(item[key]).toLowerCase();
						let filter = String(query[key]).toLowerCase();
						if (filter.startsWith('"') && filter.endsWith('"')
							|| filter.startsWith("'") && filter.endsWith("'"))
						{
							if (value !== filter.slice(1, -1))
								return false;
						} else if (value.indexOf(filter) === -1)
							return false;
					}
					return true;
				});

				res.end(JSON.stringify(filteredData));
			} catch (e)
			{
				console.error('Error parsing JSON:', e);
				res.end('Error parsing JSON');
			}
		});
	} else
	{
		// Act like a GET request
		stream.pipe(res);
	}
});

const wss = new WebSocket.Server({server});

wss.on('connection', (ws) => {
	ws.send(JSON.stringify({
		"todo": 0,
		"done": 4000,
		"text": "Processing records",
		"process": 1,
		"event": "Progress",
		"status": "PENDING"
	}));

	for (let i = 1; i <= 3999; i++)
	{
		ws.send(JSON.stringify({
			"todo": 4000,
			"done": i,
			"text": `${i} records processed`,
			"process": 1,
			"event": "Progress",
			"status": "PENDING"
		}));
	}
	ws.send(JSON.stringify({
		"todo": 4000,
		"done": 4000,
		"text": "Done",
		"process": 1,
		"event": "Progress",
		"status": "COMMITTED"
	}));
	ws.close();
});

const PORT = process.env.PORT || 8000;

server.listen(PORT, () => {
	console.log(`Server running at http://localhost:${PORT}`);
});

function getContentType(extension)
{
	switch (extension)
	{
		case '.svg':
			return 'image/svg+xml';
		case '.html':
			return 'text/html';
		case '.js':
			return 'text/javascript';
		case '.css':
			return 'text/css';
		case '.json':
			return 'application/json';
		case '.png':
			return 'image/png';
		case '.jpg':
		case '.jpeg':
			return 'image/jpeg';
		case '.gif':
			return 'image/gif';
		case '.txt':
			return 'text/event-stream';
		case '.pdf':
			return 'application/pdf';
		default:
			return 'application/octet-stream';
	}
}
