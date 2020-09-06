<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<fieldset>
		<legend>
			<g:path/>
		</legend>
		<label data-size='8'>
			Nome:
			<span>
				<g:label property='form.name'/>
			</span>
		</label>
		<label data-size='8'>
			Perfil Pai:
			<span>
				<g:label property='form.role.name'/>
			</span>
		</label>
		<label data-size='1'>
			Master:
			<span>
				<g:label property='form.master'/>
			</span>
		</label>
		<label data-size='1'>
			Ativa:
			<span>
				<g:label property='form.active'/>
			</span>
		</label>
		<label data-size='2'>
			Sigla:
			<span>
				<g:label property='form.roleID'/>
			</span>
		</label>
		<label data-size='4'>
			E-Mail:
			<span>
				<g:label property='form.email'/>
			</span>
		</label>
		<label data-size='8'>
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

	<g-coolbar>
		<g:link module='#' screen='#' action='Update' arguments="form.id=${screen.form.id}">
			Alterar<g:icon type="update"/>
		</g:link>

		<g:link module='#' screen='#' action='Delete' arguments="form.id=${screen.form.id}" style='color: #660000' data-confirm="Tem certeza de que deseja remover este registro?">
			Remover<g:icon type="delete"/>
		</g:link>
		<hr/>
		<a href="#" target="_hide">
			Retornar<g:icon type="cancel"/>
		</a>
	</g-coolbar>

	<g-tab-control>
		<g:link module="#" screen="Auth" arguments="form.role.id=${screen.form.id}" data-selected='${param.tab eq "Auth"}'/>
		<g:link module="#" screen="RoleScreen$Func" arguments="role.id=${screen.form.id}" data-selected='${param.tab eq "Func"}'/>
		<g:link module="#" screen="User" action="Import" arguments="form.role.id=${screen.form.id}&form.active=true" data-selected='${param.tab eq "User"}'/>
		<g:link module="#" screen="Role" action="Import" arguments="form.role.id=${screen.form.id}" data-selected='${param.tab eq "Role"}'/>
	</g-tab-control>
</g:template>