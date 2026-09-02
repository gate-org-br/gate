package gate.handler;

import gate.Progress;
import gate.error.AppError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ApplicationScoped
public class URLBuilderHandler implements Handler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object value) throws AppError
    {
        response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        response.setHeader("Location",
                response.encodeRedirectURL(request.getContextPath() + "/" + value));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       Progress progress, Object value) throws AppError
    {
        progress.result("text/plain",
                null, value.toString());
    }
}
