/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.util;

import gate.Doctor;
import gate.Person;
import gate.error.NotFoundException;
import gate.type.Date;
import gate.type.DateInterval;
import gate.type.ID;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.Month;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

/**
 *
 * @author davins
 */
public class GenericUtilsTest
{

	private Doctor doctor;

	@Before
	public void startup()
	{
		doctor = new Doctor();
		doctor.setId(new ID(1));
		doctor.setName("Jonh");
		doctor.setBirthdate(LocalDate.of(1950, Month.MARCH, 1));
		doctor.setContract(new DateInterval(new Date(1, 2, 2000), new Date(1, 2, 2010)));
	}

	@Test
	public void testFindField() throws NotFoundException,
			IllegalArgumentException, IllegalAccessException
	{
		Field field = Reflection
				.findField(Doctor.class, "id")
				.orElseThrow(NotFoundException::new);

		Assert.assertEquals(new ID(1), field.get(doctor));
	}

	@Test
	public void testFindMethod() throws NotFoundException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException
	{
		Method method = Reflection
				.findMethod(Doctor.class, "getId")
				.orElseThrow(NotFoundException::new);

		Assert.assertEquals(new ID(1), method.invoke(doctor));
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

		Assert.assertEquals(new ID(1), method.invoke(doctor));
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

		setter.invoke(doctor, new ID(2));

		Assert.assertEquals(new ID(2), field.get(doctor));
	}
}
