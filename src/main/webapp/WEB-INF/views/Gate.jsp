<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body style='background-color: #CCCCD0;
	      background-image: url("../gate/imge/back/BACK.png");
	      background-attachment: fixed; padding: 0'>

		<div style='display: flex; align-items: center; justify-content: center; flex-direction: column; height: 100vh; padding: 10px'>
			<div class="Panel"
			     style='width: 600px;
			     height: 344px;
			     padding: 10px;
			     border: 2px groove #CCCCCC;
			     box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75);
			     -moz-box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75);
			     -webkit-box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75);'>

				<form name='FORMULARIO' id='FORMULARIO' method='POST' target='_top'
				      action="Gate?${g:qs(pageContext.request, 'MODULE=gateconsole.screen')}">

					<div style='width: 100%; height: 64px; display: flex; align-items: stretch;
					     background-color: #FFFFFF; border-radius: 5px; padding: 8px;'>
						<img src='imge/logo.svg' style='height: 48px'/>
						<div style="color: #999999; width: 100%; display: flex; align-items: flex-start; justify-content: flex-end">
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
							tabindex='2' class="Commit">
							Concluir<g:icon type="commit"/>
						</button>
					</div>
				</form>
			</div>

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

