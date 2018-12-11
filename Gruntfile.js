module.exports = function (grunt) {
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
							"src/main/webapp/components/Gate.js",
							"src/main/webapp/components/CSV.js",
							"src/main/webapp/components/Populator.js",
							"src/main/webapp/components/Duration.js",
							"src/main/webapp/components/Datalist.js",
							"src/main/webapp/components/Keys.js",
							"src/main/webapp/components/URL.js",
							"src/main/webapp/components/Calendar.js",
							"src/main/webapp/components/Link.js",
							"src/main/webapp/components/Button.js",
							"src/main/webapp/components/ActionHandler.js",
							"src/main/webapp/components/ChangeHandler.js",
							"src/main/webapp/components/Mask.js",
							"src/main/webapp/components/Modal.js",
							"src/main/webapp/components/Block.js",
							"src/main/webapp/components/DateDialog.js",
							"src/main/webapp/components/TimeDialog.js",
							"src/main/webapp/components/DateTimeDialog.js",
							"src/main/webapp/components/DateIntervalDialog.js",
							"src/main/webapp/components/TimeIntervalDialog.js",
							"src/main/webapp/components/DateTimeIntervalDialog.js",
							"src/main/webapp/components/Chart.js",
							"src/main/webapp/components/ChartDialog.js",
							"src/main/webapp/components/PageControl.js",
							"src/main/webapp/components/LinkControl.js",
							"src/main/webapp/components/Dialog.js",
							"src/main/webapp/components/Popup.js",
							"src/main/webapp/components/TreeView.js",
							"src/main/webapp/components/DateFormat.js",
							"src/main/webapp/components/Switch.js",
							"src/main/webapp/components/Clock.js",
							"src/main/webapp/components/RichTextarea.js",
							"src/main/webapp/components/FullScreen.js",
							"src/main/webapp/components/Form.js",
							"src/main/webapp/components/Progress.js",
							"src/main/webapp/components/Focus.js",
							"src/main/webapp/components/Sortable.js",
							"src/main/webapp/components/Message.js",
							"src/main/webapp/components/Return.js",
							"src/main/webapp/components/DeskMenu.js",
							"src/main/webapp/components/DeskPane.js"],
						dest: 'src/main/webapp/Gate.js'
					}
			}

	});

	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.registerTask('default', ['watch']);
};