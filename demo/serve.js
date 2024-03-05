const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');
const querystring = require('querystring');
const event = btoa(JSON.stringify({type: "Ok", detail: {message: "success"}}));

const server = http.createServer((req, res) => {
	const parsedUrl = url.parse(req.url);
	const query = querystring.parse(parsedUrl.query);
	const filePath = path.join(__dirname, parsedUrl.pathname);
	const stream = fs.createReadStream(filePath);

	stream.on('error', (err) => {
		res.writeHead(404, {'Content-Type': 'text/plain'});
		res.end('Not Found');
	});

	if (req.method === 'GET' && parsedUrl.pathname === '/SSE')
	{
		res.writeHead(200, {'Content-Type': 'text/event-stream'});
		const timer = setInterval(() => res.write(`event: message\ndata: ${event}\n\n`), 10000);
		res.on('close', () => clearInterval(timer));
		res.end();
		return;
	}

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

		return;
	}

	if (Object.keys(query).length > 0 && Object.keys(query)[0] !== "type")
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
		return;
	}

	stream.pipe(res);

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