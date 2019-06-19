import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import javax.net.ssl.HttpsURLConnection;

import DTO.DTO;

public class Client {

    protected String baseUrl;

    public Client(String baseUrl){ this.baseUrl = baseUrl; }

    protected String Get (String url) throws IOException, Exception { return Get(url, null, null);}
    protected String Get(String url, Map<String, String> params, Map<String, String> headers) throws IOException, Exception {
        URL _url = new URL(url);

        HttpURLConnection connection;

        if(_url.getProtocol() == "https"){ connection = (HttpsURLConnection) _url.openConnection(); }
        else{ connection = (HttpURLConnection) _url.openConnection(); }

        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);

        if(params != null){
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getParamsString(params));
            out.flush();
            out.close();
        }

        //Simulazione di un browser
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
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
        else{ throw new Exception("StatusCode: " + status); }
        connection.disconnect();
        return response;
    }

    protected String utf8Encode(String str) throws UnsupportedEncodingException{ return URLEncoder.encode(str, "UTF-8"); }

    protected String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        String result = "";
        for(Map.Entry<String, String> entry  : params.entrySet()){ result += utf8Encode(entry.getKey()) + "=" + utf8Encode(entry.getValue()) + "&"; }
        int length = result.length();
        return  length > 0 ? result.substring(0, length - 1) : result;
    }

    protected <ReturnType extends DTO> ReturnType Convert(Map<String, Object> object, Class<ReturnType> dtoClass)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        ReturnType dto = dtoClass.getDeclaredConstructor(int.class).newInstance();
        for(Map.Entry<String, Object> entry : object.entrySet()){ dto.Set(entry.getKey(), entry.getValue()); }
        return dto;
    }

//    public <ReturnType extends DTO> ReturnType Update() throws Exception{
//        String result = this.Get(this.baseUrl);
//
//    }

    public String Update() throws IOException, Exception{ return this.Get(this.baseUrl); }
}
