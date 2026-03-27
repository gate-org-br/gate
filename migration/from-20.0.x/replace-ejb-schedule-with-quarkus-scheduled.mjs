/**
 * Migrates @Schedule (javax.ejb) to @Scheduled (io.quarkus.scheduler).
 *
 * WHAT GETS MIGRATED
 * - Simple intervals → every = "Ns" / "Nm" / "Nh"
 *     * /N on seconds only           → every = "Ns"
 *     * /N on minutes, second=0      → every = "Nm"
 *     * /N on hours, minute=0        → every = "Nh"
 * - Everything else → cron = "s m h dom month dow [year]" (Quartz format)
 *     Numeric dayOfWeek is shifted from EJB (0-7, 0=Sun) to Quartz (1-7, 1=Sun)
 *     Named dayOfWeek and month are uppercased ("Mon" → "MON", "Jan" → "JAN")
 *
 * LIMITATIONS — annotations that cannot be migrated are left unchanged and reported as warnings
 * - timezone: @Scheduled has no timezone attribute; would silently change behavior
 * - dayOfMonth and dayOfWeek both set: Quartz does not support combining both constraints
 */
export default {
	name: "Replace EJB Schedule with Quarkus Scheduled",
	description: "Rewrites recognized @Schedule patterns to @Scheduled with SKIP concurrency and updates the import to io.quarkus.scheduler.Scheduled.",
	filePredicate: ({file, source}) => /\.java$/i.test(file) && source.includes("@Schedule(") && source.includes("javax.ejb.Schedule"),
	warn: ({file, source}) =>
	{
		const count = source.match(/@Schedule\(/g)?.length ?? 0;
		return count > 0 ? [`${file} still contains ${count} unmigrated @Schedule annotation(s)`] : [];
	},
	apply: ({source}) =>
	{
		let migrated = source.replace(/@Schedule\(([^)]*)\)/g, (match, argumentsText) =>
		{
			const trigger = toTrigger(argumentsText);
			if (trigger == null)
				return match;
			return `@Scheduled(${trigger}, concurrentExecution = Scheduled.ConcurrentExecution.SKIP)`;
		});
		const hasLegacySchedule = migrated.includes("@Schedule(");
		const hasQuarkusSchedule = migrated.includes("@Scheduled(");
		if (hasLegacySchedule && hasQuarkusSchedule)
			migrated = migrated.replace(/\bimport\s+javax\.ejb\.Schedule\s*;/g,
				"import javax.ejb.Schedule;\nimport io.quarkus.scheduler.Scheduled;");
		else if (hasQuarkusSchedule)
			migrated = migrated.replace(/\bimport\s+javax\.ejb\.Schedule\s*;/g, "import io.quarkus.scheduler.Scheduled;");
		return migrated;
	}
};

function parseScheduleArguments(argumentsText)
{
	const schedule = new Map();
	for (const match of argumentsText.matchAll(/(\w+)\s*=\s*(?:"([^"]*)"|(true|false))/g))
		schedule.set(match[1], match[2] ?? match[3]);
	return schedule;
}

function parseEveryValue(value)
{
	return value && /^\*\/\d+$/.test(value)
		? Number.parseInt(value.slice(2), 10)
		: null;
}

function toTrigger(argumentsText)
{
	const schedule = parseScheduleArguments(argumentsText);
	const every = toEvery(schedule);
	if (every != null)
		return `every = "${every}"`;
	const cron = toCron(schedule);
	if (cron != null)
		return `cron = "${cron}"`;
	return null;
}

function toEvery(schedule)
{
	// timezone changes semantics — @Scheduled has no equivalent attribute
	if (schedule.get("timezone"))
		return null;

	const dayOfMonth = schedule.get("dayOfMonth");
	const month = schedule.get("month");
	const dayOfWeek = schedule.get("dayOfWeek");
	if (dayOfMonth != null && dayOfMonth !== "*")
		return null;
	if (month != null && month !== "*")
		return null;
	if (dayOfWeek != null && dayOfWeek !== "*")
		return null;

	const hour = schedule.get("hour");
	const minute = schedule.get("minute");
	const second = schedule.get("second");
	if (hour === "*" && minute === "*")
	{
		const seconds = parseEveryValue(second);
		if (seconds != null)
			return `${seconds}s`;
	}
	if (hour === "*")
	{
		const minutes = parseEveryValue(minute);
		if (minutes != null && (second == null || second === "0"))
			return `${minutes}m`;
	}
	const hours = parseEveryValue(hour);
	if (hours != null && (minute == null || minute === "0") && (second == null || second === "0"))
		return `${hours}h`;
	return null;
}

function convertDayOfWeek(value)
{
	if (value === "*" || value === "?")
		return value;
	if (/[a-zA-Z]/.test(value))
		return value.toUpperCase();

	if (value.includes("/"))
	{
		const [start, step] = value.split("/");
		const convertedStart = start === "*" ? "*" : shiftDayOfWeek(start);
		return `${convertedStart}/${step}`;
	}
	if (value.includes(","))
		return value.split(",").map(shiftDayOfWeek).join(",");
	if (value.includes("-"))
	{
		const [from, to] = value.split("-");
		return `${shiftDayOfWeek(from)}-${shiftDayOfWeek(to)}`;
	}
	return shiftDayOfWeek(value);
}

function shiftDayOfWeek(value)
{
	const n = Number.parseInt(value, 10);
	return String(n === 0 || n === 7 ? 1 : n + 1);
}

function toCron(schedule)
{
	// timezone changes semantics — @Scheduled has no equivalent attribute
	const timezone = schedule.get("timezone");
	if (timezone != null && timezone !== "")
		return null;

	const second = schedule.get("second") ?? "0";
	const minute = schedule.get("minute") ?? "0";
	const hour = schedule.get("hour") ?? "0";
	const dayOfMonth = schedule.get("dayOfMonth") ?? "*";
	const month = schedule.get("month") ?? "*";
	const dayOfWeek = schedule.get("dayOfWeek") ?? "*";
	const year = schedule.get("year");
	if (!isCronField(second) || !isCronField(minute) || !isCronField(hour)
		|| !isCronField(dayOfMonth) || !isCronField(month) || !isCronField(dayOfWeek))
		return null;

	// Quartz does not support combining dayOfMonth and dayOfWeek constraints
	if (dayOfMonth !== "*" && dayOfWeek !== "*")
		return null;

	const convertedDayOfWeek = convertDayOfWeek(dayOfWeek);
	const quartzDayOfMonth = dayOfWeek !== "*" && dayOfMonth === "*" ? "?" : dayOfMonth;
	const quartzDayOfWeek = dayOfMonth !== "*" && dayOfWeek === "*" ? "?" : convertedDayOfWeek;
	const fields = [second, minute, hour, quartzDayOfMonth, month.toUpperCase(), quartzDayOfWeek];
	if (year != null)
	{
		if (!isCronField(year))
			return null;
		fields.push(year);
	}
	return fields.join(" ");
}

function isCronField(value)
{
	return value != null && !/\s/.test(value);
}