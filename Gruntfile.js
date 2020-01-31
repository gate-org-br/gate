module.exports = function (grunt)
{
	grunt.initConfig({

		import: {
			options: {},
			dist: {
				src: 'src/main/webapp/components/bundle.txt',
				dest: 'src/main/resources/gate/Gate.js',
			}
		},

		watch: {
			views: {
				files: ['src/main/webapp/components/**/*'],
				tasks: ['import']
			}
		}
	});
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-import');
	grunt.registerTask('default', ['import']);
};