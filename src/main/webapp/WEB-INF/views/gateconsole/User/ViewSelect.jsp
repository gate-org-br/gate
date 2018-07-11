<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<div style="display: flex;
	     align-items: stretch;
	     justify-content: center;
	     align-content: stretch">
		<fieldset style="width: calc(100% - 248px); margin: 4px">
			<legend>
				Usu&aacute;rio<g:icon type="gate.entity.User"/>
			</legend>
			<label style='width: 12.5%'>
				Ativo:
				<span>
					<g:label property='form.active'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Cadastro:
				<span>
					<g:label property='form.registration'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Login:
				<span>
					<g:label property='form.userID'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Senha:
				<span>
					<g:icon type="passwd"/>
					<g:link module="#" screen="#" action="Passwd" arguments="form.id=${screen.form.id}">
						Clique para resetar
					</g:link>
				</span>
			</label>
			<label style='width: 25%'>
				Nome:
				<span>
					<g:label property='form.name'/>
				</span>
			</label>
			<label style='width: 25%'>
				Perfil:
				<span>
					<g:icon type="gate.entity.Role"/>
					<g:if condition="${not empty screen.form.role.id}" otherwise="N/A">
						<g:link module="#" screen="Role" action="Select"
							arguments="form.id=${screen.form.role.id}"
							otherwise="${screen.form.role.name}">
							<g:print value="${screen.form.role.name}" empty="N/A"/>
						</g:link>
					</g:if>
				</span>
			</label>
			<label style='width: 12.5%'>
				CPF:
				<span>
					<g:label property='form.CPF'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Data de Nascimento:
				<span>
					<g:icon type="gate.type.Date"/>
					<g:label property='form.birthdate'/>
				</span>
			</label>
			<label style='width: 25%'>
				Sexo:
				<span>
					<g:label property='form.sex'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Telefone:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:label property='form.phone'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Celular:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:label property='form.cellPhone'/>
				</span>
			</label>
			<label style='width: 25%'>
				E-Mail:
				<span>
					<g:icon type="2034"/>
					<g:label property='form.email'/>
				</span>
			</label>
			<label style='width: 100%'>
				Detalhes:
				<span style='height: 60px'>
					<g:label property='form.details'/>
				</span>
			</label>
		</fieldset>
		<fieldset style="width: 240px; margin: 4px">
			<legend>
				Foto<g:icon type="gate.entity.User"/>
			</legend>
			<div style="height: calc(100% - 10px); display: flex;
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
		<a href='#' class='Hide' style='float: left'>
			Fechar<g:icon type="cancel"/>
		</a>
		<g:link module='#' screen='#' action='Update' arguments="form.id=${screen.form.id}">
			Alterar<g:icon type="update"/>
		</g:link>
		<g:link module='#' screen='#' action="Delete" arguments="form.id=${screen.form.id}" style='color: #660000' data-confirm="Tem certeza de que deseja remover este registro?">
			Remover<g:icon type="delete"/>
		</g:link>
	</div>

	<div class='PageControl' data-type='Frame'>
		<ul>
			<li style='width: 200px'>
				<a href='Gate?MODULE=${MODULE}&SCREEN=Auth&form.user.id=${screen.form.id}'>
					Acessos<g:icon type="gate.entity.Auth"/>
				</a>
			</li>
		</ul>
	</div>
</g:template>