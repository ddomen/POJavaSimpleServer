import DTO.*;

import java.util.List;

public class Program {
    public static void main(String[] args){
        Client cli = new Client("https://www.dati.gov.it/api/3/action/package_show?id=32d1d774-f89d-4fdd-ba2a-1466701c4024");

        try {
            List<DataSetDTO> result = cli.Update();
            System.out.println(result);
        }
        catch(Exception ex){ ex.printStackTrace(); }
    }
}
