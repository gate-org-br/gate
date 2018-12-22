<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<div class="Main">
			<div>
				<g:icon type="gate"/>
				<label>${app.id} - ${app.name}</label>
				<label>Vers&atilde;o ${version}</label>
				<label>${screen.user.name}</label>
			</div>

			<div>
				<ul class='HMenu'>
					<g:menuitem module="gateconsole.screen" screen="Home" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Role" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="User" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Org" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="App" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Mail" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Icon" style="width: 10%"/>
					<li></li>
						<g:if condition="${not empty subscriptions}">
							<g:menuitem module="gateconsole.screen" screen="Access"
								    style='width: 10%; color: #990000'/>
						</g:if>
						<g:menuitem style="width: 10%"/>
				</ul>
			</div>

			<div>
				<g:insert/>
				<g:alert/>
			</div>
		</div>
	</body>
</g:template>
