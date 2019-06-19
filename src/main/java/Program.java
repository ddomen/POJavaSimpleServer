import DTO.*;

import java.util.List;

public class Program {
    public static void main(String[] args){
        Client cli = new Client("https://www.dati.gov.it/api/3/action/package_show?id=32d1d774-f89d-4fdd-ba2a-1466701c4024");
        System.out.println("SERVER - CREAZIONE");
        Server svr = new Server();

        DataSetMetadataDTO metadata = null;
        List<DataSetDTO> dataset = null;
        try {
            System.out.println("DATASET - RECUPERO");
            metadata = cli.CollectMetadata();
            dataset = cli.CollectData(metadata);
            System.out.println("DATASET - RECUPERATO");
        }
        catch(Exception ex){
            System.out.println("Qualcosa è andato storto!");
            ex.printStackTrace();
        }
        if(metadata != null){
            svr.SetMetadata(metadata);
            System.out.println("SERVER - METADATA PRONTO");
        }
        if(dataset != null){
            svr.SetData(dataset);
            System.out.println("SERVER - DATASET PRONTO");
        }
    }
}
