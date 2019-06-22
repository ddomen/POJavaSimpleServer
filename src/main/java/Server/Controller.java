package Server;

import java.util.*;
import java.lang.reflect.*;

import Dto.*;
import Utils.UObject;

/**
 * Classe per la generazione di risposte a partire dai parametri di connessione
 */
public class Controller {
    /**
     * Indirizzo della richiesta
     */
    protected String url;
    /**
     * Metodo della richiesta (supportati: GET, POST)
     */
    protected String method;
    /**
     * Paramteri della richiesta
     */
    protected Map<String, String> parameters;
    /**
     * Headers della richiesta
     */
    protected Map<String, String> headers;
    /**
     * Risposta da recapitare alla connessione
     */
    protected ActionResponse response;
    /**
     * Modalità verbose
     */
    protected boolean verbose;

    /**
     * Genera un controller per gestire la risposta ad una connessione
     * @param method metodo della richiesta
     * @param url url della richiesta
     * @param headers headers della richiesta
     * @param parameters parametri della richiesta
     */
    public Controller(String method, String url, Map<String, String> headers, Map<String, String> parameters){
        this.method = method;
        this.url = url;
        this.parameters = parameters;
        this.headers = headers;
    }

    /**
     * Setta la modalità verbose del client
     * @param verbose on/off modalità verbose
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public Controller SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    /**
     * Elabora la richiesta utilizzando uno specifico dataset
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse Execute(DtoPackage dtoPackage, List<DtoData> dataset){
        //Finché il server non ha recuperato i dati torna 503
        if(dtoPackage == null || dataset == null){ return ActionResponse.ServiceUnavailable; }
        Method action = null;
        Class[] arguments = new Class[2];
        arguments[0] = DtoPackage.class;
        arguments[1] = List.class;
        //Costruisco il nome del metodo da recuperare nel seguente modo: metodoUrl (ad es. GET /package => getPackage)
        String originalActionName = method.toLowerCase() + (this.url.isEmpty() ? "" : (url.substring(0, 1).toUpperCase() + url.substring(1).toLowerCase()));
        //Se l'url è vuoto ("/") l'action diventa quella di default (Index)
        String actionName = this.url.isEmpty() ? "Index" : originalActionName;

        if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONTROLLER][ACTION]: " + actionName + " (" + originalActionName + ")"); }
        if(this.url.isEmpty()){ return this.Index(dtoPackage, dataset); }

        try{ action = this.getClass().getMethod(actionName, arguments); }
        catch(Exception ex){
            //Se non è stata trovata la action si torna 404 - NotFound
            return this.NotFound(dtoPackage, dataset);
        }
        try {
            //Esecuzzione della action
            if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONTROLLER][ACTION]: " + actionName + " (" + originalActionName + ")"); }
            this.response = (ActionResponse)action.invoke(this, dtoPackage, dataset);
        }
        catch (Exception ex){
            //Se la action va in errore si torna errore lato server (500)
            if(this.verbose){
                System.err.println("[" + new Date() + "][SERVER][CONTROLLER][ACTION]: " + actionName + "(" + originalActionName + ")");
                ex.printStackTrace();
            }
            return ActionResponse.InternalServerError;
        }
        return this.response;
    }

    /**
     * Genera una risposta 404 - Not Found
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse NotFound(DtoPackage dtoPackage, List<DtoData> dataset){ return ActionResponse.NotFound; }

    /**
     * Genera una risposta di default (pagina home)
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse Index(DtoPackage dtoPackage, List<DtoData> dataset){
        String[] apis = new String[]{ "package", "metadata", "data", "stats" };
        String defaultResponse = "<!DOCTYPE html><html><head></head><body><h1>PATHS:</h1><br/>";
        for(String api : apis){ defaultResponse += "<h3><a href=\"" + api + "\">" + api + "</a></h3>"; }
        defaultResponse += "</body></html>";
        return new ActionResponse(defaultResponse).Html();
    }

    /**
     * Genera una risposta ad una richiesta POST /package
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse postPackage(DtoPackage dtoPackage, List<DtoData> dataset){ return this.getPackage(dtoPackage, dataset); }
    /**
     * Genera una risposta ad una richiesta GET /package
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse getPackage(DtoPackage dtoPackage, List<DtoData> dataset){ return new ActionResponse(dtoPackage).Json(); }

    /**
     * Genera una risposta ad una richiesta POST /metadata
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse postMetadata(DtoPackage dtoPackage, List<DtoData> dataset){ return this.getMetadata(dtoPackage, dataset); }
    /**
     * Genera una risposta ad una richiesta GET /metadata
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse getMetadata(DtoPackage dtoPackage, List<DtoData> dataset){
        List<DtoMetadata> fields = new ArrayList<DtoMetadata>();
        for(Field field : DtoData.class.getFields()){
            DtoMetadata current = new DtoMetadata();
            current.sourceField = field.getName();
            current.alias = current.sourceField.toLowerCase();
            current.type = field.getType().getName().toLowerCase().replace("java.lang.", "");
            fields.add(current);
        }
        return new ActionResponse(fields).Json();
    }

    /**
     * Genera una risposta ad una richiesta POST /data params: [ filter? ]
     * Nei parametri è possibile aggiungere il campo "filter" per filtrare il dataset
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse postData(DtoPackage dtoPackage, List<DtoData> dataset) { return this.getData(dtoPackage, dataset); }
    /**
     * Genera una risposta ad una richiesta GET /data params: [ filter? ]
     * Nei parametri è possibile aggiungere il campo "filter" per filtrare il dataset
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse getData(DtoPackage dtoPackage, List<DtoData> dataset){
        List<DtoData> result = ApplyFilter(dataset);
        if(result == null){ return ActionResponse.BadRequest; }
        return new ActionResponse(result).Json();
    }

    /**
     * Genera una risposta ad una richiesta POST /data stats: [ field, filter? ]
     * Nei parametri è necessario fornire il campo "field" che matcha con un campo del dataset
     * Inoltre è possibile aggiungere il campo "filter" per filtrare il dataset
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse postStats(DtoPackage dtoPackage, List<DtoData> dataset){ return this.getStats(dtoPackage, dataset); }
    /**
     * Genera una risposta ad una richiesta GET /data stats: [ field, filter? ]
     * Nei parametri è necessario fornire il campo "field" che matcha con un campo del dataset
     * Inoltre è possibile aggiungere il campo "filter" per filtrare il dataset
     * @param dtoPackage informazioni sul dataset
     * @param dataset dataset
     * @return risposta elaborata da scrivere sulla connessione
     */
    public ActionResponse getStats(DtoPackage dtoPackage, List<DtoData> dataset){
        if(!parameters.containsKey("field")){ return ActionResponse.BadRequest; }

        List<DtoData> filtered = ApplyFilter(dataset);
        if(filtered == null){ return ActionResponse.BadRequest; }

        String field = parameters.get("field").toUpperCase();
        try {
            DtoStats result = DtoStats.Calculate(field, filtered);
            if (result == null) { return ActionResponse.BadRequest; }
            return new ActionResponse(result).Json();
        }
        catch (NoSuchFieldException ex){ return new ActionResponse("Field " + field + " not found!", 400).Html(); }
        catch (IllegalAccessException ex){ return new ActionResponse("Field " + field + " not found!", 400).Html(); }
    }

    /**
     * Applica il filtro contenuto nei parametri della richiesta ad un determinato dataset
     * @param dataset dataset da filtrare
     * @return dataset filtrato
     */
    protected List<DtoData> ApplyFilter(List<DtoData> dataset) {
        List<DtoData> result = dataset;
        if(parameters.containsKey("filter")){
            String filterJson = parameters.get("filter").toLowerCase();
            DtoFilter filter = UObject.fromJSON(filterJson, DtoFilter.Data.class);
            if(filter == null){ return null; }
            try{ result = filter.Apply(result); }
            catch (IllegalAccessException ex){ return null; }
        }
        return result;
    }
}
