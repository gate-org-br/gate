<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/FULL.jsp">
	<style>
		div.demo { display: flex; flex-wrap: wrap }
		div.demo > div { display: flex; flex-direction: column; margin: 4px; width: calc(50% - 8px); border-left: 4px solid #006600; min-width: 300px; }
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
								<img src="imge/demo/Date.png" style="width: 100%"/>
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
								<img src="imge/demo/Time.png" style="width: 100%"/>
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
								<img src="imge/demo/Month.png" style="width: 100%"/>
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
								<img src="imge/demo/DateTime.png" style="width: 100%"/>
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
								<img src="imge/demo/DateInterval.png" style="width: 100%"/>
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
								<img src="imge/demo/TimeInterval.png" style="width: 100%"/>
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
								<img src="imge/demo/MonthInterval.png" style="width: 100%"/>
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
								<img src="imge/demo/DateTimeInterval.png" style="width: 100%"/>
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
								<img src="imge/demo/Icon.png" style="width: 100%"/>
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
								<img src="imge/demo/SimpleTooltip.png" style="width: 100%"/>
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
								<img src="imge/demo/ComplexTooltip.png" style="width: 100%"/>
							</div>
							<div>
								<g-coolbar>
									<a href="#" data-tooltip='{"Name": "Tooltip", "Type": "Attribute", "Description": "This is a tooltip"}'>
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
								<img src="imge/demo/Success.png" style="width: 100%"/>
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
								<img src="imge/demo/Warning.png" style="width: 100%"/>
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
								<img src="imge/demo/Error.png" style="width: 100%"/>
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
								<img src="imge/demo/Report.png" style="width: 100%"/>
							</div>
							<div>
								<g-coolbar>
									<g:link target="_report"
										module="#"
										screen="#"
										action="Report"/>
								</g-coolbar>
							</div>
						</div>
						<div>
							<div>
								<img src="imge/demo/Progress.png" style="width: 100%"/>
							</div>
							<div>
								<g-coolbar>
									<g:link target="_dialog"
										module="#"
										screen="#"
										action="Progress"/>
								</g-coolbar>
							</div>
						</div>

						<div>
							<div>
								<img src="imge/demo/Block.png" style="width: 100%"/>
							</div>
							<div>
								<g-coolbar>
									<g:link module="#"
										screen="#"
										action="Block"
										data-block="Aguarde"/>
								</g-coolbar>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
</g:template>