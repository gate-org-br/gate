<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<main>
			<header>
				<img src='Logo.svg'/>
				<label>${app.id} - ${app.name}</label>
				<label>Versão ${version}</label>
				<label>${screen.user.name}</label>
			</header>


			<nav>
				<g-tabbar>
					<g:link module="gateconsole.screen" screen="Home"/>
					<g:link module="gateconsole.screen" screen="Role"/>
					<g:link module="gateconsole.screen" screen="User"/>
					<g:link module="gateconsole.screen" screen="Func"/>
					<g:link module="gateconsole.screen" screen="Org"/>
					<g:link module="gateconsole.screen" screen="App"/>
					<g:link module="gateconsole.screen" screen="Mail"/>
					<g:link module="gateconsole.screen" screen="Icon"/>
					<g:link module="gateconsole.screen" screen="Demo"/>
					<g:link condition="${not empty subscriptions}"
						module="gateconsole.screen" screen="Access"/>
					<g:link/>
				</g-tabbar>
			</nav>

			<main>
				<g:insert/>
				<g:alert/>
			</main>
		</main>
	</body>
</g:template>
