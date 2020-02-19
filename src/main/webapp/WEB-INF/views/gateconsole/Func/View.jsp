<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<article>
		<section>
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
								<tr data-target='_stack' data-reload-on-hide title='Função'
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
		</section>

		<footer>
			<g-coolbar>
				<g:link class="Action" target='_stack'
					data-reload-on-hide="data-reload-on-hide"
					title="Função"
					module='#' screen='#' action='Insert' tabindex='3'/>
			</g-coolbar>
		</footer>
	</article>
</g:template>