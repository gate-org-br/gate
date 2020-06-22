<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<form action="SetupPassword" method="post">
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
							<li data-selected="true">
								<a href='SetupPassword'>
									Trocar Senha<g:icon type="passwd"/>
								</a>
							</li>
							<li >
								<a href='ResetPassword'>
									Recuperar Senha<g:icon type="2023"/>
								</a>
							</li>
							<li >
								<a href='CreateAccount'>
									Criar Conta<g:icon type="gate.entity.User"/>
								</a>
							</li>
						</ul>
						<div>
							<fieldset>
								<label data-size="8">
									Login:
									<span>
										<g:icon type="gate.entity.User"/>
										<input type='TEXT' required='required' name='user.userID' maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label data-size="8">
									Senha atual:
									<span>
										<g:icon type="passwd"/>
										<input type='PASSWORD' required='required' name='user.passwd' maxlength='64' tabindex='1' title='Entre com a sua senha atual.'/>
									</span>
								</label>
								<label  data-size="8">
									Nova senha:
									<span>
										<g:icon type="2057"/>
										<input type='PASSWORD' required='required' name='user.change' maxlength='64' tabindex='1' title='Entre com a sua nova senha.'/>
									</span>
								</label>
								<label  data-size="8">
									Repita a nova senha:
									<span>
										<g:icon type="2058"/>
										<input type='PASSWORD' required='required' name='user.repeat' maxlength='64' tabindex='1' title='Repita sua nova senha.'/>
									</span>
								</label>
							</fieldset>
						</div>
					</div>
					<g-coolbar>
						<button class="Commit" tabindex='2'>
							Concluir<g:icon type="commit"/>
						</button>
						<hr/>
						<a class="Cancel" href="Gate" tabindex='3'>
							Desistir<g:icon type="cancel"/>
						</a>
					</g-coolbar>
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

