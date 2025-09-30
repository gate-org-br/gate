module.exports = function (grunt)
{
	const { generateFonts } = require('fantasticon');

	const ICONS = "src/main/icon";
	const RESOURCES = "src/main/resources/META-INF/resources/gate";
	grunt.initConfig({
		clean: [`${RESOURCES}/*`],
		copy: {
			svg: {
				files: [{
					expand: true,
					src: 'src/main/gate.svg',
					dest: RESOURCES,
					flatten: true
				}]
			},
			icons: {
				files: [{
					expand: true,
					cwd: 'src/main/icons',
					src: '**/*',
					dest: `${RESOURCES}/icon`
				}]
			},
			styles: {
				files: [{
					expand: true,
					flatten: true,
					filter: 'isFile',
					dest: RESOURCES,
					src: ['src/main/wc/**/*.css']
				}]
			},
			modules: {
				files: [{
					expand: true,
					flatten: true,
					filter: 'isFile',
					dest: RESOURCES,
					src: ['src/main/wc/**/*.mjs'],
					rename: (dest, src) => dest + '/' + src.replace('.mjs', '.js')
				}]
			},
			classes: {
				files: [{
					expand: true,
					flatten: true,
					filter: 'isFile',
					dest: RESOURCES,
					src: ['src/main/wc/**/*.wc'],
					rename: (dest, src) => dest + '/' + src.replace('.wc', '.js')
				}]
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
template.innerHTML = \`${template} <style data-element="${name.slice(name.lastIndexOf("/") + 1, -3)}">${style}</style>\`;
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
						dest: RESOURCES,
						ext: '.css',
						flatten: true
					}]
			}
		},
		watch: {
			views: {
				files: ['src/main/wc/**/*',
					'src/main/components/**/*',
					'src/main/icons/**/*.svg'],
				tasks: ['default'],
				options: {
					spawn: false,
					debounceDelay: 250
				}
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
		grunt.file.recurse(ICONS, function (path, root, sub, filename)
		{
			if (grunt.file.isFile(path))
				icons.push(filename.replace('.svg', ''));
		});
		grunt.file.write(`${RESOURCES}/icon-list.js`, `export default ${JSON.stringify(icons)};`);
		console.log('icon-list.mjs sucessfully created.');
	});
	grunt.registerTask('create-icon-data', () =>
	{
		let content = "let icons = new Map();\n";
		grunt.file.recurse(ICONS, function (path, root, sub, filename)
		{
			if (grunt.file.isFile(path))
			{
				const code = filename.replace('.svg', '');
				const data = grunt.file.read(path, { encoding: 'utf8' });
				content += `icons.set("${code}", "data:image/svg+xml;base64,${Buffer.from(data).toString('base64')}");\n`;
			}
		});
		content += "export default icons;";
		grunt.file.write(`${RESOURCES}/icon-data.js`, content);
		console.log('icon-data.mjs sucessfully created.');
	});
	grunt.registerTask('create-icon-font', () =>
	{
		const done = grunt.task.current.async();

		const svgFiles = grunt.file.expand(`${ICONS}/*.svg`)
			.map(file => file.replace(/^.*\//, '').replace('.svg', ''));

		const codepoints = {};
		svgFiles.forEach(e => codepoints[e] = parseInt(e, 16));

		generateFonts({
			inputDir: ICONS,
			outputDir: RESOURCES,
			name: 'Gate',
			assetTypes: [],
			fontTypes: ['eot', 'woff', 'ttf'],
			templates: {},
			pathOptions: {},
			codepoints: codepoints,
			normalize: true
		}).then(() =>
		{
			grunt.log.ok('Gate font files sucessfully created.');
			done();
		}).catch((error) =>
		{
			grunt.log.error('Error trying to create Gate font files:', error);
			done(false);
		});
	});
	grunt.registerTask('startup', 'watch');
	grunt.registerTask('default', ['clean', 'copy', "less", 'create-icon-list', 'create-icon-data', 'create-icon-font']);
};