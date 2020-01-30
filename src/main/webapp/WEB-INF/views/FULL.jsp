<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<main>
		<header>
			<img src='Logo.svg'/>
			<label>${app.id}</label>
			<label>Versão ${version}</label>
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

		<section>
			<g:insert/>
		</section>

		<g:if condition="${not empty screen.user.id}">
			<footer>
				<g:alert/>
				<label>${screen.user.role.name}</label>
				<label>${screen.user.name}</label>
			</footer>
		</g:if>
	</main>
</g:template>
