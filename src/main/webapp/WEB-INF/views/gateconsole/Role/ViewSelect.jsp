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
				<g:link module="#" screen="#" action="Select" arguments="form.id=${screen.form.role.id}">
					<g:label property='form.role.name'/>
				</g:link>
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
				<g:label property='form.description' style='height: 80px'/>
			</span>
		</label>
	</fieldset>

	<div class='COOLBAR'>
		<a class='Hide' style='float: left'>
			Fechar<g:icon type="cancel"/>
		</a>

		<g:link module='#' screen='#' action='Update' arguments="form.id=${screen.form.id}">
			Alterar<g:icon type="update"/>
		</g:link>

		<g:link module='#' screen='#' action='Delete' arguments="form.id=${screen.form.id}" style='color: #660000' data-confirm="Tem certeza de que deseja remover este registro?">
			Remover<g:icon type="delete"/>
		</g:link>
	</div>

	<div class='PageControl' data-type='Frame'>
		<ul>
			<li style='width: 200px' data-selected='${param.tab eq "Auth"}'>
				<g:link id='0' module="#" screen="Auth" arguments="form.role.id=${screen.form.id}" style='text-align: left'>
					Acessos<g:icon type="gate.entity.Auth"/>
				</g:link>
			</li>
			<li style='width: 200px' data-selected='${param.tab eq "Role"}'>
				<g:link id='1' module="#"  screen="Role" action="Import" arguments="form.role.id=${screen.form.id}" style='text-align: left'>
					Sub Perfis<g:icon type="gate.entity.Role"/>
				</g:link>
			</li>
			<li style='width: 200px' data-selected='${param.tab eq "User"}'>
				<g:link id='2' module="#" screen="User" action="Import" arguments="form.role.id=${screen.form.id}&form.active=true" style='text-align: left'>
					Usu&aacute;rios<g:icon type="gate.entity.User"/>
				</g:link>
			</li>
		</ul>
	</div>
</g:template>