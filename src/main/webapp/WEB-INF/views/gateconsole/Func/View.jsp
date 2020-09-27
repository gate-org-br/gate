<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<article>
		<section>
			<fieldset>
				<legend>
					<g:path/>
				</legend>
				<fieldset>
					<g:choose>
						<g:when condition="${empty screen.page}">
							<div class='TEXT'>
								<h1>
									Nenhum registro encontrado
								</h1>
							</div>
						</g:when>
						<g:otherwise>
							<table>
								<caption>
									FUNÇÕES: ${screen.page.size()}
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
										<tr data-target='_stack' data-on-hide="reload" title='Função'
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
				</fieldset>
			</fieldset>
		</section>

		<footer>
			<g-coolbar>
				<g:link class="Action" target='_stack'
					data-on-hide="reload" 
					title="Função"
					module='#' screen='#' action='Insert' tabindex='3'/>
			</g-coolbar>
		</footer>
	</article>
</g:template>