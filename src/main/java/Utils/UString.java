package Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class UString {
    public static String Escape(String string){ return StringEscapeUtils.unescapeJava(string); }

    public static String Utf8Encode(String string) throws UnsupportedEncodingException { return URLEncoder.encode(string, "UTF-8"); }
}
