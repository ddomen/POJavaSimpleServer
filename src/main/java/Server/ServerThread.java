package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

import Dto.*;

/**
 * Thread di gestione delle connessioni del Server
 */
public class ServerThread implements Runnable{
    /**
     * Dataset utilizzato dal server
     */
    protected List<DtoData> dataset;
    /**
     * Informazioni di dataset utilizzate dal server
     */
    protected DtoPackage dtoPackage;
    /**
     * Socket di connessione client - server
     */
    protected ServerSocket socket;
    /**
     * Modalità verbose
     */
    protected boolean verbose;

    /**
     * Genera la socket del server sulla porta specificata
     * @param port porta di ascolto
     * @throws IOException
     */
    public ServerThread(int port) throws IOException { this.socket = new ServerSocket(port); }

    /**
     * Setta la modalità verbose del client
     * @param verbose on/off modalità verbose
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ServerThread SetVerbose(boolean verbose){ this.verbose = verbose; return this; }

    /**
     * Imposta il dataset utilizzato dal server
     * @param dataset dataset da utilizzare
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ServerThread SetData(List<DtoData> dataset){ this.dataset = dataset; return this; }

    /**
     * Imposta le informazioni del dataset utilizzato dal server
     * @param dtoPackage informazioni di dataset da utilizzare
     * @return oggetto this per la concatenzaione (method chaining)
     */
    public ServerThread SetPackage(DtoPackage dtoPackage){ this.dtoPackage = dtoPackage; return this; }

    /**
     * Metodo per l'esecuzione del thread
     */
    @Override
    public void run() {
        while(true){
            Connection connect = null;
            try{
                if(this.verbose){ System.out.println("[" + new Date() + "][SERVER][CONNECTION]: CREAZIONE"); }
                //Genero una connessione in ascolto attendendo un client che si colleghi
                connect = new Connection(this.socket.accept(), this.dtoPackage, this.dataset).SetVerbose(this.verbose);
            }
            catch (Exception ex){
                System.err.println("[" + new Date() + "][SERVER][CONNECTION]: ERRORE CREAZIONE");
                if(this.verbose){ ex.printStackTrace(); }
            }

            if(connect != null){
                //Se la connessione è stata creata genero un nuovo thread di Connessione e rimetto il server in attesa
                //di un nuovo client
                Thread response = new Thread(connect);
                response.start();
            }
        }
    }
}