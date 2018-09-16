<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body style='background-image: url("../gate/imge/back/BACK.png");
	      background-attachment: fixed'>
		<div style='width: 100%; margin: auto; display: flex; flex-direction: column'>
			<div style='width: 100%; flex-basis: 100px; display: flex; background-color: #FFFFFF; padding:
			     8px; border-radius: 5px; align-items: stretch; justify-content: space-between'>
				<img src='imge/gate.svg'/>
				<div style='display: flex; flex-direction: column; justify-content: space-between; align-items: flex-end'>
					<div style='display: flex; justify-content: flex-start'>
						Vers&atilde;o ${version}
					</div>
					<div style='display: flex; justify-content: flex-end'>
						${screen.user.name}
					</div>
				</div>
			</div>
			<div class='NAVI'>
				<ul class='NAVI'>
					<g:menuitem module="gateconsole.screen" screen="Home" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Role" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="User" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Org" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="App" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Mail" style="width: 10%"/>
					<g:menuitem module="gateconsole.screen" screen="Icon" style="width: 10%"/>
					<g:menuitem style="width: 10%; float: right"/>
					<g:if condition="${not empty subscriptions}">
						<g:menuitem module="gateconsole.screen" screen="Access"
							    style='width: 10%; float: right; color: #990000'/>
					</g:if>
				</ul>
			</div>

			<div class='Panel' style='max-height: calc(100vh - 176px); overflow: auto'>
				<g:insert/>
			</div>

			<g:alert/>
		</div>
	</body>
</g:template>
