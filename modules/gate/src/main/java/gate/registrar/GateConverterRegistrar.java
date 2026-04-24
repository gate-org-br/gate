package gate.registrar;

import gate.converter.AppConverter;
import gate.converter.Converter;
import gate.converter.ConverterRegistrar;
import gate.entity.App;
import java.util.Map;

public class GateConverterRegistrar implements ConverterRegistrar
{

	@Override
	public void register(Map<Class<?>, Converter> registry)
	{
		registry.put(App.class, new AppConverter());
	}
}
