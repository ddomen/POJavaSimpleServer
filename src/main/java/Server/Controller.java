package Server;

import java.util.*;
import java.lang.reflect.*;

import Dto.*;
import Utils.UObject;

public class Controller {
    protected String url;
    protected String method;
    protected Map<String, String> parameters;
    protected ActionResponse response;

    public Controller(String method, String url, Map<String, String> parameters){
        this.method = method;
        this.url = url;
        this.parameters = parameters;
    }

    public ActionResponse Execute(DtoPackage dtoPackage, List<DtoDataSet> dataset){
        if(dtoPackage == null || dataset == null){ return ActionResponse.ServiceUnavailable; }
        Method action = null;
        Class[] arguments = new Class[2];
        arguments[0] = DtoPackage.class;
        arguments[1] = List.class;
        try{ action = this.getClass().getMethod(method.toLowerCase() + url.substring(0, 1).toUpperCase() + url.substring(1).toLowerCase(), arguments); }
        catch(Exception ex){
            try { action = this.getClass().getMethod("NotFound", arguments); }
            catch(Exception ex2){ System.err.println("Impossibile instanziare Action"); }
        }
        try { this.response = (ActionResponse)action.invoke(this, dtoPackage, dataset); }
        catch (Exception ex){ this.response = ActionResponse.InternalServerError; }
        return this.response;
    }

    public ActionResponse NotFound(DtoPackage dtoPackage, List<DtoDataSet> dataset){ return ActionResponse.NotFound; }

    public ActionResponse getPackage(DtoPackage dtoPackage, List<DtoDataSet> dataset){ return new ActionResponse(dtoPackage); }

    public ActionResponse getMetadata(DtoPackage dtoPackage, List<DtoDataSet> dataset){
        List<DtoMetadata> fields = new ArrayList<DtoMetadata>();
        for(Field field : DtoDataSet.class.getFields()){
            DtoMetadata current = new DtoMetadata();
            current.sourceField = field.getName();
            current.alias = current.sourceField.toLowerCase();
            current.type = field.getType().getName().toLowerCase().replace("java.lang.", "");
            fields.add(current);
        }
        return new ActionResponse(fields);
    }

    public ActionResponse getData(DtoPackage dtoPackage, List<DtoDataSet> dataset){
        List<DtoDataSet> result = dataset;
        if(parameters.containsKey("filter")){
            String filterJson = parameters.get("filter").toLowerCase();
            DtoDataFilter filter = UObject.fromJSON(filterJson, DtoDataFilter.class);
            if(filter == null){ return ActionResponse.BadRequest; }
            result = filter.Apply(result);
        }
        return new ActionResponse(result);
    }
}
