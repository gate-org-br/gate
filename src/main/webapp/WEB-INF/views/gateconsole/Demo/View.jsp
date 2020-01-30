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

		div.demo > div > div:first-child { flex-basis: 0 }
		div.demo > div > div:not(:first-child){ background-color: #F5F2F0 }

		div.demo > div > div > * { margin: 0; flex-grow: 1; }
	</style>

	<div class="PageControl">
		<ul>
			<li>
				<a href="#">
					Tab Bar
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<g-scroll-tabbar>
									<a href="#">Link de texto 01</a>
									<a href="#">Link de texto 02</a>
									<a href="#">Link de texto 03</a>
									<a href="#">Link de texto 04</a>
									<a href="#">Link de texto 05</a>
									<a href="#">Link de texto 06</a>
									<a href="#">Link de texto 07</a>
									<a href="#">Link de texto 08</a>
									<a href="#">Link de texto 09</a>
									<a href="#">Link de texto 11</a>
									<a href="#">Link de texto 12</a>
									<a href="#" aria-selected="true">Link de texto 13</a>
									<a href="#">Link de texto 14</a>
									<a href="#">Link de texto 15</a>
									<a href="#">Link de texto 16</a>
									<a href="#">Link de texto 17</a>
									<a href="#">Link de texto 18</a>
									<a href="#">Link de texto 19</a>
								</g-scroll-tabbar>
							</div>
							<div>
								<pre class="language-markup"><code><!--
									<g-scroll-tabbar>
										<a href="#">Link de texto 01</a>
										<a href="#">Link de texto 02</a>
										<a href="#">Link de texto 03</a>
										<a href="#">Link de texto 04</a>
										<a href="#">Link de texto 05</a>
										<a href="#">Link de texto 06</a>
										<a href="#">Link de texto 07</a>
										<a href="#">Link de texto 08</a>
										<a href="#">Link de texto 09</a>
										<a href="#">Link de texto 11</a>
										<a href="#">Link de texto 12</a>
										<a href="#" aria-selected="true">
											Link de texto 13
										</a>
										<a href="#">Link de texto 14</a>
										<a href="#">Link de texto 15</a>
										<a href="#">Link de texto 16</a>
										<a href="#">Link de texto 17</a>
										<a href="#">Link de texto 18</a>
										<a href="#">Link de texto 19</a>
									</g-scroll-tabbar>
								--></code></pre>
							</div>
						</div>
						<div>
							<div>
								<g-tabbar>
									<a href="#">Link de texto 01</a>
									<a href="#">Link de texto 02</a>
									<a href="#">Link de texto 03</a>
									<a href="#">Link de texto 04</a>
									<a href="#">Link de texto 05</a>
									<a href="#">Link de texto 06</a>
									<a href="#">Link de texto 07</a>
									<a href="#">Link de texto 08</a>
									<a href="#">Link de texto 09</a>
									<a href="#">Link de texto 11</a>
									<a href="#">Link de texto 12</a>
									<a href="#">Link de texto 13</a>
									<a href="#">Link de texto 14</a>
									<a href="#">Link de texto 15</a>
									<a href="#">Link de texto 16</a>
									<a href="#">Link de texto 17</a>
									<a href="#">Link de texto 18</a>
									<a href="#">Link de texto 19</a>
								</g-tabbar>
							</div>
							<div>
								<pre class="language-markup"><code><!--
									<g-tabbar>
										<a href="#">Link de texto 01</a>
										<a href="#">Link de texto 02</a>
										<a href="#">Link de texto 03</a>
										<a href="#">Link de texto 04</a>
										<a href="#">Link de texto 05</a>
										<a href="#">Link de texto 06</a>
										<a href="#">Link de texto 07</a>
										<a href="#">Link de texto 08</a>
										<a href="#">Link de texto 09</a>
										<a href="#">Link de texto 11</a>
										<a href="#">Link de texto 12</a>
										<a href="#">Link de texto 13</a>
										<a href="#">Link de texto 14</a>
										<a href="#">Link de texto 15</a>
										<a href="#">Link de texto 16</a>
										<a href="#">Link de texto 17</a>
										<a href="#">Link de texto 18</a>
										<a href="#">Link de texto 19</a>
									</g-tabbar>
								--></code></pre>
							</div>
						</div>
						<div>
							<div>
								<g-scroll-tabbar>
									<a href="#">Link 01<i>&#X2003;</i></a>
									<a href="#">Link 02<i>&#X2004;</i></a>
									<a href="#">Link 03<i>&#X2005;</i></a>
									<a href="#">Link 04<i>&#X2006;</i></a>
									<a href="#">Link 05<i>&#X2007;</i></a>
									<a href="#">Link 06<i>&#X2008;</i></a>
									<a href="#">Link 07<i>&#X2009;</i></a>
									<a href="#">Link 08<i>&#X2010;</i></a>
									<a href="#">Link 09<i>&#X2011;</i></a>
									<a href="#">Link 11<i>&#X2012;</i></a>
									<a href="#">Link 12<i>&#X2013;</i></a>
									<a href="#" aria-selected="true">Link 13<i>&#X2014;</i></a>
									<a href="#">Link 14<i>&#X2015;</i></a>
									<a href="#">Link 15<i>&#X2016;</i></a>
									<a href="#">Link 16<i>&#X2017;</i></a>
									<a href="#">Link 17<i>&#X2018;</i></a>
									<a href="#">Link 18<i>&#X2019;</i></a>
									<a href="#">Link 19<i>&#X2020;</i></a>
								</g-scroll-tabbar>
							</div>
							<div>
								<pre class="language-markup"><code><!--
									<g-scroll-tabbar>
										<a href="#">Link 01<i>&#X2003;</i></a>
										<a href="#">Link 02<i>&#X2004;</i></a>
										<a href="#">Link 03<i>&#X2005;</i></a>
										<a href="#">Link 04<i>&#X2006;</i></a>
										<a href="#">Link 05<i>&#X2007;</i></a>
										<a href="#">Link 06<i>&#X2008;</i></a>
										<a href="#">Link 07<i>&#X2009;</i></a>
										<a href="#">Link 08<i>&#X2010;</i></a>
										<a href="#">Link 09<i>&#X2011;</i></a>
										<a href="#">Link 11<i>&#X2012;</i></a>
										<a href="#">Link 12<i>&#X2013;</i></a>
										<a href="#" aria-selected="true">
											Link 13<i>&#X2014;</i>
										</a>
										<a href="#">Link 14<i>&#X2015;</i></a>
										<a href="#">Link 15<i>&#X2016;</i></a>
										<a href="#">Link 16<i>&#X2017;</i></a>
										<a href="#">Link 17<i>&#X2018;</i></a>
										<a href="#">Link 18<i>&#X2019;</i></a>
										<a href="#">Link 19<i>&#X2020;</i></a>
									</g-scroll-tabbar>
								--></code></pre>
							</div>
						</div>
						<div>
							<div>
								<g-tabbar>
									<a href="#">Link 01<i>&#X2003;</i></a>
									<a href="#">Link 02<i>&#X2004;</i></a>
									<a href="#">Link 03<i>&#X2005;</i></a>
									<a href="#">Link 04<i>&#X2006;</i></a>
									<a href="#">Link 05<i>&#X2007;</i></a>
									<a href="#">Link 06<i>&#X2008;</i></a>
									<a href="#">Link 07<i>&#X2009;</i></a>
									<a href="#">Link 08<i>&#X2010;</i></a>
									<a href="#">Link 09<i>&#X2011;</i></a>
									<a href="#">Link 11<i>&#X2012;</i></a>
									<a href="#">Link 12<i>&#X2013;</i></a>
									<a href="#">Link 13<i>&#X2014;</i></a>
									<a href="#">Link 14<i>&#X2015;</i></a>
									<a href="#">Link 15<i>&#X2016;</i></a>
									<a href="#">Link 16<i>&#X2017;</i></a>
									<a href="#">Link 17<i>&#X2018;</i></a>
									<a href="#">Link 18<i>&#X2019;</i></a>
									<a href="#">Link 19<i>&#X2020;</i></a>
								</g-tabbar>
							</div>
							<div>
								<pre class="language-markup"><code><!--
										<g-tabbar>
											<a href="#">Link 01<i>&#X2003;</i></a>
											<a href="#">Link 02<i>&#X2004;</i></a>
											<a href="#">Link 03<i>&#X2005;</i></a>
											<a href="#">Link 04<i>&#X2006;</i></a>
											<a href="#">Link 05<i>&#X2007;</i></a>
											<a href="#">Link 06<i>&#X2008;</i></a>
											<a href="#">Link 07<i>&#X2009;</i></a>
											<a href="#">Link 08<i>&#X2010;</i></a>
											<a href="#">Link 09<i>&#X2011;</i></a>
											<a href="#">Link 11<i>&#X2012;</i></a>
											<a href="#">Link 12<i>&#X2013;</i></a>
											<a href="#">Link 13<i>&#X2014;</i></a>
											<a href="#">Link 14<i>&#X2015;</i></a>
											<a href="#">Link 15<i>&#X2016;</i></a>
											<a href="#">Link 16<i>&#X2017;</i></a>
											<a href="#">Link 17<i>&#X2018;</i></a>
											<a href="#">Link 18<i>&#X2019;</i></a>
											<a href="#">Link 19<i>&#X2020;</i></a>
										</g-tabbar>
								--></code></pre>
							</div>
						</div>
					</div>
				</div>
			</li>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Date:
											<span>
												<input class="Date"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Time:
											<span>
												<input class="Time"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Month:
											<span>
												<input class="Month"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Date Time
											<span>
												<input class="DateTime"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Date Interval:
											<span>
												<input class="DateInterval"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Time Interval:
											<span>
												<input class="TimeInterval"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Month Interval:
											<span>
												<input class="MonthInterval"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Date Time Interval:
											<span>
												<input class="DateTimeInterval"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<fieldset>
										<label>
											Icon:
											<span>
												<input class="Icon"/>
											</span>
										</label>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#" data-tooltip='This is a tooltip'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#" data-tooltip='{"Name": "Tooltip",
										   "Type": "Attribute",
										   "Description": "This is a tooltip"}'>
											Hover here<G:icon type="2015"/>
										</a>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #006600"
										   onclick="Message.success('Sucesso')">
										<G:icon type="1000"/>Sucesso
										</a>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #444400"
										   onclick="Message.warning('Alerta')" >
										<G:icon type="1007"/>Alerta
										</a>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<a href="#"
										   style="color: #660000"
										   onclick="Message.error('Erro')">
										<G:icon type="1001"/>Erro
										</a>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
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
								--></code></pre>
							</div>
						</div>
					</div>
				</div>
			</li>

			<li>
				<a href="#">
					Context Menu
				</a>
				<div>
					<div class='demo'>
						<div>
							<div>
								<g-context-menu id="contextmenu">
									<G:link module="#" screen="Icon"/>
									<button id="action1" href="#">
										Action 1<i>&#X2003;</i>
									</button>
								</g-context-menu>

								<g-coolbar>
									<a href="#" data-context-menu="contextmenu">
										Botão direito
									</a>
								</g-coolbar>

								<script>
									window.addEventListener("load", function ()
									{
										document.getElementById("action1")
											.addEventListener("click",
												e => alert(e.target.parentNode.context.innerText));

										let command = new Command();
										command.innerHTML = "Action 2<i>&#X2003;</i>";
										command.action = e => alert(e.parentNode.context.innerText);
										document.getElementById("contextmenu").appendChild(command);

									});
								</script>
							</div>
							<div>
								<pre class="language-markup"><code><!--
									<g-context-menu id="contextmenu">
										<g:link module="#" screen="Icon"/>
										<button id="action1" href="#">
											Action 1<i>&#X2003;</i>
										</button>
									</g-context-menu>

									<g-coolbar>
										<a href="#" data-context-menu="contextmenu">
											Botão direito
										</a>
									</g-coolbar>
								--></code></pre>
							</div>
							<div>
								<pre class="language-javascript"><code><!--
									window.addEventListener("load", function ()
									{
										document.getElementById("action1")
										.addEventListener("click",
											e => alert(e.target.parentNode
											.context.innerText));

										let command = new Command();
										command.innerHTML =
											"Action 2<i>&#X2003;</i>";
										command.action = e =>
										alert(e.parentNode.context.innerText);
										document.getElementById("contextmenu")
											.appendChild(command);
									});
								--></code></pre>
							</div>
						</div>
					</div>
				</div>
			</li>
			<li>
				<a href="#">
					Select
				</a>
				<div>
					<div class='demo'>
						<div>
							<div style="flex-basis: 220px">
								<fieldset>
									<G:selectn property="types"
										   options="${screen.getOptions()}"/>
								</fieldset>
							</div>
							<div>
								<pre class="language-markup"><code><!--
									<fieldset>
										<g:selectn property="types"
											   options="${screen.getOptions()}"/>
									</fieldset>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link target="_report"
											module="#"
											screen="#"
											action="Report"/>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link target="_dialog"
											module="#"
											screen="#"
											action="Progress"/>
									</g-coolbar>
								--></code></pre>
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
								<pre class="language-markup"><code><!--
									<g-coolbar>
										<g:link module="#"
											screen="#"
											action="Block"
											data-block="Aguarde"/>
									</g-coolbar>
								--></code></pre>
							</div>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
</G:template>