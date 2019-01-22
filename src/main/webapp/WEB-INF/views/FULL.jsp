<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<div class="Main">
			<div>
				<img src='Logo.svg'/>
				<label>${app.id} - ${app.name}</label>
				<label>Vers&atilde;o ${version}</label>
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
						<g:if condition="${not empty subscriptions}">
							<g:menuitem module="gateconsole.screen" screen="Access"
								    style='width: 10%; color: #990000'/>
						</g:if>
						<g:menuitem/>
				</ul>
			</div>

			<div>
				<g:insert/>
				<g:alert/>
			</div>
		</div>
	</body>
</g:template>
