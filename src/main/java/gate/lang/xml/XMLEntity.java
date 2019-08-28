package gate.lang.xml;

import gate.error.TemplateException;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

class XMLEntity implements XMLEvaluable
{

	private final char value;
	private final String string;

	public static final List<XMLEntity> VALUES
		= Arrays
			.asList(new XMLEntity("&nbsp;", ' '),
				new XMLEntity("&ccedil;", 'ç'),
				new XMLEntity("&Ccedil;", 'Ç'),
				new XMLEntity("&aacute;", 'á'),
				new XMLEntity("&eacute;", 'é'),
				new XMLEntity("&iacute;", 'í'),
				new XMLEntity("&oacute;", 'ó'),
				new XMLEntity("&uacute;", 'ú'),
				new XMLEntity("&Aacute;", 'Á'),
				new XMLEntity("&Eacute;", 'É'),
				new XMLEntity("&Iacute;", 'Í'),
				new XMLEntity("&Oacute;", 'Ó'),
				new XMLEntity("&Uacute;", 'Ú'),
				new XMLEntity("&acirc;", 'â'),
				new XMLEntity("&ecirc;", 'ê'),
				new XMLEntity("&icirc;", 'î'),
				new XMLEntity("&ocirc;", 'ô'),
				new XMLEntity("&ucirc;", 'û'),
				new XMLEntity("&Acirc;", 'Â'),
				new XMLEntity("&Ecirc;", 'Ê'),
				new XMLEntity("&Icirc;", 'Î'),
				new XMLEntity("&Ocirc;", 'Ô'),
				new XMLEntity("&Ucirc;", 'Û'),
				new XMLEntity("&atilde;", 'ã'),
				new XMLEntity("&etilde;", 'ẽ'),
				new XMLEntity("&itilde;", 'ĩ'),
				new XMLEntity("&otilde;", 'õ'),
				new XMLEntity("&utilde;", 'ũ'),
				new XMLEntity("&Atilde;", 'Ã'),
				new XMLEntity("&Etilde;", 'Ẽ'),
				new XMLEntity("&Itilde;", 'Ĩ'),
				new XMLEntity("&Otilde;", 'Õ'),
				new XMLEntity("&Utilde;", 'Ũ'),
				new XMLEntity("&agrave;", 'à'),
				new XMLEntity("&egrave;", 'è'),
				new XMLEntity("&igrave;", 'ì'),
				new XMLEntity("&ograve;", 'ò'),
				new XMLEntity("&ugrave;", 'ù'),
				new XMLEntity("&Agrave;", 'À'),
				new XMLEntity("&Egrave;", 'È'),
				new XMLEntity("&Igrave;", 'Ì'),
				new XMLEntity("&Ograve;", 'Ò'),
				new XMLEntity("&Ugrave;", 'Ù'));

	private XMLEntity(String string, char value)
	{
		this.value = value;
		this.string = string;
	}

	@Override
	public Type getType()
	{
		return Type.ENTITY;
	}

	@Override
	public void evaluate(Writer writer) throws TemplateException
	{
		try
		{
			writer.write(value);
		} catch (IOException ex)
		{
			throw new TemplateException(String.format("Error trying to evaluate template: %s.", ex.getMessage()));
		}
	}

	@Override
	public String toString()
	{
		return string;
	}
}
