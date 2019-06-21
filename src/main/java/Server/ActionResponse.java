package Server;

import Utils.UObject;

public class ActionResponse {
    public String result;
    public String contentType = "plain/text";
    public int status = 200;
    public int length;

    public ActionResponse(String result, int status){ this.SetResult(result).SetStatus(status); }
    public ActionResponse(String result){ this.SetResult(result); }
    public ActionResponse(Object result, int status){ this.SetResult(result).SetStatus(status); }
    public ActionResponse(Object result){ this.SetResult(result); }

    public ActionResponse SetResult(String result){ this.result = result; this.length = this.result.length(); return this;}
    public ActionResponse SetResult(Object result){ return this.SetResult(UObject.toJSON(result)); }
    public ActionResponse SetStatus(int status){ this.status = status; return this;}
    public ActionResponse SetContentType(String contentType){ this.contentType = contentType; return this;}
    public ActionResponse Json(){ return this.SetContentType("application/json"); }
    public ActionResponse Html(){ return this.SetContentType("text/html"); }

    public static ActionResponse Ok = new ActionResponse("Ok").Html();
    public static ActionResponse BadRequest = new ActionResponse("Bad Request", 400).Html();
    public static ActionResponse NotFound = new ActionResponse("Not Found", 404).Html();
    public static ActionResponse InternalServerError = new ActionResponse("Internal Server Error", 500).Html();
    public static ActionResponse ServiceUnavailable = new ActionResponse("Service Unavailable", 503).Html();
}
