package gate.adapter.registrar;

import gate.adapter.collector.Collector;

/**
 * Service provider interface for registering custom collectors.
 * <p>
 * To register custom collectors, implement this interface and declare it in
 * {@code META-INF/services/gate.adapter.registrar.CollectorRegistrar}.
 */
public interface CollectorRegistrar extends Registrar<Collector>
{
}
