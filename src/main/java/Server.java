import DTO.DataSetDTO;
import DTO.DataSetMetadataDTO;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Server {
    protected final int port;
    protected Main runner;
    protected Thread thread;

    public Server(){
        this.port = 80;
        this.Start();
    }
    public Server(List<DataSetDTO> dataset, int port) {
        this.port = port;
        this.Start();
    }

    protected Server Start(){
        try{
            this.runner = new Main(this.port);
            this.thread = new Thread(this.runner);
            thread.start();
        }
        catch (Exception ex){ System.err.println("Impossibile avviare il server!"); }
        return this;
    }

    public Server Stop(){
        if(this.thread != null && this.thread.isAlive()) { this.thread.stop(); }
        return this;
    }

    public Server SetData(List<DataSetDTO> dataset){ this.runner.SetData(dataset); return this; }
    public Server SetMetadata(DataSetMetadataDTO metadata){ this.runner.SetMetadata(metadata); return this; }

    protected class Main implements Runnable{
        protected List<DataSetDTO> dataset;
        protected DataSetMetadataDTO metadata;
        protected ServerSocket socket;
        public Main(int port) throws IOException{ this.socket = new ServerSocket(port); }

        protected Main SetData(List<DataSetDTO> dataset){ this.dataset = dataset; return this; }
        protected Main SetMetadata(DataSetMetadataDTO metadata){ this.metadata = metadata; return this; }

        public void run() {
            while(true){
                Connection connect = null;
                try{ connect = new Connection(this.socket.accept(), this.metadata, this.dataset); }
                catch (Exception ex){ System.err.println("Connessione rifiutata!"); }

                if(connect != null){
                    Thread response = new Thread(connect);
                    response.start();
                }
            }
        }
    }

    protected class Connection implements Runnable{
        protected Socket connect;
        protected BufferedReader input;
        protected PrintWriter output;
        protected BufferedOutputStream dataOutput;
        protected List<DataSetDTO> dataset;
        protected DataSetMetadataDTO metadata;

        public Connection(Socket connect, DataSetMetadataDTO metadata, List<DataSetDTO> dataset){
            this.connect = connect;
            this.metadata = metadata;
            this.dataset = dataset;
        }

        public void run() {
            try {
                this.input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
                this.output = new PrintWriter(connect.getOutputStream());
                this.dataOutput = new BufferedOutputStream(connect.getOutputStream());

                String input = this.input.readLine();
                StringTokenizer parse = new StringTokenizer(input);
                String method = parse.nextToken().toUpperCase();
                String url = parse.nextToken().toLowerCase().substring(1);

                Controller cnt = new Controller(method, url);

                Response(cnt.Execute(this.metadata, this.dataset));

                System.out.println("URL: " + url);
            }
            catch (Exception ex){ System.err.println("Impossibile stabilire la connessione");  }
            finally { this.CloseBuffers(); }
        }

        protected Connection CloseBuffers(){
            try {
                this.input.close();
                this.output.close();
                this.dataOutput.close();
                this.connect.close();
            }
            catch (Exception e) { System.err.println("Errore nella chiusura degli stream di connessione"); }
            return this;
        }

        protected HashMap<Integer, String> StatusCodes = new HashMap<Integer, String>(){
            {
                put(200, "OK");
                put(404, "NOT FOUND");
                put(500, "INTERNAL SERVER ERROR");
            }
        };

        protected Connection Response(ActionResponse response) throws IOException{
            this.output.println("HTTP/1.1 " + response.status + " " + StatusCodes.get(response.status));
            this.output.println("Server: Java HTTP Server");
            this.output.println("Date: " + new Date());
            this.output.println("Content-type: application/json" );
            this.output.println("Content-length: " + response.length);
            this.output.println();
            this.output.flush();

            this.dataOutput.write(response.result.getBytes(), 0, response.length);
            this.dataOutput.flush();
            return this;
        }
    }

    protected class ActionResponse{
        public String result;
        public int status;
        public int length;
        public ActionResponse(String result, int status){
            this.result = result;
            this.status = status;
            this.length = this.result.length();
        }
        public ActionResponse(String result){
            this.result = result;
            this.status = 200;
            this.length = this.result.length();
        }
    }

    protected class Controller{
        protected String url;
        protected String method;
        protected ActionResponse response;
        public Controller(String method, String url){
            this.method = method;
            this.url = url;
        }

        public ActionResponse Execute(DataSetMetadataDTO metadata, List<DataSetDTO> dataset){
            Method action = null;
            Class[] arguments = new Class[2];
            arguments[0] = DataSetMetadataDTO.class;
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

        public ActionResponse NotFound(DataSetMetadataDTO metadata, List<DataSetDTO> dataset){ return new ActionResponse("Not Found", 404); }

        public ActionResponse getMetadata(DataSetMetadataDTO metadata, List<DataSetDTO> dataset){ return new ActionResponse("METADATA :D"); }
    }
}
