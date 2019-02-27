<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>
<g:template filename="/WEB-INF/views/FULL.jsp">

	<form method="post" action="Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Post">
		<fieldset>
			<legend>
				<g:icon type="gateconsole.screen.MailScreen:callPost()"/>
				Destino
			</legend>
			<label style="width: 100%">
				<g:email property="destination" tabindex="1"/>
			</label>
		</fieldset>
		<div class="Coolbar">
			<g:link method="post" module="#" screen="#" action="Post"/>
		</div>
	</form>

	<g:choose>
		<g:when condition="${not empty screen.messages}">
			<div class='TEXT'>
				<table>
					<col style="width: 25%">
					<col style="width: 25%">
					<col style="width: 50%">
					<caption>
						RECURSOS A CONFIGURAR NO CONTAINER                        
					</caption>
					<thead>
						<tr>
							<th style="text-align: center">
								Tipo
							</th>
							<th style="text-align: center">
								Nome
							</th>
							<th>
								JNDI
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td style="text-align: center">
								Queue
							</td>
							<td style="text-align: center">
								MailBox
							</td>
							<td>
								java:/jms/queue/MailBox
							</td>
						</tr>
						<tr>
							<td style="text-align: center">
								Mail Session
							</td>
							<td style="text-align: center">
								MailSession
							</td>
							<td>
								java:/comp/env/MailSession
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</g:when>
		<g:when condition="${empty screen.page}">
			<div class='TEXT'>
				<h2>N&atilde;o h&aacute; nenhuma mensagem pendente de envio</h2>
			</div>
		</g:when>
		<g:otherwise>
			<table style='table-layout: fixed'>
				<col style='width: 80px'/>
				<col style='width: 120px'/>
				<col style="width: calc(50% - 100px)"/>
				<col style="width: calc(50% - 100px)"/>
				<caption>MENSAGENS PENDENTES DE ENVIO: ${screen.page.size()}</caption>
				<thead>
					<tr>
						<th style='text-align: center'>
							<g:icon type="gateconsole.screen.MailScreen"/>
						</th>
						<th style='text-align: center'>Postagem</th>
						<th style='text-align: left'>Remetente</th>
						<th style='text-align: left'>Destinat&aacute;rio</th>
					</tr>
				</thead>
				<tbody>
					<g:iterator source="${screen.page}" target="item" index="indx">
						<tr title="${item.data.subject}">
							<td style='text-align: center'>
								${indx+1}
							</td>
							<td style='text-align: center'>
								<g:print value="${item.date}"/>
							</td>
							<td style='text-align: center'>
								<g:print value="${item.sender}"/>
							</td>
							<td style='text-align: center'>
								<g:print value="${item.receiver}"/>
							</td>
						</tr>
					</g:iterator>
				</tbody>
			</table>
		</g:otherwise>
	</g:choose>
</g:template>