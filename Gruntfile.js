module.exports = function (grunt)
{
	grunt.initConfig({

		clean: ['src/main/resources/gate/Gate.js',
			'src/main/resources/gate/*.mjs',
			'src/main/resources/gate/*.css'],

		copy: {
			styles: {
				files: [
					{expand: true,
						flatten: true,
						filter: 'isFile',
						dest: 'src/main/resources/gate',
						src: ['src/main/wc/**/*.css']}
				]
			},
			modules: {
				files: [
					{expand: true,
						flatten: true,
						filter: 'isFile',
						dest: 'src/main/resources/gate',
						src: ['src/main/wc/**/*.mjs'],
						rename: (dest, src) => dest + '/' + src.replace('.mjs', '.mjs')}
				]
			},
			classes: {
				files: [
					{expand: true,
						flatten: true,
						filter: 'isFile',
						dest: 'src/main/resources/gate',
						src: ['src/main/wc/**/*.wc'],
						rename: (dest, src) => dest + '/' + src.replace('.wc', '.mjs')}
				]
			},
			options: {
				process: function (data, name)
				{
					if (!name.endsWith("wc"))
						return data;
					let template = /<template>([\s\S]*?)<\/template>/g.exec(data);
					if (template && template.length === 2)
						template = template = template[1];
					let script = "";
					let scriptTag = /<script>([\s\S]*?)<\/script>/g.exec(data);
					if (scriptTag && scriptTag.length === 2)
						script = scriptTag[1];
					else if (grunt.file.exists(name + 'c'))
						script = grunt.file.read(name + 'c');
					let style = "";
					let styleTag = /<style>([\s\S]*?)<\/style>/g.exec(data);
					if (styleTag && styleTag.length === 2)
						style = styleTag[1];
					else if (grunt.file.exists(name + "s"))
						style = grunt.file.read(name + "s");

					if (template)
						if (style)
							return `let template = document.createElement("template");
template.innerHTML = \`${template} <style>${style}</style>\`;

${script}`;
						else
							return `let template = document.createElement("template");
template.innerHTML = \`${template}\`;

${script}`;
					else
						return script;
				}
			}
		},

		less: {
			development: {
				options: {
					paths: ['assets/css']
				},
				files: [
					{
						expand: true,
						src: ['src/main/wc/**/*.less'],
						dest: 'src/main/resources/gate',
						ext: '.css',
						flatten: true
					}]
			}
		},

		watch: {
			views: {
				files: ['src/main/wc/**/*',
					'src/main/components/**/*'],
				tasks: ['default']
			}
		}
	});
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-less');
	grunt.loadNpmTasks('grunt-contrib-copy');
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.registerTask('default', ['clean', 'copy', "less"]);
};