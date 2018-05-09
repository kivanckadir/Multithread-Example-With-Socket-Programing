package parsers;

public class XmlParser {
	public static String get(String message, String type) {
		int beginIndex, lastIndex;
		String ret;
		
		beginIndex = message.indexOf("<" + type + ">") + type.length()+2;
		lastIndex = message.lastIndexOf("</" + type + ">");
		ret = (String) message.subSequence(beginIndex, lastIndex);
		return ret;
	}
}
