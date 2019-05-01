<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>


<g:template filename="/WEB-INF/views/FULL.jsp">
	<div style="height: calc(100vh - 200px); overflow: auto">
		<g:choose>
			<g:when condition="${empty screen.page}">
				<div class='TEXT'>
					<h1>
						Nenhum registro encontrado para os crit&iacute;rios de busca selecionados
					</h1>
				</div>
			</g:when>
			<g:otherwise>
				<table>
					<caption>
						FUNÇÕES ENCONTRADAS: ${screen.page.size()}
					</caption>
					<col/>
					<thead>
						<tr>
							<td>
								<input data-filter type="text"/>
							</td>
						</tr>
						<tr>
							<th data-sortable>
								Nome
							</th>
						</tr>
					</thead>
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
	</div>

	<div class='Coolbar'>
		<g:link target='_dialog' module='#' screen='#' action='Insert' tabindex='3'/>
	</div>

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