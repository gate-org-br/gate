package gateconsole.contol;

import gate.base.Control;
import gate.entity.Auth;
import gate.error.AppException;

import java.util.Collection;

import gate.constraint.Constraints;
import gate.error.NotFoundException;
import gate.type.ID;
import gateconsole.dao.AuthDao;
import java.util.stream.Stream;

public class AuthControl extends Control
{

	public Collection<Auth> search(Auth auth) throws AppException
	{
		try (AuthDao dao = new AuthDao())
		{
			return dao.search(auth);
		}
	}

	public Auth select(ID id) throws NotFoundException
	{
		try (AuthDao dao = new AuthDao())
		{
			return dao.select(id);
		}
	}

	public void insert(Auth model) throws AppException
	{
		Constraints.validate(model, "mode", "type", "module", "screen", "action");
		if (Stream.of(model.getRole().getId(),
			model.getUser().getId(),
			model.getFunc().getId())
			.filter(e -> e != null).count() != 1)
			throw new AppException("Selecione um usuário, perfil ou função para o acesso.");
		if (model.getUser().getId() != null
			&& model.getType().equals(Auth.Type.PUBLIC))
			throw new AppException("Acessos de usuário não podem ser públicos.");

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

			if (Stream.of(model.getRole().getId(),
				model.getUser().getId(),
				model.getFunc().getId())
				.filter(e -> e != null).count() != 1)
				throw new AppException("Selecione um usuário, perfil ou função para o acesso.");

			if (model.getUser().getId() != null
				&& model.getType().equals(Auth.Type.PUBLIC))
				throw new AppException("Acessos de usuário não podem ser públicos.");
			dao.update(model);
		}
	}

	public void delete(Auth model) throws AppException
	{

		try (AuthDao dao = new AuthDao())
		{
			dao.delete(model);
		}
	}
}
