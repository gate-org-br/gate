<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<g:login module="gateconsole.screen">
			<div class="Login">
				<div>
					<div>
						<img src='Logo.svg'/>
						<label>${app.id}</label>
						<label>Vers&atilde;o ${version}</label>
					</div>

					<div class='LinkControl'>
						<ul>
							<li style='width: 25%'>
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
									Solicitar acesso<g:icon type="gate.entity.User"/>
								</a>
							</li>
						</ul>
						<div>
							<fieldset>
								<label style="width: 100%">
									Login:
									<span>
										<g:icon type="gate.entity.User"/>
										<input type='text' required='required' name='$userid' maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label style="width: 100%">
									Senha:
									<span>
										<g:icon type="passwd"/>
										<input type='password' required='required' name='$passwd' maxlength='32' tabindex='1' title='Entre com a sua senha.'/>
									</span>
								</label>
							</fieldset>
						</div>
					</div>
					<div class='COOLBAR'>
						<button class="Commit" tabindex='2'>
							Concluir<g:icon type="commit"/>
						</button>
					</div>
				</div>
			</div>

			<g:superuser>
				<g:if condition="${not empty exception}">
					<g:stacktrace popup="popup"
						      title="Erro de sistema"
						      exception="${exception}"/>
				</g:if>
			</g:superuser>

			<g:if condition="${not empty messages}">
				<script type='text/javascript'>
					<g:iterator source="${messages}" target="message">
					alert('${message}');
					</g:iterator>
				</script>
			</g:if>
		</g:login>
	</body>
</g:template>

