package model;

public class HexBuilder
{
	private StringBuilder sb;
	public HexBuilder(int red, int green, int blue)
	{
		sb = new StringBuilder();
		toHexStr(red);
		toHexStr(green);
		toHexStr(blue);
	}
	
	private void toHexStr(int val)
	{
		StringBuilder s = new StringBuilder();
		s.append(Integer.toHexString(val));
		if (s.length() < 2) {
		    s.insert(0, '0');
		}
		sb.append(s.toString().toUpperCase());
	}

	public String getHex() {
		return sb.toString();
	}
}
