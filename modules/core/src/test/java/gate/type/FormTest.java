package gate.type;

import gate.error.AppException;
import gate.lang.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FormTest
{
	@Test
	public void testShouldSerializeAndParseFormWithNewFieldFormat()
	{
		Form form = new Form()
				.add(Field.builder()
						.id("name")
						.name("Name")
						.required(true)
						.value(Field.Value.of("Alice"))
						.build())
				.add(Field.builder()
						.id("department")
						.name("Department")
						.options(List.of("Sales", "Support"))
						.value(Field.Value.of("Support"))
						.build());

		Form parsed = Form.valueOf(form.toJson());

		Assertions.assertEquals(form.getFields().size(), parsed.getFields().size());
		Assertions.assertEquals(form.getFields().get(0).schema(), parsed.getFields().get(0).schema());
		Assertions.assertEquals(form.getFields().get(0).getValue(), parsed.getFields().get(0).getValue());
		Assertions.assertEquals(form.getFields().get(1).schema(), parsed.getFields().get(1).schema());
		Assertions.assertEquals(form.getFields().get(1).getValue(), parsed.getFields().get(1).getValue());
	}

	@Test
	public void testShouldParseObjectAsSimpleValueForm()
	{
		Form form = Form.valueOf(JsonObject.parse("""
				{
					"name": "Alice",
					"department": "Support"
				}
				"""));

		Assertions.assertEquals(2, form.getFields().size());
		Assertions.assertEquals("name", form.getFields().get(0).schema().name());
		Assertions.assertEquals(List.of("Alice"), form.getFields().get(0).getValue());
		Assertions.assertEquals("department", form.getFields().get(1).schema().name());
		Assertions.assertEquals(List.of("Support"), form.getFields().get(1).getValue());
	}

	@Test
	public void testShouldValidateAllFields()
	{
		Form form = new Form()
				.add(Field.builder()
						.name("Name")
						.required(true)
						.value(Field.Value.of("Alice"))
						.build());

		Assertions.assertDoesNotThrow(() -> {form.validate();});
	}

	@Test
	public void testShouldValidateInputFormAgainstSchemaForm()
	{
		Form schema = new Form()
				.add(Field.builder()
						.id("name")
						.name("Name")
						.required(true)
						.build());

		Form input = new Form()
				.add(Field.builder()
						.id("name")
						.name("Name")
						.required(true)
						.value(Field.Value.of("Alice"))
						.build());

		Assertions.assertDoesNotThrow(() -> schema.validate(input));
	}

	@Test
	public void testShouldRejectInputFormWithSchemaMismatch()
	{
		Form schema = new Form()
				.add(Field.builder()
						.id("name")
						.name("Name")
						.required(true)
						.build());

		Form input = new Form()
				.add(Field.builder()
						.id("name")
						.name("Name")
						.required(false)
						.value(Field.Value.of("Alice"))
						.build());

		Assertions.assertThrows(AppException.class, () -> schema.validate(input));
	}
}
