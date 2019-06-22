package Server;

import Utils.UObject;

/**
 * Classe per la generazino di risposte lato server
 */
public class ActionResponse {
    /**
     * stringa da scrivere nel corpo della risposta
     */
    protected String result;
    /**
     * content type da aggiungere agli header
     */
    protected String contentType = "plain/text";
    /**
     * status della risposta
     */
    protected int status = 200;

    /**
     * Genera una risposta
     * @param result corpo del messaggio
     * @param status stato del messaggio
     */
    public ActionResponse(String result, int status){ this.SetResult(result).SetStatus(status); }

    /**
     * Genera una risposta
     * @param result corpo del messaggio
     */
    public ActionResponse(String result){ this.SetResult(result); }

    /**
     * Genera una risposta
     * @param result oggetto corpo del messaggio - verrà deserializzato in stringa json
     * @param status stato del messaggio
     */
    public ActionResponse(Object result, int status){ this.SetResult(result).SetStatus(status); }

    /**
     * Genera una risposta
     * @param result oggetto corpo del messaggio - verrà deserializzato in stringa json
     */
    public ActionResponse(Object result){ this.SetResult(result); }

    /**
     * Setta il corpo del messaggio
     * @param result cortpo del messaggio
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ActionResponse SetResult(String result){ this.result = result; return this;}

    /**
     * Setta il corpo del messaggio
     * @param result oggetto corpo del messaggio - verrà deserializzato in stringa json
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ActionResponse SetResult(Object result){ return this.SetResult(UObject.toJSON(result)); }


    /**
     * Recupera il corpo del messaggio
     * @return corpo del messaggio
     */
    public byte[] GetResultBytes(){ return this.result.getBytes(); }

    /**
     * Setta lo stato del messaggio
     * @param status stato del messaggio
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ActionResponse SetStatus(int status){ this.status = status; return this;}


    /**
     * Recupera lo stato del messaggio
     * @return status
     */
    public int GetStatus(){ return this.status; }

    /**
     * Setta l'header content type del messaggio
     * @param contentType content type del messaggio
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ActionResponse SetContentType(String contentType){ this.contentType = contentType; return this;}


    /**
     * Recupera il content type della risposta
     * @return content type della risposta
     */
    public String GetContentType(){ return this.contentType; }

    /**
     * Setta il content type dell'header del messaggio in application/json
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ActionResponse Json(){ return this.SetContentType("application/json"); }

    /**
     * Setta il content type dell'header del messaggio in text/html
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ActionResponse Html(){ return this.SetContentType("text/html"); }

    /**
     * Recupera la lunghezza da riportare nell'header del messaggio
     * @return lunghezza corpo del messaggio
     */
    public int length(){ return this.result.length(); }

    /**
     * Preset di risposta 200 - OK
     */
    public static ActionResponse Ok = new ActionResponse("Ok").Html();
    /**
     * Preset di risposta 400 - BAD REQUEST
     */
    public static ActionResponse BadRequest = new ActionResponse("Bad Request", 400).Html();
    /**
     * Preset di risposta 404 - NOT FOUND
     */
    public static ActionResponse NotFound = new ActionResponse("Not Found", 404).Html();
    /**
     * Preset di risposta 500 - INTERNAL SERVER ERROR
     */
    public static ActionResponse InternalServerError = new ActionResponse("Internal Server Error", 500).Html();
    /**
     * Preset di risposta 503 - SERVICE UNAVAILABLE
     */
    public static ActionResponse ServiceUnavailable = new ActionResponse("Service Unavailable", 503).Html();
}
