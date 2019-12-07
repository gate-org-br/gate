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
							"src/main/webapp/components/DigitalClock.js",
							"src/main/webapp/components/Gate.js",
							"src/main/webapp/components/Objects.js",
							"src/main/webapp/components/Colorizer.js",
							"src/main/webapp/components/DataFormat.js",
							"src/main/webapp/components/DeskMenu.js",
							"src/main/webapp/components/DeskPane.js",
							"src/main/webapp/components/Mask.js",
							"src/main/webapp/components/Modal.js",
							"src/main/webapp/components/Block.js",
							"src/main/webapp/components/CSV.js",
							"src/main/webapp/components/Populator.js",
							"src/main/webapp/components/Duration.js",
							"src/main/webapp/components/Datalist.js",
							"src/main/webapp/components/Keys.js",
							"src/main/webapp/components/URL.js",
							"src/main/webapp/components/Link.js",
							"src/main/webapp/components/Button.js",

							"src/main/webapp/components/Clipboard.js",
							"src/main/webapp/components/ContextMenu.js",
							"src/main/webapp/components/ContextMenuItem.js",
							"src/main/webapp/components/ActionContextMenu.js",

							"src/main/webapp/components/ActionHandler.js",
							"src/main/webapp/components/ChangeHandler.js",
							"src/main/webapp/components/NavBar.js",
							"src/main/webapp/components/Slider.js",
							"src/main/webapp/components/Calendar.js",
							"src/main/webapp/components/DateSelector.js",
							"src/main/webapp/components/TimeSelector.js",
							"src/main/webapp/components/MonthSelector.js",
							"src/main/webapp/components/DateTimeSelector.js",
							"src/main/webapp/components/DateIntervalSelector.js",
							"src/main/webapp/components/TimeIntervalSelector.js",
							"src/main/webapp/components/MonthIntervalSelector.js",
							"src/main/webapp/components/DateTimeIntervalSelector.js",
							"src/main/webapp/components/Picker.js",
							"src/main/webapp/components/DatePicker.js",
							"src/main/webapp/components/TimePicker.js",
							"src/main/webapp/components/MonthPicker.js",
							"src/main/webapp/components/DateTimePicker.js",
							"src/main/webapp/components/DateIntervalPicker.js",
							"src/main/webapp/components/TimeIntervalPicker.js",
							"src/main/webapp/components/MonthIntervalPicker.js",
							"src/main/webapp/components/DateTimeIntervalPicker.js",
							"src/main/webapp/components/IconPicker.js",
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
							"src/main/webapp/components/ReportSelector.js",
							"src/main/webapp/components/ReportDialog.js",
							"src/main/webapp/components/TableFilter.js",
							"src/main/webapp/components/Coolbar.js",
							"src/main/webapp/components/Overflow.js",
							"src/main/webapp/components/AutoClick.js",
							"src/main/webapp/components/DefinitionList.js",
							"src/main/webapp/components/AppEvents.js",
						],
						dest: 'src/main/resources/gate/Gate.js'
					}
			}

	});
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.registerTask('default', ['watch']);
};