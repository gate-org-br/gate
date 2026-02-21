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
const documentationGate = path.resolve(__dirname, "documentation/gate");
const sourceIconsDir = path.resolve(__dirname, "src/main/icons");
const resourcesIconDir = path.join(resources, "icon");

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

async function clean()
{
	const files = await glob(`${resources}/*.{js,mjs,css}`);

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
	const files = await glob(`${resourcesIconDir}/*.svg`);
	const icons = files.map(f => path.basename(f, ".svg"));

	await fs.writeFile(
		`${resources}/icon-list.js`,
		`export default ${JSON.stringify(icons)};`
	);
}

async function createIconData()
{
	const files = await glob(`${resourcesIconDir}/*.svg`);
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

async function copyIconsToResources() {
	if (!await exists(sourceIconsDir))
		throw new Error(`Icons source directory not found: ${sourceIconsDir}`);

	await fs.rm(resourcesIconDir, {recursive: true, force: true});
	await fs.cp(sourceIconsDir, resourcesIconDir, {recursive: true});
}

async function createIconFont() {
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
		getIconUnicode: (name, unicode, startUnicode) => {
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
	await clean();
	await copyCss();
	await copyModules();
	await processWC();
	await compileLess();
	await copyIconsToResources();
	await createIconList();
	await createIconData();
	await createIconFont();
	await minifyJavaScript();
	await copyResourcesToDocumentation();
}

build();
