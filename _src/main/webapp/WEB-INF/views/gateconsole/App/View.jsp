<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<g:choose>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h2>
					N&atilde;o h&aacute; nenhuma aplica&ccedil;&atilde;o cadastrada no sistema.
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
				<caption>APLICA&Ccedil;&Otilde;ES</caption>
				<thead>
					<tr>
						<th style='text-align: center'>
							<i>&#x2009;</i>
						</th>
						<th style='text-align: center'>Sigla</th>
						<th style='text-align: left'>Nome</th>
					</tr>
				</thead>
				<tfoot>
					<tr>
						<td colspan='3'>&nbsp;</td>
					</tr>
				</tfoot>
				<tbody>
					<g:iterator source="${screen.page}" target="item" index="indx">
						<tr data-target='_dialog' 
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
</g:template>