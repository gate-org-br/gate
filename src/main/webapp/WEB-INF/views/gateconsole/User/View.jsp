<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/FULL.jsp">
	<form id='form' method='POST' action='#'>
		<fieldset>
			<legend>
				<g:icon type="search"/>Pesquisar Usu&aacute;rios
			</legend>

			<label x4>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1' required=''/>
				</span>
			</label>
			<label x4>
				Login:
				<span>
					<g:text property='form.userID' tabindex='1' required=''/>
				</span>
			</label>
			<label x8>
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
				Ativo:
				<span>
					<g:select property="form.active" tabindex='1' required=''/>
				</span>
			</label>
			<label x2>
				CPF:
				<span>
					<g:text property='form.CPF' tabindex='1' required=''/>
				</span>
			</label>
			<label x2>
				Data de Nascimento:
				<span>
					<g:text class='Date'
						property='form.birthdate' tabindex='1' required=''/>
				</span>
			</label>
			<label x2>
				Sexo:
				<span>
					<g:select property='form.sex' tabindex='1' required=''/>
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
		</fieldset>

		<div class='Coolbar'>
			<g:link method="post" module='#' screen='#' tabindex='2'>
				Pesquisar<g:icon type="search"/>
			</g:link>
			<g:link target='_dialog' module='#' screen='#' action='Insert' tabindex='3' title='Novo Usu&aacute;rio'/>
			<g:link target='_dialog' module='#' screen='#' action='Upload' tabindex='3' title='Importar Usu&aacute;rios'/>
			<g:link target="_report-dialog" method="post" module='#' screen='#' action="Report" tabindex='2'/>
		</div>

		<g:choose>
			<g:when condition="${screen.GET}">
				<div class='TEXT'>
					<h1>
						Entre com os crit&eacute;rios de busca e clique em Pesquisar
					</h1>
				</div>
			</g:when>
			<g:when condition="${empty screen.page}">
				<div class='TEXT'>
					<h1>
						Nenhum registro encontrado para os crit&iacute;rios de busca selecionados
					</h1>
				</div>
			</g:when>
			<g:otherwise>
				<div style="overflow-x:auto;">
					<table class="c2 c3 c4" style="min-width: 820px">
						<caption>
							USUÁRIOS ENCONTRADOS: ${screen.page.paginator.dataSize}
						</caption>
						<col/>
						<col style="width: 250px"/>
						<col style="width: 125px"/>
						<col style="width: 125px"/>
						<thead>
							<tr>
								<th>
									<g:ordenator method="post" property="name">
										Nome
									</g:ordenator>
								</th>
								<th>
									<g:ordenator method="post" property="userID">
										Login
									</g:ordenator>
								</th>
								<th>
									<g:ordenator method="post" property="registration">
										Cadastro
									</g:ordenator>
								</th>
								<th>
									<g:ordenator method="post" property="active">
										Ativo
									</g:ordenator>
								</th>
							</tr>
						</thead>
						<tfoot>
							<tr>
								<td colspan='4' style='text-align: right'>
									<g:paginator/>
								</td>
							</tr>
						</tfoot>
						<tbody>
							<g:iterator source="${screen.page}" target="target">
								<tr data-target='_dialog' title='Usuário'
								    data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${target.id}'>
									<td><g:print value="${target.name}"/></td>
									<td><g:print value="${target.userID}"/></td>
									<td><g:print value="${target.registration}"/></td>
									<td><g:print value="${target.active}"/></td>
								</tr>
							</g:iterator>
						</tbody>
					</table>
				</div>
			</g:otherwise>
		</g:choose>
	</form>

	<script>
		window.addEventListener("load", function ()
		{
			Array.from(document.querySelectorAll("tr[data-target=_dialog]")).forEach(function (element)
			{
				element.addEventListener("hide", () => document.getElementById("form").submit());
			});
		});
	</script>
</g:template>