package Client;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

import Dto.*;
import Utils.*;

public class Client {

    protected String baseUrl;
    protected boolean verbose = false;

    public Client(String baseUrl){ this.baseUrl = baseUrl; }

    public Client SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    protected String Get (String url) throws Exception { return Get(url, null, null);}
    protected String Get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        URL _url = new URL(url);
        if(verbose){ System.out.println("[" + new Date() + "][GET][OUT]: " + _url); }
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
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        if(headers != null){ for(Map.Entry<String, String> entry : params.entrySet()){ connection.setRequestProperty(entry.getKey(), entry.getValue()); } }

        int status = connection.getResponseCode();
        String response = null;
        if(status == 200){
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while((inputLine = inputBuffer.readLine()) != null){ content.append(inputLine + "\n"); }
            inputBuffer.close();
            response = UString.Escape(content.toString());
        }
        else{ throw new Exception("StatusCode: " + status); }
        connection.disconnect();
        return response;
    }

    protected String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        String result = "";
        for(Map.Entry<String, String> entry  : params.entrySet()){ result += UString.Utf8Encode(entry.getKey()) + "=" + UString.Utf8Encode(entry.getValue()) + "&"; }
        int length = result.length();
        return  length > 0 ? result.substring(0, length - 1) : result;
    }

    public DtoPackage CollectPackage() throws Exception{
        String requestResult = this.Get(this.baseUrl).replace("\n", "");
        return UObject.fromJSON(requestResult, DtoPackage.class);
    }

    public List<DtoData> CollectData(DtoPackage dtoPackage) throws Exception{
        DtoPackageResource source = null;
        for(DtoPackageResource resource: dtoPackage.result.resources){
            if(resource.format.equalsIgnoreCase("csv")){
                source = resource;
                break;
            }
        }
        if(source == null){ throw new Exception("Impossible to find csv resurce"); }

        String requestResult = this.Get(source.url);
        return UCsv.Parse(requestResult, DtoData.class);
    }
}