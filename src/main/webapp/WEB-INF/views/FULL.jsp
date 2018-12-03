<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body style='background-image: url("../gate/imge/back/BACK.png");
	      background-attachment: fixed'>
		<div class="main">
			<div>
				<img src='imge/logo.svg' style="height: 40px"/>
				<div style='display: flex; flex-direction: column; justify-content: space-between; align-items: flex-end; height: 40px'>
					<div style='display: flex; justify-content: flex-start'>
						Vers&atilde;o ${version}
					</div>
					<div style='display: flex; justify-content: flex-end'>
						${screen.user.name}
					</div>
				</div>
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
			</div>

			<g:alert/>
		</div>
	</body>
</g:template>
