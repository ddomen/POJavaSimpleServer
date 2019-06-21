import java.util.*;

import Client.*;
import Dto.*;
import Server.*;
import Utils.UObject;

public class Program {

    /**
     * Funzione di avvio (main) del programma
     * @param args argomenti passati al programma - se viene passato "dev" come primo argomento il programma verrà eseguito in modalità verbose
     */
    public static void main(String[] args){
        boolean verbose = false;
        //Controllo se ci sono argomenti, nel caso il primo è "dev" allora setto la modalità del programma a verbose
        if(args.length > 0) { verbose = args[0].equalsIgnoreCase("dev"); }

        if(verbose){ System.out.println("[" + new Date() + "][PROGRAM]: STARTED WITH VERBOSE MODE") ; }

        //Registro i Deserializer per parsare i json dei filtri
        UObject.RegisterJsonDeserializer(DtoFilter.Data.class, DtoFilter.Deserializer);
        UObject.RegisterJsonDeserializer(DtoFilter.Stats.class, DtoFilter.Deserializer);

        //Recupero il dataset e le informazioni con un client https
        Client cli = new Client("https://www.dati.gov.it/api/3/action/package_show?id=32d1d774-f89d-4fdd-ba2a-1466701c4024").SetVerbose(verbose);
        System.out.println("[" + new Date() + "][SERVER]: CREAZIONE");

        //Istanzio e lancio il server - finchè non saranno recuperati i dati dal client risponderà con 503
        Server svr = new Server().SetVerbose(verbose).Start();

        DtoPackage dtoPackage = null;
        List<DtoData> dataset = null;
        try {
            System.out.println("[" + new Date() + "][SERVER][DATASET]: RECUPERO");
            //Recupero i dati attraverso il client
            dtoPackage = cli.CollectPackage();
            dataset = cli.CollectData(dtoPackage);
            System.out.println("[" + new Date() + "][SERVER][DATASET]: RECUPERATO");
        }
        catch(Exception ex){
            System.err.println("[" + new Date() + "][CLIENT]: IMPOSSIBILE RECUPERARE I DATI");
            if(verbose){ ex.printStackTrace(); }
        }
        if(dtoPackage != null) {
            //Setto il pacchetto metadati del server
            svr.SetPackage(dtoPackage);
            System.out.println("[" + new Date() + "][SERVER][PACKAGE]: SETTATO");
        }
        if(dataset != null) {
            //Setto il dataset del server
            svr.SetData(dataset);
            System.out.println("[" + new Date() + "][SERVER][DATASET]: SETTATO");

            //Il Server è ora pronto
            if(dtoPackage != null){ System.out.println("[" + new Date() + "][SERVER]: PRONTO"); }
        }

        if(dtoPackage == null || dataset == null){ System.out.println("[" + new Date() + "][PROGRAMMA]: IL PROGRAMMA HA RISCONTRATO UN ERRORE - RIAVVIARE!"); }
    }
}
