package gate.adapter.metadata;

import gate.adapter.registry.MetadataRegistry;
import gate.annotation.*;
import gate.icon.Icon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Metadata
{
	Metadata EMPTY = new SimpleMetadata();

	default String name() {return null;}

	default String description() {return null;}


	default String tooltip() {return null;}

	default String placeholder() {return null;}


	default String mask() {return null;}

	default String color() {return null;}

	default String code() {return null;}

	default Icon icon() {return null;}

	default boolean isEmpty()
	{
		return name() == null
		       && description() == null
		       && tooltip() == null
		       && placeholder() == null
		       && mask() == null
		       && color() == null
		       && code() == null
		       && icon() == null;
	}

	class Instances
	{
		private static final Map<Class<?>, Metadata> CACHE
				= new ConcurrentHashMap<>();
	}

	static Metadata getMetadata(Class<?> type)
	{
		return Instances.CACHE.computeIfAbsent(type,
				clazz -> MetadataRegistry.INSTANCE.get(clazz)
						.merge(SimpleMetadata.builder()
								.name(Name.Extractor.extract(type).orElse(null))
								.description(Description.Extractor.extract(type).orElse(null))
								.tooltip(Tooltip.Extractor.extract(type).orElse(null))
								.placeholder(Placeholder.Extractor.extract(type).orElse(null))
								.mask(Mask.Extractor.extract(type).orElse(null))
								.color(Color.Extractor.extract(type).orElse(null))
								.code(Code.Extractor.extract(type).orElse(null))
								.icon(gate.annotation.Icon.Extractor.extract(type).orElse(null))
								.build()));
	}

	default Metadata merge(Metadata override)
	{
		return override != null && !override.isEmpty()
				? SimpleMetadata.builder()
				  .name(override.name() != null ? override.name() : name())
				  .description(override.description() != null ? override.description() : description())
				  .tooltip(override.tooltip() != null ? override.tooltip() : tooltip())
				  .placeholder(override.placeholder() != null ? override.placeholder() : placeholder())
				  .mask(override.mask() != null ? override.mask() : mask())
				  .color(override.color() != null ? override.color() : color())
				  .code(override.code() != null ? override.code() : code())
				  .icon(override.icon() != null ? override.icon() : icon())
				  .build()
				: this;
	}
}
