<%@ taglib uri="http://www.gate.com.br/gate" prefix="G"%>

<G:template filename="/WEB-INF/views/FULL.jsp">
	<style>
		div.demo { display: flex;
			   flex-wrap: wrap }

		div.demo > div { width: 50%;
				 flex-grow: 1;
				 display: flex;
				 min-width: 300px;
				 flex-direction: column;
				 border: 8px solid transparent }

		div.demo > div > div { margin: 0;
				       padding: 0;
				       flex-grow: 1;
				       display: flex;
				       min-height: 100px;
				       align-items: center;}

		div.demo > div > div:last-child { background-color: #F5F2F0 }

		div.demo > div > div > * { margin: 0; flex-grow: 1; }
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
								<fieldset>
									<label>
										Date:
										<span>
											<input class="Date"/>
										</span>
									</label>
								</fieldset>
							</div>
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Date:
											<span>
												<input class="Date"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Time:
											<span>
												<input class="Time"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Month:
											<span>
												<input class="Month"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Date Time
											<span>
												<input class="DateTime"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Date Interval:
											<span>
												<input class="DateInterval"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Time Interval:
											<span>
												<input class="TimeInterval"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Month Interval:
											<span>
												<input class="MonthInterval"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>
						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Date Time Interval:
											<span>
												<input class="DateTimeInterval"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
						</div>

						<div>
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
							<div>
								<G:source-code language="html">
									<fieldset>
										<label>
											Icon:
											<span>
												<input class="Icon"/>
											</span>
										</label>
									</fieldset>
								</G:source-code>
							</div>
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
								<g-coolbar>
									<a href="#" data-tooltip='This is a tooltip'>
										Hover here<G:icon type="2015"/>
									</a>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<a href="#" data-tooltip='This is a tooltip'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>
						<div>
							<div>
								<g-coolbar>
									<a href="#" data-tooltip='{"Name": "Tooltip", "Type": "Attribute", "Description": "This is a tooltip"}'>
										Hover here<G:icon type="2015"/>
									</a>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<a href="#" data-tooltip='{"Name": "Tooltip",
										   "Type": "Attribute",
										   "Description": "This is a tooltip"}'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								</G:source-code>
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
								<g-coolbar>
									<a href="#"
									   style="color: #006600"
									   onclick="Message.success('Sucesso')">
										<G:icon type="1000"/>Sucesso
									</a>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<a href="#"
										   style="color: #006600"
										   onclick="Message.success('Sucesso')">
											<G:icon type="1000"/>Sucesso
										</a>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>
						<div>
							<div>
								<g-coolbar>
									<a href="#"
									   style="color: #444400"
									   onclick="Message.warning('Alerta')" >
										<G:icon type="1007"/>Alerta
									</a>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<a href="#"
										   style="color: #444400"
										   onclick="Message.warning('Alerta')" >
											<G:icon type="1007"/>Alerta
										</a>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>

						<div>
							<div>
								<g-coolbar>
									<a href="#"
									   style="color: #660000"
									   onclick="Message.error('Erro')">
										<G:icon type="1001"/>Erro
									</a>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<a href="#"
										   style="color: #660000"
										   onclick="Message.error('Erro')">
											<G:icon type="1001"/>Erro
										</a>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>
					</div>
				</div>
			</li>
			<li>
				<a href="#">
					Charts
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<g-coolbar>
									<a href='#'
									   data-chart='pchart'
									   title='Programadores por Linguagem'
									   data-series='[
									   ["Linguagem", "Programadores"],
									   ["Java",10000],
									   ["Javascript",8000],
									   ["Phyton",6000],
									   ["C++",6000],
									   ["C#",5000],
									   ["Ruby",2500],
									   ["Indefinida",80]]'>
										Gráfico<G:icon type="pchart"/>
									</a>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<a href='#'
										   data-chart='pchart'
										   title='Programadores por Linguagem'
										   data-series='[
										   ["Linguagem", "Programadores"],
										   ["Java",10000],
										   ["Javascript",8000],
										   ["Phyton",6000],
										   ["C++",6000],
										   ["C#",5000],
										   ["Ruby",2500],
										   ["Indefinida",80]]'>
											Gráfico<g:icon type="pchart"/>
										</a>
									</g-coolbar>
								</G:source-code>
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
								<g-coolbar>
									<G:link target="_report"
										module="#"
										screen="#"
										action="Report"/>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<g:link target="_report"
											module="#"
											screen="#"
											action="Report"/>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>
						<div>
							<div>
								<g-coolbar>
									<G:link target="_dialog"
										module="#"
										screen="#"
										action="Progress"/>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<g:link target="_dialog"
											module="#"
											screen="#"
											action="Progress"/>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>

						<div>
							<div>
								<g-coolbar>
									<G:link module="#"
										screen="#"
										action="Block"
										data-block="Aguarde"/>
								</g-coolbar>
							</div>
							<div>
								<G:source-code language="html">
									<g-coolbar>
										<g:link module="#"
											screen="#"
											action="Block"
											data-block="Aguarde"/>
									</g-coolbar>
								</G:source-code>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
</G:template>