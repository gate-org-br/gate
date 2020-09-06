<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/FULL.jsp">
	<form id='form' method='POST' action='#'>
		<fieldset>
			<legend>
				<g:path/>
			</legend>

			<label data-size='4'>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1' required=''/>
				</span>
			</label>
			<label data-size='4'>
				Login:
				<span>
					<g:text property='form.userID' tabindex='1' required=''/>
				</span>
			</label>
			<label data-size='8'>
				Perfil
				<span>
					<g:hidden id='form.role.id' property="form.role.id" required=''/>
					<g:text id='form.role.name' property='form.role.name' required='' data-getter="adsfasdf"/>
					<g:link id="adsfasdf" module="#" screen="Role" action="Search" data-get='form.role.id, form.role.name'
						arguments="form.name=@{form.role.name}"
						tabindex='1' title='Selecionar Perfil'>
						<g:icon type="search"/>
					</g:link>
				</span>
			</label>
			<label data-size='2'>
				Ativo:
				<span>
					<g:select property="form.active" tabindex='1' required=''/>
				</span>
			</label>
			<label data-size='2'>
				CPF:
				<span>
					<g:text property='form.CPF' tabindex='1' required=''/>
				</span>
			</label>
			<label data-size='2'>
				Data de Nascimento:
				<span>
					<g:text class='Date'
						property='form.birthdate' tabindex='1' required=''/>
				</span>
			</label>
			<label data-size='2'>
				Sexo:
				<span>
					<g:select property='form.sex' tabindex='1' required=''/>
				</span>
			</label>
			<label data-size='2'>
				Telefone:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:text property='form.phone' tabindex='1'/>
				</span>
			</label>
			<label data-size='2'>
				Celular:
				<span>
					<g:icon type="gate.type.Phone"/>
					<g:text property='form.cellPhone' tabindex='1'/>
				</span>
			</label>
			<label data-size='4'>
				E-Mail:
				<span>
					<g:icon type="2034"/>
					<g:text property='form.email' tabindex='1'/>
				</span>
			</label>
		</fieldset>

		<g-coolbar>
			<g:link class="Action" method="post" module='#' screen='#' tabindex='2'>
				Pesquisar<g:icon type="search"/>
			</g:link>
			<g:link target="_report" method="post" module='#' screen='#' action="Report" tabindex='2'/>
			<g:link target='_stack' data-submit-on-hide='form' module='#' screen='#' action='Upload' tabindex='3'/>
			<g:link target='_stack' data-submit-on-hide='form' module='#' screen='#' action='Insert' tabindex='3'/>
		</g-coolbar>

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
				<table class="c1 c5"  data-collapse="Phone">
					<caption>
						USUÁRIOS: ${screen.page.paginator.dataSize}
					</caption>
					<thead>
						<tr>
							<th style="width: 60px">
								<g:ordenator method="post" property="active">
									Ativo
								</g:ordenator>
							</th>
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
								<g:ordenator method="post" property="role.name">
									Perfil
								</g:ordenator>
							</th>
							<th style="width: 120px">
								<g:ordenator method="post" property="registration">
									Cadastro
								</g:ordenator>
							</th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan="5">
								<g:paginator/>
							</td>
						</tr>
					</tfoot>
					<tbody>
						<g:iterator source="${screen.page}" target="target">
							<tr data-target='_stack' data-submit-on-hide='form' title='${target.name}' autofocus
							    data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${target.id}'>
								<td title="Ativo"><g:print value="${target.active}"/></td>
								<td title="Nome"><g:print value="${target.name}"/></td>
								<td title="Login"><g:print value="${target.userID}"/></td>
								<td title="Perfil"><g:print value="${target.role.name}"/></td>
								<td title="Cadastro"><g:print value="${target.registration}"/></td>
							</tr>
						</g:iterator>
					</tbody>
				</table>
			</g:otherwise>
		</g:choose>
	</form>
</g:template>

