package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import Dto.*;
import Utils.UObject;


/**
 * Classe per la modellazione e gestione delle connessioni
 */
public class Connection implements Runnable{
    /**
     * socket di conessione server-client
     */
    protected Socket connect;
    /**
     * Buffer di lettura in input
     */
    protected BufferedReader input;
    /**
     * Buffer di scrittura header in output
     */
    protected PrintWriter output;
    /**
     * Buffer di scrittura body in output
     */
    protected BufferedOutputStream dataOutput;
    /**
     * dataset ricevuto dal server
     */
    protected List<DtoData> dataset;
    /**
     * informazioni di dataset ricevute dal server
     */
    protected DtoPackage dtoPackage;
    /**
     * modalità verbose
     */
    protected boolean verbose;
    /**
     * Identificativo della connessione
     */
    protected long ID;

    /**
     * Contatore identificativi di connessione
     */
    private static long CONNECTION_ID = 1;

    /**
     * Crea un modello di connessione server - client
     * @param connect socket di connessione
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     */
    public Connection(Socket connect, DtoPackage dtoPackage, List<DtoData> dataset){
        this.connect = connect;
        this.dtoPackage = dtoPackage;
        this.dataset = dataset;
        this.ID = CONNECTION_ID++;
    }

    /**
     * Setta la modalità verbose del client
     * @param verbose on/off modalità verbose
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public Connection SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    public void run() {
        try {
            if (this.verbose) { System.out.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: RICEZIONE"); }
            this.input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            this.output = new PrintWriter(connect.getOutputStream());
            this.dataOutput = new BufferedOutputStream(connect.getOutputStream());

            String input = this.input.readLine();
            if (input != null) {
                StringTokenizer parse = new StringTokenizer(input);
                String method = parse.nextToken().toUpperCase();
                String[] uri = parse.nextToken().toLowerCase().substring(1).split("\\?");
                String url = uri[0];
                String params = "";
                if (uri.length > 1) { params = uri[1]; }

                Map<String, String> headers = this.GetHeaders();

                Controller cnt = new Controller(method, url, headers, GetParameters(params)).SetVerbose(this.verbose);

                Response(cnt.Execute(this.dtoPackage, this.dataset));
            }
        }
        catch (Exception ex){
            System.err.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: ERRORE RICEZIONE");
            if(this.verbose){ ex.printStackTrace(); }
        }
        finally { this.CloseBuffers(); }
    }

    /**
     * Recupera tutti i parametri relativi alla connessione
     * @param params query string
     * @return mappa chiave - valore dei parametri di connessione
     * @throws IOException
     */
    protected Map<String, String> GetParameters(String params) throws IOException {
        Map<String, String> parameterMap = this.ParseParameters(params);
        String body = this.GetBody();

        if(body.length() > 0) {
            Map<String, String> bodyMap = this.ParseBody(body);
            parameterMap.putAll(bodyMap);
        }
        return parameterMap;
    }

    /**
     * Recupera il corpo del messaggio
     * @return corpo del messaggio
     * @throws IOException
     */
    protected String GetBody() throws IOException{
        StringBuilder body = new StringBuilder();
        while(this.input.ready()){ body.append((char) this.input.read()); }
        return body.toString();
    }

    /**
     * Recupera gli header della connessione in una serie di chiavi - valore
     * @return serie chiavi - valore degli header della connessione
     * @throws IOException
     */
    protected Map<String, String> GetHeaders() throws IOException{
        Map<String, String> result = new HashMap<String, String>();

        String currentLine = this.input.readLine();
        while(currentLine != null && !currentLine.isEmpty()){
            String[] head = currentLine.split(":", 2);
            result.put(head[0].trim(), head[1].trim());
            currentLine = this.input.readLine();
        }

        return result;
    }

    /**
     * Recupera i parametri dal corpo della connessione in formato json
     * @param body corpo del messaggio
     * @return mappa chiave - valore del corpo della connessione
     */
    protected Map<String, String> ParseBody(String body){
        Map<String, String> result = new HashMap<String, String>();
        if(!body.isEmpty()){
            Map<String, Object> bodyMap = UObject.fromJSON(body, Map.class);
            for(Map.Entry<String, Object> entry : bodyMap.entrySet()){
                Object value = entry.getValue();
                result.put(entry.getKey().trim(), (value == null ? "null" : value.toString()).trim());
            }
        }
        return result;
    }

    /**
     * Converte una query string in una mappa chiave - valore
     * @param parameters query string
     * @return mappa chiave - valore della query string
     */
    protected Map<String, String> ParseParameters(String parameters){
        Map<String, String> result = new HashMap<String, String>();
        if(parameters.length() == 0){ return result; }
        String[] params = parameters.split("\\&");
        for(String param : params){
            String[] pair = param.split("\\=");
            String key = pair[0];
            String value = "";
            if(pair.length > 1){ value = URLDecoder.decode(pair[1]); }
            result.put(key, value);
        }
        return result;
    }

    /**
     * Chiude tutti i Buffer relativi alla connessione, infine il socket della connessione
     * @return oggetto this per la concatenzaione (method chaining)
     */
    protected Connection CloseBuffers(){
        try {
            if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: CHIUSURA"); }
            this.input.close();
            this.output.close();
            this.dataOutput.close();
            this.connect.close();
            if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: CHIUSA"); }
        }
        catch (Exception ex) {
            System.err.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: ERRORE CHIUSURA");
            if(this.verbose){ ex.printStackTrace(); }
        }
        return this;
    }

    /**
     * Mappatura degli stati possibili nella risposta da aggiungere all'header
     */
    protected HashMap<Integer, String> StatusCodes = new HashMap<Integer, String>(){
        {
            put(200, "Ok");
            put(400, "Bad Request");
            put(404, "Not Found");
            put(500, "Internal Server Error");
            put(503, "Service Unavailable");
        }
    };

    /**
     * Scrive una risposta sul Buffer di output
     * @param response ActionResponse contenente la risposta
     * @return oggetto this per la concatenzaione (method chaining)
     * @throws IOException
     */
    protected Connection Response(ActionResponse response) throws IOException {
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: INVIO - " + response.status); }
        this.output.println("HTTP/1.1 " + response.status + " " + StatusCodes.get(response.status));
        this.output.println("Server: Java HTTP Server");
        this.output.println("Date: " + new Date());
        this.output.println("Content-type: " + response.contentType);
        this.output.println("Content-length: " + response.length());
        this.output.println();
        this.output.flush();

        this.dataOutput.write(response.result.getBytes(), 0, response.length());
        this.dataOutput.flush();
        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONNECTION][" + ID + "]: INVIATA"); }
        return this;
    }
}
