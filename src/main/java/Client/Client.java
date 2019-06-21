package Client;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

import Dto.*;
import Utils.*;


/**
 * Classe per creare un piccolo client che recupera il dataset dall'url richiesto (protocolli: [ http, https ])
 */
public class Client {

    /**
     * Url da cui recuperare le informazioni del dataset
     */
    protected String baseUrl;

    /**
     * Modalità verbose
     */
    protected boolean verbose = false;

    /**
     * @param baseUrl Url da cui recuperare le informazioni del dataset
     */
    public Client(String baseUrl){ this.baseUrl = baseUrl; }

    /**
     * Setta la modalità verbose del client
     * @param verbose on/off modalità verbose
     * @return
     */
    public Client SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    /**
     * Effettua una chiamata GET e ne recupera il testo
     * @param url indirizzo a cui effettuare la chiamata
     * @return testo della risposta
     * @throws Exception
     */
    protected String Get(String url) throws Exception { return Get(url, null, null);}

    /**
     * Effettua una chiamata GET e ne recupera il testo
     * @param url indirizzo a cui effettuare la chiamata
     * @param params parametri da mettere in query string
     * @param headers header da aggiungere alla chiamata
     * @return testo della risposta
     * @throws Exception
     */
    protected String Get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        URL _url = new URL(url);
        if(verbose){ System.out.println("[" + new Date() + "][CLIENT][GET][OUT]: " + _url); }
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

        //Simulazione di un browser (stranamente richiesta dal server del dataset)
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        if(headers != null){ for(Map.Entry<String, String> entry : params.entrySet()){ connection.setRequestProperty(entry.getKey(), entry.getValue()); } }

        int status = connection.getResponseCode();
        String response = null;
        if(verbose){ System.out.println("[" + new Date() + "][CLIENT][GET][OUT]: " + status); }
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

    /**
     * Converte una serie di coppie chiavi-valore in una query string
     * @param params serie di coppie chiavi-valore
     * @return query string
     * @throws UnsupportedEncodingException
     */
    protected String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        String result = "";
        for(Map.Entry<String, String> entry  : params.entrySet()){ result += UString.Utf8Encode(entry.getKey()) + "=" + UString.Utf8Encode(entry.getValue()) + "&"; }
        int length = result.length();
        return  length > 0 ? result.substring(0, length - 1) : result;
    }

    /**
     * Recupera le informazioni di indirizzo del dataset e di alcuni metadati
     * @return oggetto di informazioni relative al dataset
     * @throws Exception
     */
    public DtoPackage CollectPackage() throws Exception{
        String requestResult = this.Get(this.baseUrl).replace("\n", "");
        return UObject.fromJSON(requestResult, DtoPackage.class);
    }

    /**
     * Recupera il dataset e lo converte in una lista di dati
     * @param dtoPackage oggetto di informazioni relative al dataset
     * @return dataset (lista di dati)
     * @throws Exception
     */
    public List<DtoData> CollectData(DtoPackage dtoPackage) throws Exception{
        DtoPackageResource source = null;
        for(DtoPackageResource resource: dtoPackage.result.resources){
            if(resource.format.equalsIgnoreCase("csv")){
                source = resource;
                break;
            }
        }
        if(source == null){ throw new Exception("Impossible to find csv resource"); }

        String requestResult = this.Get(source.url);
        return UCsv.Parse(requestResult, DtoData.class);
    }
}