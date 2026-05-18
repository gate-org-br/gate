import rewriteScreenChildNotation from "./rewrite-screen-child-notation.mjs";
import moveBrazilianTypesToGateTypeBr from "./move-brazilian-types-to-gate-type-br.mjs";
import moveMd5TypeToGateSecurityHash from "./move-md5-type-to-gate-security-hash.mjs";
import replaceGateIoUrlWithGateIoUrlBuilder from "./replace-gate-io-url-with-gate-io-url-builder.mjs";
import replaceGateTypeSafeStringWithGateTypeSafeName from "./replace-gate-type-safestring-with-gate-type-safename.mjs";
import replaceBearerAuthorizationConstructorReference from "./replace-bearerauthorization-constructor-reference.mjs";
import replaceGateTypeParameterWithGateUtilParameters from "./replace-gate-type-parameter-with-gate-util-parameters.mjs";
import moveMechanicalJavaxImportsToJakarta from "./move-mechanical-javax-imports-to-jakarta.mjs";
import replaceLinkConstructorWithLinkOf from "./replace-link-constructor-with-link-of.mjs";
import removeLinkSourceInjectionAndReplaceGetLinkUsage from "./remove-linksource-injection-and-replace-getlink-usage.mjs";
import replaceEjbScheduleWithQuarkusScheduled from "./replace-ejb-schedule-with-quarkus-scheduled.mjs";
import removeEnumStringConverterAnnotations from "./remove-enumstringconverter-annotations.mjs";
import replaceSetAttributesWithHasAttributes from "./replace-set-attributes-with-has-attributes.mjs";

export default [
	rewriteScreenChildNotation,
	moveBrazilianTypesToGateTypeBr,
	moveMd5TypeToGateSecurityHash,
	replaceGateIoUrlWithGateIoUrlBuilder,
	replaceGateTypeSafeStringWithGateTypeSafeName,
	replaceBearerAuthorizationConstructorReference,
	replaceGateTypeParameterWithGateUtilParameters,
	moveMechanicalJavaxImportsToJakarta,
	replaceLinkConstructorWithLinkOf,
	removeLinkSourceInjectionAndReplaceGetLinkUsage,
	replaceEjbScheduleWithQuarkusScheduled,
	removeEnumStringConverterAnnotations,
	replaceSetAttributesWithHasAttributes
];
