package gate.adapter.registrar;


import gate.adapter.columnMapper.ColumnMapper;

import java.util.Map;

/**
 * Registers JDBC column mappers for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 */
public interface ColumnMapperRegistrar extends Registrar<ColumnMapper>
{
	/**
	 * Gets the column mappers registered by this registrar.
	 *
	 * @return map of java types to their associated column mappers
	 */
	@Override
	Map<Class<?>, ColumnMapper> entries();
}