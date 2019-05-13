<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='#' enctype='multipart/form-data'>
		<fieldset>
			<legend>
				Novo Usu&aacute;rio<g:icon type="update"/>
			</legend>
			<g:hidden property='form'/>

			<label x2>
				Ativo:
				<span>
					<g:select property="form.active" tabindex='1'/>
				</span>
			</label>
			<label x2>
				Cadastro:
				<span>
					<g:print value="${screen.form.registration}"/>
				</span>
			</label>
			<label x2>
				Login:
				<span>
					<g:text property='form.userID' tabindex='1'/>
				</span>
			</label>
			<label x2>
				Senha:
				<span>
					******
				</span>
			</label>
			<label x4>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1'/>
				</span>
			</label>
			<label x4>
				Perfil
				<span>
					<g:hidden id='form.role.id' property="form.role.id" required=''/>
					<g:text id='form.role.name' property='form.role.name' readonly='readonly' required=''
						style='width: calc(100% - 32px)'/>
					<g:link module="#" screen="Role" action="Search"
						data-get='form.role.id, form.role.name'
						tabindex='1' style='width: 32px' title='Selecionar Perfil'>
						<g:icon type="search"/>
					</g:link>
				</span>
			</label>
			<label x2>
				CPF:
				<span>
					<g:text property='form.CPF' tabindex='1'/>
				</span>
			</label>
			<label x2>
				Data de Nascimento:
				<span>
					<g:text class='Date' property='form.birthdate' tabindex='1'/>
				</span>
			</label>
			<label x4>
				Sexo:
				<span>
					<g:select property='form.sex' tabindex='1'/>
				</span>
			</label>
			<label x2>
				Telefone:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:text property='form.phone' tabindex='1'/>
				</span>
			</label>
			<label x2>
				Celular:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:text property='form.cellPhone' tabindex='1'/>
				</span>
			</label>
			<label x4>
				E-Mail:
				<span>
					<g:icon type="2034"/>
					<g:text property='form.email' tabindex='1'/>
				</span>
			</label>
			<label>
				Detalhes:
				<span style='flex-basis: 80px'>
					<g:textarea property='form.details' tabindex='1'/>
				</span>
			</label>
			<label>
				Foto:
				<span>
					<g:file property='form.photo' tabindex='1'/>
				</span>
			</label>
		</fieldset>

		<div class='COOLBAR'>
			<g:link class="Commit" method="post" module="#" screen="#" action="#" tabindex='2'>
				Concluir<g:icon type="commit"/>
			</g:link>
			<a class='Cancel Hide' tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</a>
		</div>
	</form>
</g:template>