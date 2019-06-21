import Client.*;
import Dto.*;
import Server.*;

import java.util.List;

public class Program {
    public static void main(String[] args){
        Client cli = new Client("https://www.dati.gov.it/api/3/action/package_show?id=32d1d774-f89d-4fdd-ba2a-1466701c4024");
        System.out.println("SERVER - CREAZIONE");
        Server svr = new Server();

        DtoPackage dtoPackage = null;
        List<DtoData> dataset = null;
        try {
            System.out.println("DATASET - RECUPERO");
            dtoPackage = cli.CollectPackage();
            dataset = cli.CollectData(dtoPackage);
            System.out.println("DATASET - RECUPERATO");
        }
        catch(Exception ex){
            System.out.println("Qualcosa è andato storto!");
            ex.printStackTrace();
        }
        if(dtoPackage != null){
            svr.SetPackage(dtoPackage);
            System.out.println("SERVER - PACKAGE PRONTO");
        }
        if(dataset != null){
            svr.SetData(dataset);
            System.out.println("SERVER - DATASET PRONTO");
        }
    }
}
