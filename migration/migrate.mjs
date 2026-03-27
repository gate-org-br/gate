#!/usr/bin/env node
import fs from "node:fs";
import path from "node:path";
import migrations from "./from-20.0.x/index.mjs";

const args = process.argv.slice(2);
const target = args.find(arg => !arg.startsWith("--")) ?? ".";
try
{
	const root = path.resolve(target);
	console.log(`Starting migration in ${root}`);
	for (const migration of migrations)
	{
		console.log(`Running: "${migration.name}"`);
		console.log(`Description: ${migration.description}`);
		walk(root, migration);
	}
} catch (error)
{
	console.error(error instanceof Error ? error.message : String(error));
	process.exitCode = 1;
}

function walk(root, migration)
{
	if (fs.statSync(root).isFile())
	{
		migrate(root, migration);
		return;
	}
	const entries = fs.readdirSync(root, {withFileTypes: true});
	for (const entry of entries)
		walk(path.join(root, entry.name), migration);
}

function migrate(file, migration)
{
	const source = fs.readFileSync(file, "utf8");
	if (!migration.filePredicate({file, source}))
		return;
	const migrated = migration.apply({file, source});
	if (typeof migrated !== "string")
		throw new TypeError(`Migration "${migration.name}" must return a string for ${file}`);
	if (migrated !== source)
	{
		fs.writeFileSync(file, migrated, "utf8");
		console.log(`${file} migrated successfully`);
	}
	if (typeof migration.warn === "function")
	{
		for (const warning of migration.warn({file, source: migrated}))
		{
			console.warn(`\u001B[33mWarning:\u001B[0m ${warning}`);
			process.exitCode = 1;
		}
	}
}