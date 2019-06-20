package Server;

import java.lang.reflect.Method;
import java.util.List;

import Dto.*;

import javax.swing.*;

public class Controller {
    protected String url;
    protected String method;
    protected ActionResponse response;
    public Controller(String method, String url){
        this.method = method;
        this.url = url;
    }

    public ActionResponse Execute(DtoMetadata metadata, List<DtoDataSet> dataset){
        Method action = null;
        Class[] arguments = new Class[2];
        arguments[0] = DtoMetadata.class;
        arguments[1] = List.class;
        try{ action = this.getClass().getMethod(method.toLowerCase() + url.substring(0, 1).toUpperCase() + url.substring(1).toLowerCase(), arguments); }
        catch(Exception ex){
            try { action = this.getClass().getMethod("NotFound", arguments); }
            catch(Exception ex2){ System.err.println("Impossibile instanziare Action"); }
        }
        try { this.response = (ActionResponse)action.invoke(this, metadata, dataset); }
        catch (Exception ex){ this.response = new ActionResponse("Server Error", 500); }
        return this.response;
    }

    public ActionResponse NotFound(DtoMetadata metadata, List<DtoDataSet> dataset){ return new ActionResponse("Not Found", 404); }

    public ActionResponse getMetadata(DtoMetadata metadata, List<DtoDataSet> dataset){
        if(metadata == null){
            return new ActionResponse("Servizio Non Ancora Disponibile", 503);
        }
        return new ActionResponse(metadata);
    }
}
