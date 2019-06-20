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
        this.result = UObject.JSON(result);
        this.status = status;
        this.length = this.result.length();
    }
    public ActionResponse(Object result){
        this.result = UObject.JSON(result);
        this.status = 200;
        this.length = this.result.length();
    }
}
