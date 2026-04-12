package gate.annotation.authorizationtest;

import gate.annotation.Action;
import gate.annotation.Authorization;
import gate.annotation.Screen;

@Screen("AnnotatedScreen")
public class PackageScreen
{
	@Action("Select")
	public void select()
	{
	}

	public void defaultAction()
	{
	}

	@Authorization(action = "ReplaceAll")
	public void replaced()
	{
	}
}
