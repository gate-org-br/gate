package gate.type;

import gate.error.AppException;
import gate.lang.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

public class FieldTest
{
	@Test
	public void testShouldBuildFieldWithSchemaAndValue()
	{
		Field field = Field.builder()
				.id("name")
				.name("Name")
				.required(true)
				.size(Field.Schema.Size.TWO)
				.value(Field.Value.of("Alice"))
				.build();

		Assertions.assertEquals("name", field.schema().id());
		Assertions.assertEquals("Name", field.schema().name());
		Assertions.assertTrue(field.schema().required());
		Assertions.assertEquals(Field.Schema.Size.TWO, field.schema().size());
		Assertions.assertEquals(List.of("Alice"), field.getValue());
	}

	@Test
	public void testShouldSerializeAndParseNewJsonFormat()
	{
		Field source = Field.builder()
				.id("department")
				.name("Department")
				.options(List.of("Sales", "Support"))
				.value(Field.Value.of("Support"))
				.build();

		Field parsed = Field.valueOf(source.toJson());

		Assertions.assertEquals(source.schema(), parsed.schema());
		Assertions.assertEquals(source.getValue(), parsed.getValue());
	}

	@Test
	public void testShouldParseLegacyFlatJsonFormat()
	{
		Field field = Field.valueOf(JsonObject.parse("""
				{
					"id": "name",
					"name": "Name",
					"required": true,
					"value": ["Alice"]
				}
				"""));

		Assertions.assertEquals("name", field.schema().id());
		Assertions.assertEquals("Name", field.schema().name());
		Assertions.assertTrue(field.schema().required());
		Assertions.assertEquals(List.of("Alice"), field.getValue());
	}

	@Test
	public void testShouldValidateValueAgainstSchema()
	{
		Field field = Field.builder()
				.name("Code")
				.required(true)
				.maxlength(4)
				.pattern(Pattern.compile("\\d+"))
				.value(Field.Value.of("1234"))
				.build();

		Assertions.assertDoesNotThrow(() -> {field.validate();});
	}

	@Test
	public void testShouldRejectInvalidValue()
	{
		Field field = Field.builder()
				.name("Code")
				.required(true)
				.maxlength(4)
				.pattern(Pattern.compile("\\d+"))
				.value(Field.Value.of("ABCD"))
				.build();

		Assertions.assertThrows(AppException.class, field::validate);
	}

	@Test
	public void testShouldRejectSchemaMismatch()
	{
		Field schema = Field.builder()
				.id("name")
				.name("Name")
				.required(true)
				.build();

		Field input = Field.builder()
				.id("name")
				.name("Changed")
				.required(true)
				.value(Field.Value.of("Alice"))
				.build();

		Assertions.assertThrows(AppException.class, () -> schema.validate(input));
	}
}
