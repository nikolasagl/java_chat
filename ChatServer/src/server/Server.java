package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private ServerSocket servidor; // Servidor localhost:9999
    private ClientManager client;
    public final ArrayList<ClientManager> clientes; //Lista que guarda os clientes

    //Construtor
    //Inicia a lista de clientes
    //Inicia o servidor
    public Server(int port) throws IOException {
        clientes = new ArrayList<ClientManager>();
        listen(port);
    }

    //Inicia o servidor
    //Recebe a conexao do cliente
    //Adiciona cliente a lista de clientes
    private void listen(int port) throws IOException {
        servidor = new ServerSocket(port);
        System.out.println("Servidor iniciado em " + servidor);

        while (true) {

            Socket cliente = servidor.accept();
            System.out.println("Ligação aceita de " + cliente);

            client = new ClientManager(cliente, this);

            clientes.add(client);
            System.out.println(client.clientName);
        }
    }

    //Replica a mensagem recebida para todos os clientes da lista
    public void replicarMensagem(String mensagem) {
        String[] result = mensagem.split(":");

        //Synchronized para sincronizar o acesso a lista de clientes
        synchronized (clientes) {
            for (ClientManager cl : clientes) {
                cl.enviarMensagem(result[0] + ":" + result[2]);
            }
        }
    }

    //Remove o cliente da lista de clientes
    //Chama a funcao fechar do cliente, responsavel por fechar a conexao do socket
    public void removerCliente(ClientManager cliente) {
        //Synchronized para sincronizar o acesso a lista de clientes
        synchronized (clientes) {
            System.out.println("Terminando conexao com " + cliente);
            clientes.remove(cliente);
            System.out.println("Conexoes restantes: " + clientes.size());
            try {
                cliente.fechar();
            } catch (IOException ex) {
                System.out.println("Erro ao desconectar " + cliente);
                System.out.println(ex.getMessage());
            }
        }
    }

    //Envia a lista de clientes para cada um dos clientes
    public void listaUsuario(){
        //Synchronized para sincronizar o acesso a lista de clientes
        synchronized (clientes) {
            for (ClientManager cliente: clientes) {
                if(cliente.clientName != null){
                    cliente.enviarLista(clientes);
                }                
            }
        }
    }

    //Encontra o destino na lista de clientes e encaminha a ele a mensagem
    public void mensagemDestino(String[] result) {
        for(ClientManager cliente: clientes){
            if(cliente.clientName.equals(result[2])){
                String mensagem = result[1] + ":" + result[3];
                cliente.enviarMensagem(mensagem);
            }
        }
    }
    
    //Realiza a verificacao na lista de clientes
    //Se o nome ja estiver cadastrado retorna false
    public Boolean tentaLogar(String nome) {
        synchronized (clientes) {
            for(ClientManager cliente: clientes){
                if(((cliente.clientName) != null) && (nome.equalsIgnoreCase(cliente.clientName.toLowerCase()))){
                    return false;
                }
            }
            return true;
        }
    }

    //Main
    public static void main(String args[]) {
        try {
            new Server(9999);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
