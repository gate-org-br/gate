<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/FULL.jsp">
	<form id='form' method='POST' action='#'>
		<fieldset>
			<legend>
				<g:icon type="search"/>Pesquisar Funções
			</legend>
			<label style='width: 100%'>
				Nome:
				<span>
					<g:text property='form.name' tabindex='1' required=''/>
				</span>
			</label>
		</fieldset>

		<div class='Coolbar'>
			<g:link method="post" module='#' screen='#' tabindex='2'>
				Pesquisar<g:icon type="search"/>
			</g:link>
			<g:link target='_dialog' module='#' screen='#' action='Insert' tabindex='3'/>
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
				<table style='table-layout: fixed'>
					<caption>
						FUNÇÕES ENCONTRADAS: ${screen.page.paginator.dataSize}
					</caption>
					<col/>
					<thead>
						<tr>
							<th>
								<g:ordenator method="post" property="name">
									Nome
								</g:ordenator>
							</th>
						</tr>
					</thead>
					<tfoot>
						<tr>
							<td colspan='1' style='text-align: right'>
								<g:paginator/>
							</td>
						</tr>
					</tfoot>
					<tbody>
						<g:iterator source="${screen.page}" target="target">
							<tr data-target='_dialog' title='Usuário'
							    data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${target.id}'>
								<td>
									<g:print value="${target.name}"/>
								</td>
							</tr>
						</g:iterator>
					</tbody>
				</table>
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