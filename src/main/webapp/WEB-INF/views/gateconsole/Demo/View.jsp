<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<style>
		div.demo { display: flex; flex-wrap: wrap }
		div.demo > div { display: flex; flex-direction: column; margin: 4px; width: calc(25% - 8px); border-left: 4px solid #006600; min-width: 300px; flex-grow: 1; }
		div.demo > div > div { display: flex; align-items: center; padding: 8px; flex-grow: 1; min-height: 100px }
		div.demo > div > div > * { flex-grow: 1; margin: 0 }
		div.demo > div > div:first-child { background-color: #FFFFFF; }
		div.demo > div > div:last-child { background-color: #EFEFEF; }
	</style>

	<div class="PageControl">
		<ul>
			<li>
				<a href="#">
					Pickers
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Date:
		&lt;span>
			&lt;input class="Date"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Date:
										<span>
											<input class="Date"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Time:
		&lt;span>
			&lt;input class="Time"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Time:
										<span>
											<input class="Time"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Month:
		&lt;span>
			&lt;input class="Month"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Month:
										<span>
											<input class="Month"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Date Time:
		&lt;span>
			&lt;input class="DateTime"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Date Time
										<span>
											<input class="DateTime"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Date Interval:
		&lt;span>
			&lt;input class="DateInterval"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Date Interval:
										<span>
											<input class="DateInterval"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Time Interval:
		&lt;span>
			&lt;input class="TimeInterval"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Time Interval:
										<span>
											<input class="TimeInterval"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Month Interval:
		&lt;span>
			&lt;input class="MonthInterval"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Month Interval:
										<span>
											<input class="MonthInterval"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Date Time Interval:
		&lt;span>
			&lt;input class="DateTimeInterval"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Date Time Interval:
										<span>
											<input class="DateTimeInterval"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>

						<div>
							<div>
								<pre>&lt;fieldset>
	&lt;label>
		Icon:
		&lt;span>
			&lt;input class="Icon"/>
		&lt;/span>
	&lt;/lael>
&lt;/fieldset></pre>
							</div>
							<div>
								<fieldset>
									<label>
										Icon:
										<span>
											<input class="Icon"/>
										</span>
									</label>
								</fieldset>
							</div>
						</div>
					</div>
			</li>
			<li>
				<a href="#">
					Tooltip
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<pre>&lt;g-coolbar>
	&lt;a href='#' data-tooltip='This is a tooltip'>
		Hover here&lt;i>&amp;#X2015;&lt;/i>
	&lt;/a>
&lt/g-coolbar></pre>
							</div>
							<div>
								<g-coolbar>
									<a href="#" data-tooltip='This is a tooltip'>
										Hover here<g:icon type="2015"/>
									</a>
								</g-coolbar>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;g-coolbar>
	&lt;a href='#' data-tooltip='{"Name": "Tooltip",
	"Type": "Attribute",
	"Description": "This is a tooltip"}'>
		Hover here&lt;i>&amp;#X2015;&lt;/i>
	&lt;/a>
&lt/g-coolbar></pre>
							</div>
							<div>
								<g-coolbar>
									<a href="#" data-tooltip='{"Name": "Tooltip", "Type": "Attribute",
									   "Description": "This is a tooltip"}'>
										Hover here<g:icon type="2015"/>
									</a>
								</g-coolbar>
							</div>
						</div>
					</div>
				</div>
			</li>
			<li>
				<a href="#">
					Mensagens
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<pre>&lt;g-coolbar>
	&lt;a href="#"
		onclick="Message.success('Sucesso')"
		style="color: #006600">
		&lt;i>&amp;#X1000;&lt;/i> Sucesso
	&lt;/a>
&lt;/g-coolbar></pre>
							</div>
							<div>
								<g-coolbar>
									<a href="#" onclick="Message.success('Sucesso')" style="color: #006600">
										<g:icon type="1000"/>Sucesso
									</a>
								</g-coolbar>
							</div>
						</div>
						<div>
							<div>
								<pre>&lt;g-coolbar>
	&lt;a href="#"
		onclick="Message.warning('Alerta')"
		style="color: #444400">
		&lt;i>&amp;#X1007;&lt;/i> Alerta
	&lt;/a>
&lt;/g-coolbar></pre>
							</div>
							<div>
								<g-coolbar>
									<a href="#" onclick="Message.warning('Alerta')" style="color: #444400">
										<g:icon type="1007"/>Alerta
									</a>
								</g-coolbar>
							</div>
						</div>

						<div>
							<div>
								<pre>&lt;g-coolbar>
	&lt;a href="#"
		onclick="Message.error('Erro')"
		style="color: #660000">
		&lt;i>&amp;#X1001;&lt;/i> Erro
	&lt;/a>
&lt;/g-coolbar></pre>
							</div>
							<div>
								<g-coolbar>
									<a href="#" onclick="Message.error('Erro')" style="color: #660000">
										<g:icon type="1001"/>Erro
									</a>
								</g-coolbar>
							</div>
						</div>
					</div>
				</div>
			</li>
			<li>
				<a href="#">
					Outros
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<pre>&lt;g-coolbar>
	&lt;a href="#" target='_report'>
		&lt;i>&amp;#X2021;&lt;/i> Imprimir
	&lt;/a>
&lt;/g-coolbar></pre>
							</div>
							<div>
								<g-coolbar>
									<a href="#" target='_report'>
										<g:icon type="report"/>Imprimir
									</a>
								</g-coolbar>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>


</g:template>