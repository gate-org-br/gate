package gate.type;

import gate.util.Parameters;

public record RequestCommand(String module, String screen, String action)
{

    public RequestCommand
    {
        module = module != null && !module.isBlank() ? module : null;
        screen = screen != null && !screen.isBlank() ? screen : null;
        action = action != null && !action.isBlank() ? action : null;
    }

    public static final RequestCommand DEFAULT
            = new RequestCommand(null, null, null);

    public RequestCommand with(String module, String screen, String action)
    {
        if (this.module != null && !this.module.isBlank())
            return new RequestCommand(
                    "#".equals(this.module) ? module : this.module,
                    "#".equals(this.screen) ? screen : this.screen,
                    "#".equals(this.action) ? action : this.action);

        if (this.screen != null && !this.screen.isBlank())
            return new RequestCommand(
                    module,
                    "#".equals(this.screen) ? screen : this.screen,
                    "#".equals(this.action) ? action : this.action);

        if (this.action != null && !this.action.isBlank())
            return new RequestCommand(
                    module,
                    screen,
                    "#".equals(this.action) ? action : this.action);

        return new RequestCommand(module, screen, action);
    }

    public boolean matches(RequestCommand command)
    {
        if (command.action() != null
                && !command.action().equals(action))
            return false;

        if (command.screen() != null
                && !command.screen().equals(screen))
            return false;

        return command.module() == null
                || command.module().equals(module);

    }

    @Override
    public String toString()
    {
        Parameters parameters = new Parameters();
        parameters.put("MODULE", module());
        parameters.put("SCREEN", screen());
        parameters.put("ACTION", action());
        return parameters.isEmpty() ? "Gate" : "Gate?" + parameters;
    }

    public String toString(Parameters parameters)
    {
        parameters = new Parameters(parameters);
        parameters.put("MODULE", module());
        parameters.put("SCREEN", screen());
        parameters.put("ACTION", action());
        return parameters.isEmpty() ? "Gate" : "Gate?" + parameters;
    }
}
