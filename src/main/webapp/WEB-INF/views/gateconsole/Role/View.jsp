<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
    <div class='COOLBAR'>
        <g:link module='#' screen='#' action='Insert' tabindex='1'>
            Inserir<g:icon type="insert"/>
        </g:link>
    </div>

    <g:if condition="${not empty screen.page}">
        <table class='TREEVIEW'>
            <col style='width: 64px'/>
            <col/>

            <caption>
                PERFIS
            </caption>

            <thead>
                <tr>
                    <th style='text-align: center'>
                        #
                    </th>
                    <th style='text-align: left'>
                        Nome
                    </th>
                </tr>
            </thead>

            <tfoot></tfoot>

            <tbody>
                <g:iterator source="${screen.page}" target="role" children="roles" depth="depth" >
                    <tr data-depth='${depth}' data-target='_dialog' title='Perfil'
                        data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${role.id}'>
                        <td></td>
                        <td style='text-align: left; padding-left: ${depth*40}px'>
                            <g:choose>
                                <g:when condition="${role.isMaster()}">
                                    <strong>
                                        <i style='color: gray; margin: 0 4px 0 8px'>&#x2006;</i>${role.name}
                                        <g:if condition="${not empty role.roleID}">
                                            (<g:print value="${role.roleID}"/>)
                                        </g:if>
                                    </strong>
                                </g:when>
                                <g:otherwise>
                                    <i style='color: gray; margin: 0 4px 0 8px'>&#x2006;</i>${role.name}
                                    <g:if condition="${not empty role.roleID}">
                                        <span style='color: gray'>
                                            (<g:print value="${role.roleID}"/>)
                                        </span>
                                    </g:if>
                                </g:otherwise>
                            </g:choose>
                        </td>
                    </tr>
                    <g:if condition="${not empty role.auths}">
                        <tr data-depth='${depth+1}' data-target='_dialog'
                            data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${role.id}'>
                            <td></td>
                            <td style='text-align: left; padding-left: ${(depth+1)*40}px; color: #000099; font-weight: bold'>
                                <i style='color: gray; margin: 0 4px 0 8px'>&#x2002;</i>Acessos
                            </td>
                        </tr>
                        <g:iterator source="${role.auths}" target="auth">
                            <tr data-depth='${depth+2}' data-target='_dialog'
                                data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${role.id}'>
                                <td></td>
                                <td style='text-align: left;
                                    padding-left: ${(depth+2)*40}px;
                                    color: ${auth.mode eq "ALLOW" ? "green" : "red"};
                                    font-weight: ${auth.type eq "PUBLIC" ? "bold" : "normal"}'>
                                    <g:icon type="${auth.mode}" title='${auth.mode.toString()}'/>&nbsp;<g:icon type="${auth.type}" title='${auth.type.toString()}'/>&nbsp;<g:print value="${auth}"/>
                                </td>
                            </tr>
                        </g:iterator>
                    </g:if>

                    <g:if condition="${not empty role.users}">
                        <tr data-depth='${depth+1}' data-target='_dialog'
                            data-action='Gate?MODULE=${MODULE}&SCREEN=${SCREEN}&ACTION=Select&form.id=${role.id}'>
                            <td></td>
                            <td style='text-align: left; padding-left: ${(depth+1)*40}px; color: #000099; font-weight: bold'>
                                <i style='color: gray; margin: 0 4px 0 8px'>&#x2005;</i>Usu&aacute;rios
                            </td>
                        </tr>
                        <g:iterator source="${role.users}" target="_user">
                            <tr data-depth='${depth+2}' data-target='_dialog'
                                data-action='Gate?MODULE=${MODULE}&SCREEN=User&ACTION=Select&form.id=${_user.id}'>
                                <td></td>
                                <td style='text-align: left; padding-left: ${(depth+2)*40}px'>
                                    <i style='color: gray; margin: 0 4px 0 8px'>&#x2004;</i><g:print value="${_user}"/>
                                </td>
                            </tr>
                            <g:iterator source="${_user.auths}" target="auth">
                                <tr data-depth='${depth+3}' data-target='_dialog'
                                    data-action='Gate?MODULE=${MODULE}&SCREEN=User&ACTION=Select&form.id=${_user.id}'>
                                    <td></td>
                                    <td style='text-align: left;
                                        padding-left: ${(depth+3)*40}px;
                                        color: ${auth.mode eq "ALLOW" ? "green" : "red"};
                                        font-weight: ${auth.type eq "PUBLIC" ? "bold" : "normal"}'>
                                        <g:icon type="${auth.mode}" title='${auth.mode.toString()}'/>&nbsp;<g:icon type="${auth.type}" title='${auth.type.toString()}'/>&nbsp;<g:print value="${auth}"/>
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