<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<main>
		<form method='POST' action='#' enctype='multipart/form-data'
		      style="height: 100%">
			<fieldset style="height: 100%">
				<label data-size="4">
					Sigla:
					<span>
						<g:text property='form.orgID' tabindex='1'/>
					</span>
				</label>
				<label data-size="4">
					Nome:
					<span>
						<g:text property='form.name' tabindex='1'/>
					</span>
				</label>
				<label data-size="8">
					Logo:
					<span>
						<g:file property="form.icon" tabindex='1' required=''/>
					</span>
				</label>
				<label style="height: calc(50% - 80px)">
					Descrição:
					<span>
						<g:textarea property='form.description' tabindex='1'/>
					</span>
				</label>
				<label style="height: calc(50% - 80px)">
					Autenticadores:
					<span>
						<g:textarea property='form.authenticators' tabindex='1'/>
					</span>
				</label>
				<fieldset>
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
			</fieldset>
		</form>
	</main>
	<nav>
		<g-coolbar>
			<g:link method="post" module="#" screen="#" action="#" style='color: #006600' tabindex='2'>
				Concluir<g:icon type='commit'/>
			</g:link>
			<hr/>
			<g:link module="#" screen="#" style='float: left; color: #660000' tabindex='2'>
				Desistir<g:icon type='cancel'/>
			</g:link>
		</g-coolbar>
	</nav>
</g:template>