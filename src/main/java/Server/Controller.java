package Server;

import java.util.*;
import java.lang.reflect.*;

import Dto.*;
import Utils.UObject;

public class Controller {
    protected String url;
    protected String method;
    protected Map<String, String> parameters;
    protected Map<String, String> headers;
    protected ActionResponse response;
    protected boolean verbose;

    public Controller(String method, String url, Map<String, String> headers, Map<String, String> parameters){
        this.method = method;
        this.url = url;
        this.parameters = parameters;
        this.headers = headers;
    }

    public Controller SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    public ActionResponse Execute(DtoPackage dtoPackage, List<DtoData> dataset){
        if(dtoPackage == null || dataset == null){ return ActionResponse.ServiceUnavailable; }
        Method action = null;
        Class[] arguments = new Class[2];
        arguments[0] = DtoPackage.class;
        arguments[1] = List.class;
        String originalActionName = method.toLowerCase() + (this.url.isEmpty() ? "" : (url.substring(0, 1).toUpperCase() + url.substring(1).toLowerCase()));
        String actionName = this.url.isEmpty() ? "Default" : originalActionName;
        try{ action = this.getClass().getMethod(actionName, arguments); }
        catch(Exception ex){
            try {
                actionName = "NotFound";
                action = this.getClass().getMethod(actionName, arguments);
            }
            catch(Exception ex2){
                System.err.println("[" + new Date() + "][SERVER][CONTROLLER]: IMPOSSIBILE COMPLETARE AZIONE - " + originalActionName);
                if(this.verbose){ ex2.printStackTrace(); }
            }
        }
        try {
            if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONTROLLER][ACTION]: " + actionName + " (" + originalActionName + ")"); }
            this.response = (ActionResponse)action.invoke(this, dtoPackage, dataset);
        }
        catch (Exception ex){
            this.response = ActionResponse.InternalServerError;
            if(this.verbose){
                System.err.println("[" + new Date() + "][SERVER][CONTROLLER][ACTION]: " + actionName + "(" + originalActionName + ")");
                ex.printStackTrace();
            }
        }
        return this.response;
    }

    public ActionResponse NotFound(DtoPackage dtoPackage, List<DtoData> dataset){ return ActionResponse.NotFound; }
    public ActionResponse Default(DtoPackage dtoPackage, List<DtoData> dataset){
        String[] apis = new String[]{ "package", "metadata", "data", "stats" };
        String defaultResponse = "<!DOCTYPE html><html><head></head><body><h1>PATHS:</h1><br/>";
        for(String api : apis){ defaultResponse += "<h3><a href=\"" + api + "\">" + api + "</a></h3>"; }
        defaultResponse += "</body></html>";
        return new ActionResponse(defaultResponse).Html();
    }

    public ActionResponse postPackage(DtoPackage dtoPackage, List<DtoData> dataset){ return this.getPackage(dtoPackage, dataset); }
    public ActionResponse getPackage(DtoPackage dtoPackage, List<DtoData> dataset){ return new ActionResponse(dtoPackage).Json(); }

    public ActionResponse postMetadata(DtoPackage dtoPackage, List<DtoData> dataset){ return this.getMetadata(dtoPackage, dataset); }
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

    public ActionResponse postData(DtoPackage dtoPackage, List<DtoData> dataset) { return this.getData(dtoPackage, dataset); }
    public ActionResponse getData(DtoPackage dtoPackage, List<DtoData> dataset){
        List<DtoData> result = ApplyFilter(dataset);
        if(result == null){ return ActionResponse.BadRequest; }
        return new ActionResponse(result).Json();
    }

    public ActionResponse postStats(DtoPackage dtoPackage, List<DtoData> dataset){ return this.getStats(dtoPackage, dataset); }
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
        catch(IllegalAccessException ex){ return new ActionResponse("Field " + field + " not found!", 400).Html(); }
    }

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
