package gate.thymeleaf.processors.tag.anchor;

import gate.adapter.renderer.Renderer;

import gate.Call;
import gate.adapter.converter.Converter;
import gate.entity.User;
import gate.type.Attributes;
import gate.util.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

import java.util.Optional;
import java.util.StringJoiner;

@ApplicationScoped
public class LinkProcessor extends AnchorProcessor
{

	public LinkProcessor()
	{
		super("link");
	}

	@Override
	protected void process(
			ITemplateContext context,
			IModel model,
			IElementModelStructureHandler handler,
			IProcessableElementTag element,
			User user,
			Call call,
			Attributes attributes,
			Parameters parameters)
	{
		if (!call.accessRule().allows(user))
		{
			model.reset();
			return;
		}

		if (!condition(attributes))
		{
			otherwise(attributes)
					.ifPresentOrElse(
							e -> replaceWith(context, model, handler, e),
							model::reset);
			return;
		}

		if ("POST".equalsIgnoreCase(method(attributes)))
			renderButton(context, model, handler, element, call, attributes, parameters);
		else
			renderLink(context, model, handler, element, call, attributes, parameters);
	}

	private void renderButton(
			ITemplateContext context,
			IModel model,
			IElementModelStructureHandler handler,
			IProcessableElementTag element,
			Call call,
			Attributes attributes,
			Parameters parameters)
	{
		attributes.put("formaction", call.command().toString(parameters));
		if (element.hasAttribute("form"))
			attributes.put("form", element.getAttributeValue("form"));

		target(call, attributes)
				.ifPresent(t -> attributes.put("formtarget", t));

		render(context, model, handler, element, "button", attributes, call);
	}

	private void renderLink(
			ITemplateContext context,
			IModel model,
			IElementModelStructureHandler handler,
			IProcessableElementTag element,
			Call call,
			Attributes attributes,
			Parameters parameters)
	{
		attributes.put("href", call.command().toString(parameters));

		target(call, attributes)
				.ifPresent(t -> attributes.put("target", t));

		render(context, model, handler, element, "a", attributes, call);
	}

	private void render(
			ITemplateContext context,
			IModel model,
			IElementModelStructureHandler handler,
			IProcessableElementTag element,
			String tag,
			Attributes attributes,
			Call call)
	{
		if (element instanceof IStandaloneElementTag)
		{
			String body = buildBody(call);
			replaceWith(
					context,
					model,
					handler,
					"<" + tag + " " + attributes + ">" + body + "</" + tag + ">");
		} else
		{
			replaceTag(context, model, handler, tag, attributes);
		}
	}

	private String buildBody(Call call)
	{
		StringJoiner body = new StringJoiner("").setEmptyValue("unamed");
		var meta = call.metadata();

		if (meta.name() != null)
			body.add(meta.name());

		if (meta.icon() != null)
			body.add("<i>" + meta.icon() + "</i>");
		else if (meta.emoji() != null)
			body.add("<e>" + meta.emoji() + "</e>");

		return body.toString();
	}

	private Optional<String> otherwise(Attributes attributes)
	{
		if (!attributes.containsKey("otherwise"))
			return Optional.empty();

		Object otherwise = attributes.remove("otherwise");
		otherwise = expression.create().evaluate((String) otherwise);

		if (otherwise == null)
			return Optional.empty();

		return Optional.of(Renderer.render(otherwise));
	}
}