package Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Classe di utilità nella gestione delle stringhe
 */
public final class UString {
    /**
     * Rimuove i caratteri unicode dannosi per le chiamate http
     * @param string stringa da sanare
     * @return stringa sanata
     */
    public static String Escape(String string){ return StringEscapeUtils.unescapeJava(string); }

    /**
     * Codifica una stringa in formato utf-8
     * @param string stringa da codificare
     * @return string codificata
     * @throws UnsupportedEncodingException
     */
    public static String Utf8Encode(String string) throws UnsupportedEncodingException { return URLEncoder.encode(string, "UTF-8"); }
}
