package Server;

import Utils.UObject;

public class ActionResponse {
    public String result;
    public int status;
    public int length;
    public ActionResponse(String result, int status){
        this.result = result;
        this.status = status;
        this.length = this.result.length();
    }
    public ActionResponse(String result){
        this.result = result;
        this.status = 200;
        this.length = this.result.length();
    }
    public ActionResponse(Object result, int status){
        this.result = UObject.toJSON(result);
        this.status = status;
        this.length = this.result.length();
    }
    public ActionResponse(Object result){
        this.result = UObject.toJSON(result);
        this.status = 200;
        this.length = this.result.length();
    }

    public static ActionResponse Ok = new ActionResponse("Ok");
    public static ActionResponse BadRequest = new ActionResponse("Bad Request", 400);
    public static ActionResponse NotFound = new ActionResponse("Not Found", 404);
    public static ActionResponse InternalServerError = new ActionResponse("Internal Server Error", 500);
    public static ActionResponse ServiceUnavailable = new ActionResponse("Service Unavailable", 503);
}
