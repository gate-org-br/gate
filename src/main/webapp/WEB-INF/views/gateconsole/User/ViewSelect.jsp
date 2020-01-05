<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<fieldset>
		<legend>
			Usuário<g:icon type="gate.entity.User"/>
		</legend>
		<fieldset style="width: calc(100% - 220px)">
			<label data-size='2'>
				Ativo:
				<span>
					<g:label property='form.active'/>
				</span>
			</label>
			<label data-size='2'>
				Cadastro:
				<span>
					<g:label property='form.registration'/>
				</span>
			</label>
			<label data-size='2'>
				Login:
				<span>
					<g:label property='form.userID'/>
				</span>
			</label>
			<label data-size='2'>
				Senha:
				<span>
					<label>Resetar</label>
					<g:shortcut module="#" screen="#" action="Passwd" arguments="form.id=${screen.form.id}"/>
				</span>
			</label>
			<label data-size='4'>
				Nome:
				<span>
					<g:label property='form.name'/>
				</span>
			</label>
			<label data-size='4'>
				Perfil:
				<span>
					<label>
						<g:print value="${screen.form.role.name}" empty="N/A"/>
					</label>
					<g:if condition="${not empty screen.form.role.id}">
						<g:shortcut module="#" screen="Role" action="Select"
							    arguments="form.id=${screen.form.role.id}"/>
					</g:if>
				</span>
			</label>
			<label data-size='2'>
				CPF:
				<span>
					<g:label property='form.CPF'/>
				</span>
			</label>
			<label data-size='2'>
				Nascimento:
				<span>
					<g:icon type="gate.type.Date"/>
					<g:label property='form.birthdate'/>
				</span>
			</label>
			<label data-size='4'>
				Sexo:
				<span>
					<g:label property='form.sex'/>
				</span>
			</label>
			<label data-size='2'>
				Telefone:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:label property='form.phone'/>
				</span>
			</label>
			<label data-size='2'>
				Celular:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:label property='form.cellPhone'/>
				</span>
			</label>
			<label data-size='4'>
				E-Mail:
				<span>
					<g:icon type="2034"/>
					<g:label property='form.email'/>
				</span>
			</label>
			<label>
				Detalhes:
				<span style='flex-basis: 80px'>
					<g:label property='form.details'/>
				</span>
			</label>
		</fieldset>
		<fieldset style="width: 220px">
			<div style="height: 210px;
			     display: flex;
			     align-items: center;
			     justify-content: center">
				<g:with name="photo" value="${screen.photo}">
					<g:choose>
						<g:when condition="${not empty photo}">
							<img src='${photo}'
							     style="max-width: 200px;
							     max-height: 200px; padding: 0"/>
						</g:when>
						<g:otherwise>
							<h1>
								Sem Foto
							</h1>
						</g:otherwise>
					</g:choose>
				</g:with>
			</div>
		</fieldset>
	</div>

	<div class='COOLBAR'>
		<a href='#' class='Hide Cancel'>
			Fechar<g:icon type="cancel"/>
		</a>
		<g:link class="Command" module='#' screen='#' action='Update' arguments="form.id=${screen.form.id}"/>
		<g:link class="Command" module='#' screen='#' action="Delete" arguments="form.id=${screen.form.id}"
			style='color: #660000' data-confirm="Tem certeza de que deseja remover este registro?"/>
	</div>

	<div class='PageControl'>
		<ul>
			<g:menuitem module="#" screen="Auth" arguments="form.user.id=${screen.form.id}"/>
			<g:menuitem module="#" screen="UserScreen$Func" arguments="user.id=${screen.form.id}"/>
		</ul>
	</div>
</g:template>