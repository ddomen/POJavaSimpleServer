import javax.xml.ws.http.HTTPException;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    protected String Get (String url) throws IOException { return Get(url, null, null);}
    protected String Get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        URL _url = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) _url.openConnection();

        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);

        if(params != null){
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getParamsString(params));
            out.flush();
            out.close();
        }

        if(headers != null){ for(Map.Entry<String, String> entry : params.entrySet()){ connection.setRequestProperty(entry.getKey(), entry.getValue()); } }

        int status = connection.getResponseCode();
        String response = null;
        if(status == 200){
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while((inputLine = inputBuffer.readLine()) != null){ content.append(inputLine); }
            inputBuffer.close();
            response = content.toString();
        }
        else{ throw new HTTPException(status); }
        connection.disconnect();
        return response;
    }

    protected String utf8Encode(String str) throws UnsupportedEncodingException{ return URLEncoder.encode(str, "UTF-8"); }

    protected String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        String result = "";
        for(Map.Entry<String, String> entry  : params.entrySet()){
            result += utf8Encode(entry.getKey()) + "=" + utf8Encode(entry.getValue()) + "&";
        }
        int length = result.length();

        return  length > 0 ? result.substring(0, length - 1) : result;
    }
}
