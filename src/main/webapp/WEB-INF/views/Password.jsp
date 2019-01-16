<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
	<body>
		<form action="Password" method="post">
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
							<li style='width: 25%' data-selected="true">
								<a href='Password'>
									Trocar Senha<g:icon type="passwd"/>
								</a>
							</li>
						</ul>
						<div>
							<fieldset>
								<label style='width: 50%'>
									Login:
									<span>
										<g:icon type="gate.entity.User"/>
										<input type='TEXT' required='required' name='user.userID' maxlength='64' tabindex='1' title='Entre com o seu login.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Senha atual:
									<span>
										<g:icon type="passwd"/>
										<input type='PASSWORD' required='required' name='user.passwd' maxlength='64' tabindex='1' title='Entre com a sua senha atual.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Nova senha:
									<span>
										<g:icon type="2057"/>
										<input type='PASSWORD' required='required' name='user.change' maxlength='64' tabindex='1' title='Entre com a sua nova senha.'/>
									</span>
								</label>
								<label style='width: 50%'>
									Repita a nova senha:
									<span>
										<g:icon type="2058"/>
										<input type='PASSWORD' required='required' name='user.repeat' maxlength='64' tabindex='1' title='Repita sua nova senha.'/>
									</span>
								</label>
							</fieldset>
						</div>
					</div>
					<div class='COOLBAR'>
						<a class="Cancel" href="Gate" tabindex='3'>
							Desistir<g:icon type="cancel"/>
						</a>
						<button class="Commit" formaction="Password" tabindex='2'>
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

