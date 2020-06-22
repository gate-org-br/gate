<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<form method='POST' action='Gate?MODULE=gateconsole.screen&SCREEN=ROLE&ACTION=Search'>
		<g:if condition="${not empty screen.page}">
			<table>
				<tbody>
					<g:iterator source="${screen.page}" children="${e -> e.children}">
						<tr data-ret='${target.id}, ${target.name}' tabindex="1">
							<td style='padding-left: ${depth*40}px'>
								${target.name}
								<g:if condition="${not empty target.roleID}">
									<span style='color: gray'>
										(<g:print value="${target.roleID}"/>)
									</span>
								</g:if>
							</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:if>
	</form>
</g:template>