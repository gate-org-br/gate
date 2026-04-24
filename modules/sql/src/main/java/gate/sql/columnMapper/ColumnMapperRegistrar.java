package gate.sql.columnMapper;

import java.util.Map;

/**
 * Registers JDBC column mappers for java types.
 * <p>
 * Implementations are discovered automatically via {@link java.util.ServiceLoader}.
 */
public interface ColumnMapperRegistrar
{

	/**
	 * Registers column mappers into the provided registry map.
	 *
	 * @param registry map of java types to their associated column mappers
	 */
	void register(Map<Class<?>, ColumnMapper> registry);
}
