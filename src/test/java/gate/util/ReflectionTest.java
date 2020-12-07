/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.util;

import gate.Doctor;
import gate.error.NotFoundException;
import gate.type.LocalDateInterval;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.Month;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author davins
 */
public class ReflectionTest
{

	private Doctor doctor;

	@Before
	public void startup()
	{
		doctor = new Doctor();
		doctor.setId(1);
		doctor.setName("Jonh");
		doctor.setBirthdate(LocalDate.of(1950, Month.MARCH, 1));
		doctor.setContract(LocalDateInterval.of(LocalDate.of(2000, 2, 1), LocalDate.of(2010, 2, 1)));
	}

	@Test
	public void testFindField() throws NotFoundException,
		IllegalArgumentException, IllegalAccessException
	{
		Field field = Reflection
			.findField(Doctor.class, "id")
			.orElseThrow(NotFoundException::new);

		Assert.assertEquals(1, field.getInt(doctor));
	}

	@Test
	public void testFindMethod() throws NotFoundException,
		IllegalAccessException, IllegalArgumentException,
		InvocationTargetException
	{
		Method method = Reflection
			.findMethod(Doctor.class, "getId")
			.orElseThrow(NotFoundException::new);

		Assert.assertEquals(1, (int) method.invoke(doctor));
	}

	@Test
	public void testFindGetter() throws NotFoundException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException
	{
		Field field = Reflection
			.findField(Doctor.class, "id")
			.orElseThrow(NotFoundException::new);

		Method method = Reflection.findGetter(field)
			.orElseThrow(NotFoundException::new);

		Assert.assertEquals(1, (int) method.invoke(doctor));
	}

	@Test
	public void testFindSetter() throws NotFoundException, IllegalAccessException,
		IllegalAccessException, IllegalArgumentException, IllegalArgumentException,
		InvocationTargetException
	{
		Field field = Reflection
			.findField(Doctor.class, "id")
			.orElseThrow(NotFoundException::new);

		Method setter = Reflection.findSetter(field)
			.orElseThrow(NotFoundException::new);

		setter.invoke(doctor, 2);

		Assert.assertEquals(2, field.getInt(doctor));
	}

	@Test
	public void testFind() throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException
	{
		Assert.assertEquals(gate.entity.User.class, Reflection.find("gate.entity.User").get());

		Assert.assertEquals(gate.entity.User.class.getDeclaredField("name"), Reflection.find("gate.entity.User:name").get());
		Assert.assertEquals(gate.entity.User.class.getDeclaredMethod("getName"), Reflection.find("gate.entity.User:getName()").get());
	}
}
