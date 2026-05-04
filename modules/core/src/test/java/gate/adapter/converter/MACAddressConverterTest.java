package gate.adapter.converter;

import gate.type.MACAddress;

public class MACAddressConverterTest extends AbstractSimpleConverterTest<MACAddress>
{
	@Override protected Class<MACAddress> getType() {return MACAddress.class;}
	@Override protected MACAddress getValue() {return MACAddress.valueOf("aabb.ccdd.eeff");}
	@Override protected String getString() {return "aabb.ccdd.eeff";}
}
