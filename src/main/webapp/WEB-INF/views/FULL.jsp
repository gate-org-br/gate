<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body style='background-color: #b9bccd;
	      background-image: url("../gate/imge/back/BACK.png");
	      background-attachment: fixed'>
		<div style='width: 100%; margin: auto; display: flex; flex-direction: column'>
			<div class='TEXT' style='width: 100%; flex-basis: 80px; padding: 10px;'>
				<img src='${org.icon}' style='float: left; height: 60px'/>
				<span style='float: left; height: 60px; margin-left: 10px'>
					<strong style='font-size: 28px'>${app.id}</strong>
					<br/><br/><span style='font-size: 14px'>${app.name}</span>
				</span>
				<div style="float: right; height: 100%">
					<div style='height: 80%; text-align: right'>
						Vers&atilde;o ${version}
					</div>
					<div style='height: 20%; text-align: right'>
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

			<div class='Panel' style='max-height: calc(100vh - 160px); overflow: auto'>
				<g:insert/>
			</div>

			<g:alert/>
		</div>
	</body>
</g:template>
