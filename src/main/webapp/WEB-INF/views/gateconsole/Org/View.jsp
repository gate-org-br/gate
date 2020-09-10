<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<article>
		<section>
			<fieldset style="height: 100%">
				<legend>
					<g:path/>
				</legend>
				<label data-size="8">
					Sigla:
					<span>
						<g:print value='${screen.form.orgID}'/>
					</span>
				</label>
				<label data-size="8">
					Nome:
					<span>
						<g:print value='${screen.form.name}'/>
					</span>
				</label>
				<label style="height: calc(50% - 80px)">
					Descri��o:
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
					<label data-size="2">
						Seg:
						<span>
							<g:print value="${screen.form.mon}" empty="N/A"/>
						</span>
					</label>
					<label data-size="2">
						Ter:
						<span>
							<g:print value="${screen.form.tue}" empty="N/A"/>
						</span>
					</label>
					<label data-size="2">
						Qua:
						<span>
							<g:print value="${screen.form.wed}" empty="N/A"/>
						</span>
					</label>
					<label data-size="2">
						Qui:
						<span>
							<g:print value="${screen.form.thu}" empty="N/A"/>
						</span>
					</label>
					<label data-size="2">
						Sex:
						<span>
							<g:print value="${screen.form.fri}" empty="N/A"/>
						</span>
					</label>
					<label data-size="2">
					</label>
					<label data-size="2">
						Sab:
						<span>
							<g:print value="${screen.form.sat}" empty="N/A"/>
						</span>
					</label>
					<label data-size="2">
						Dom:
						<span>
							<g:print value="${screen.form.sun}" empty="N/A"/>
						</span>
					</label>
				</fieldset>
			</fieldset>
		</section>

		<footer>
			<g-coolbar>
				<g:link module="#" screen="#" action="Update"/>
			</g-coolbar>
		</footer>
	</article>
</g:template>