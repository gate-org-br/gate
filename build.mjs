import fs from "fs/promises";
import path from "path";
import {glob} from "glob";
import less from "less";
import {transform} from "esbuild";
import {fileURLToPath} from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const resources = path.resolve(
    __dirname,
    "src/main/resources/META-INF/resources/gate"
);

async function clean() {
    const files = await glob(`${resources}/*.{js,mjs,css}`);

    for (const file of files)
        await fs.unlink(file);
}

async function copyCss() {
    const files = await glob("src/main/wc/**/*.css");

    for (const file of files) {
        const dest = path.join(resources, path.basename(file));
        await fs.copyFile(file, dest);
    }
}

async function copyModules() {
    const files = await glob("src/main/wc/**/*.mjs");

    for (const file of files) {
        const dest = path.join(
            resources,
            path.basename(file).replace(".mjs", ".js")
        );
        await fs.copyFile(file, dest);
    }
}

async function processWC() {
    const files = await glob("src/main/wc/**/*.wc");

    for (const file of files) {
        const data = await fs.readFile(file, "utf8");

        let template = /<template>([\s\S]*?)<\/template>/.exec(data)?.[1];
        let script = /<script>([\s\S]*?)<\/script>/.exec(data)?.[1];
        let style = /<style>([\s\S]*?)<\/style>/.exec(data)?.[1];

        if (!script) {
            const wcc = file + "c";
            try {
                script = await fs.readFile(wcc, "utf8");
            } catch {
            }
        }

        if (!style) {
            const wcs = file + "s";
            try {
                style = await fs.readFile(wcs, "utf8");
            } catch {
            }
        }

        let output = script ?? "";

        if (template) {
            const name = path.basename(file, ".wc");

            if (style)
                output =
                    `let template = document.createElement("template");
template.innerHTML = \`${template} <style data-element="${name}">${style}</style>\`;
${script ?? ""}`;
            else
                output =
                    `let template = document.createElement("template");
template.innerHTML = \`${template}\`;

${script ?? ""}`;
        }

        const dest = path.join(
            resources,
            path.basename(file).replace(".wc", ".js")
        );

        await fs.writeFile(dest, output);
    }
}

async function compileLess() {
    const files = await glob("src/main/wc/**/*.less");

    for (const file of files) {
        const data = await fs.readFile(file, "utf8");
        const result = await less.render(data);

        const dest = path.join(
            resources,
            path.basename(file).replace(".less", ".css")
        );

        await fs.writeFile(dest, result.css);
    }
}

async function createIconList() {
    const files = await glob(`${resources}/icon/*.svg`);
    const icons = files.map(f => path.basename(f, ".svg"));

    await fs.writeFile(
        `${resources}/icon-list.js`,
        `export default ${JSON.stringify(icons)};`
    );
}

async function createIconData() {
    const files = await glob(`${resources}/icon/*.svg`);
    let content = "let icons = new Map();\n";

    for (const file of files) {
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

async function minifyJavaScript() {
    const files = await glob(`${resources}/*.js`);

    for (const file of files) {
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

async function build() {
    await clean();
    await copyCss();
    await copyModules();
    await processWC();
    await compileLess();
    await createIconList();
    await createIconData();
    await minifyJavaScript();
}

build();
