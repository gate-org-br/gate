package gate.thymeleaf;

import gate.base.Screen;
import gate.type.Attributes;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

public class Model
{

	private final String name;
	private final IModel model;
	private final ITemplateContext context;
	private final IProcessableElementTag element;
	private final IElementModelStructureHandler handler;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;
	private Screen screen;

	public Model(String name, ITemplateContext context, IModel model,
		IElementModelStructureHandler handler)
	{
		this.name = name;
		this.context = context;
		this.model = model;
		this.handler = handler;
		this.element = (IProcessableElementTag) model.get(0);
	}

	public boolean isStandalone()
	{
		return element instanceof IStandaloneElementTag;
	}

	public String tag()
	{
		return element.getElementCompleteName();
	}

	public boolean has(String attribute)
	{
		return element.hasAttribute(attribute);
	}

	public String get(String attribute)
	{
		return element.getAttributeValue(attribute);
	}

	public Stream<IAttribute> stream()
	{
		return Stream.of(element.getAllAttributes());
	}

	public String getName()
	{
		return name;
	}

	public ITemplateContext getContext()
	{
		return context;
	}

	public IElementModelStructureHandler getHandler()
	{
		return handler;
	}

	public IModel cloneModel()
	{
		return model.cloneModel();
	}

	public Model add(String text)
	{
		model.add(context.getModelFactory().createText(text));
		return this;
	}

	public void removeAll()
	{
		model.reset();
	}

	public void replaceAll(String text)
	{
		model.reset();
		model.add(context.getModelFactory().createText(text));
	}

	public void replaceTag(String tag, Attributes attributes)
	{
		model.replace(0, context.getModelFactory().createText("<" + tag + " " + attributes + ">"));
		model.replace(model.size() - 1, context.getModelFactory().createText("</" + tag + ">"));
	}

	public void replaceTag(String start, String close)
	{
		model.replace(0, context.getModelFactory().createText(start));
		model.replace(model.size() - 1, context.getModelFactory().createText(close));
	}

	public void removeTag()
	{
		model.remove(0);
		model.remove(model.size() - 1);
	}

	public void encloseWith(String tag, Attributes attributes)
	{
		model.insert(0, context.getModelFactory().createText("<" + tag + " " + attributes + ">"));
		model.insert(model.size(), context.getModelFactory().createText("</" + tag + ">"));
	}

	public Screen screen()
	{
		if (screen == null)
			screen = (Screen) request().getAttribute("screen");
		return screen;
	}

	public HttpSession session()
	{
		if (session == null)
			session = request().getSession();
		return session;
	}

	public HttpServletRequest request()
	{
		if (request == null)
			request = ((IWebContext) context).getRequest();
		return request;
	}

	public HttpServletResponse response()
	{
		if (response == null)
			response = ((IWebContext) context).getResponse();
		return response;
	}

	public IModel getModel()
	{
		return model;
	}
}
