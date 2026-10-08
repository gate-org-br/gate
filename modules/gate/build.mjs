import fs from "fs/promises";
import path from "path";
import {glob} from "glob";
import less from "less";
import {transform} from "esbuild";
import svgtofont from "svgtofont";
import {fileURLToPath} from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const resources = path.resolve(
	__dirname,
	"src/main/resources/META-INF/resources/gate"
);
const documentation = path.resolve(__dirname, "../../docs");
const documentationGate = path.resolve(__dirname, "../../docs/gate");
const documentationIconPage = path.join(documentation, "icon/main.html");
const sourceIconsDir = path.resolve(__dirname, "src/main/icon");
const resourcesIconDir = path.join(resources, "icon");
const shouldMinify = process.argv.includes("--minify");
const fontTimestamp = 0;
const iconCategories = [
	{id: "actions", title: "Actions"},
	{id: "animals", title: "Animals"},
	{id: "arrows", title: "Arrows"},
	{id: "buildings", title: "Buildings"},
	{id: "charts", title: "Charts"},
	{id: "database", title: "Database"},
	{id: "development", title: "Development"},
	{id: "documents", title: "Documents"},
	{id: "emotions", title: "Emotions"},
	{id: "graphs", title: "Graphs"},
	{id: "hands", title: "Hands"},
	{id: "hardware", title: "Hardware"},
	{id: "navigation", title: "Navigation"},
	{id: "people", title: "People"},
	{id: "phone", title: "Phone"},
	{id: "playback", title: "Playback"},
	{id: "security", title: "Security"},
	{id: "symbols", title: "Symbols"},
	{id: "text", title: "Text"},
	{id: "types", title: "Types"},
	{id: "ui", title: "UI"},
	{id: "vehicles", title: "Vehicles"}
];

async function exists(file)
{
	try
	{
		await fs.access(file);
		return true;
	} catch (error)
	{
		if (error?.code === "ENOENT")
			return false;

		throw error;
	}
}

async function sortedGlob(pattern)
{
	const files = await glob(pattern);

	return files.sort((a, b) => a.localeCompare(b));
}

async function clean()
{
	const files = await glob(`${resources}/*.{js,mjs,css,map}`);

	for (const file of files)
		await fs.unlink(file);
}

async function copyCss()
{
	const files = await glob("src/main/wc/**/*.css");

	for (const file of files)
	{
		const dest = path.join(resources, path.basename(file));
		await fs.copyFile(file, dest);
	}
}

async function copyModules()
{
	const files = await glob("src/main/wc/**/*.mjs");

	for (const file of files)
	{
		const dest = path.join(
			resources,
			path.basename(file).replace(".mjs", ".js")
		);
		await fs.copyFile(file, dest);
	}
}

function minifyTemplateHtml(content)
{
	return content
		.replace(/\r\n?/g, "\n")
		.replace(/>[\t ]*\n[\t \n]*</g, "><");
}

function escapeTemplateLiteral(content)
{
	return content
		.replace(/`/g, "\\`")
		.replace(/\$\{/g, "\\${");
}

async function minifyCss(content, sourcefile)
{
	if (!shouldMinify)
		return content;

	try
	{
		const result = await transform(content, {
			loader: "css",
			minify: true,
			sourcefile
		});

		return result.code;
	} catch
	{
		return content;
	}
}

async function processWC()
{
	const files = await glob("src/main/wc/**/*.wc");

	for (const file of files)
	{
		const data = await fs.readFile(file, "utf8");

		let template = /<template>([\s\S]*?)<\/template>/.exec(data)?.[1];
		let script = /<script>([\s\S]*?)<\/script>/.exec(data)?.[1];
		let style = /<style>([\s\S]*?)<\/style>/.exec(data)?.[1];

		if (!script)
		{
			const wcc = file + "c";
			if (await exists(wcc))
				script = await fs.readFile(wcc, "utf8");
		}

		if (!style)
		{
			const wcs = file + "s";
			if (await exists(wcs))
				style = await fs.readFile(wcs, "utf8");
		}

		const minifiedTemplate = template ? minifyTemplateHtml(template) : undefined;
		const minifiedStyle = style
			? await minifyCss(style, `${path.basename(file, ".wc")}.css`)
			: undefined;

		let output = script ?? "";

		if (minifiedTemplate)
		{
			const name = path.basename(file, ".wc");
			const escapedTemplate = escapeTemplateLiteral(minifiedTemplate);

			if (minifiedStyle)
			{
				const escapedStyle = escapeTemplateLiteral(minifiedStyle);
				output =
					`let template = document.createElement("template");
template.innerHTML = \`${escapedTemplate}<style data-element="${name}">${escapedStyle}</style>\`;
${script ?? ""}`;
			} else
				output =
					`let template = document.createElement("template");
template.innerHTML = \`${escapedTemplate}\`;

${script ?? ""}`;
		}

		const dest = path.join(
			resources,
			path.basename(file).replace(".wc", ".js")
		);

		await fs.writeFile(dest, output);
	}
}

async function compileLess()
{
	const files = await glob("src/main/wc/**/*.less");

	for (const file of files)
	{
		const data = await fs.readFile(file, "utf8");
		const result = await less.render(data);

		const dest = path.join(
			resources,
			path.basename(file).replace(".less", ".css")
		);

		await fs.writeFile(dest, result.css);
	}
}

async function createIconList()
{
	const files = await sortedGlob(`${resourcesIconDir}/*.svg`);
	const icons = files.map(f => path.basename(f, ".svg"));

	await fs.writeFile(
		`${resources}/icon-list.js`,
		`export default ${JSON.stringify(icons)};`
	);
}

async function readIconMetadata(code)
{
	const file = path.join(sourceIconsDir, `${code}.json`);

	if (!await exists(file))
		return {};

	return JSON.parse(await fs.readFile(file, "utf8"));
}

function renderIconButton(metadata, level = 1)
{
	const text = `${metadata.name || metadata.code}`;
	const icon = `<g-icon>&#X${metadata.code};</g-icon>`;
	const action = `data:text/plain,${metadata.code}`;
	const tooltip = `data:application/json,${JSON.stringify(metadata)}`;
	const target = "@clipboard > @map(e => `${e} was copied to clipboard`) > @message(1000)";
	return `<a href="${action}" target="${target}" data-tooltip:source='${tooltip}'>${text}${icon}</a>`;
}

function renderIconGroup({title, icons})
{
	return `\t<g-desk-pane>
\t\t${title}
\t\t<g-icon>&#X${icons[0].code};</g-icon>
${icons.map(icon => renderIconButton(icon, 2)).join("\n")}
\t</g-desk-pane>`;
}

async function createIconDocumentation()
{
	const files = await sortedGlob(`${resourcesIconDir}/*.svg`);
	const icons = files.map(f => path.basename(f, ".svg"));
	const metadata = new Map();
	const categorized = new Set();
	const groups = [];

	for (const code of icons)
		metadata.set(code, await readIconMetadata(code));

	for (const category of iconCategories.toSorted((a, b) =>
		a.title.localeCompare(b.title)))
	{
		const categoryIcons = icons
			.filter(code => metadata.get(code).categories?.includes(category.id))
			.map(code => ({
				code,
				name: metadata.get(code).name,
				categories: metadata.get(code).categories
			}));

		if (categoryIcons.length < 2)
			continue;

		categoryIcons.forEach(icon => categorized.add(icon.code));
		groups.push({
			title: category.title,
			icons: categoryIcons
		});
	}

	const uncategorized = icons
		.filter(code => !categorized.has(code))
		.map(code => ({code}));

	if (uncategorized.length)
		groups.push({
			title: "Uncategorized",
			icons: uncategorized
		});

	groups.push({
		title: "All",
		icons: icons.map(code => ({code}))
	});

	const content = `<input data-trigger="input" 
data-action="data:text/plain,@value(this)" 
			data-target="@attribute(#desk-pane:filter)">
<g-desk-pane id="desk-pane" class="inline">
${groups.map(renderIconGroup).join("\n")}
</g-desk-pane>
`;

	await fs.mkdir(path.dirname(documentationIconPage), {recursive: true});
	await fs.writeFile(documentationIconPage, content);
}

async function createIconData()
{
	const files = await sortedGlob(`${resourcesIconDir}/*.svg`);
	let content = "let icons = new Map();\n";

	for (const file of files)
	{
		const code = path.basename(file, ".svg");
		const data = await fs.readFile(file, "utf8");
		const base64 = Buffer.from(data).toString("base64");

		content += `icons.set("${code}", "data:image/svg+xml;base64,${base64}");\n`;
	}

	content += "export default icons;";

	await fs.writeFile(
		`${resources}/icon-data.js`,
		content
	);
}

async function copyIconsToResources()
{
	if (!await exists(sourceIconsDir))
		throw new Error(`Icons source directory not found: ${sourceIconsDir}`);

	await fs.rm(resourcesIconDir, {recursive: true, force: true});
	await fs.cp(sourceIconsDir, resourcesIconDir, {recursive: true});
}

async function createIconFont()
{
	await svgtofont({
		src: resourcesIconDir,
		dist: resources,
		fontName: "Gate",
		css: false,
		website: false,
		outSVGReact: false,
		outSVGReactNative: false,
		log: false,
		svgicons2svgfont: {
			normalize: true
		},
		svg2ttf: {
			ts: fontTimestamp
		},
		getIconUnicode: (name, unicode, startUnicode) =>
		{
			const codepoint = Number.parseInt(name, 16);
			if (Number.isFinite(codepoint))
				return [String.fromCodePoint(codepoint), codepoint + 1];

			return [unicode, startUnicode];
		}
	});
}

async function minifyJavaScript()
{
	const files = await glob(`${resources}/*.js`);

	for (const file of files)
	{
		const data = await fs.readFile(file, "utf8");
		const sourceMapFile = `${path.basename(file)}.map`;
		const result = await transform(data, {
			loader: "js",
			minify: true,
			sourcemap: "external",
			sourcefile: path.basename(file)
		});

		await fs.writeFile(
			file,
			`${result.code}\n//# sourceMappingURL=${sourceMapFile}\n`
		);
		await fs.writeFile(`${file}.map`, result.map);
	}
}

async function copyResourcesToDocumentation()
{
	await fs.rm(documentationGate, {recursive: true, force: true});
	await fs.cp(resources, documentationGate, {recursive: true});
}

async function build()
{
	await fs.mkdir(resources, {recursive: true});
	await clean();
	await copyCss();
	await copyModules();
	await processWC();
	await compileLess();
	await copyIconsToResources();
	await createIconList();
	await createIconDocumentation();
	await createIconData();
	await createIconFont();
	if (shouldMinify)
		await minifyJavaScript();
	await copyResourcesToDocumentation();
}

build();
