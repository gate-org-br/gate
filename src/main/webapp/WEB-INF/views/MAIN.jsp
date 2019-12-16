<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<html lang="pt-BR">
	<head>
		<meta charset="UTF-8">
		<title>${app.id} - ${app.name}</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" type="image/png" href="Icon.svg"/>
		<link rel='stylesheet' type='text/css' href='app/resources/Gate.css?version=${version}'/>
		<script type='text/javascript' src='app/resources/echarts.min.js?version=${version}' charset=\"utf-8\"></script>
		<script type='text/javascript' src='app/resources/Gate.js?version=${version}' charset=\"utf-8\"></script>
	</head>
	<g:insert/>
</html>