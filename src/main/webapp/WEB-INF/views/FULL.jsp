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
				<g:link target="_top" module="gateconsole.screen" screen="Home"/>
				<g:link target="_top" module="gateconsole.screen" screen="Role"/>
				<g:link target="_top" module="gateconsole.screen" screen="User"/>
				<g:link target="_top" module="gateconsole.screen" screen="Func"/>
				<g:link target="_top" module="gateconsole.screen" screen="Org"/>
				<g:link target="_top" module="gateconsole.screen" screen="App"/>
				<g:link target="_top" module="gateconsole.screen" screen="Mail"/>
				<g:link target="_top" module="gateconsole.screen" screen="Icon"/>
				<g:link target="_top" module="gateconsole.screen" screen="Demo"/>
				<g:link target="_top" condition="${not empty subscriptions}"
					module="gateconsole.screen" screen="Access"/>
				<hr/>
				<g:link target="_top"/>
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
