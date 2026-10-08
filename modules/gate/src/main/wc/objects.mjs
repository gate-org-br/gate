export default class Objects
{
	static isEmpty(value)
	{
		return !value && value !== 0
			&& value !== false;
	}

	static compare(s1, s2)
	{
		if (Objects.isEmpty(s1)
			&& Objects.isEmpty(s2))
			return 0;

		if (Objects.isEmpty(s1))
			return 1;

		if (Objects.isEmpty(s2))
			return -1;

		return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
	}
}