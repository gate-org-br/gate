<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body style='background-color: #CCCCD0;
	      background-image: url("../gate/imge/back/BACK.png");
	      background-attachment: fixed'>
		<div style='width: 970px; margin: auto; padding-top: 12px'>
			<form name='FORMULARIO' id='FORMULARIO' method='POST' target='_top'
			      action="Gate?${g:qs(pageContext.request, 'MODULE=gateconsole.screen')}">
				<div style='width: 600px; height: 344px; position: absolute; top: 50%; margin-top: -172px; left: 50%; margin-left: -300px;
				     padding: 10px; -webkit-box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75); -moz-box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75);
				     box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75); background-color: #CDCAB9;  border: 2px groove #CCCCCC'>
					<div class='TEXT' style='width: 100%; height: 64px; padding: 10px'>
						<img src='${org.icon}' style='float: left; height: 48px'/>
						<span style='float: left; height: 48px; margin-left: 10px'>
							<strong style='font-size: 14px'>${app.id}</strong>
							<br/><br/><span style='font-size: 14px'>${app.name}</span>
						</span>
						<div style="color: #999999; float: right; height: 100%">
							Vers&atilde;o ${version}
						</div>
					</div>

					<div class='LinkControl'>
						<ul>
							<li data-selected='true' style='width: 25%'>
								<a href='#'>
									Fazer Logon<g:icon type="2000"/>
								</a>
							</li>
							<li style='width: 25%'>
								<a href='Password'>
									Trocar Senha<g:icon type="passwd"/>
								</a>
							</li>
							<li style='width: 25%'>
								<a href='Register'>
									Criar Conta<g:icon type="gate.entity.User"/>
								</a>
							</li>
						</ul>
						<div>
							<fieldset style='margin-top: 10px'>
								<span>
									<g:icon type="gate.entity.User" style='font-size: 24px; color: #CCCCCC; position: absolute; margin: 12px'/>
									<input type='TEXT' required='required' name='$userid' maxlength='64' tabindex='1' title='Entre com o seu login.'
									       style='padding: 12px; padding-left: 48px; height: 42px; font-size: 18px; border-radius: 5px; border-color: #CCCCCC; margin: 2px'/>
								</span>
								<span>
									<g:icon type="passwd" style='font-size: 24px; color: #CCCCCC; position: absolute; margin: 12px'/>
									<input type='PASSWORD' required='required' name='$passwd' maxlength='32' tabindex='1' title='Entre com a sua senha.'
									       style='padding: 12px; padding-left: 48px; height: 42px; font-size: 18px;  border-radius: 5px; border-color: #CCCCCC; margin: 2px'/>
								</span>
							</fieldset>
						</div>
					</div>
					<div class='COOLBAR'>
						<button target='_top'
							formaction="Gate?${g:qs(pageContext.request, 'MODULE=gateconsole.screen')}"
							tabindex='2' style='color: #006600'>
							Concluir<g:icon type="commit"/>
						</button>
					</div>
				</div>
			</form>

			<g:admin>
				<g:exception/>
			</g:admin>
		</div>

		<g:if condition="${not empty messages}">
			<script type='text/javascript'>
				<g:iterator source="${messages}" target="message">
				alert('${message}');
				</g:iterator>
			</script>
		</g:if>
	</body>
</g:template>

