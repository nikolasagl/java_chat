package client;

import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlueLightLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaOrangeMetallicLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaStandardLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaWhiteVisionLookAndFeel;
import de.javasoft.synthetica.dark.SyntheticaDarkLookAndFeel;
import java.awt.ComponentOrientation;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

public class Client extends javax.swing.JFrame {
    
    private Socket client;
    private Scanner leitor;
    private PrintStream escritor;
    private String clientName;
    public static final ArrayList<String> lista = new ArrayList();
    
    //Construtor inicia os componentes do JFrame
    public Client() {
        initComponents();
        btnAtualizar.setVisible(false);
    }

    //Funcao chamada com o click do botao conectar
    //Cria a conexao do socket com o servidor
    //Define um nome ao socket "clientName" e loga no servidor com esse nome
    public void ligar() {
        try {
            final String aux_nome = txtNomeUsuario.getText().trim();
            
            txtAreaConversa.append("Conectando...\n");
            
            String host = txtServerIp.getText().trim();            
            int port = Integer.parseInt(txtServerPorta.getText().trim());
            client = new Socket(host, port);

            leitor = new Scanner(client.getInputStream());
            escritor = new PrintStream(client.getOutputStream());
            
            escritor.println("login:" + aux_nome);
            
            //Thread que ouve as respostas do servidor que chegam atraves do clientManager //LISTENER
            //Trata a mensagem recebida e, de acordo com o que foi enviado pelo clientManager define quais acoes realizar
            new Thread(){
                @Override
                public void run() {
                    try {
                        while (leitor.hasNextLine()) {
                            String msg = leitor.nextLine();
                            String[] result = msg.split(":");
                            System.err.println("Mensagem recebida do servidor: " + msg);
                            
                            if(msg.toLowerCase().startsWith("transmitir:")){
                                String mensagem = msg.substring(11, msg.length());   
                                if (result[1].equals("*")){
                                    String aux = msg.substring(13, msg.length());
                                    txtAreaConversa.append("Todos:" + aux + "\n");
                                }else{
                                    String[] aux = mensagem.split(":");
                                    txtAreaConversa.append("De: " + aux[0] + " | Para: " + aux[1] + ": " + aux[2] + "\n");    
                                }   
                                
                            }else if(msg.toLowerCase().startsWith("lista_usuarios:")){
                                String[] aux = msg.split(":");
                                
                                String[] nomes = aux[1].split(";");
                                for(String nome: nomes){
                                    if(nome != null){
                                        addLista(nome);                                             
                                    }
                                } 
                                //Atualiza a lista de clientes
                                btnAtualizar.doClick();
                                lista.clear();
                                
                            }else if(msg.toLowerCase().equals("login:false")){
                               System.err.println("Nome de usuario ja existe... " + msg);                               
                               txtAreaConversa.append("Nome de usuario ja existe... Escolha outro!" + "\n");    
                               client.close();
                               
                            }else if(msg.toLowerCase().equals("login:true")){
                               clientName = aux_nome;
                               txtAreaConversa.append("Usuario conectado...\n");
                               System.err.println("Usuario conectado...");
                               txtNomeUsuario.setEditable(false);
                               txtServerIp.setEditable(false);
                               txtServerPorta.setEditable(false);
                               btnConectar.setEnabled(false);
                               
                            }else{
                               System.err.println("Mensagem Invalida do Servidor");
                            }                            
                        }
                    } catch (IOException ex) {
                        txtAreaConversa.append("<-cliente->:" + ex.getMessage() + "\n");
                    }
                }

                private void addLista(String nome) {
                    if(nome != null){
                        ListModel model = (ListModel) listaOnline.getModel();
                        lista.add(nome);
                    }
                }                
            }.start(); //Starta a Thread    
            
        } catch (IOException ex) {
            txtAreaConversa.append("<-cliente->:" + ex.getMessage() + "\n");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpLigacao = new javax.swing.JPanel();
        jlblEndereco = new javax.swing.JLabel();
        txtServerIp = new javax.swing.JTextField();
        jlblPorto = new javax.swing.JLabel();
        txtServerPorta = new javax.swing.JFormattedTextField();
        btnConectar = new javax.swing.JButton();
        jlblNick = new javax.swing.JLabel();
        txtNomeUsuario = new javax.swing.JTextField();
        btnSair = new javax.swing.JButton();
        jpMensagens = new javax.swing.JPanel();
        jscpScrollMensagens = new javax.swing.JScrollPane();
        txtAreaConversa = new javax.swing.JTextArea();
        btnLimpar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listaOnline = new javax.swing.JList<>();
        btnAtualizar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtAreaEnviar = new javax.swing.JTextField();
        btnEnviar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpLigacao.setBorder(javax.swing.BorderFactory.createTitledBorder("Conexão"));

        jlblEndereco.setText("IP:");

        jlblPorto.setText("Porta:");

        txtServerPorta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        jlblNick.setText("Nome:");

        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpLigacaoLayout = new org.jdesktop.layout.GroupLayout(jpLigacao);
        jpLigacao.setLayout(jpLigacaoLayout);
        jpLigacaoLayout.setHorizontalGroup(
            jpLigacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpLigacaoLayout.createSequentialGroup()
                .addContainerGap()
                .add(jlblNick)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtNomeUsuario, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jlblEndereco)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtServerIp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jlblPorto)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtServerPorta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnConectar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(btnSair, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpLigacaoLayout.setVerticalGroup(
            jpLigacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpLigacaoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btnConectar)
                .add(txtServerPorta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblPorto)
                .add(txtServerIp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblEndereco)
                .add(jlblNick)
                .add(txtNomeUsuario, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(btnSair))
        );

        jpMensagens.setBorder(javax.swing.BorderFactory.createTitledBorder("Conversa"));

        txtAreaConversa.setColumns(20);
        txtAreaConversa.setRows(5);
        jscpScrollMensagens.setViewportView(txtAreaConversa);

        btnLimpar.setText("Limpar");
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpMensagensLayout = new org.jdesktop.layout.GroupLayout(jpMensagens);
        jpMensagens.setLayout(jpMensagensLayout);
        jpMensagensLayout.setHorizontalGroup(
            jpMensagensLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpMensagensLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpMensagensLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jscpScrollMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMensagensLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(btnLimpar)))
                .addContainerGap())
        );
        jpMensagensLayout.setVerticalGroup(
            jpMensagensLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMensagensLayout.createSequentialGroup()
                .add(btnLimpar)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jscpScrollMensagens, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 303, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Online"));

        jScrollPane1.setViewportView(listaOnline);

        btnAtualizar.setText("Atualizar");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnAtualizar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnAtualizar)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Mensagens"));

        txtAreaEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAreaEnviarActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtAreaEnviar)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtAreaEnviar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpLigacao, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jpMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnEnviar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jpLigacao, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jpMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(btnEnviar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(31, 31, 31))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtAreaEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAreaEnviarActionPerformed
        btnEnviar.doClick();
    }//GEN-LAST:event_txtAreaEnviarActionPerformed

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectarActionPerformed
        if (txtNomeUsuario.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de usuario não pode ser vazio.", "Nome de usuario vazio...", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtServerIp.getText().trim().isEmpty()) {
            txtServerIp.setText("127.0.0.1");
        }
        
        if (txtServerPorta.getText().trim().isEmpty()) {
            txtServerPorta.setText("6666");
        }
        
        ligar();
    }//GEN-LAST:event_btnConectarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (client != null) {
            try {
                client.close(); 
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < lista.size(); i++)
        {
            listModel.addElement(lista.get(i));
        }
        listaOnline.setModel(listModel);       
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        String texto_mensagem = txtAreaEnviar.getText().trim();
        String destino = null;
        int[] destinos = this.listaOnline.getSelectedIndices();
        
        //Mensagem para varios destinatarios
        if(destinos.length > 1){    
            String mensagem = "mensagem:";
            for (int index: destinos){
                destino = (String) this.listaOnline.getModel().getElementAt(index);
                if(!(destino.equals(clientName))){
                    mensagem += destino + ";";
                }else{
                    txtAreaConversa.append("Voce nao pode enviar mensagens para voce mesmo!!\n");
                }                   
            }
            mensagem += ": " + texto_mensagem;
            escritor.println(mensagem);           
            txtAreaConversa.append("Para: " + mensagem.substring(9, mensagem.length()) + "\n");
            
        //Mensagem para um destinatario apenas
        }else if (this.listaOnline.getSelectedIndex() > -1) {
            destino = (String) this.listaOnline.getSelectedValue();
            if(!(destino.equals(clientName))){
                escritor.println("mensagem:" + destino + ":" + texto_mensagem);
                txtAreaConversa.append("Para: " + destino + ": " + texto_mensagem + "\n");
            }else{
                txtAreaConversa.append("Voce nao pode enviar mensagens para voce mesmo!!\n");
            }
           
        //Mensagem para todos
        }else{
            escritor.println("mensagem:" + "*: " + texto_mensagem);
            txtAreaConversa.append("Todos: " + texto_mensagem + "\n");
        }
        txtAreaEnviar.setText("");
        listaOnline.clearSelection();
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        txtAreaConversa.setText("");        
    }//GEN-LAST:event_btnLimparActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        escritor.println("sair");
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws ParseException {        
        UIManager.put("Synthetica.window.decoration", Boolean.TRUE);
        
        SyntheticaAluOxideLookAndFeel AluOxide = new SyntheticaAluOxideLookAndFeel();
        SyntheticaBlackEyeLookAndFeel BlackEye = new SyntheticaBlackEyeLookAndFeel();
        SyntheticaBlackMoonLookAndFeel BlackMoon = new SyntheticaBlackMoonLookAndFeel();
        SyntheticaOrangeMetallicLookAndFeel OrangeMetallic = new SyntheticaOrangeMetallicLookAndFeel();
        SyntheticaBlueLightLookAndFeel BlueLight = new SyntheticaBlueLightLookAndFeel();
        SyntheticaWhiteVisionLookAndFeel WhiteVision = new SyntheticaWhiteVisionLookAndFeel();        
        
        Random rand = new Random();
        try{
//            switch (rand.nextInt(6)) {
//                case 0:
//                    UIManager.setLookAndFeel(AluOxide);
//                    break;
//                case 1:
//                    UIManager.setLookAndFeel(BlackEye);
//                    break;
//                case 2:
//                    UIManager.setLookAndFeel(BlackMoon);
//                    break;
//                case 3:
//                    UIManager.setLookAndFeel(OrangeMetallic);
//                    break;
//                case 4:
//                    UIManager.setLookAndFeel(BlueLight);
//                    break;
//                case 5:
//                    UIManager.setLookAndFeel(WhiteVision);
//                    break;
//                
//            }
            UIManager.setLookAndFeel(BlueLight);
        }

        catch (Exception e){
          e.printStackTrace();
        }

//        try {
//            UIManager.setLookAndFeel( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//        }
//        catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex){}
                
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                new Client().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnConectar;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlblEndereco;
    private javax.swing.JLabel jlblNick;
    private javax.swing.JLabel jlblPorto;
    private javax.swing.JPanel jpLigacao;
    private javax.swing.JPanel jpMensagens;
    private javax.swing.JScrollPane jscpScrollMensagens;
    private javax.swing.JList<String> listaOnline;
    private javax.swing.JTextArea txtAreaConversa;
    private javax.swing.JTextField txtAreaEnviar;
    private javax.swing.JTextField txtNomeUsuario;
    private javax.swing.JTextField txtServerIp;
    private javax.swing.JFormattedTextField txtServerPorta;
    // End of variables declaration//GEN-END:variables
}
