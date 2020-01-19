<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<div class='COOLBAR'>
		<g:link target="_dialog" module='#' screen='#' action='Insert' tabindex='1'>
			Inserir<g:icon type="insert"/>
		</g:link>
	</div>

	<g:if condition="${not empty screen.page}">
		<table class='TREEVIEW' style="table-layout: auto">
			<col style='width: 50px; min-width: 50px'/>
			<col/>

			<caption>
				PERFIS
			</caption>

			<thead>
				<tr>
					<th style='text-align: center'>
						#
					</th>
					<th>
						Nome
					</th>
				</tr>
			</thead>

			<tfoot></tfoot>

			<tbody>
				<g:iterator source="${screen.page}" target="role" children="${e -> e.children}">
					<tr data-depth='${depth}' data-target='_dialog' title='Perfil'
					    data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${role.id}'>
						<td></td>
						<td style='padding-left: ${depth*40}px; font-weight: ${role.isMaster() ? "bold" : "normal"}'>
							<g:icon type="gate.entity.Role"/>
							${role.name}
							<g:if condition="${not empty role.roleID}">
								(<g:print value="${role.roleID}"/>)
							</g:if>
						</td>
					</tr>
					<g:if condition="${not empty role.funcs}">
						<tr data-depth='${depth+1}'>
							<td></td>
							<td style='padding-left: ${(depth+1)*40}px; color: #000099;'>
								<g:icon type="gate.entity.Func"/>
								Funções
							</td>
						</tr>
						<g:iterator source="${role.funcs}" target="_func">
							<tr data-depth='${depth+1}' data-target='_dialog'
							    data-action='Gate?MODULE=${MODULE}&SCREEN=Func&ACTION=Select&form.id=${_func.id}'>
								<td></td>
								<td style='padding-left: ${(depth+1)*40}px;'>
									<g:icon type="gate.entity.Func"/>
									<g:print value="${_func}"/>
								</td>
							</tr>
							<g:iterator source="${_func.auths}" target="auth">
								<tr data-depth='${depth+1}'>
									<td></td>
									<td style='padding-left: ${(depth+1)*40}px;
									    color: ${auth.mode eq "ALLOW" ? "green" : "red"};
									    font-weight: ${auth.type eq "PUBLIC" ? "bold" : "normal"}'>
										<g:icon type="${auth.mode}" title='${auth.mode.toString()}'/>&nbsp;<g:icon type="${auth.type}" title='${auth.type.toString()}'/>&nbsp;<g:print value="${auth}"/>
									</td>
								</tr>
							</g:iterator>
						</g:iterator>
					</g:if>
					<g:if condition="${not empty role.auths}">
						<tr data-depth='${depth+1}'>
							<td></td>
							<td style='padding-left: ${(depth+1)*40}px; color: #000099;'>
								<g:icon type="gate.entity.Auth"/>
								Acessos
							</td>
						</tr>
						<g:iterator source="${role.auths}" target="auth">
							<tr data-depth='${depth+1}'>
								<td></td>
								<td style='padding-left: ${(depth+1)*40}px;
								    color: ${auth.mode eq "ALLOW" ? "green" : "red"};
								    font-weight: ${auth.type eq "PUBLIC" ? "bold" : "normal"}'>
									<g:icon type="${auth.mode}" title='${auth.mode.toString()}'/>&nbsp;<g:icon type="${auth.type}" title='${auth.type.toString()}'/>&nbsp;<g:print value="${auth}"/>
								</td>
							</tr>
						</g:iterator>
					</g:if>
					<g:if condition="${not empty role.users}">
						<tr data-depth='${depth+1}'>
							<td></td>
							<td style='padding-left: ${(depth+1)*40}px; color: #000099;'>
								<g:icon type="gate.entity.User"/>
								Usu&aacute;rios
							</td>
						</tr>
						<g:iterator source="${role.users}" target="_user">
							<tr data-depth='${depth+1}' data-target='_dialog'
							    data-action='Gate?MODULE=${MODULE}&SCREEN=User&ACTION=Select&form.id=${_user.id}'>
								<td></td>
								<td style='padding-left: ${(depth+1)*40}px;'>
									<g:icon type="gate.entity.User"/>
									<g:print value="${_user}"/>
								</td>
							</tr>
							<g:iterator source="${_user.funcs}" target="_func">
								<tr data-depth='${depth+1}' data-target='_dialog'
								    data-action='Gate?MODULE=${MODULE}&SCREEN=Func&ACTION=Select&form.id=${_func.id}'>
									<td></td>
									<td style='padding-left: ${(depth+1)*40}px; color: black; font-weight: bold'>
										<g:icon type="gate.entity.Func"/>
										<g:print value="${_func}"/>
									</td>
								</tr>
								<g:iterator source="${_func.auths}" target="auth">
									<tr data-depth='${depth+1}'>
										<td></td>
										<td style='padding-left: ${(depth+1)*40}px;
										    color: ${auth.mode eq "ALLOW" ? "green" : "red"};
										    font-weight: ${auth.type eq "PUBLIC" ? "bold" : "normal"}'>
											<g:icon type="${auth.mode}" title='${auth.mode.toString()}'/>&nbsp;<g:icon type="${auth.type}" title='${auth.type.toString()}'/>&nbsp;<g:print value="${auth}"/>
										</td>
									</tr>
								</g:iterator>
							</g:iterator>

							<g:iterator source="${_user.auths}" target="auth">
								<tr data-depth='${depth+1}'>
									<td></td>
									<td style='padding-left: ${(depth+1)*40}px;
									    color: ${auth.mode eq "ALLOW" ? "green" : "red"};
									    font-weight: ${auth.type eq "PUBLIC" ? "bold" : "normal"}'>
										<g:icon type="${auth.mode}" title='${auth.mode.toString()}'/>
										<g:icon type="${auth.type}" title='${auth.type.toString()}'/>
										<g:print value="${auth}"/>
									</td>
								</tr>
							</g:iterator>
						</g:iterator>
					</g:if>
				</g:iterator>
			</tbody>
		</table>
	</g:if>
</g:template>