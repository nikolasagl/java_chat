package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientManager extends Thread {

    private final Server server;
    private final Socket socket;
    private final PrintStream writer;
    public String clientName;
    
    //Contrutor recebe o socket q ele vai administrar
    //Recebe o servidor com quem vai se comunicar
    public ClientManager(Socket socket, Server servidor) throws IOException {
        this.socket = socket;
        this.server = servidor;
        this.writer = new PrintStream(socket.getOutputStream());
        
        //Thread Start pq sou uma thread
        start();
    }
    
    //Ouve tudo o que eh enviado pela view atraves da variavel leitor //LISTENER
    //Trata a mensagem e, de acordo com o que foi enviado pela view define qual funcao chamar no servidor
    @Override
    public void run() {
        try {
            Scanner reader = new Scanner(socket.getInputStream());
            while (reader.hasNextLine()) {
                String message = reader.nextLine();
                String[] result = message.split(":");

                String method = result[0];
                String receiver = (result.length > 1) ? receiver = result[1] : null;
                String msg = (result.length > 2) ? msg = result[2] : null;

                System.err.println("Mensagem recebida pelo servidor: " + message);
                
                if(message.equals("sair")){
                    System.err.println("Entrei no Sair");
                    server.removeClient(this); 
                    
                }else if(method.equals("login")){
                    System.err.println("Entrei no login");                    
                                       
                    String resp = server.tryLogin(receiver.toLowerCase());
                    if(resp.equals("login:true")){
                        this.clientName = receiver;
                        server.listUsers();
                        writer.println("login:true");
                        
                    }else{
                        writer.println("login:false");
                    }
                    
                }else if(receiver.equals("*")){
                    System.err.println("Entrei no Replicar Mensagem");
                    server.replicateMessage(message, clientName);

                }else if((method.equals("mensagem")) && !(receiver.equals("*")) && (msg!=null) && (msg!="")){
                    String[] test = message.split(":");
                    String[] destinos = test[1].split(";");
                    System.err.println("Entrei no Mensagem com Destinatario: " + destinos.length);
                    server.messageReceiver(message, destinos, clientName);
                    
                }else{
                    System.err.println("Nao entrei em porra nenhuma");                                       
                }
            }
        } catch (EOFException ex) {
            //DO NOTHING
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            server.removeClient(this);
        }
    }
    
    //FUNCOES UTILIZADAS PELO SERVIDOR PARA COMUNICACAO COM AS VIEWS
    //Envia a mensagem
    public void sendMessage(String mensagem) {
        writer.println(mensagem);
    }
}
