<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
    <body style='background-color: #CCCCD0;
          background-image: url("../gate/imge/back/BACK.png");
          background-attachment: fixed'>
        <div style='width: 100%; margin: auto'>
            <div class='TEXT' style='width: 100%; height: 80px; padding: 10px'>
                <img src='${org.icon}' style='float: left; height: 60px'/>
                <span style='float: left; height: 60px; margin-left: 10px'>
                    <strong style='font-size: 28px'>${app.id}</strong>
                    <br/><br/><span style='font-size: 14px'>${app.name}</span>
                </span>
                <div style="float: right; height: 100%">
                    <div style='height: 80%; text-align: right'>
                        Vers&atilde;o ${version}
                    </div>
                    <div style='height: 20%; text-align: right'>
                        ${screen.user.role.name}
                    </div>
                </div>
            </div>
            <div class='NAVI'>
                <ul class='NAVI'>
                    <g:secure module='contel.modulos.nacional'>
                        <li>
                            <a href='#'>
                                Gate<g:icon type="gate.entity.User"/>
                            </a>
                            <ul>
                                <g:secure module="gateconsole.screen" screen="Home">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="Home"/>
                                    </li>
                                </g:secure>
                                <g:secure module="gateconsole.screen" screen="Org">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="Org"/>
                                    </li>
                                </g:secure>
                                <g:secure module="gateconsole.screen" screen="App">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="App"/>
                                    </li>
                                </g:secure>
                                <g:secure module="gateconsole.screen" screen="Role">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="Role"/>
                                    </li>
                                </g:secure>
                                <g:secure module="gateconsole.screen" screen="User">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="User"/>
                                    </li>
                                </g:secure>
                                <g:secure module="gateconsole.screen" screen="Icon">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="Icon"/>
                                    </li>
                                </g:secure>
                                <g:secure module="gateconsole.screen" screen="Subscription">
                                    <li>
                                        <g:link module="gateconsole.screen" screen="Subscription"/>
                                    </li>
                                </g:secure>
                            </ul>
                        </li>

                        <li style='width: 12.5%; float: right'>
                            <a href='#'>
                                ${screen.user.name}<g:icon type="gate.entity.User"/>
                            </a>
                            <ul>
                                <li>
                                    <a href='Gate'>
                                        Sair do Sistema<g:icon type="2007"/>
                                    </a>
                                </li>
                            </ul>
                        </li>
                    </g:secure>
                </ul>
            </div>
            <div class="LinkControl">
                <ul>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "Home"}'>
                        <g:link module="gateconsole.screen" screen="Home"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "Org"}'>
                        <g:link module="gateconsole.screen" screen="Org"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "App"}'>
                        <g:link module="gateconsole.screen" screen="App"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "Role"}'>
                        <g:link module="gateconsole.screen" screen="Role"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "User"}'>
                        <g:link module="gateconsole.screen" screen="User"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "Icon"}'>
                        <g:link module="gateconsole.screen" screen="Icon"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "Mail"}'>
                        <g:link module="gateconsole.screen" screen="Mail"/>
                    </li>
                    <li style='width: 12.5%; min-width: 160px' data-selected='${SCREEN eq "Subscription"}'>
                        <g:link module="gateconsole.screen" screen="Subscription"/>
                    </li>
                </ul>
                <div>
                    <g:insert/>
                </div>
            </div>

            <g:alert/>
        </div>
    </body>
</g:template>
