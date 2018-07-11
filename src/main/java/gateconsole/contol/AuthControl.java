package gateconsole.contol;

import gate.base.Control;
import gate.entity.Auth;
import gate.error.AppException;

import java.util.Collection;

import gate.constraint.Constraints;
import gate.error.NotFoundException;
import gateconsole.dao.AuthDao;

public class AuthControl extends Control
{

	public Collection<Auth> search(Auth auth) throws AppException
	{
		try (AuthDao dao = new AuthDao())
		{
			return dao.search(auth);
		}
	}

	public Auth select(Auth auth) throws AppException
	{
		try (AuthDao dao = new AuthDao())
		{
			return dao.select(auth).orElseThrow(NotFoundException::new);
		}
	}

	public void insert(Auth model) throws AppException
	{
		Constraints.validate(model, "mode", "type", "module", "screen", "action");
		if (model.getRole().getId() == null && model.getUser().getId() == null)
			throw new AppException(
					"Selecione um usuário ou grupo para o acesso.");
		if (model.getUser().getId() != null
				&& model.getType().equals(Auth.Type.PUBLIC))
			throw new AppException(
					"Acessos de usuário não podem ser públicos.");

		try (AuthDao dao = new AuthDao())
		{
			dao.insert(model);
		}
	}

	public void update(Auth model) throws AppException
	{

		try (AuthDao dao = new AuthDao())
		{
			Constraints.validate(model, "mode", "type", "module", "screen", "action");
			if (model.getRole().getId() == null
					&& model.getUser().getId() == null)
				throw new AppException(
						"Selecione um usuário ou grupo para o acesso.");
			if (model.getUser().getId() != null
					&& model.getType().equals(Auth.Type.PUBLIC))
				throw new AppException(
						"Acessos de usuário não podem ser públicos.");
			if (!dao.update(model))
				throw new AppException("Tentativa de alterar um ACESSO inexistente.");
		}
	}

	public void delete(Auth model) throws AppException
	{

		try (AuthDao dao = new AuthDao())
		{
			if (!dao.delete(model))
				throw new AppException("Tentativa de remover um ACESSO inexistente.");
		}
	}
}
