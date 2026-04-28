package gate.adapter.registrar;

import gate.adapter.converter.AppConverter;
import gate.adapter.converter.Converter;
import gate.entity.App;

import java.util.HashMap;
import java.util.Map;

public class GateConverterRegistrar implements ConverterRegistrar
{

	@Override
	public Map<Class<?>, Converter> entries()
	{
		Map<Class<?>, Converter> registry = new HashMap<>();
		registry.put(App.class, new AppConverter());
		return registry;
	}
}