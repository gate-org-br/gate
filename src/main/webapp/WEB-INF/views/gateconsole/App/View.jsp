<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<section>
		<fieldset>
			<legend>
				<g:path/>
			</legend>
			<fieldset>
				<g:choose>
					<g:when condition="${empty screen.page}">
						<div class='TEXT'>
							<h2>
								Nenhuma aplicação encontrada
							</h2>
						</div>
					</g:when>
					<g:otherwise>
						<table>
							<colgroup>
								<col style='width: 40px'/>
								<col style='width: 120px'/>
								<col/>
							</colgroup>
							<caption>APLICAÇÕES: ${screen.page.size()}</caption>
							<thead>
								<tr>
									<th style='text-align: center'>
										<i>&#x2009;</i>
									</th>
									<th style='text-align: center'>Sigla</th>
									<th style='text-align: left'>Nome</th>
								</tr>
							</thead>
							<tbody>
								<g:iterator source="${screen.page}" target="item" index="indx">
									<tr data-target='_stack'
									    title='${item.id}'
									    data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&id=${item.id}'>
										<td style='text-align: center'>
											<g:icon type="gate.entity.App"/>
										</td>
										<td style='text-align: center'>
											<g:print value="${item.id}" />
										</td>
										<td style='text-align: left'>
											<g:print value="${item.name}" />
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
</g:template>