<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<form action="Register" method="post">
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
								<a href='Gate'>
									Fazer Logon<g:icon type="2000"/>
								</a>
							</li>
							<li style='width: 25%'>
								<a href='Password'>
									Trocar Senha<g:icon type="passwd"/>
								</a>
							</li>
							<li style='width: 25%' data-selected="true">
								<a href='Register'>
									Solicitar acesso<g:icon type="gate.entity.User"/>
								</a>
							</li>
						</ul>
						<div>
							<fieldset>
								<label style='width: 100%'>
									Nome:
									<span>
										<g:icon type="gate.entity.User"/>
										<input type='TEXT' required='required' name='user.name'
										       maxlength='64' tabindex='1' title='Entre com o seu nome.'/>
									</span>
								</label>
								<label style='width: 100%'>
									Login:
									<span>
										<g:icon type="2096"/>
										<input type='TEXT' required='required' name='user.userID'
										       maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Senha:
									<span>
										<g:icon type="passwd"/>
										<input type='PASSWORD' required='required' name='user.passwd'
										       maxlength='32' tabindex='1' title='Entre com a sua senha.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Repita:
									<span>
										<g:icon type="passwd"/>
										<input type='PASSWORD' required='required' name='user.repeat'
										       maxlength='32' tabindex='1' title='repita sua senha.'/>
									</span>
								</label>
								<label style='width: 100%'>
									Empresa:
									<span>
										<g:icon type="2048"/>
										<input type='TEXT' required='required' name='user.details'
										       maxlength='256' tabindex='1' title='Entre com o nome da empresa onde trabalha.'/>
									</span>
								</label>
								<label style='width: 100%'>
									E-Mail:
									<span>
										<g:icon type="2034"/>
										<input type='TEXT' required='required' name='user.email'
										       maxlength='64' tabindex='1' title='Entre com o seu endere&ccedil;o de email.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Telefone:
									<span>
										<g:icon type="gate.type.Phone"/>
										<input type='TEXT' name='user.phone'
										       maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone fixo.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Celular:
									<span>
										<g:icon type="2145"/>
										<input type='TEXT' name='user.cellPhone'
										       maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone celular.'/>
									</span>
								</label>
							</fieldset>
						</div>
					</div>
					<div class='COOLBAR'>
						<a class="Cancel" href="Gate" tabindex='3'>
							Desistir<g:icon type="cancel"/>
						</a>
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
		</form>
	</body>
</g:template>