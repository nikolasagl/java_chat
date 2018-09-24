package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientManager extends Thread {

    private final Server servidor;
    private final Socket socket;
    private final PrintStream escritor;
    public String clientName;
    
    //Contrutor recebe o socket q ele vai administrar
    //Recebe o servidor com quem vai se comunicar
    public ClientManager(Socket socket, Server servidor) throws IOException {
        this.socket = socket;
        this.servidor = servidor;
        this.escritor = new PrintStream(socket.getOutputStream());
        
        //Thread Start pq sou uma thread
        start();
    }
    
    //Ouve tudo o que eh enviado pela view atraves da variavel leitor //LISTENER
    //Trata a mensagem e, de acordo com o que foi enviado pela view define qual funcao chamar no servidor
    @Override
    public void run() {
        try {
            Scanner leitor = new Scanner(socket.getInputStream());
            while (leitor.hasNextLine()) {
                String mensagem = leitor.nextLine();
                String[] result = mensagem.split(":");

                String metodo = result[0];
                String destinatario = null;
                String msg = null;
                
                if(result.length > 1){
                    destinatario = result[1];
                }

                if(result.length > 2){
                    msg = result[2];
                }

                System.err.println(metodo + ":" + destinatario + ":" + msg);
                
                if(mensagem.equals("sair")){
                    System.err.println("Entrei no Sair");
                    escritor.println("sair");
                    servidor.removerCliente(this); 
                    
//                }else if(metodo.equals("lista_usuarios")){
//                    System.err.println("Entrei no lista_usuario");
//                    servidor.listaUsuario();

                }else if((result.length > 1) && (metodo.equals("login"))){
                    System.err.println("Entrei no clientName");
                    
                    String aux_nome = result[1];
                    aux_nome = aux_nome.toLowerCase();
                    
                    String resp = servidor.tentaLogar(aux_nome);
                    String[] teste = resp.split(":");
                    if(teste[1].equals("true")){
                        this.clientName = result[1];
                        escritor.println("true");
                        
                    }else{
                        escritor.println("false");
                    }
                    
                }else if(destinatario.equals("*")){
                    System.err.println("Entrei no Replicar Mensagem");
                    servidor.replicarMensagem(mensagem);

                }else if((result.length >= 3) && (metodo.equals("mensagem"))){
                    System.err.println("Entrei no Mensagem com Destinatario");
                    servidor.mensagemDestino(result, clientName);

                    for (String aux: result) {
                        System.err.println(aux);
                    }
                    
                }else{
                    System.err.println("Nao entrei em porra nenhuma");                                       
                }
            }
        } catch (EOFException ex) {
            //DO NOTHING
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            servidor.removerCliente(this);
        }
    }
    
    //FUNCOES UTILIZADAS PELO SERVIDOR PARA COMUNICACAO COM AS VIEWS
    //Envia a mensagem
    public void enviarMensagem(String mensagem) {
        escritor.println(mensagem);
    }
    
    //Encerra a conexao entre o socket e o servidor
    public void fechar() throws IOException {
        socket.close();
    }
}
