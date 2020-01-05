module.exports = function (grunt)
{
	grunt.initConfig({
		watch: {
			views: {
				files: ['src/main/webapp/components/**/*.js'],
				tasks: ['concat']
			}
		},
		concat:
			{
				dist:
					{
						src: ["src/main/webapp/polyfill/array-polyfill.js",
							"src/main/webapp/polyfill/dialog-polyfill.js",
							"src/main/webapp/polyfill/fetch-polyfill.js",
							"src/main/webapp/polyfill/webcomponents-ce.js",

							"src/main/webapp/components/Proxy.js",

							"src/main/webapp/components/Command.js",
							"src/main/webapp/components/Commands.js",

							"src/main/webapp/components/DigitalClock.js",
							"src/main/webapp/components/Gate.js",
							"src/main/webapp/components/Objects.js",
							"src/main/webapp/components/Colorizer.js",
							"src/main/webapp/components/DataFormat.js",
							"src/main/webapp/components/DeskMenu.js",
							"src/main/webapp/components/DeskPane.js",
							"src/main/webapp/components/Mask.js",

							"src/main/webapp/components/Modal.js",
							"src/main/webapp/components/Window.js",

							"src/main/webapp/components/Block.js",
							"src/main/webapp/components/CSV.js",
							"src/main/webapp/components/Populator.js",
							"src/main/webapp/components/Duration.js",
							"src/main/webapp/components/Datalist.js",
							"src/main/webapp/components/Keys.js",
							"src/main/webapp/components/URL.js",
							"src/main/webapp/components/Link.js",
							"src/main/webapp/components/Button.js",

							"src/main/webapp/components/SideMenu.js",

							"src/main/webapp/components/Clipboard.js",
							"src/main/webapp/components/ContextMenuItem.js",
							"src/main/webapp/components/ContextMenu.js",
							"src/main/webapp/components/ActionContextMenu.js",

							"src/main/webapp/components/ActionHandler.js",
							"src/main/webapp/components/ChangeHandler.js",
							"src/main/webapp/components/NavBar.js",
							"src/main/webapp/components/Slider.js",
							"src/main/webapp/components/Calendar.js",

							"src/main/webapp/components/selector/DateSelector.js",
							"src/main/webapp/components/selector/TimeSelector.js",
							"src/main/webapp/components/selector/MonthSelector.js",
							"src/main/webapp/components/selector/DateTimeSelector.js",
							"src/main/webapp/components/selector/DateIntervalSelector.js",
							"src/main/webapp/components/selector/TimeIntervalSelector.js",
							"src/main/webapp/components/selector/MonthIntervalSelector.js",
							"src/main/webapp/components/selector/DateTimeIntervalSelector.js",
							"src/main/webapp/components/selector/IconSelector.js",
							"src/main/webapp/components/selector/ReportSelector.js",

							"src/main/webapp/components/picker/Picker.js",
							"src/main/webapp/components/picker/DatePicker.js",
							"src/main/webapp/components/picker/TimePicker.js",
							"src/main/webapp/components/picker/MonthPicker.js",
							"src/main/webapp/components/picker/DateTimePicker.js",
							"src/main/webapp/components/picker/DateIntervalPicker.js",
							"src/main/webapp/components/picker/TimeIntervalPicker.js",
							"src/main/webapp/components/picker/MonthIntervalPicker.js",
							"src/main/webapp/components/picker/DateTimeIntervalPicker.js",
							"src/main/webapp/components/picker/IconPicker.js",

							"src/main/webapp/components/Chart.js",
							"src/main/webapp/components/ChartDialog.js",
							"src/main/webapp/components/PageControl.js",
							"src/main/webapp/components/LinkControl.js",
							"src/main/webapp/components/Dialog.js",
							"src/main/webapp/components/Popup.js",
							"src/main/webapp/components/TreeView.js",
							"src/main/webapp/components/DateFormat.js",
							"src/main/webapp/components/Switch.js",
							"src/main/webapp/components/FullScreen.js",
							"src/main/webapp/components/Form.js",
							"src/main/webapp/components/Focus.js",
							"src/main/webapp/components/Sortable.js",
							"src/main/webapp/components/Message.js",
							"src/main/webapp/components/Return.js",
							"src/main/webapp/components/Copy.js",
							"src/main/webapp/components/ProgressStatus.js",
							"src/main/webapp/components/ProgressDialog.js",
							"src/main/webapp/components/ProgressWindow.js",
							"src/main/webapp/components/DownloadStatus.js",
							"src/main/webapp/components/ReportDialog.js",
							"src/main/webapp/components/TableFilter.js",
							"src/main/webapp/components/Coolbar.js",
							"src/main/webapp/components/Overflow.js",
							"src/main/webapp/components/AutoClick.js",
							"src/main/webapp/components/DefinitionList.js",
							"src/main/webapp/components/AppEvents.js",
							"src/main/webapp/components/Tooltip.js",
							"src/main/webapp/components/Checkable.js",

							"src/main/webapp/components/TabBar.js"
						],
						dest: 'src/main/resources/gate/Gate.js'
					}
			}

	});
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.registerTask('default', ['watch']);
};