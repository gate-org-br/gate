<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<form method='POST' action='#' enctype='multipart/form-data'>
		<fieldset>
			<legend>
				Organiza&ccedil;&atilde;o
			</legend>
			<label style='width: 25%'>
				Sigla:
				<span>
					<g:text property='form.orgID' tabindex='1'/>
				</span>
			</label>
			<label style='width: 75%'>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1'/>
				</span>
			</label>
			<label style='width: 100%'>
				Descri&ccedil;&atilde;o:
				<span style='height: 80px'>
					<g:textarea property='form.description' tabindex='1'/>
				</span>
			</label>
			<label style='width: 100%'>
				Autenticadores:
				<span style='height: 40px'>
					<g:textarea property='form.authenticators' tabindex='1'/>
				</span>
			</label>				

			<fieldset style='width: 100%'>
				<legend>
					<g:icon type="2003"/>Expediente
				</legend>
				<label style='width: 15%'>
					Dom:
					<span>
						<g:text property="form.sun" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
				<label style='width: 14%'>
					Seg:
					<span>
						<g:text property="form.mon" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
				<label style='width: 14%'>
					Ter:
					<span>
						<g:text property="form.tue" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
				<label style='width: 14%'>
					Qua:
					<span>
						<g:text property="form.wed" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
				<label style='width: 14%'>
					Qui:
					<span>
						<g:text property="form.thu" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
				<label style='width: 14%'>
					Sex:
					<span>
						<g:text property="form.fri" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
				<label style='width: 15%'>
					Sab:
					<span>
						<g:text property="form.sat" tabindex='1' required='' style='text-align: center'/>
					</span>
				</label>
			</fieldset>

			<fieldset style='width: 100%'>
				<legend>
					Logo
				</legend>
				<label style='width: 100%'>
					<span>
						<g:file property="form.icon" tabindex='1' required=''/>
					</span>
				</label>
			</fieldset>	
		</fieldset>

		<div class='COOLBAR'>
			<g:link module="#" screen="#" style='float: left; color: #660000' tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</g:link>
			<g:link method="post" module="#" screen="#" action="#" style='color: #006600' tabindex='2'>
				Concluir<g:icon type='commit'/>
			</g:link>
		</div>
	</form>
</g:template>