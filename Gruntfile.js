module.exports = function (grunt)
{
	grunt.initConfig({

		import: {
			options: {},
			dist: {
				src: 'src/main/resources/components/bundle.txt',
				dest: 'src/main/resources/gate/Gate.js',
			}
		},

		watch: {
			views: {
				files: ['src/main/resources/components/**/*'],
				tasks: ['import', "less"]
			}
		},

		less: {
			development: {
				options: {
					paths: ['assets/css']
				},
				files: {
					'src/main/resources/gate/Gate.css': 'src/main/resources/components/Gate.less'
				}
			}
		}
	});
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-less');
	grunt.loadNpmTasks('grunt-import');
	grunt.registerTask('default', ['watch']);
};