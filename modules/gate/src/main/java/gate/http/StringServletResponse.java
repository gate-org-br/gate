package gate.http;

import gate.i18n.CurrentLocale;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class StringServletResponse implements HttpServletResponse
{

	private final StringWriter stringWriter = new StringWriter();
	private final PrintWriter writer = new PrintWriter(stringWriter);
	private String contentType = "";
	private String characterEncoding = "UTF-8";

	@Override
	public String toString()
	{
		return stringWriter.toString();
	}

	@Override public PrintWriter getWriter() {return writer;}
	@Override public String getContentType() {return contentType;}
	@Override public void setContentType(String type) {this.contentType = type;}
	@Override public String getCharacterEncoding() {return characterEncoding;}
	@Override public void setCharacterEncoding(String charset) {this.characterEncoding = charset;}
	@Override public ServletOutputStream getOutputStream() {return null;}
	@Override public void setContentLength(int len) {}
	@Override public void setContentLengthLong(long len) {}
	@Override public void setBufferSize(int size) {}
	@Override public void flushBuffer() {}
	@Override public void resetBuffer() {}
	@Override public void reset() {}
	@Override public void setLocale(Locale loc) {}
	@Override public Locale getLocale() {return CurrentLocale.get();}
	@Override public int getBufferSize() {return 0;}
	@Override public boolean isCommitted() {return false;}
	@Override public void addCookie(Cookie cookie) {}
	@Override public void sendError(int sc) {}
	@Override public void sendError(int sc, String msg) {}
	@Override public void sendRedirect(String location) {}
	@Override public void setDateHeader(String name, long date) {}
	@Override public void addDateHeader(String name, long date) {}
	@Override public void setHeader(String name, String value) {}
	@Override public void addHeader(String name, String value) {}
	@Override public void setIntHeader(String name, int value) {}
	@Override public void addIntHeader(String name, int value) {}
	@Override public void setStatus(int sc) {}
	@Override public boolean containsHeader(String name) {return false;}
	@Override public String encodeURL(String url) {return url;}
	@Override public String encodeRedirectURL(String url) {return url;}
	@Override public int getStatus() {return 200;}
	@Override public String getHeader(String name) {return null;}
	@Override public Collection<String> getHeaders(String name) {return List.of();}
	@Override public Collection<String> getHeaderNames() {return List.of();}
}