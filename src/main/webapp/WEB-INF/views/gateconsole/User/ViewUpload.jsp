<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<form method='POST' action='#'
	      enctype='multipart/form-data'>
		<fieldset>
			<legend>
				<g:path/>
			</legend>
			<label data-size='8'>
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
			<label data-size='8'>
				Arquivo:
				<span>
					<g:file property="file"/>
				</span>
			</label>
		</fieldset>
		<g-coolbar>
			<g:link class="Commit" method='post' module="#" screen="#" action="Commit" tabindex='2'>
				Concluir<g:icon type="upload"/>
			</g:link>
			<hr/>
			<a target="_hide" href="#" tabindex='3'>
				Desistir<g:icon type='cancel'/>
			</a>    
		</g-coolbar>
	</form>

	<div class='TEXT'>
		<h1>Formato do Arquivo CSV</h1>
		<p style='text-indent: 40px;'>
			O arquivo deve estar no formato CSV (Comma Separated Values), 
			deve estar em UTF-8, e deve conter os campos abaixo na ordem correta
			para que possa ser carregado. Os campos n&atilde;o requeridos podem ser omitidos ou deixados em branco.
		</p>
		<g:properties source="${screen.properties}"/>
	</div>
</g:template>