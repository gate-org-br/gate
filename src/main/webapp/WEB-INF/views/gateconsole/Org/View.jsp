<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<article>
		<section>
			<g:choose>
				<fieldset style="height: 100%">
					<label data-size="4">
						Sigla:
						<span>
							<g:print value='${screen.form.orgID}'/>
						</span>
					</label>
					<label data-size="4">
						Nome:
						<span>
							<g:print value='${screen.form.name}'/>
						</span>
					</label>
					<label data-size="8">
						Logo:
						<span>
							<label>
								Baixar
							</label>
							<g:link type="icon"
								module="#" screen="#" action="Dwload"/>
						</span>
					</label>
					<label style="height: calc(50% - 80px)">
						Descrição:
						<span style='flex-grow: 1'>
							<g:print value='${screen.form.description}'/>
						</span>
					</label>
					<label style="height: calc(50% - 80px)">
						Autenticadores:
						<span style='flex-basis: 80px'>
							<g:print value='${screen.form.authenticators}'/>
						</span>
					</label>

					<fieldset>
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
				</fieldset>
			</g:choose>
		</section>

		<nav>
			<g-coolbar>
				<g:link module="#" screen="#" action="Update"/>
			</g-coolbar>
		</nav>
	</article>
</g:template>