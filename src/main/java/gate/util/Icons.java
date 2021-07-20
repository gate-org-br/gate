package gate.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Icons
{

	public static final Icon UNKNOWN
		= new Icon("1006", "??????");

	private final List<Icon> icons;

	private static Icons instance;

	public static Icons getInstance()
	{
		if (instance == null)
			instance = new Icons();
		return instance;
	}

	private Icons()
	{
		List<Icon> models = new ArrayList<>();
		models.add(new Icon("1000", "commit"));
		models.add(new Icon("1001", "hide"));
		models.add(new Icon("1002", "insert"));
		models.add(new Icon("1003", "??????"));
		models.add(new Icon("1006", "??????"));
		models.add(new Icon("1007", "??????"));
		models.add(new Icon("1010", "??????"));
		models.add(new Icon("1011", "??????"));
		models.add(new Icon("1012", "??????"));
		models.add(new Icon("1013", "??????"));
		models.add(new Icon("1014", "??????"));
		models.add(new Icon("1020", "??????"));
		models.add(new Icon("1021", "??????"));
		models.add(new Icon("1022", "??????"));
		models.add(new Icon("1023", "??????"));
		models.add(new Icon("1024", "??????"));
		models.add(new Icon("2000", "closed"));
		models.add(new Icon("2001", "opened"));
		models.add(new Icon("2002", "passwd"));
		models.add(new Icon("2003", "date"));
		models.add(new Icon("2004", "person"));
		models.add(new Icon("2005", "people"));
		models.add(new Icon("2006", "??????"));
		models.add(new Icon("2007", "exit"));
		models.add(new Icon("2008", "home"));
		models.add(new Icon("2009", "??????"));
		models.add(new Icon("200A", "??????"));
		models.add(new Icon("2010", "search"));
		models.add(new Icon("2011", "??????"));
		models.add(new Icon("2012", "??????"));
		models.add(new Icon("2013", "tag"));
		models.add(new Icon("2014", "tags"));
		models.add(new Icon("2015", "??????"));
		models.add(new Icon("2016", "??????"));
		models.add(new Icon("2017", "??????"));
		models.add(new Icon("2018", "??????"));
		models.add(new Icon("2019", "??????"));
		models.add(new Icon("2020", "??????"));
		models.add(new Icon("2021", "report"));
		models.add(new Icon("2022", "filter"));
		models.add(new Icon("2023", "return"));
		models.add(new Icon("2024", "??????"));
		models.add(new Icon("2025", "??????"));
		models.add(new Icon("2026", "delete"));
		models.add(new Icon("2027", "cancel"));
		models.add(new Icon("2030", "??????"));
		models.add(new Icon("2031", "pchart"));
		models.add(new Icon("2032", "lchart"));
		models.add(new Icon("2033", "cchart"));
		models.add(new Icon("2034", "??????"));
		models.add(new Icon("2035", "dwload"));
		models.add(new Icon("2036", "upload"));
		models.add(new Icon("2037", "??????"));
		models.add(new Icon("2038", "??????"));
		models.add(new Icon("2039", "??????"));
		models.add(new Icon("2040", "??????"));
		models.add(new Icon("2041", "??????"));
		models.add(new Icon("2042", "??????"));
		models.add(new Icon("2043", "??????"));
		models.add(new Icon("2044", "??????"));
		models.add(new Icon("2045", "??????"));
		models.add(new Icon("2046", "??????"));
		models.add(new Icon("2047", "??????"));
		models.add(new Icon("2048", "??????"));
		models.add(new Icon("2049", "??????"));
		models.add(new Icon("2050", "??????"));
		models.add(new Icon("2051", "??????"));
		models.add(new Icon("2052", "??????"));
		models.add(new Icon("2053", "??????"));
		models.add(new Icon("2054", "??????"));
		models.add(new Icon("2055", "select"));
		models.add(new Icon("2056", "??????"));
		models.add(new Icon("2057", "update"));
		models.add(new Icon("2058", "??????"));
		models.add(new Icon("2059", "client"));
		models.add(new Icon("2070", "??????"));
		models.add(new Icon("2071", "??????"));
		models.add(new Icon("2072", "??????"));
		models.add(new Icon("2073", "??????"));
		models.add(new Icon("2074", "??????"));
		models.add(new Icon("2075", "??????"));
		models.add(new Icon("2076", "??????"));
		models.add(new Icon("2077", "enter"));
		models.add(new Icon("2078", "leave"));
		models.add(new Icon("2079", "attach"));
		models.add(new Icon("2080", "??????"));
		models.add(new Icon("2081", "??????"));
		models.add(new Icon("2082", "??????"));
		models.add(new Icon("2083", "??????"));
		models.add(new Icon("2084", "??????"));
		models.add(new Icon("2085", "??????"));
		models.add(new Icon("2086", "??????"));
		models.add(new Icon("2087", "??????"));
		models.add(new Icon("2088", "??????"));
		models.add(new Icon("2089", "??????"));
		models.add(new Icon("2090", "??????"));
		models.add(new Icon("2091", "??????"));
		models.add(new Icon("2092", "??????"));
		models.add(new Icon("2093", "??????"));
		models.add(new Icon("2094", "??????"));
		models.add(new Icon("2095", "??????"));
		models.add(new Icon("2096", "??????"));
		models.add(new Icon("2097", "??????"));
		models.add(new Icon("2098", "??????"));
		models.add(new Icon("2099", "??????"));
		models.add(new Icon("2100", "??????"));
		models.add(new Icon("2101", "trophy"));
		models.add(new Icon("2102", "??????"));
		models.add(new Icon("2103", "??????"));
		models.add(new Icon("2104", "??????"));
		models.add(new Icon("2105", "??????"));
		models.add(new Icon("2106", "??????"));
		models.add(new Icon("2107", "??????"));
		models.add(new Icon("2108", "??????"));
		models.add(new Icon("2109", "??????"));
		models.add(new Icon("2110", "??????"));
		models.add(new Icon("2111", "??????"));
		models.add(new Icon("2112", "??????"));
		models.add(new Icon("2113", "??????"));
		models.add(new Icon("2114", "??????"));
		models.add(new Icon("2115", "??????"));
		models.add(new Icon("2116", "??????"));
		models.add(new Icon("2117", "??????"));
		models.add(new Icon("2118", "??????"));
		models.add(new Icon("2119", "??????"));
		models.add(new Icon("2120", "??????"));
		models.add(new Icon("2121", "??????"));
		models.add(new Icon("2122", "??????"));
		models.add(new Icon("2123", "??????"));
		models.add(new Icon("2124", "??????"));
		models.add(new Icon("2125", "??????"));
		models.add(new Icon("2126", "??????"));
		models.add(new Icon("2127", "??????"));
		models.add(new Icon("2128", "??????"));
		models.add(new Icon("2129", "??????"));
		models.add(new Icon("2130", "??????"));
		models.add(new Icon("2131", "??????"));
		models.add(new Icon("2132", "??????"));
		models.add(new Icon("2133", "??????"));
		models.add(new Icon("2134", "??????"));
		models.add(new Icon("2135", "??????"));
		models.add(new Icon("2136", "??????"));
		models.add(new Icon("2137", "??????"));
		models.add(new Icon("2138", "??????"));
		models.add(new Icon("2139", "??????"));
		models.add(new Icon("2140", "??????"));
		models.add(new Icon("2141", "??????"));
		models.add(new Icon("2142", "??????"));
		models.add(new Icon("2143", "??????"));
		models.add(new Icon("2144", "??????"));
		models.add(new Icon("2145", "??????"));
		models.add(new Icon("2146", "??????"));
		models.add(new Icon("2147", "??????"));
		models.add(new Icon("2148", "??????"));
		models.add(new Icon("2149", "??????"));
		models.add(new Icon("2150", "??????"));
		models.add(new Icon("2151", "??????"));
		models.add(new Icon("2152", "??????"));
		models.add(new Icon("2153", "??????"));
		models.add(new Icon("2154", "??????"));
		models.add(new Icon("2155", "??????"));
		models.add(new Icon("2156", "??????"));
		models.add(new Icon("2157", "??????"));
		models.add(new Icon("2158", "??????"));
		models.add(new Icon("2159", "??????"));
		models.add(new Icon("2160", "??????"));
		models.add(new Icon("2161", "??????"));
		models.add(new Icon("2162", "??????"));
		models.add(new Icon("2163", "??????"));
		models.add(new Icon("2164", "??????"));
		models.add(new Icon("2165", "??????"));
		models.add(new Icon("2166", "??????"));
		models.add(new Icon("2167", "time"));
		models.add(new Icon("2168", "??????"));
		models.add(new Icon("2169", "??????"));
		models.add(new Icon("2170", "??????"));
		models.add(new Icon("2171", "??????"));
		models.add(new Icon("2172", "??????"));
		models.add(new Icon("2173", "??????"));
		models.add(new Icon("2174", "??????"));
		models.add(new Icon("2175", "??????"));
		models.add(new Icon("2176", "??????"));
		models.add(new Icon("2177", "??????"));
		models.add(new Icon("2178", "??????"));
		models.add(new Icon("2179", "??????"));
		models.add(new Icon("2180", "??????"));
		models.add(new Icon("2181", "??????"));
		models.add(new Icon("2182", "??????"));
		models.add(new Icon("2183", "??????"));
		models.add(new Icon("2187", "??????"));
		models.add(new Icon("2188", "??????"));
		models.add(new Icon("2189", "prompt"));
		models.add(new Icon("2190", "??????"));
		models.add(new Icon("2191", "??????"));
		models.add(new Icon("2192", "??????"));
		models.add(new Icon("2193", "??????"));
		models.add(new Icon("2194", "??????"));
		models.add(new Icon("2195", "??????"));
		models.add(new Icon("2196", "??????"));
		models.add(new Icon("2197", "??????"));
		models.add(new Icon("2198", "??????"));
		models.add(new Icon("2199", "??????"));
		models.add(new Icon("2200", "??????"));
		models.add(new Icon("2201", "??????"));
		models.add(new Icon("2202", "??????"));
		models.add(new Icon("2203", "??????"));
		models.add(new Icon("2204", "??????"));
		models.add(new Icon("2205", "??????"));
		models.add(new Icon("2206", "??????"));
		models.add(new Icon("2207", "??????"));
		models.add(new Icon("2208", "??????"));
		models.add(new Icon("2209", "??????"));
		models.add(new Icon("2210", "??????"));
		models.add(new Icon("2211", "??????"));
		models.add(new Icon("2212", "??????"));
		models.add(new Icon("2213", "??????"));
		models.add(new Icon("2214", "??????"));
		models.add(new Icon("2215", "??????"));
		models.add(new Icon("2216", "??????"));
		models.add(new Icon("2217", "??????"));
		models.add(new Icon("2218", "pdf"));
		models.add(new Icon("2219", "odt"));
		models.add(new Icon("2220", "doc"));
		models.add(new Icon("2221", "xls"));
		models.add(new Icon("2222", "csv"));
		models.add(new Icon("2223", "??????"));
		models.add(new Icon("2224", "??????"));
		models.add(new Icon("2225", "??????"));
		models.add(new Icon("2226", "??????"));
		models.add(new Icon("2227", "??????"));
		models.add(new Icon("2228", "??????"));
		models.add(new Icon("2229", "??????"));
		models.add(new Icon("2230", "??????"));
		models.add(new Icon("2231", "??????"));
		models.add(new Icon("2232", "??????"));
		models.add(new Icon("2233", "??????"));
		models.add(new Icon("2234", "??????"));
		models.add(new Icon("2235", "??????"));
		models.add(new Icon("2236", "??????"));
		models.add(new Icon("2237", "??????"));
		models.add(new Icon("2238", "??????"));
		models.add(new Icon("2239", "??????"));
		models.add(new Icon("2240", "??????"));
		models.add(new Icon("2241", "??????"));
		models.add(new Icon("2242", "??????"));
		models.add(new Icon("2243", "??????"));
		models.add(new Icon("2244", "achart"));
		models.add(new Icon("2245", "dchart"));
		models.add(new Icon("2246", "bchart"));
		models.add(new Icon("2247", "rchart"));
		models.add(new Icon("2248", "??????"));
		models.add(new Icon("2249", "??????"));
		models.add(new Icon("2250", "??????"));
		models.add(new Icon("2251", "??????"));
		models.add(new Icon("2252", "??????"));
		models.add(new Icon("2253", "??????"));
		models.add(new Icon("2254", "??????"));
		models.add(new Icon("2255", "??????"));
		models.add(new Icon("2256", "dialog"));
		models.add(new Icon("2257", "??????"));
		models.add(new Icon("2258", "??????"));
		models.add(new Icon("2259", "??????"));
		models.add(new Icon("2260", "??????"));
		models.add(new Icon("2261", "island"));
		models.add(new Icon("2262", "??????"));
		models.add(new Icon("2263", "??????"));
		models.add(new Icon("2264", "??????"));
		models.add(new Icon("2265", "??????"));
		models.add(new Icon("2266", "??????"));
		models.add(new Icon("2267", "??????"));
		models.add(new Icon("2268", "??????"));
		models.add(new Icon("2269", "??????"));
		models.add(new Icon("2270", "??????"));
		models.add(new Icon("2271", "??????"));
		models.add(new Icon("2272", "??????"));
		models.add(new Icon("2273", "??????"));
		models.add(new Icon("2274", "??????"));
		models.add(new Icon("2275", "??????"));
		models.add(new Icon("2276", "??????"));
		models.add(new Icon("2277", "??????"));
		models.add(new Icon("2278", "??????"));
		models.add(new Icon("2279", "??????"));
		models.add(new Icon("2280", "??????"));
		models.add(new Icon("2281", "??????"));
		models.add(new Icon("2282", "??????"));
		models.add(new Icon("2283", "??????"));
		models.add(new Icon("3000", "??????"));
		models.add(new Icon("3001", "??????"));
		models.add(new Icon("3002", "??????"));
		models.add(new Icon("3003", "??????"));
		models.add(new Icon("3004", "??????"));
		models.add(new Icon("3005", "??????"));
		models.add(new Icon("3006", "??????"));
		models.add(new Icon("3007", "??????"));
		models.add(new Icon("3008", "??????"));
		models.add(new Icon("3009", "??????"));
		models.add(new Icon("3010", "??????"));
		models.add(new Icon("3011", "??????"));
		models.add(new Icon("3012", "??????"));
		models.add(new Icon("3013", "??????"));
		models.add(new Icon("3014", "??????"));
		models.add(new Icon("3015", "??????"));
		models.add(new Icon("3016", "??????"));
		models.add(new Icon("3017", "??????"));
		models.add(new Icon("3018", "??????"));
		models.add(new Icon("3019", "??????"));
		models.add(new Icon("3020", "save"));
		models.add(new Icon("3021", "??????"));
		models.add(new Icon("3022", "??????"));
		models.add(new Icon("3023", "??????"));
		models.add(new Icon("3024", "??????"));
		models.add(new Icon("3025", "??????"));
		models.add(new Icon("3026", "??????"));
		models.add(new Icon("3027", "??????"));
		models.add(new Icon("3028", "??????"));
		models.add(new Icon("3029", "??????"));
		models.add(new Icon("3030", "??????"));
		models.add(new Icon("3031", "??????"));
		models.add(new Icon("3032", "??????"));
		models.add(new Icon("3033", "??????"));
		models.add(new Icon("3034", "??????"));
		models.add(new Icon("3035", "??????"));
		models.add(new Icon("3036", "??????"));
		models.add(new Icon("3037", "??????"));
		models.add(new Icon("3038", "??????"));
		models.add(new Icon("3039", "??????"));
		models.add(new Icon("3040", "??????"));
		models.add(new Icon("3041", "??????"));
		models.add(new Icon("3042", "??????"));
		models.add(new Icon("3043", "??????"));
		models.add(new Icon("3044", "??????"));
		models.add(new Icon("3045", "??????"));
		models.add(new Icon("3046", "??????"));
		models.add(new Icon("3047", "??????"));
		models.add(new Icon("3048", "??????"));
		models.add(new Icon("3049", "??????"));
		models.add(new Icon("3050", "??????"));
		models.add(new Icon("3051", "??????"));
		models.add(new Icon("3052", "??????"));
		models.add(new Icon("3053", "??????"));
		models.add(new Icon("3054", "??????"));
		this.icons = Collections.unmodifiableList(models);
	}

	public List<Icon> get()
	{
		return icons;
	}

	public Optional<Icon> get(String string)
	{
		if (string.length() == 1)
			return Optional.of(new Icon(string, "??????"));

		for (Icons.Icon icon : Icons.getInstance().get())
			if (icon.getName().equals(string))
				return Optional.of(icon);

		for (Icons.Icon icon : Icons.getInstance().get())
			if (icon.getCode().equals(string))
				return Optional.of(icon);

		return Optional.empty();
	}

	public static Icon getIcon(String string)
	{
		return getInstance().get(string).orElse(UNKNOWN);
	}

	public static class Icon
	{

		private final String code;
		private final String name;

		public Icon(String code, String name)
		{
			this.code = code;
			this.name = name;
		}

		public String getCode()
		{
			return code;
		}

		public String getName()
		{
			return name;
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Icon
				&& Objects.equals(code, ((Icon) obj).code)
				&& Objects.equals(name, ((Icon) obj).name);

		}

		@Override
		public int hashCode()
		{
			return Objects.hashCode(code)
				+ Objects.hashCode(name);
		}

		@Override
		public String toString()
		{
			return "&#X" + getCode() + ";";
		}
	}
}
