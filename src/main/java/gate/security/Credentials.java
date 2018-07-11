package gate.security;

import gate.entity.Auth;
import gate.entity.User;
import gate.type.ID;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

public class Credentials
{

	private static final Pattern BEARER
		= Pattern.compile("Bearer (.*)");
	private static final byte[] SECRET = TextCodec.BASE64.decode(
		"kwhwLSdhmZ2gC5NajfdZjd3etENuKYSVPEZ3AVQesoaqKhVJgRTNxNgf2XkSXDll/TOaYj5xVEocvI8vlAqiIg==");

	private Credentials()
	{

	}

	public static User of(String string)
	{
		Claims claims = Jwts.parser()
			.setSigningKey(SECRET)
			.parseClaimsJws(string)
			.getBody();

		User user = new User();
		user.setId(new ID(claims.get("id", String.class)));
		user.setName(claims.get("name", String.class));
		user.getRole().setId(new ID(claims.get("role.id", String.class)));
		user.getRole().setName(claims.get("role.name", String.class));

		for (String row : claims.get("auths", String.class).split(","))
		{
			String[] cols = row.split(":");
			Auth auth = new Auth();
			auth.setMode(Auth.Mode.valueOf(cols[0]));
			auth.setType(Auth.Type.valueOf(cols[1]));
			if (!cols[2].equals("*"))
				auth.setModule(cols[2]);
			if (!cols[3].equals("*"))
				auth.setScreen(cols[3]);
			if (!cols[4].equals("*"))
				auth.setAction(cols[4]);
			user.getAuths().add(auth);
		}

		return user;
	}

	public static String create(User user)
	{
		return Jwts.builder()
			.claim("id", user.getId().toString())
			.claim("name", user.getName())
			.claim("role.id", user.getRole().getId().toString())
			.claim("role.name", user.getRole().getName())
			.claim("auths", user.getAllAuths().stream().map(e -> new StringJoiner(":")
			.add(e.getMode().name())
			.add(e.getType().name())
			.add(e.getModule() != null ? e.getModule() : "*")
			.add(e.getScreen() != null ? e.getScreen() : "*")
			.add(e.getAction() != null ? e.getAction() : "*").toString())
				.collect(Collectors.joining(",")))
			.signWith(SignatureAlgorithm.HS512, SECRET).compact();
	}

	public static Optional<User> of(HttpServletRequest request)
	{
		return Optional
			.ofNullable(request)
			.map(e -> e.getHeader("Authorization"))
			.map(e -> BEARER.matcher(e))
			.filter(e -> e.matches())
			.map(e -> e.group(1))
			.map(e -> Credentials.of(e));
	}
}
