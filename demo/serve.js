const WebSocket = require('ws');
const http = require('http');
const fs = require('fs');
const path = require('path');


const server = http.createServer((req, res) => {
    const filePath = path.join(__dirname, req.url);
    const stream = fs.createReadStream(filePath);

    stream.on('error', (err) => {
        res.writeHead(404, { 'Content-Type': 'text/plain' });
        res.end('Not Found');
    });

    // Set Content-Type based on file extension
    const ext = path.extname(filePath).toLowerCase();
    const contentType = getContentType(ext);
    res.writeHead(200, { 'Content-Type': contentType });

    stream.pipe(res);
});

const wss = new WebSocket.Server({ server });

wss.on('connection', (ws) => {
    ws.send(JSON.stringify({
        "todo": 0,
        "done": 4000,
        "text": "Processing records",
        "process": 1,
        "event": "Progress",
        "status": "PENDING"
    }));

    for (let i = 1; i <= 3999; i++) {
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
        "status": "COMMITED"
    }));
    ws.close();
});

const PORT = process.env.PORT || 8000;

server.listen(PORT, () => {
    console.log(`Server running at http://localhost:${PORT}`);
});

function getContentType(extension) {
    switch (extension) {
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
        default:
            return 'application/octet-stream';
    }
}
