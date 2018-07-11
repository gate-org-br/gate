<!DOCTYPE HTML>
<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/MAIN.jsp">
    <body style='background-color: #CCCCD0; 
          background-image: url("../gate/imge/back/BACK.png"); 
          background-attachment: fixed'>
        <div style='width: 970px; margin: auto; padding-top: 12px'>
            <form name='FORMULARIO' id='FORMULARIO' method='POST' action='User'>
                <div style='width: 600px; height: 524px; position: absolute; top: 50%; margin-top: -262px; left: 50%; margin-left: -300px;
                     padding: 10px; -webkit-box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75); -moz-box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75);
                     box-shadow: 10px 10px 5px 0px rgba(0,0,0,0.75); border: 2px groove #CCCCCC; background-color: #CDCAB9'>
                    <div class='TEXT' style='width: 100%; height: 60px; padding: 10px; margin: 10px 0 0 0'>
                        <img src='${org.icon}' style='float: left; height: 40px'/>
                        <span style='float: left; height: 40px; margin-left: 10px; line-height: 14px'>
                            <strong style='font-size: 16px'>${app.id}</strong>
                            <br/><br/><span style='font-size: 12px'>${app.name}</span>
                        </span>
                    </div>
                    <div class='LinkControl'>
                        <ul>
                            <li style='width: 25%'>
                                <a href='Gate'>
                                    Fazer Logon<g:icon type="2000"/>
                                </a>
                            </li>
                            <li style='width: 25%'>
                                <a href='Password'>
                                    Trocar Senha<g:icon type="passwd"/>
                                </a>
                            </li>
                            <li data-selected='true' style='width: 25%'>
                                <a href='Register'>
                                    Criar Conta<g:icon type="gate.entity.User"/>
                                </a>
                            </li>							
                        </ul>
                        <div>							
                            <fieldset style='margin-top: 10px'>
                                <label style='width: 100%'>
                                    Nome:
                                    <span>
                                        <g:icon type="gate.entity.User"/>
                                        <input type='TEXT' required='required' name='user.name' 
                                               maxlength='64' tabindex='1' title='Entre com o seu nome.'/>
                                    </span>
                                </label>
                                <label style='width: 100%'>
                                    Login:
                                    <span>
                                        <g:icon type="2096"/>
                                        <input type='TEXT' required='required' name='user.userID' 
                                               maxlength='64' tabindex='1' title='Entre com o seu login.'/>
                                    </span>
                                </label>
                                <label style='width: 50%'>
                                    Senha:
                                    <span>
                                        <g:icon type="passwd"/>
                                        <input type='PASSWORD' required='required' name='user.passwd' 
                                               maxlength='32' tabindex='1' title='Entre com a sua senha.'/>
                                    </span>
                                </label>						
                                <label style='width: 50%'>
                                    Repita:
                                    <span>
                                        <g:icon type="passwd"/>
                                        <input type='PASSWORD' required='required' name='user.repeat' 
                                               maxlength='32' tabindex='1' title='repita sua senha.'/>
                                    </span>
                                </label>
                                <label style='width: 100%'>
                                    Empresa:
                                    <span>
                                        <g:icon type="2048"/>
                                        <input type='TEXT' required='required' name='user.details' 
                                               maxlength='256' tabindex='1' title='Entre com o nome da empresa onde trabalha.'/>
                                    </span>
                                </label>
                                <label style='width: 100%'>
                                    E-Mail:
                                    <span>
                                        <g:icon type="2034"/>
                                        <input type='TEXT' required='required' name='user.email' 
                                               maxlength='64' tabindex='1' title='Entre com o seu endere&ccedil;o de email.'/>
                                    </span>
                                </label>
                                <label style='width: 50%'>
                                    Telefone:
                                    <span>
                                        <g:icon type="gate.type.Phone"/>
                                        <input type='TEXT' name='user.phone' 
                                               maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone fixo.'/>
                                    </span>
                                </label>						
                                <label style='width: 50%'>
                                    Celular:
                                    <span>
                                        <g:icon type="2145"/>
                                        <input type='TEXT' name='user.cellPhone' 
                                               maxlength='24' tabindex='1' title='Entre com o seu n&uacute;mero de telefone celular.'/>
                                    </span>
                                </label>								
                            </fieldset>
                        </div>
                    </div>					
                    <div class='COOLBAR'>
                        <a href="Gate" tabindex='3' style='float: left; color: #660000'>
                            Desistir<g:icon type="cancel"/>
                        </a>			    	
                        <button formaction="User" tabindex='2' style='color: #006600'>
                            Concluir<g:icon type="commit"/>
                        </button>
                    </div>
                </div>
            </form>

            <g:if condition="${not empty messages}">
                <script type='text/javascript'>
                    <g:iterator source="${messages}" target="message">
                    alert('${message}');
                    </g:iterator>
                </script>
            </g:if>	

            <g:admin>
                <div class='TEXT'>
                    <g:exception/>
                </div>
            </g:admin>
        </div>
    </body>
</g:template>