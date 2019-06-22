package Client;

import Dto.DtoData;
import Dto.DtoPackage;
import Dto.DtoPackageResource;
import Utils.UCsv;
import Utils.UObject;
import Utils.UString;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;


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
     * @return oggetto this per la concatenzaione (method chaining)
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

        //Controllo il protocollo della connessione e genero la relativa connection
        if(_url.getProtocol() == "https"){ connection = (HttpsURLConnection) _url.openConnection(); }
        else{ connection = (HttpURLConnection) _url.openConnection(); }

        //Impsoto il metodo a GET e il follow sui reindirizzamenti
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);

        //Se sono presenti parametri li aggiungo al corpo della chiamata
        if(params != null){
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(getParamsString(params));
            out.flush();
            out.close();
        }

        //Simulazione di un browser (stranamente richiesta dal server del dataset)
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36");
        //Charset accettato per parsare la risposta
        connection.setRequestProperty("Accept-Charset", "UTF-8");

        //Se sono presenti headers li aggiungo in testata alla chimata
        if(headers != null){ for(Map.Entry<String, String> entry : params.entrySet()){ connection.setRequestProperty(entry.getKey(), entry.getValue()); } }

        //Effettuo la chiamata recuperando lo status code
        int status = connection.getResponseCode();
        String response = null;
        if(verbose){ System.out.println("[" + new Date() + "][CLIENT][GET][OUT]: " + status); }
        if(status == 200){
            //SUCCESSO -> leggo il corpo della risposta
            BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while((inputLine = inputBuffer.readLine()) != null){ content.append(inputLine + "\n"); }
            inputBuffer.close();
            response = UString.Escape(content.toString());
        }
        else{
            //FALLIMENTO -> langio un errore con lo status code
            throw new Exception("StatusCode: " + status);
        }
        //Chiudo la connessione
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
        //Concatenazione: "chiave=valore&"
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
        //A causa della presenza di caratteri unicode particolari è necessario rimuovere gli \n dalla risposta per
        //poter parsare correttamente il json
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
        //Cerco nelle informazioni del dataset la risorsa in formato csv
        DtoPackageResource source = null;
        for(DtoPackageResource resource: dtoPackage.result.resources){
            if(resource.format.equalsIgnoreCase("csv")){
                source = resource;
                break;
            }
        }
        //Se non ho trovato la risorsa lancio un errore
        if(source == null){ throw new Exception("Impossible to find csv resource"); }

        //Recupero la risorsa csv
        String requestResult = this.Get(source.url);
        //Parsing della risorsa
        return UCsv.Parse(requestResult, DtoData.class);
    }
}