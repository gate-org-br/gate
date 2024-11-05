package gate.security;

import java.net.Socket;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

public class UnsecureHttpClient
{

	private static final TrustManager TRUST_MANAGER = new X509ExtendedTrustManager()
	{
		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return new X509Certificate[]
			{
			};
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
		{
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
		{
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
		{
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
		{
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
		{
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
		{
		}
	};

	public static HttpClient.Builder newBuilder()
	{
		try
		{
			var sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, new TrustManager[]
			{
				TRUST_MANAGER
			}, new SecureRandom());

			SSLParameters sslParameters = new SSLParameters();
			sslParameters.setEndpointIdentificationAlgorithm("");

			HttpClient.Builder builder = HttpClient.newBuilder();
			builder.sslContext(sslContext);
			builder.sslParameters(sslParameters);
			return builder;
		} catch (NoSuchAlgorithmException | KeyManagementException ex)
		{
			throw new java.lang.SecurityException(ex);
		}

	}
}
