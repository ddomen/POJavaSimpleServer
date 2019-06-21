import java.util.*;

import Client.*;
import Dto.*;
import Server.*;
import Utils.UObject;

public class Program {
    public static void main(String[] args){
        boolean verbose = false;
        if(args.length > 0) { verbose = args[0].equalsIgnoreCase("dev"); }

        if(verbose){ System.out.println("[" + new Date() + "][PROGRAM]: STARTED WITH VERBOSE MODE") ; }

        UObject.RegisterJsonDeserializer(DtoFilter.Data.class, DtoFilter.Deserializer);
        UObject.RegisterJsonDeserializer(DtoFilter.Stats.class, DtoFilter.Deserializer);

        Client cli = new Client("https://www.dati.gov.it/api/3/action/package_show?id=32d1d774-f89d-4fdd-ba2a-1466701c4024").SetVerbose(verbose);
        System.out.println("[" + new Date() + "][SERVER]: CREAZIONE");
        Server svr = new Server().SetVerbose(verbose).Start();

        DtoPackage dtoPackage = null;
        List<DtoData> dataset = null;
        try {
            System.out.println("[" + new Date() + "][SERVER][DATASET]: RECUPERO");
            dtoPackage = cli.CollectPackage();
            dataset = cli.CollectData(dtoPackage);
            System.out.println("[" + new Date() + "][SERVER][DATASET]: RECUPERATO");
        }
        catch(Exception ex){
            System.err.println("Qualcosa è andato storto!");
            if(verbose){ ex.printStackTrace(); }
        }
        if(dtoPackage != null) {
            svr.SetPackage(dtoPackage);
            System.out.println("[" + new Date() + "][SERVER][PACKAGE]: SETTATO");
        }
        if(dataset != null) {
            svr.SetData(dataset);
            System.out.println("[" + new Date() + "][SERVER][DATASET]: SETTATO");
            if(dtoPackage != null){
                System.out.println("[" + new Date() + "][SERVER]: PRONTO");
            }
        }
    }
}
