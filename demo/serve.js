const express = require('express');
const jsonServer = require('json-server');
const fs = require('fs');
const path = require('path');

const app = express();
const middlewares = jsonServer.defaults();

const sessionTimeout = 60 * 60 * 1000; // 1 hora em milissegundos
const sessions = {}; // Objeto para armazenar as sessões temporárias na memória

// Carrega os dados iniciais do db.json uma vez como string
const initialDataString = fs.readFileSync('db.json', 'utf-8');

// Middleware para definir o Content-Type para arquivos .evs
app.use((req, res, next) => {
	if (path.extname(req.url) === '.evs') {
		res.setHeader('Content-Type', 'text/event-stream');
	}
	next();
});

app.use(express.json());
app.use(middlewares);

// Middleware de gerenciamento de sessão por IP
app.use((req, res, next) => {
	const ip = req.ip;

	// Cria uma nova sessão para o IP, se não existir
	if (!sessions[ip]) {
		sessions[ip] = {data: JSON.parse(initialDataString), timeout: null};
	}

	// Restaura o timer de expiração da sessão
	if (sessions[ip].timeout) {
		clearTimeout(sessions[ip].timeout);
	}

	// Define o timer para expirar a sessão após uma hora de inatividade
	sessions[ip].timeout = setTimeout(() => {
		delete sessions[ip]; // Remove a sessão da memória
		console.log(`Sessão do IP ${ip} expirada e removida da memória.`);
	}, sessionTimeout);

	// Configura o roteador JSON Server para usar os dados da sessão em vez de um arquivo
	req.db = sessions[ip].data;
	next();
});

app.get('/array/*', async (req, res) => {
	// Extrai o caminho após "/array/" e monta a URL para o `json-server`

	const jsonServerPath = req.path.replace('/array', '');
	const queryString = new URLSearchParams(req.query).toString();
	const jsonServerUrl = `http://localhost:3000${jsonServerPath}?${queryString}`;

	try {
		// Faz uma requisição para a própria rota no json-server
		const response = await fetch(jsonServerUrl);
		const data = await response.json();

		// Retorna o array ao cliente
		res.json(data.length ? [Object.keys(data[0])].concat(data.map(Object.values)) : []);
	} catch (error) {
		console.error('Erro ao buscar dados do json-server:', error);
		res.status(500).send('Erro ao buscar dados.');
	}
});

app.get('/form', async (req, res) => {

	try {
		const response = await fetch(`http://localhost:3000/form-data`);
		const data = await response.json();
		res.json(data.fields);
	} catch (error) {
		console.error('Erro ao buscar dados do json-server:', error);
		res.status(500).send('Erro ao buscar dados.');
	}
});

app.put('/form', async (req, res) => {
	try {
		body = JSON.stringify({fields: req.body});

		const response = await fetch('http://localhost:3000/form-data', {
			method: 'PUT',
			headers: {
				'Content-Type': 'application/json',
			},
			body
		});

		const result = await response.json();
		res.json(result.fields);
	} catch (error) {
		console.error('Error posting data to /form-data:', error);
		res.status(500).send('Error processing request.');
	}
});


// Configura o roteador JSON Server com dados de sessão
app.use((req, res, next) => {
	const router = jsonServer.router(req.db);
	router(req, res, next);
});

// Servir arquivos estáticos da pasta 'public'
app.use('/public', express.static(path.join(__dirname, 'public')));

app.listen(3000, () => {
	console.log('JSON Server está rodando em http://localhost:3000');
});
