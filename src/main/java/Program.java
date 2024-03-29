import Client.Client;
import Dto.DtoData;
import Dto.DtoFilter;
import Dto.DtoPackage;
import Server.Server;
import Utils.UObject;

import java.io.FileInputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Classe di programma per l'avvio
 */
public class Program {

    /**
     * Funzione di avvio (main) del programma
     * @param args argomenti passati al programma - se viene passato "dev" come primo argomento il programma verrà eseguito in modalità verbose
     */
    public static void main(String[] args){
        Properties prop = new Properties();
        try {
            //Recupero le informazioni dal file di configurazione
            FileInputStream ip = new FileInputStream("./config.properties");
            prop.load(ip);
        }
        catch(Exception ex){
            System.err.println("[" + new Date() +  "][PROGRAM]: IMPOSSIBILE TROVARE FILE DI CONFIGURAZIONE - USCITA");
            return;
        }
        String verboseProp = prop.getProperty("verbose", "false");
        String portProp = prop.getProperty("port", "80");

        boolean verbose = verboseProp.equalsIgnoreCase("true") || verboseProp.equalsIgnoreCase("1");
        //Controllo se ci sono argomenti, nel caso il primo è "dev" allora setto la modalità del programma a verbose
        if(args.length > 0) { verbose = args[0].equalsIgnoreCase("dev"); }

        if(verbose){ System.out.println("[" + new Date() + "][PROGRAM]: AVVIATO IN MODALITA' VERBOSE") ; }

        //Registro i Deserializer per parsare i json dei filtri
        UObject.RegisterJsonDeserializer(DtoFilter.Data.class, DtoFilter.Deserializer);
        //UObject.RegisterJsonDeserializer(DtoFilter.Stats.class, DtoFilter.Deserializer); //(inusato)

        //Recupero il dataset e le informazioni con un client https
        String baseUrl = prop.getProperty("baseUrl");
        Client cli = new Client(baseUrl).SetVerbose(verbose);
        if(verbose){ System.out.println("[" + new Date() + "][PROGRAM][BASEURL]: " + baseUrl); }
        System.out.println("[" + new Date() + "][SERVER]: CREAZIONE");

        //Istanzio e lancio il server - finchè non saranno recuperati i dati dal client risponderà con 503
        int port = 80;
        try{ port = Integer.parseInt(portProp); }
        catch (Exception ex){ port = 80; }
        if(port <= 0){ port = 80; }
        Server svr = new Server(port).SetVerbose(verbose).Start();

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
