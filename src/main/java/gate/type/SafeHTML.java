package gate.type;

import gate.annotation.Converter;
import gate.converter.custom.SafeHTMLConverter;
import java.io.Serializable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Safelist;

@Converter(SafeHTMLConverter.class)
public class SafeHTML implements Serializable
{

	private final String value;

	private static final Safelist ALLOWED = new Safelist()
		.addTags("div",
			"span",
			"br",
			"hr",
			"a",
			"img",
			"p",
			"h1",
			"h2",
			"h3",
			"h4",
			"h5",
			"h6",
			"b", "i", "strong", "em", "ul", "ol", "li",
			"table", "caption", "thead", "tbody", "tfoot", "tr", "td", "th")
		.addAttributes("div", "style")
		.addAttributes("span", "style")
		.addAttributes("a", "href", "style", "download")
		.addAttributes("img", "src", "style")
		.addAttributes("p", "style")
		.addAttributes("h1", "style")
		.addAttributes("h2", "style")
		.addAttributes("h3", "style")
		.addAttributes("h4", "style")
		.addAttributes("h5", "style")
		.addAttributes("h6", "style")
		.addAttributes("b", "style")
		.addAttributes("i", "style")
		.addAttributes("strong", "style")
		.addAttributes("em", "style")
		.addAttributes("ul", "style")
		.addAttributes("ol", "style")
		.addAttributes("li", "style")
		.addAttributes("table", "style")
		.addAttributes("caption", "style")
		.addAttributes("thead", "style")
		.addAttributes("tbody", "style")
		.addAttributes("tfoot", "style")
		.addAttributes("td", "style")
		.addAttributes("th", "style");

	private SafeHTML(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value;
	}

	public String text()
	{
		return Jsoup.parse(value
			.replace("<br>", "\n")
			.replace("<br/>", "\n"))
			.wholeText()
			.trim();
	}

	public boolean isBlank()
	{
		return value.isBlank();
	}

	public static SafeHTML of(String value)
	{
		OutputSettings outputSettings = new OutputSettings();
		outputSettings.prettyPrint(false);
		return new SafeHTML(Jsoup.clean(value, "", ALLOWED, outputSettings).trim());
	}
}
