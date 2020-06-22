package gateconsole.contol;

import gate.base.Control;
import gate.error.AppException;
import gateconsole.dao.SearchDao;

import java.util.List;

public class SearchControl extends Control
{

	public List<Object> search(String text) throws AppException
	{
		if (text == null || text.length() < 3)
			throw new AppException("O texto de busca deve conter pelo menos 3 caracteres.");
		try (SearchDao dao = new SearchDao())
		{
			return dao.search(text);
		}
	}
}
