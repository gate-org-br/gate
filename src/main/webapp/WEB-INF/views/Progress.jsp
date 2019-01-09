<%@ taglib uri="http://www.gate.com.br/gate" prefix="g"%>

<g:template filename="/WEB-INF/views/DIAG.jsp">
	<div style='padding: 12.5% 25% 12.5% 25%'>
		<div class="PageControl" data-type="Fetch">
			<ul>
				<li>
					<a href="#">
						Progresso<g:icon type="2206"/>
					</a>
					<div>
						<div id='Progress'>
							<label id='Title'>Conectando ao servidor</label>
							<progress id='ProgressBar'></progress>
							<div id="Status">
								<label id="Clock" data-clock="0">00:00:00</label>
								<label id='StatusBar'></label>
							</div>
						</div>
					</div>
				</li>
				<li>
					<a href="#">
						Detalhe<g:icon type="2189"/>
					</a>
					<div>
						<div style="height: 140px; overflow: auto">
							<table style="table-layout: fixed">
								<col/>
								<col style="width: 120px"/>
								<tbody id="Log"></tbody>
							</table>
						</div>
					</div>
				</li>
			</ul>
		</div>

		<div class='COOLBAR'>
			<a id='Action' href='#' data-alert='Aguarde'>
				Processando<g:icon type="2017"/>
			</a>
		</div>
	</div>

	<script>
		var colors = new Object();
		colors["PENDING"] = '#000000';
		colors["COMMITED"] = '#006600';
		colors["CANCELED"] = '#660000';

		document.getElementById("Title").onProgressEvent = function (event)
		{
			this.innerHTML = event.text;
			this.style.color = colors[event.status];
		};

		document.getElementById("StatusBar").onProgressEvent = function (event)
		{
			this.innerHTML = event.toString();
			this.style.color = colors[event.status];
		};

		document.getElementById("Action").onProgressEvent = function (event)
		{
			this.style.color = colors[event.status];
			if (event.status === "COMMITED"
				|| event.status === "CANCELED")
			{
				this.onclick = function ()
				{
					if (window.frameElement
						&& window.frameElement.dialog
						&& window.frameElement.dialog.hide)
						window.frameElement.dialog.hide();
					else
						window.close();
				};

				this.removeAttribute("data-alert");
				if (event.status === "COMMITED")
					this.innerHTML = "Ok<i>&#X1000;</i>";
				else
					this.innerHTML = "Ok<i>&#X1001;</i>";
			}
		};

		document.getElementById("ProgressBar").onProgressEvent = function (event)
		{
			switch (event.status)
			{
				case "COMMITED":
					if (!this.max)
						this.max = 100;
					if (!this.value)
						this.value = 100;
					break;
				case "CANCELED":
					if (!this.max)
						this.max = 100;
					if (!this.value)
						this.value = 0;
					break;
			}

			if (event.todo !== -1)
			{
				this.max = event.todo;
				if (event.done !== -1)
					this.value = event.done;
			}
		};

		document.getElementById("Clock").onProgressEvent = function (event)
		{
			this.style.color = colors[event.status];
			if (event.status === "COMMITED"
				|| event.status === "CANCELED")
				this.removeAttribute("data-clock");
		};

		document.getElementById("Log").onProgressEvent = function (event)
		{
			var tr = this.insertBefore(document.createElement("tr"), this.firstChild);
			var td = tr.appendChild(document.createElement("td"))
			td.style.color = colors[event.status];
			td.innerHTML = event.text;

			var td = tr.appendChild(document.createElement("td"))
			td.style.textAlign = "center";
			td.style.color = colors[event.status];
			td.innerHTML = event.toString();
		};

		new Progress(${process});
	</script>

	<style>
		#Progress
		{
			width: 100%;
			height: 140px;
			display: flex;
			padding: 10px;
			border-radius: 5px;
			align-items: center;
			flex-direction: column;
			justify-content: center;
			background-image: linear-gradient(to bottom, #FDFAE9 0%, #B3B0A4 100%);
		}

		#Status
		{
			width: 100%;
			padding: 2px;
			display: flex;
			margin-top: 10px;
			justify-content: space-between
		}

		#ProgressBar
		{
			width: 100%;
			height: 100px;
			margin-top: 10px;
		}


		#Title
		{
			width: 100%;
			color: black;
			height: 50px;
			font-size: 20px;
			margin-top: 10px;
		}

		#Clock
		{
			color: black;
			flex-basis: 50%;
			font-size: 12px;
			text-align: left;
		}

		#StatusBar
		{
			color: black;
			flex-basis: 50%;
			font-size: 12px;
			text-align: right;
		}
	</style>
</g:template>