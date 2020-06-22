package gate.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PasswordGenerator
{

	private static final String DIGIT = "0123456789";
	private static final String OTHER = "!@#$%&*=-?";
	private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
	private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String EVERY = DIGIT + OTHER + LOWER + UPPER;

	public static String generate()
	{
		Random random = new Random();
		List<Character> password = new ArrayList<>();

		password.add(UPPER.charAt(random.nextInt(UPPER.length())));
		password.add(LOWER.charAt(random.nextInt(LOWER.length())));
		password.add(DIGIT.charAt(random.nextInt(DIGIT.length())));
		password.add(OTHER.charAt(random.nextInt(OTHER.length())));

		for (int i = 4; i < 8; i++)
			password.add(EVERY.charAt(random.nextInt(EVERY.length())));

		Collections.shuffle(password);
		return password.stream().map(e -> String.valueOf(e)).collect(Collectors.joining());
	}
}
