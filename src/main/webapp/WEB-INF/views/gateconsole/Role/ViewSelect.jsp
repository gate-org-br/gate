<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<fieldset>
		<label x8>
			Nome:
			<span>
				<g:label property='form.name'/>
			</span>
		</label>
		<label x8>
			Perfil Pai:
			<span>
				<g:label property='form.role.name'/>
			</span>
		</label>
		<label x1>
			Master:
			<span>
				<g:label property='form.master'/>
			</span>
		</label>
		<label x1>
			Ativa:
			<span>
				<g:label property='form.active'/>
			</span>
		</label>
		<label x2>
			Sigla:
			<span>
				<g:label property='form.roleID'/>
			</span>
		</label>
		<label x4>
			E-Mail:
			<span>
				<g:label property='form.email'/>
			</span>
		</label>
		<label x8>
			Respons&aacute;vel:
			<span>
				<g:label property='form.manager.name'/>
			</span>
		</label>
		<label>
			Descri&ccedil;&atilde;o:
			<span style='height: 60px'>
				<g:print value='${screen.form.description}'/>
			</span>
		</label>
	</fieldset>

	<div class='COOLBAR'>
		<a href="#" class='Cancel Hide'>
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
			<g:menuitem module="#" screen="Auth" arguments="form.role.id=${screen.form.id}" data-selected='${param.tab eq "Auth"}'/>
			<g:menuitem module="#" screen="RoleScreen$Func" arguments="role.id=${screen.form.id}" data-selected='${param.tab eq "Func"}'/>
			<g:menuitem module="#" screen="User" action="Import" arguments="form.role.id=${screen.form.id}&form.active=true" data-selected='${param.tab eq "User"}'/>
			<g:menuitem module="#" screen="Role" action="Import" arguments="form.role.id=${screen.form.id}" data-selected='${param.tab eq "Role"}'/>
		</ul>
	</div>
</g:template>