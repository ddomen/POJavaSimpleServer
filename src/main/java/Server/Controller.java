package Server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import Dto.*;
import Utils.UObject;

public class Controller {
    protected String url;
    protected String method;
    protected ActionResponse response;
    public Controller(String method, String url){
        this.method = method;
        this.url = url;
    }

    public ActionResponse Execute(DtoPackage metadata, List<DtoDataSet> dataset){
        if(metadata == null || dataset == null){ return new ActionResponse("Servizio Non Ancora Disponibile", 503); }
        Method action = null;
        Class[] arguments = new Class[2];
        arguments[0] = DtoPackage.class;
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

    public ActionResponse NotFound(DtoPackage metadata, List<DtoDataSet> dataset){ return new ActionResponse("Not Found", 404); }

    public ActionResponse getFullmetadata(DtoPackage metadata, List<DtoDataSet> dataset){ return new ActionResponse(metadata); }

    public ActionResponse getMetadata(DtoPackage metadata, List<DtoDataSet> dataset){
        List<DtoMetadata> fields = new ArrayList<DtoMetadata>();
        for(Field field : DtoDataSet.class.getFields()){
            DtoMetadata current = new DtoMetadata();
            current.alias = field.getName();
            current.sourceField = current.alias;
            current.type = field.getType().getName().toLowerCase().replace("java.lang.", "");
            fields.add(current);
        }
        return new ActionResponse(fields);
    }
}
