package partable.webdb.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

public class Util
{
	public static Logger log;
	
	public static String streamToString(InputStream stream) throws IOException
	{
        InputStreamReader reader = new InputStreamReader(stream);
        StringBuilder sb = new StringBuilder();

        char[] buf = new char[100];
        int len;
        while ( (len = reader.read(buf)) != -1 ){
        	sb.append(buf, 0, len);
        }
        return sb.toString();
	}
	
	/**
	 * Parse a 'concatenated' date string, used in bank mutation exports:
	 * Example: parseConcatDateString("20090402") returns april 2nd, 2009 
	 */
	public static Date parseConcatDateString(String dateStr) 
	{
		String year = dateStr.substring(0, 4);
		String month = dateStr.substring(4, 6);
		String day = dateStr.substring(6, 8);

		Calendar cal = Calendar.getInstance();
		// Note that January has index 0
		cal.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day), 0, 0, 0);
			
		return cal.getTime();
	}

    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, allowing JSON
     * text to be delivered in HTML. In JSON text, a string cannot contain a
     * control character or an unescaped quote or backslash.
     * @param string A String
     * @return  A String correctly formatted for insertion in a JSON text.
     */
	public static String quoteJsonString(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         b;
        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuffer sb = new StringBuffer(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') ||
                               (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
	}

	public static String stringHashToJson(Set<String> set) {
		
		if (set == null)
			return "[]";
		
		StringBuilder json = new StringBuilder();
		boolean first = true;
		json.append("[");
		for(String k : set) {
			if(!first) json.append(",");
			first = false;
			json.append(Util.quoteJsonString(k) + "\n");
		}
		json.append("]");

		return json.toString();
	}
}
