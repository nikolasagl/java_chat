package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server{

    private ServerSocket server; // Servidor localhost:9999
    private ClientManager client;
    public final ArrayList<ClientManager> users; //Lista que guarda os clientes

    //Construtor
    //Inicia a lista de clientes
    //Inicia o servidor
    public Server(int port) throws IOException{
        users = new ArrayList<ClientManager>();
        listen(port);
    }

    //Inicia o servidor
    //Recebe a conexao do cliente
    //Adiciona cliente a lista de clientes
    private void listen(int port) throws IOException{
        server = new ServerSocket(port);
        System.out.println("Servidor iniciado em " + server);

        while (true){

            Socket clientSocket = server.accept();
            System.out.println("Ligação aceita de " + clientSocket);

            client = new ClientManager(clientSocket, this);
            users.add(client);                        
        }
    }
    
    //Envia a lista de clientes para cada um dos clientes
    public void listUsers(){
        String lista = "lista_usuarios:";
        for (ClientManager cliente: users){
            if(cliente.clientName != null){
                lista += cliente.clientName + ";";
            }                
        }
        
        //Synchronized para sincronizar o acesso a lista de clientes
        synchronized (users){
            for (ClientManager cliente: users){
                if(cliente.clientName != null){
                    cliente.sendMessage(lista);
                }                
            }
        }
    }

    //Replica a mensagem recebida para todos os clientes da lista
    public void replicateMessage(String mensagem, String remetente){
        String[] result = mensagem.split(":");

        //Synchronized para sincronizar o acesso a lista de clientes
        synchronized (users){
            for (ClientManager cliente : users) {
                if(!(cliente.clientName.equals(remetente))){
//                    cliente.enviarMensagem("transmitir:" + "*:" + result[2]); Implementacao conforme protocolo
                    cliente.sendMessage("transmitir:" + remetente + ":" + "*:" + result[2]); //Implementacao conforme chat da sala
                }                    
            }
        }
    }

    //Encontra o destino na lista de clientes e encaminha a ele a mensagem
    public void messageReceiver(String msg, String[] destinos, String remetente){  
        String message = msg.substring(9, msg.length()); 
        String[] result = message.split(":");
        
        for(ClientManager client: users){
            for(String destino: destinos){
                if(client.clientName.equals(destino)){
                    if(destinos.length == 1){
                        client.sendMessage("transmitir:" + remetente + ":" + result[0] + " :" + result[1]);
                    }else{
                        client.sendMessage("transmitir:" + remetente + ":" + result[0] + ":" + result[1]);
                    }                    
                }
            }
        }
    }
    
    //Realiza a verificacao na lista de clientes
    //Se o nome ja estiver cadastrado retorna false
    public String tryLogin(String nome){
        synchronized (users){
            for(ClientManager client: users){
                if(((client.clientName) != null) && (nome.equalsIgnoreCase(client.clientName.toLowerCase()))){
                    return "login:false";
                }
            }
            return "login:true";
        }
    }
    
    //Remove o cliente da lista de clientes
    //Atualiza lista de clientes
    public void removeClient(ClientManager client){
        //Synchronized para sincronizar o acesso a lista de clientes
        synchronized (users){
            System.out.println("Terminando conexao com: " + client);
            users.remove(client);
            System.out.println("Conexoes restantes: " + users.size());
            listUsers();
        }
    }

    //Main
    public static void main(String args[]){
        try {
            new Server(6666);
        } catch (IOException ex){
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
