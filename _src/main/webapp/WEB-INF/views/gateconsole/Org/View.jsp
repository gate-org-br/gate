<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
    <g:choose>
        <fieldset>
            <legend>
                <g:icon type="gate.entity.Org"/>Organização
            </legend>

            <label style='width: 25%'>
                Sigla:
                <span>
                    <g:print value='${screen.form.orgID}'/>
                </span>
            </label>
            <label style='width: 75%'>
                Nome:
                <span>
                    <g:print value='${screen.form.name}'/>
                </span>
            </label>
            <label style='width: 100%'>
                Descri&ccedil;&atilde;o:
                <span style='height: 80px'>
                    <g:print value='${screen.form.description}'/>
                </span>
            </label>
            <label style='width: 100%'>
                Autenticadores:
                <span style='height: 40px'>
                    <g:print value='${screen.form.authenticators}'/>
                </span>
            </label>				

            <fieldset style='width: 100%'>
                <legend>
                    <g:icon type="2003"/>Expediente
                </legend>
                <label style='width: 15%'>
                    Dom:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.sun}" empty="N/A"/>
                    </span>
                </label>
                <label style='width: 14%'>
                    Seg:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.mon}" empty="N/A"/>
                    </span>
                </label>
                <label style='width: 14%'>
                    Ter:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.tue}" empty="N/A"/>
                    </span>
                </label>
                <label style='width: 14%'>
                    Qua:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.wed}" empty="N/A"/>
                    </span>
                </label>
                <label style='width: 14%'>
                    Qui:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.thu}" empty="N/A"/>
                    </span>
                </label>
                <label style='width: 14%'>
                    Sex:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.fri}" empty="N/A"/>
                    </span>
                </label>
                <label style='width: 15%'>
                    Sab:
                    <span style='text-align: center'>
                        <g:print value="${screen.form.sat}" empty="N/A"/>
                    </span>
                </label>
            </fieldset>

            <g:if condition="${not empty screen.form.orgID}">
                <fieldset style='width: 100%'>
                    <div class='TEXT' style='width: 100%; text-align: center'>
                        <img src='${screen.form.icon.toString()}' style='height: 60px'/>
                    </div>
                </fieldset>
            </g:if>
        </fieldset>

        <div class='COOLBAR'>
            <g:link module="#" screen="#" action="Update">
                Alterar<g:icon type="update"/>
            </g:link>
        </div>
    </g:choose>
</g:template>