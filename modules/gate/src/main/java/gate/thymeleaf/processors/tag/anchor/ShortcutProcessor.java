package gate.thymeleaf.processors.tag.anchor;

import gate.Call;
import gate.entity.User;
import gate.type.Attributes;
import gate.util.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

@ApplicationScoped
public class ShortcutProcessor extends AnchorProcessor
{

    public ShortcutProcessor()
    {
        super("shortcut");
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
        if (!call.accessRule().allows(user) || !condition(attributes))
        {
            model.reset();
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
            replaceWith(
                    context,
                    model,
                    handler,
                    "<" + tag + " " + attributes + ">" + buildIcon(call) + "</" + tag + ">");
        else
            replaceTag(context, model, handler, tag, attributes);
    }

    private String buildIcon(Call call)
    {
        var meta = call.metadata();

        if (meta.icon() != null)
            return "<g-icon>" + meta.icon() + "</g-icon>";

        if (meta.emoji() != null)
            return "<e>" + meta.emoji() + "</e>";

        return "?";
    }
}
