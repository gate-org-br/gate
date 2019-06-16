package gateclient;

import gate.lang.property.Entity;
import gate.lang.template.Template;

public class DaoGenerator
{

	private final DaoGeneratorEntity entity;

	private static final Template DAO = Template.compile(DaoGenerator.class.getResource("DaoGenerator/dao().gtf"));

	private static final Template SELECT = Template.compile(DaoGenerator.class.getResource("DaoGenerator/select().gtf"));
	private static final Template SEARCH = Template.compile(DaoGenerator.class.getResource("DaoGenerator/search().gtf"));
	private static final Template INSERT = Template.compile(DaoGenerator.class.getResource("DaoGenerator/insert().gtf"));
	private static final Template UPDATE = Template.compile(DaoGenerator.class.getResource("DaoGenerator/update().gtf"));
	private static final Template DELETE = Template.compile(DaoGenerator.class.getResource("DaoGenerator/delete().gtf"));

	public DaoGenerator(Class<?> type)
	{
		this.entity = new DaoGeneratorEntity(type);
	}

	public String dao()
	{
		return DAO.evaluate(entity);
	}

	public String search()
	{
		return SEARCH.evaluate(entity);
	}

	public String select()
	{
		return SELECT.evaluate(entity);
	}

	public String insert()
	{
		return INSERT.evaluate(entity);
	}

	public String update()
	{
		return UPDATE.evaluate(entity);
	}

	public String delete()
	{
		return DELETE.evaluate(entity);
	}

	private class DaoGeneratorEntity extends Entity
	{

		public DaoGeneratorEntity(Class<?> type)
		{
			super(type);
		}

		public String dao()
		{
			return DaoGenerator.this.dao();
		}

		public String search()
		{
			return DaoGenerator.this.search();
		}

		public String select()
		{
			return DaoGenerator.this.select();
		}

		public String insert()
		{
			return DaoGenerator.this.insert();
		}

		public String update()
		{
			return DaoGenerator.this.update();
		}

		public String delete()
		{
			return DaoGenerator.this.delete();
		}
	}
}
