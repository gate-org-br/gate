<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<div class="Main">
			<div>
				<img src='Logo.svg'/>
				<label>${app.id} - ${app.name}</label>
				<label>Versão ${version}</label>
				<label>${screen.user.name}</label>
			</div>

			<div>
				<ul class='HMenu'>
					<g:menuitem module="gateconsole.screen" screen="Home"/>
					<g:menuitem module="gateconsole.screen" screen="Role"/>
					<g:menuitem module="gateconsole.screen" screen="User"/>
					<g:menuitem module="gateconsole.screen" screen="Func"/>
					<g:menuitem module="gateconsole.screen" screen="Org"/>
					<g:menuitem module="gateconsole.screen" screen="App"/>
					<g:menuitem module="gateconsole.screen" screen="Mail"/>
					<g:menuitem module="gateconsole.screen" screen="Icon"/>
					<li></li>
						<g:menuitem/>
				</ul>
			</div>

			<div>
				<g:if condition="${not empty subscriptions}">
					<div class="TEXT">
						<g:link module="gateconsole.screen" screen="Access">
							<h1>
								Verificar pendências de cadastro
							</h1>
						</g:link>
					</div>
				</g:if>

				<g:insert/>
				<g:alert/>
			</div>
		</div>
	</body>
</g:template>
