<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='#'>
		<fieldset>
			<legend>
				Alterar Perfil<g:icon type="update"/>
				<g:hidden property="form.id"/>
			</legend>
			<label data-size="8">
				Nome:
				<span>
					<g:text property='form.name' tabindex='1'/>
				</span>
			</label> 
			<label data-size="8">
				Perfil Pai:
				<span>
					<g:hidden id='form.role.id' property="form.role.id" required=''/>
					<g:text id='form.role.name' property='form.role.name' readonly='readonly' required=''
						style='width: calc(100% - 32px)'/>
					<g:link module="#" screen="#" action="Search"
						data-get='form.role.id, form.role.name'
						tabindex='1' style='width: 32px' title='Selecionar Perfil'>
						<g:icon type="search"/>
					</g:link>
				</span>
			</label>
			<label data-size="1">
				Master:
				<span>
					<g:select property='form.master' tabindex='1'/>
				</span>
			</label>
			<label data-size="1">
				Ativa:
				<span>
					<g:select property='form.active' tabindex='1'/>
				</span>
			</label>
			<label data-size="2">
				Sigla:
				<span>
					<g:text property='form.roleID' tabindex='1'/>
				</span>
			</label>
			<label data-size="4">
				E-Mail:
				<span>
					<g:text property='form.email' tabindex='1'/>
				</span>
			</label>
			<label data-size="8">
				Responsável:
				<span>
					<g:select property='form.manager.id' values="${e -> e.id}"
						  tabindex='1' options="${screen.users}" required=''/>
				</span>
			</label>
			<label>
				Descrição:
				<span style='flex-basis: 120px'>
					<g:textarea property='form.description' tabindex='1'/>
				</span>
			</label>
		</fieldset>

		<div class='COOLBAR'>
			<g:link class="Commit" method="post" module="#" screen="#" action="#" tabindex='2'>
				Concluir<g:icon type="commit"/>
			</g:link>
			<g:link class="Cancel" module="#" screen="#" action="Select" arguments="form.id=${screen.form.id}" tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</g:link>
		</div>
	</form>
</g:template>