module.exports = function (grunt)
{
	const resources = "src/main/resources/META-INF/resources/gate";

	grunt.initConfig({

		clean: [`${resources}/*.mjs`,
			`${resources}/*.js`,
			`${resources}/*.css`],

		copy: {
			styles: {
				files: [{expand: true,
						flatten: true,
						filter: 'isFile',
						dest: resources,
						src: ['src/main/wc/**/*.css']}]
			},
			modules: {
				files: [{expand: true,
						flatten: true,
						filter: 'isFile',
						dest: resources,
						src: ['src/main/wc/**/*.mjs'],
						rename: (dest, src) => dest + '/' + src.replace('.mjs', '.js')}]
			},
			classes: {
				files: [{expand: true,
						flatten: true,
						filter: 'isFile',
						dest: resources,
						src: ['src/main/wc/**/*.wc'],
						rename: (dest, src) => dest + '/' + src.replace('.wc', '.js')}]
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
						dest: resources,
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

	grunt.registerTask('create-icon-list', () =>
	{
		let icons = [];
		grunt.file.recurse(`${resources}/icon`, function (path, root, sub, filename)
		{
			if (grunt.file.isFile(path))
				icons.push(filename.replace('.svg', ''));
		});

		grunt.file.write(`${resources}/icon-list.js`, `export default ${JSON.stringify(icons)};`);
		console.log('icon-list.mjs sucessfully created.');
	});

	grunt.registerTask('create-icon-data', () =>
	{
		let content = "let icons = new Map();\n";
		grunt.file.recurse(`${resources}/icon`, function (path, root, sub, filename)
		{
			if (grunt.file.isFile(path))
			{
				const code = filename.replace('.svg', '');
				const data = grunt.file.read(path, {encoding: 'utf8'});
				content += `icons.set("${code}", "data:image/svg+xml;base64,${btoa(data)}");\n`;
			}
		});
		content += "export default icons;";

		grunt.file.write(`${resources}/icon-data.js`, content);
		console.log('icon-data.mjs sucessfully created.');
	});


	grunt.registerTask('default', ['clean', 'copy', "less", 'create-icon-list', 'create-icon-data']);
};