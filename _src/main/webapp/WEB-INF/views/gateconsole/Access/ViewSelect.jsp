<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='#'>
		<fieldset>
			<legend>
				Usu&aacute;rio
				<g:hidden property="form.id"/>
			</legend>
			<label style='width: 12.5%'>
				Ativo:
				<span>
					<g:label property='form.active'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Login:
				<span>
					<g:label property='form.userID'/>
				</span>
			</label>
			<label style='width: 25%'>
				Nome:
				<span>
					<g:label property='form.name'/>
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

			<label style='width: 100%'>
				Perfil:
				<span>
					<g:select property='form.role.id'
						  options="${screen.roles}"
						  children="${e -> e.children}"
						  values="${e -> e.id}">
						${option.name}
					</g:select>
				</span>
			</label>

		</fieldset>

		<div class='COOLBAR'>
			<g:link method="post" module="#" screen="#" action="Update" style='color: #006600' data-confirm='Tem certeza de que deseja acatar o cadastro?'>
				Aceitar<g:icon type="2037"/>
			</g:link>
			<g:link module="#" screen="#" action="Delete" arguments="form.id=${screen.form.id}" style='float: left; color: #660000' data-confirm='Tem certeza de que deseja recusar o cadastro?'>
				Recusar<g:icon type="2038"/>
			</g:link>
		</div>
	</form>
</g:template>