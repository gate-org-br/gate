<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<fieldset>
		<label style='width: 50%'>
			Nome:
			<span>
				<g:label property='form.name'/>
			</span>
		</label>
		<label style='width: 50%'>
			Perfil Pai:
			<span>
				<g:label property='form.role.name'/>
			</span>
		</label>
		<label style='width: 6.25%'>
			Master:
			<span>
				<g:label property='form.master'/>
			</span>
		</label>
		<label style='width: 6.25%'>
			Ativa:
			<span>
				<g:label property='form.active'/>
			</span>
		</label>
		<label style='width: 12.5%'>
			Sigla:
			<span>
				<g:label property='form.roleID'/>
			</span>
		</label>
		<label style='width: 25%'>
			E-Mail:
			<span>
				<g:label property='form.email'/>
			</span>
		</label>
		<label style='width: 50%'>
			Respons&aacute;vel:
			<span>
				<g:label property='form.manager.name'/>
			</span>
		</label>
		<label style='width: 100%'>
			Descri&ccedil;&atilde;o:
			<span style='height: 60px'>
				<g:print value='${screen.form.description}'/>
			</span>
		</label>
	</fieldset>

	<div class='COOLBAR'>
		<a href="#" class='Hide' style='float: left'>
			Fechar<g:icon type="cancel"/>
		</a>

		<g:link module='#' screen='#' action='Update' arguments="form.id=${screen.form.id}">
			Alterar<g:icon type="update"/>
		</g:link>

		<g:link module='#' screen='#' action='Delete' arguments="form.id=${screen.form.id}" style='color: #660000' data-confirm="Tem certeza de que deseja remover este registro?">
			Remover<g:icon type="delete"/>
		</g:link>
	</div>

	<div class='PageControl'>
		<ul>
			<g:menuitem module="#" screen="Auth" arguments="form.role.id=${screen.form.id}"
				    style='width: 200px' data-selected='${param.tab eq "Auth"}'/>

			<g:menuitem module="#" screen="RoleScreen$Func"
				    arguments="role.id=${screen.form.id}"
				    style='width: 200px' data-selected='${param.tab eq "Func"}'/>

			<g:menuitem module="#" screen="User" action="Import"
				    arguments="form.role.id=${screen.form.id}&form.active=true"
				    style='width: 200px' data-selected='${param.tab eq "User"}'/>

			<g:menuitem module="#" screen="Role" action="Import"
				    arguments="form.role.id=${screen.form.id}"
				    style='width: 200px' data-selected='${param.tab eq "Role"}'/>
		</ul>
	</div>
</g:template>