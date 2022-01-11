let template = document.createElement("template");
template.innerHTML = `
	<a href='#' data-code='1000'>&#X1000;</a>
	<a href='#' data-code='1001'>&#X1001;</a>
	<a href='#' data-code='1002'>&#X1002;</a>
	<a href='#' data-code='1003'>&#X1003;</a>
	<a href='#' data-code='1006'>&#X1006;</a>
	<a href='#' data-code='1007'>&#X1007;</a>
	<a href='#' data-code='1010'>&#X1010;</a>
	<a href='#' data-code='1011'>&#X1011;</a>
	<a href='#' data-code='1012'>&#X1012;</a>
	<a href='#' data-code='1013'>&#X1013;</a>
	<a href='#' data-code='1014'>&#X1014;</a>
	<a href='#' data-code='1020'>&#X1020;</a>
	<a href='#' data-code='1021'>&#X1021;</a>
	<a href='#' data-code='1022'>&#X1022;</a>
	<a href='#' data-code='1023'>&#X1023;</a>
	<a href='#' data-code='1024'>&#X1024;</a>
	<a href='#' data-code='2000'>&#X2000;</a>
	<a href='#' data-code='2001'>&#X2001;</a>
	<a href='#' data-code='2002'>&#X2002;</a>
	<a href='#' data-code='2003'>&#X2003;</a>
	<a href='#' data-code='2004'>&#X2004;</a>
	<a href='#' data-code='2005'>&#X2005;</a>
	<a href='#' data-code='2006'>&#X2006;</a>
	<a href='#' data-code='2007'>&#X2007;</a>
	<a href='#' data-code='2008'>&#X2008;</a>
	<a href='#' data-code='2009'>&#X2009;</a>
	<a href='#' data-code='200A'>&#X200A;</a>
	<a href='#' data-code='2010'>&#X2010;</a>
	<a href='#' data-code='2011'>&#X2011;</a>
	<a href='#' data-code='2012'>&#X2012;</a>
	<a href='#' data-code='2013'>&#X2013;</a>
	<a href='#' data-code='2014'>&#X2014;</a>
	<a href='#' data-code='2015'>&#X2015;</a>
	<a href='#' data-code='2016'>&#X2016;</a>
	<a href='#' data-code='2017'>&#X2017;</a>
	<a href='#' data-code='2018'>&#X2018;</a>
	<a href='#' data-code='2019'>&#X2019;</a>
	<a href='#' data-code='2020'>&#X2020;</a>
	<a href='#' data-code='2021'>&#X2021;</a>
	<a href='#' data-code='2022'>&#X2022;</a>
	<a href='#' data-code='2023'>&#X2023;</a>
	<a href='#' data-code='2024'>&#X2024;</a>
	<a href='#' data-code='2025'>&#X2025;</a>
	<a href='#' data-code='2026'>&#X2026;</a>
	<a href='#' data-code='2027'>&#X2027;</a>
	<a href='#' data-code='2030'>&#X2030;</a>
	<a href='#' data-code='2031'>&#X2031;</a>
	<a href='#' data-code='2032'>&#X2032;</a>
	<a href='#' data-code='2033'>&#X2033;</a>
	<a href='#' data-code='2034'>&#X2034;</a>
	<a href='#' data-code='2035'>&#X2035;</a>
	<a href='#' data-code='2036'>&#X2036;</a>
	<a href='#' data-code='2037'>&#X2037;</a>
	<a href='#' data-code='2038'>&#X2038;</a>
	<a href='#' data-code='2039'>&#X2039;</a>
	<a href='#' data-code='2040'>&#X2040;</a>
	<a href='#' data-code='2041'>&#X2041;</a>
	<a href='#' data-code='2042'>&#X2042;</a>
	<a href='#' data-code='2043'>&#X2043;</a>
	<a href='#' data-code='2044'>&#X2044;</a>
	<a href='#' data-code='2045'>&#X2045;</a>
	<a href='#' data-code='2046'>&#X2046;</a>
	<a href='#' data-code='2047'>&#X2047;</a>
	<a href='#' data-code='2048'>&#X2048;</a>
	<a href='#' data-code='2049'>&#X2049;</a>
	<a href='#' data-code='2050'>&#X2050;</a>
	<a href='#' data-code='2051'>&#X2051;</a>
	<a href='#' data-code='2052'>&#X2052;</a>
	<a href='#' data-code='2053'>&#X2053;</a>
	<a href='#' data-code='2054'>&#X2054;</a>
	<a href='#' data-code='2055'>&#X2055;</a>
	<a href='#' data-code='2056'>&#X2056;</a>
	<a href='#' data-code='2057'>&#X2057;</a>
	<a href='#' data-code='2058'>&#X2058;</a>
	<a href='#' data-code='2059'>&#X2059;</a>
	<a href='#' data-code='2070'>&#X2070;</a>
	<a href='#' data-code='2071'>&#X2071;</a>
	<a href='#' data-code='2072'>&#X2072;</a>
	<a href='#' data-code='2073'>&#X2073;</a>
	<a href='#' data-code='2074'>&#X2074;</a>
	<a href='#' data-code='2075'>&#X2075;</a>
	<a href='#' data-code='2076'>&#X2076;</a>
	<a href='#' data-code='2077'>&#X2077;</a>
	<a href='#' data-code='2078'>&#X2078;</a>
	<a href='#' data-code='2079'>&#X2079;</a>
	<a href='#' data-code='2080'>&#X2080;</a>
	<a href='#' data-code='2081'>&#X2081;</a>
	<a href='#' data-code='2082'>&#X2082;</a>
	<a href='#' data-code='2083'>&#X2083;</a>
	<a href='#' data-code='2084'>&#X2084;</a>
	<a href='#' data-code='2085'>&#X2085;</a>
	<a href='#' data-code='2086'>&#X2086;</a>
	<a href='#' data-code='2087'>&#X2087;</a>
	<a href='#' data-code='2088'>&#X2088;</a>
	<a href='#' data-code='2089'>&#X2089;</a>
	<a href='#' data-code='2090'>&#X2090;</a>
	<a href='#' data-code='2091'>&#X2091;</a>
	<a href='#' data-code='2092'>&#X2092;</a>
	<a href='#' data-code='2093'>&#X2093;</a>
	<a href='#' data-code='2094'>&#X2094;</a>
	<a href='#' data-code='2095'>&#X2095;</a>
	<a href='#' data-code='2096'>&#X2096;</a>
	<a href='#' data-code='2097'>&#X2097;</a>
	<a href='#' data-code='2098'>&#X2098;</a>
	<a href='#' data-code='2099'>&#X2099;</a>
	<a href='#' data-code='2100'>&#X2100;</a>
	<a href='#' data-code='2101'>&#X2101;</a>
	<a href='#' data-code='2102'>&#X2102;</a>
	<a href='#' data-code='2103'>&#X2103;</a>
	<a href='#' data-code='2104'>&#X2104;</a>
	<a href='#' data-code='2105'>&#X2105;</a>
	<a href='#' data-code='2106'>&#X2106;</a>
	<a href='#' data-code='2107'>&#X2107;</a>
	<a href='#' data-code='2108'>&#X2108;</a>
	<a href='#' data-code='2109'>&#X2109;</a>
	<a href='#' data-code='2110'>&#X2110;</a>
	<a href='#' data-code='2111'>&#X2111;</a>
	<a href='#' data-code='2112'>&#X2112;</a>
	<a href='#' data-code='2113'>&#X2113;</a>
	<a href='#' data-code='2114'>&#X2114;</a>
	<a href='#' data-code='2115'>&#X2115;</a>
	<a href='#' data-code='2116'>&#X2116;</a>
	<a href='#' data-code='2117'>&#X2117;</a>
	<a href='#' data-code='2118'>&#X2118;</a>
	<a href='#' data-code='2119'>&#X2119;</a>
	<a href='#' data-code='2120'>&#X2120;</a>
	<a href='#' data-code='2121'>&#X2121;</a>
	<a href='#' data-code='2122'>&#X2122;</a>
	<a href='#' data-code='2123'>&#X2123;</a>
	<a href='#' data-code='2124'>&#X2124;</a>
	<a href='#' data-code='2125'>&#X2125;</a>
	<a href='#' data-code='2126'>&#X2126;</a>
	<a href='#' data-code='2127'>&#X2127;</a>
	<a href='#' data-code='2128'>&#X2128;</a>
	<a href='#' data-code='2129'>&#X2129;</a>
	<a href='#' data-code='2130'>&#X2130;</a>
	<a href='#' data-code='2131'>&#X2131;</a>
	<a href='#' data-code='2132'>&#X2132;</a>
	<a href='#' data-code='2133'>&#X2133;</a>
	<a href='#' data-code='2134'>&#X2134;</a>
	<a href='#' data-code='2135'>&#X2135;</a>
	<a href='#' data-code='2136'>&#X2136;</a>
	<a href='#' data-code='2137'>&#X2137;</a>
	<a href='#' data-code='2138'>&#X2138;</a>
	<a href='#' data-code='2139'>&#X2139;</a>
	<a href='#' data-code='2140'>&#X2140;</a>
	<a href='#' data-code='2141'>&#X2141;</a>
	<a href='#' data-code='2142'>&#X2142;</a>
	<a href='#' data-code='2143'>&#X2143;</a>
	<a href='#' data-code='2144'>&#X2144;</a>
	<a href='#' data-code='2145'>&#X2145;</a>
	<a href='#' data-code='2146'>&#X2146;</a>
	<a href='#' data-code='2147'>&#X2147;</a>
	<a href='#' data-code='2148'>&#X2148;</a>
	<a href='#' data-code='2149'>&#X2149;</a>
	<a href='#' data-code='2150'>&#X2150;</a>
	<a href='#' data-code='2151'>&#X2151;</a>
	<a href='#' data-code='2152'>&#X2152;</a>
	<a href='#' data-code='2153'>&#X2153;</a>
	<a href='#' data-code='2154'>&#X2154;</a>
	<a href='#' data-code='2155'>&#X2155;</a>
	<a href='#' data-code='2156'>&#X2156;</a>
	<a href='#' data-code='2157'>&#X2157;</a>
	<a href='#' data-code='2158'>&#X2158;</a>
	<a href='#' data-code='2159'>&#X2159;</a>
	<a href='#' data-code='2160'>&#X2160;</a>
	<a href='#' data-code='2161'>&#X2161;</a>
	<a href='#' data-code='2162'>&#X2162;</a>
	<a href='#' data-code='2163'>&#X2163;</a>
	<a href='#' data-code='2164'>&#X2164;</a>
	<a href='#' data-code='2165'>&#X2165;</a>
	<a href='#' data-code='2166'>&#X2166;</a>
	<a href='#' data-code='2167'>&#X2167;</a>
	<a href='#' data-code='2168'>&#X2168;</a>
	<a href='#' data-code='2169'>&#X2169;</a>
	<a href='#' data-code='2170'>&#X2170;</a>
	<a href='#' data-code='2171'>&#X2171;</a>
	<a href='#' data-code='2172'>&#X2172;</a>
	<a href='#' data-code='2173'>&#X2173;</a>
	<a href='#' data-code='2174'>&#X2174;</a>
	<a href='#' data-code='2175'>&#X2175;</a>
	<a href='#' data-code='2176'>&#X2176;</a>
	<a href='#' data-code='2177'>&#X2177;</a>
	<a href='#' data-code='2178'>&#X2178;</a>
	<a href='#' data-code='2179'>&#X2179;</a>
	<a href='#' data-code='2180'>&#X2180;</a>
	<a href='#' data-code='2181'>&#X2181;</a>
	<a href='#' data-code='2182'>&#X2182;</a>
	<a href='#' data-code='2183'>&#X2183;</a>
	<a href='#' data-code='2187'>&#X2187;</a>
	<a href='#' data-code='2188'>&#X2188;</a>
	<a href='#' data-code='2189'>&#X2189;</a>
	<a href='#' data-code='2190'>&#X2190;</a>
	<a href='#' data-code='2191'>&#X2191;</a>
	<a href='#' data-code='2192'>&#X2192;</a>
	<a href='#' data-code='2193'>&#X2193;</a>
	<a href='#' data-code='2194'>&#X2194;</a>
	<a href='#' data-code='2195'>&#X2195;</a>
	<a href='#' data-code='2196'>&#X2196;</a>
	<a href='#' data-code='2197'>&#X2197;</a>
	<a href='#' data-code='2198'>&#X2198;</a>
	<a href='#' data-code='2199'>&#X2199;</a>
	<a href='#' data-code='2200'>&#X2200;</a>
	<a href='#' data-code='2201'>&#X2201;</a>
	<a href='#' data-code='2202'>&#X2202;</a>
	<a href='#' data-code='2203'>&#X2203;</a>
	<a href='#' data-code='2204'>&#X2204;</a>
	<a href='#' data-code='2205'>&#X2205;</a>
	<a href='#' data-code='2206'>&#X2206;</a>
	<a href='#' data-code='2207'>&#X2207;</a>
	<a href='#' data-code='2208'>&#X2208;</a>
	<a href='#' data-code='2209'>&#X2209;</a>
	<a href='#' data-code='2210'>&#X2210;</a>
	<a href='#' data-code='2211'>&#X2211;</a>
	<a href='#' data-code='2212'>&#X2212;</a>
	<a href='#' data-code='2213'>&#X2213;</a>
	<a href='#' data-code='2214'>&#X2214;</a>
	<a href='#' data-code='2215'>&#X2215;</a>
	<a href='#' data-code='2216'>&#X2216;</a>
	<a href='#' data-code='2217'>&#X2217;</a>
	<a href='#' data-code='2218'>&#X2218;</a>
	<a href='#' data-code='2219'>&#X2219;</a>
	<a href='#' data-code='2220'>&#X2220;</a>
	<a href='#' data-code='2221'>&#X2221;</a>
	<a href='#' data-code='2222'>&#X2222;</a>
	<a href='#' data-code='2223'>&#X2223;</a>
	<a href='#' data-code='2224'>&#X2224;</a>
	<a href='#' data-code='2225'>&#X2225;</a>
	<a href='#' data-code='2226'>&#X2226;</a>
	<a href='#' data-code='2227'>&#X2227;</a>
	<a href='#' data-code='2228'>&#X2228;</a>
	<a href='#' data-code='2229'>&#X2229;</a>
	<a href='#' data-code='2230'>&#X2230;</a>
	<a href='#' data-code='2231'>&#X2231;</a>
	<a href='#' data-code='2232'>&#X2232;</a>
	<a href='#' data-code='2233'>&#X2233;</a>
	<a href='#' data-code='2234'>&#X2234;</a>
	<a href='#' data-code='2235'>&#X2235;</a>
	<a href='#' data-code='2236'>&#X2236;</a>
	<a href='#' data-code='2237'>&#X2237;</a>
	<a href='#' data-code='2238'>&#X2238;</a>
	<a href='#' data-code='2239'>&#X2239;</a>
	<a href='#' data-code='2240'>&#X2240;</a>
	<a href='#' data-code='2241'>&#X2241;</a>
	<a href='#' data-code='2242'>&#X2242;</a>
	<a href='#' data-code='2243'>&#X2243;</a>
	<a href='#' data-code='2244'>&#X2244;</a>
	<a href='#' data-code='2245'>&#X2245;</a>
	<a href='#' data-code='2246'>&#X2246;</a>
	<a href='#' data-code='2247'>&#X2247;</a>
	<a href='#' data-code='2248'>&#X2248;</a>
	<a href='#' data-code='2249'>&#X2249;</a>
	<a href='#' data-code='2250'>&#X2250;</a>
	<a href='#' data-code='2251'>&#X2251;</a>
	<a href='#' data-code='2252'>&#X2252;</a>
	<a href='#' data-code='2253'>&#X2253;</a>
	<a href='#' data-code='2254'>&#X2254;</a>
	<a href='#' data-code='2255'>&#X2255;</a>
	<a href='#' data-code='2256'>&#X2256;</a>
	<a href='#' data-code='2257'>&#X2257;</a>
	<a href='#' data-code='2258'>&#X2258;</a>
	<a href='#' data-code='2259'>&#X2259;</a>
	<a href='#' data-code='2260'>&#X2260;</a>
	<a href='#' data-code='2261'>&#X2261;</a>
	<a href='#' data-code='2262'>&#X2262;</a>
	<a href='#' data-code='2263'>&#X2263;</a>
	<a href='#' data-code='2264'>&#X2264;</a>
	<a href='#' data-code='2265'>&#X2265;</a>
	<a href='#' data-code='2266'>&#X2266;</a>
	<a href='#' data-code='2267'>&#X2267;</a>
	<a href='#' data-code='2268'>&#X2268;</a>
	<a href='#' data-code='2269'>&#X2269;</a>
	<a href='#' data-code='2270'>&#X2270;</a>
	<a href='#' data-code='2271'>&#X2271;</a>
	<a href='#' data-code='2272'>&#X2272;</a>
	<a href='#' data-code='2273'>&#X2273;</a>
	<a href='#' data-code='2274'>&#X2274;</a>
	<a href='#' data-code='2275'>&#X2275;</a>
	<a href='#' data-code='2276'>&#X2276;</a>
	<a href='#' data-code='2277'>&#X2277;</a>
	<a href='#' data-code='2278'>&#X2278;</a>
	<a href='#' data-code='2279'>&#X2279;</a>
	<a href='#' data-code='2280'>&#X2280;</a>
	<a href='#' data-code='2281'>&#X2281;</a>
	<a href='#' data-code='2282'>&#X2282;</a>
	<a href='#' data-code='2283'>&#X2283;</a>
	<a href='#' data-code='3000'>&#X3000;</a>
	<a href='#' data-code='3001'>&#X3001;</a>
	<a href='#' data-code='3002'>&#X3002;</a>
	<a href='#' data-code='3003'>&#X3003;</a>
	<a href='#' data-code='3004'>&#X3004;</a>
	<a href='#' data-code='3005'>&#X3005;</a>
	<a href='#' data-code='3006'>&#X3006;</a>
	<a href='#' data-code='3007'>&#X3007;</a>
	<a href='#' data-code='3008'>&#X3008;</a>
	<a href='#' data-code='3009'>&#X3009;</a>
	<a href='#' data-code='3010'>&#X3010;</a>
	<a href='#' data-code='3011'>&#X3011;</a>
	<a href='#' data-code='3012'>&#X3012;</a>
	<a href='#' data-code='3013'>&#X3013;</a>
	<a href='#' data-code='3014'>&#X3014;</a>
	<a href='#' data-code='3015'>&#X3015;</a>
	<a href='#' data-code='3016'>&#X3016;</a>
	<a href='#' data-code='3017'>&#X3017;</a>
	<a href='#' data-code='3018'>&#X3018;</a>
	<a href='#' data-code='3019'>&#X3019;</a>
	<a href='#' data-code='3020'>&#X3020;</a>
	<a href='#' data-code='3021'>&#X3021;</a>
	<a href='#' data-code='3022'>&#X3022;</a>
	<a href='#' data-code='3023'>&#X3023;</a>
	<a href='#' data-code='3024'>&#X3024;</a>
	<a href='#' data-code='3024'>&#X3024;</a>
	<a href='#' data-code='3025'>&#X3025;</a>
	<a href='#' data-code='3026'>&#X3026;</a>
	<a href='#' data-code='3027'>&#X3027;</a>
	<a href='#' data-code='3028'>&#X3028;</a>
	<a href='#' data-code='3029'>&#X3029;</a>
	<a href='#' data-code='3030'>&#X3030;</a>
	<a href='#' data-code='3031'>&#X3031;</a>
	<a href='#' data-code='3032'>&#X3032;</a>
	<a href='#' data-code='3033'>&#X3033;</a>
	<a href='#' data-code='3034'>&#X3034;</a>
	<a href='#' data-code='3035'>&#X3035;</a>
	<a href='#' data-code='3036'>&#X3036;</a>
	<a href='#' data-code='3037'>&#X3037;</a>
	<a href='#' data-code='3038'>&#X3038;</a>
	<a href='#' data-code='3039'>&#X3039;</a>
	<a href='#' data-code='3040'>&#X3040;</a>
	<a href='#' data-code='3041'>&#X3041;</a>
	<a href='#' data-code='3042'>&#X3042;</a>
	<a href='#' data-code='3043'>&#X3043;</a>
	<a href='#' data-code='3044'>&#X3044;</a>
	<a href='#' data-code='3045'>&#X3045;</a>
	<a href='#' data-code='3046'>&#X3046;</a>
	<a href='#' data-code='3047'>&#X3047;</a>
	<a href='#' data-code='3048'>&#X3048;</a>
	<a href='#' data-code='3049'>&#X3049;</a>
	<a href='#' data-code='3050'>&#X3050;</a>
	<a href='#' data-code='3051'>&#X3051;</a>
	<a href='#' data-code='3052'>&#X3052;</a>
	<a href='#' data-code='3053'>&#X3053;</a>
	<a href='#' data-code='3054'>&#X3054;</a>
 <style>:host(*)
{
	width: 100%;
	margin: 8px;
	display: flex;
	overflow: auto;
	flex-wrap: wrap;
	align-items: flex-start;
	justify-content: flex-start;
	background-color: var(--main);
}

a {
	width: 64px;
	speak: none;
	margin: 8px;
	height: 64px;
	display: flex;
	font-size: 28px;
	font-style: normal;
	font-weight: normal;
	align-items: center;
	font-family: 'gate';
	font-variant: normal;
	text-transform: none;
	text-decoration: none;
	justify-content: center;
	background-color: white;
}

a:hover {
	background-color: var(--hovered)
}</style>`;

/* global DateFormat, customElements */

customElements.define('g-icon-selector', class extends HTMLElement
{
	constructor()
	{
		super();
		this.attachShadow({mode: "open"});
		this.shadowRoot.appendChild(template.content.cloneNode(true));

		Array.from(this.shadowRoot.children).forEach(icon =>
		{
			icon.addEventListener("click", event =>
			{
				event.preventDefault();
				let code = icon.getAttribute("data-code");
				this.dispatchEvent(new CustomEvent('selected',
					{detail: {selector: this, icon: code}}));
			});
		});
	}
});
