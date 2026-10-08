package gate.lang.constructionStrategy;

import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

abstract class ConstructionStrategyTestSupport
{
	static final LocalDate VALUE2 = LocalDate.of(2, 2, 2);

	@BeforeEach
	void clearCache()
	{
		Cache.INSTANCE.clear();
	}
}
