<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<form action="CreateAccount" method="post">
			<div class="Login">
				<div>
					<div>
						<img src='Logo.svg'/>
						<label>${app.id}</label>
						<label>Versão ${version}</label>
					</div>

					<div class='LinkControl'>
						<ul>
							<li>
								<a href='Gate'>
									Fazer Logon<g:icon type="2000"/>
								</a>
							</li>
							<li>
								<a href='SetupPassword'>
									Trocar Senha<g:icon type="passwd"/>
								</a>
							</li>
							<li>
								<a href='ResetPassword'>
									Recuperar Senha<g:icon type="2023"/>
								</a>
							</li>
							<li data-selected="true">
								<a href='CreateAccount'>
									Criar Conta<g:icon type="gate.entity.User"/>
								</a>
							</li>

						</ul>
						<div>
							<fieldset>
								<label>
									Nome:
									<span>
										<g:icon type="gate.entity.User"/>
										<input type='TEXT' required='required' name='user.name'
										       maxlength='64' tabindex='1' title='Entre com o seu nome.'/>
									</span>
								</label>
								<label>
									Login:
									<span>
										<g:icon type="2096"/>
										<input type='TEXT' required='required' name='user.userID'
										       maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label data-size="8">
									Senha:
									<span>
										<g:icon type="passwd"/>
										<input type='PASSWORD' required='required' name='user.passwd'
										       maxlength='32' tabindex='1' title='Entre com a sua senha.'/>
									</span>
								</label>
								<label data-size="8">
									Repita:
									<span>
										<g:icon type="passwd"/>
										<input type='PASSWORD' required='required' name='user.repeat'
										       maxlength='32' tabindex='1' title='repita sua senha.'/>
									</span>
								</label>
								<label>
									Empresa:
									<span>
										<g:icon type="2048"/>
										<input type='TEXT' required='required' name='user.details'
										       maxlength='256' tabindex='1' title='Entre com o nome da empresa onde trabalha.'/>
									</span>
								</label>
								<label>
									E-Mail:
									<span>
										<g:icon type="2034"/>
										<input type='TEXT' required='required' name='user.email'
										       maxlength='64' tabindex='1' title='Entre com o seu endere&ccedil;o de email.'/>
									</span>
								</label>
								<label data-size="8">
									Telefone:
									<span>
										<g:icon type="gate.type.Phone"/>
										<input type='TEXT' name='user.phone'
										       maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone fixo.'/>
									</span>
								</label>
								<label data-size="8">
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