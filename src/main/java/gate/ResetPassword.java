package gate;

import gate.entity.User;
import gate.error.*;
import gate.http.BearerAuthorization;
import gate.http.ScreenServletRequest;
import gate.messaging.Messenger;
import gate.type.ID;
import gate.type.mime.MimeMail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.Serial;
import java.io.Writer;
import java.time.Instant;
import java.util.Date;

@MultipartConfig
@WebServlet("/ResetPassword")
public class ResetPassword extends HttpServlet
{

	@Inject
	private PasswordControl control;

	@Inject
	private Messenger messenger;


	@Serial
	private static final long serialVersionUID = 1L;

	private static final SecretKey SECRET = Jwts.SIG.HS256.key().build();

	@Override
	public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setCharacterEncoding("UTF-8");
		httpServletRequest.setCharacterEncoding("UTF-8");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try (Writer writer = response.getWriter())
		{

			try
			{

				User user = control.select(request.getParameter("username"));

				if (user.getEmail() == null)
					throw new BadRequestException(
							"Você não definiu um email para o qual seu token possa ser enviado");

				messenger.post(user.getEmail(), MimeMail.builder()
						.subject("Redefinição de senha")
						.content(gate.type.mime.MimeText.of("Utilize este token para redefinir sua senha: " + createToken(user)))
						.build());

			} catch (BadRequestException | InvalidUsernamePasswordException ex)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write(ex.getMessage());
			} catch (RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Erro de sistema");
			}
		}
	}

	@Override
	public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setCharacterEncoding("UTF-8");
		httpServletRequest.setCharacterEncoding("UTF-8");
		ScreenServletRequest request = new ScreenServletRequest(httpServletRequest);

		try (Writer writer = response.getWriter())
		{

			try
			{
				if (request.getAuthorization() instanceof BearerAuthorization authorization)
				{
					User user = parseToken(authorization.token());
					control.update(user, request.getBody().trim());
				} else
					throw new BadRequestException("Credentials not supplied");

			} catch (BadRequestException | NotFoundException | AuthenticationException ex)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				writer.write(ex.getMessage());
			} catch (RuntimeException ex)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				writer.write("Erro de sistema");
			}
		}
	}

	public static User parseToken(String string) throws InvalidCredentialsException
	{
		try
		{
			Claims claims = Jwts.parser()
					.verifyWith(SECRET)
					.build()
					.parseSignedClaims(string)
					.getPayload();

			return new User().setId(ID.valueOf(claims.get("id", String.class)));
		} catch (RuntimeException ex)
		{
			throw new InvalidCredentialsException(ex.getMessage());
		}
	}

	public static String createToken(User user)
	{
		String credentials = Jwts.builder()
				.claim("id", user.getId().toString())
				.expiration(Date.from(Instant.now().plusSeconds(600)))
				.signWith(SECRET).compact();

		return credentials;
	}

}
