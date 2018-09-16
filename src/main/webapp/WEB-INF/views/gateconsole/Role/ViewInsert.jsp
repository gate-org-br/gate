<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='#'>
		<fieldset>
			<legend>
				Novo Perfil
			</legend>
			<label style='width: 50%'>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1'/>
				</span>
			</label>
			<label style='width: 50%'>
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
			<label style='width: 6.25%'>
				Master:
				<span>
					<g:select property='form.master' tabindex='1'/>
				</span>
			</label>
			<label style='width: 6.25%'>
				Ativa:
				<span>
					<g:select property='form.active' tabindex='1'/>
				</span>
			</label>
			<label style='width: 12.5%'>
				Sigla:
				<span>
					<g:text property='form.roleID' tabindex='1'/>
				</span>
			</label>
			<label style='width: 25%'>
				E-Mail:
				<span>
					<g:text property='form.email' tabindex='1'/>
				</span>
			</label>
			<label style='width: 50%'>
				Respons&aacute;vel:
				<span>
					<g:select property='form.manager.id' identifiedBy="id"
						  tabindex='1' options="${screen.users}" required=''/>
				</span>
			</label>
			<label style='width: 100%'>
				Descri&ccedil;&atilde;o:
				<span style='height: 60px'>
					<g:textarea property='form.description' tabindex='1'/>
				</span>
			</label>
		</fieldset>

		<div class='COOLBAR'>
			<g:link method="post" module="#" screen="#" action="#" style='color: #006600' tabindex='2'>
				Concluir<g:icon type="commit"/>
			</g:link>
			<a href='#' class="Hide" style='color: #660000; float: left' tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</a>
		</div>
	</form>
</g:template>