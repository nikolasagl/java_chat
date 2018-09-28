package client;

import de.javasoft.plaf.synthetica.SyntheticaSimple2DLookAndFeel;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

public class Client extends javax.swing.JFrame {
    
    private Socket client;
    private Scanner reader;
    private PrintStream writer;
    private String clientName;
    public static final ArrayList<String> list = new ArrayList();
    
    
    //Construtor inicia os componentes do JFrame
    public Client() {
        initComponents();
        DefaultCaret caret = (DefaultCaret)txtMessageArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.setTitle("Annoying Chat");
    }
    
    public void playLaugh(){
        String sound_laugh = "Risada.wav";
        AudioInputStream audioInputStream_4;
        
        try {
            audioInputStream_4 = AudioSystem.getAudioInputStream(new File(sound_laugh).getAbsoluteFile());
            Clip laugh = AudioSystem.getClip();
            
            laugh.open(audioInputStream_4);
            laugh.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    public void playSound() throws UnsupportedAudioFileException, LineUnavailableException{
        String sound_hey = "Hey.wav";
        String sound_heyApple = "HeyApple.wav";
        String sound_apple = "Apple.wav";
        
        AudioInputStream audioInputStream_1;
        AudioInputStream audioInputStream_2;
        AudioInputStream audioInputStream_3;        
        
        try {
            Random rand = new Random();
            int i = rand.nextInt(3);
            
            audioInputStream_1 = AudioSystem.getAudioInputStream(new File(sound_hey).getAbsoluteFile());
            Clip hey = AudioSystem.getClip();

            audioInputStream_2 = AudioSystem.getAudioInputStream(new File(sound_heyApple).getAbsoluteFile());
            Clip heyApple = AudioSystem.getClip();

            audioInputStream_3 = AudioSystem.getAudioInputStream(new File(sound_apple).getAbsoluteFile());
            Clip apple = AudioSystem.getClip();            
          
            switch(i){
              case 0:
                hey.open(audioInputStream_1);
                hey.start();
                break;

              case 1:
                heyApple.open(audioInputStream_2);
                heyApple.start();
                break;

              case 2:
                apple.open(audioInputStream_3);
                apple.start();
                break;
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void btnRefresh(){
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < list.size(); i++)
        {
            listModel.addElement(list.get(i));
        }
        listOnline.setModel(listModel);   
    }

    //Funcao chamada com o click do botao conectar
    //Cria a conexao do socket com o servidor
    //Define um nome ao socket "clientName" e loga no servidor com esse nome
    public void connect() {
        try {
            final String aux_name = txtUserName.getText().trim();
            
            txtMessageArea.append("Conectando...\n");
            
            String host = txtServerIp.getText().trim();            
            int port = Integer.parseInt(txtServerPort.getText().trim());
            client = new Socket(host, port);

            reader = new Scanner(client.getInputStream());
            writer = new PrintStream(client.getOutputStream());
            
            writer.println("login:" + aux_name);
            
            //Thread que ouve as respostas do servidor que chegam atraves do clientManager //LISTENER
            //Trata a mensagem recebida e, de acordo com o que foi enviado pelo clientManager define quais acoes realizar
            new Thread(){
                @Override
                public void run() {
                    try {
                        while (reader.hasNextLine()) {
                            String msg = reader.nextLine();
                            String[] result = msg.split(":");
                            System.err.println("Mensagem recebida do servidor: " + msg);
                            
                            if(msg.toLowerCase().startsWith("transmitir:")){
                                String mensagem = msg.substring(11, msg.length());   
                                if (result[2].equals("*")){ // result[1] para implementacao antiga
//                                    String aux = msg.substring(13, msg.length());
//                                    txtAreaConversa.append("Todos:" + aux + "\n"); Implementacao antiga
                                    txtMessageArea.append("De: " + result[1] + " | Para:*:" + result[3] + "\n");
                                    txtMessageArea.append("\n");
                                    try {
                                        playSound();
                                    } catch (UnsupportedAudioFileException | LineUnavailableException ex) {
                                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }else{
                                    String[] aux = mensagem.split(":");
                                    String aux2 = aux[1].substring(0, (aux[1].length())-1);
                                    txtMessageArea.append("De: " + aux[0] + " | Para: " + aux2 + ": " + aux[2] + "\n");   
                                    txtMessageArea.append("\n");
                                    try {
                                        playSound();
                                    } catch (UnsupportedAudioFileException | LineUnavailableException ex) {
                                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }   
                                
                            }else if(msg.toLowerCase().startsWith("lista_usuarios:")){
                                String[] aux = msg.split(":");
                                
                                String[] names = aux[1].split(";");
                                for(String name: names){
                                    if(name != null){
                                        addList(name);                                             
                                    }
                                } 
                                //Atualiza a lista de clientes
                                btnRefresh();
                                list.clear();
                                
                            }else if(msg.toLowerCase().equals("login:false")){
                               System.err.println("Nome de usuario ja existe... " + msg);                               
                               txtMessageArea.append("Nome de usuario ja existe... Escolha outro!" + "\n"); 
                               txtMessageArea.append("\n");
                               client.close();
                               
                            }else if(msg.toLowerCase().equals("login:true")){
                               playLaugh();
                               clientName = aux_name;
                               txtMessageArea.append("Usuario conectado...\n");                               
                               txtMessageArea.append("\n");
                               System.err.println("Usuario conectado...");
                               txtUserName.setEditable(false);
                               txtServerIp.setEditable(false);
                               txtServerPort.setEditable(false);
                               btnConnect.setEnabled(false);
                               
                            }else{
                               System.err.println("Mensagem Invalida do Servidor");
                            }                            
                        }
                    } catch (IOException ex) {
                        txtMessageArea.append("<-cliente->:" + ex.getMessage() + "\n");
                    }
                }

                private void addList(String name) {
                    if(name != null){
                        ListModel model = (ListModel) listOnline.getModel();
                        list.add(name);
                    }
                }                
            }.start(); //Starta a Thread    
            
        } catch (IOException ex) {
            txtMessageArea.append("<-cliente->:" + ex.getMessage() + "\n");
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

        jpConnect = new javax.swing.JPanel();
        jlblAdress = new javax.swing.JLabel();
        txtServerIp = new javax.swing.JTextField();
        jlblPort = new javax.swing.JLabel();
        txtServerPort = new javax.swing.JFormattedTextField();
        btnConnect = new javax.swing.JButton();
        jlblName = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        btnLogout = new javax.swing.JButton();
        jpMessages = new javax.swing.JPanel();
        jscpScrollMensagens = new javax.swing.JScrollPane();
        txtMessageArea = new javax.swing.JTextArea();
        btnCls = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listOnline = new javax.swing.JList<>();
        btnClsSelection = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        txtSendArea = new javax.swing.JTextField();
        btnSend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jpConnect.setBorder(javax.swing.BorderFactory.createTitledBorder("Conexão"));

        jlblAdress.setText("IP:");

        jlblPort.setText("Porta:");

        txtServerPort.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        btnConnect.setText("Conectar");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jlblName.setText("Nome:");

        btnLogout.setText("Sair");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpConnectLayout = new org.jdesktop.layout.GroupLayout(jpConnect);
        jpConnect.setLayout(jpConnectLayout);
        jpConnectLayout.setHorizontalGroup(
            jpConnectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpConnectLayout.createSequentialGroup()
                .addContainerGap()
                .add(jlblName)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jlblAdress)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtServerIp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 171, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jlblPort)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtServerPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnConnect)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(btnLogout, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpConnectLayout.setVerticalGroup(
            jpConnectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpConnectLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(btnConnect)
                .add(txtServerPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblPort)
                .add(txtServerIp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jlblAdress)
                .add(jlblName)
                .add(txtUserName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(btnLogout))
        );

        jpMessages.setBorder(javax.swing.BorderFactory.createTitledBorder("Conversa"));

        txtMessageArea.setColumns(20);
        txtMessageArea.setRows(5);
        jscpScrollMensagens.setViewportView(txtMessageArea);

        btnCls.setText("Limpar");
        btnCls.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jpMessagesLayout = new org.jdesktop.layout.GroupLayout(jpMessages);
        jpMessages.setLayout(jpMessagesLayout);
        jpMessagesLayout.setHorizontalGroup(
            jpMessagesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jpMessagesLayout.createSequentialGroup()
                .addContainerGap()
                .add(jpMessagesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jscpScrollMensagens, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMessagesLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(btnCls)))
                .addContainerGap())
        );
        jpMessagesLayout.setVerticalGroup(
            jpMessagesLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jpMessagesLayout.createSequentialGroup()
                .add(btnCls)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jscpScrollMensagens, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 303, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Online"));

        jScrollPane1.setViewportView(listOnline);

        btnClsSelection.setText("Limpar Selecao");
        btnClsSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClsSelectionActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(btnClsSelection)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(btnClsSelection)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Mensagens"));

        txtSendArea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSendAreaActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtSendArea)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(txtSendArea, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        btnSend.setText("Enviar");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jpConnect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jpMessages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(btnSend, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jpConnect, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jpMessages, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(btnSend, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSendAreaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSendAreaActionPerformed
        btnSend.doClick();
    }//GEN-LAST:event_txtSendAreaActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        if (txtUserName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome de usuario não pode ser vazio.", "Nome de usuario vazio...", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (txtServerIp.getText().trim().isEmpty()) {
            txtServerIp.setText("127.0.0.1");
        }
        
        if (txtServerPort.getText().trim().isEmpty()) {
            txtServerPort.setText("6666");
        }
        
        connect();
    }//GEN-LAST:event_btnConnectActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (client != null) {
            try {
                client.close(); 
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        if((txtSendArea.getText().trim() != null) && (!txtSendArea.getText().trim().isEmpty())){        
            String texto_mensagem = txtSendArea.getText().trim();        
            String destino = null;
            int[] destinos = this.listOnline.getSelectedIndices();        
        
            //Mensagem para varios destinatarios
            if(destinos.length > 1){    
                String mensagem = "mensagem:";
                for (int index: destinos){
                    destino = (String) this.listOnline.getModel().getElementAt(index);
                    if(!(destino.equals(clientName))){
                        mensagem += destino + ";";
                    }else{
                        txtMessageArea.append("Voce nao pode enviar mensagens para voce mesmo!!\n");
                        txtMessageArea.append("\n");
                    }                   
                }
                mensagem += ": " + texto_mensagem;
                writer.println(mensagem);    
                String[] result = mensagem.split(":");
                txtMessageArea.append("Para: " + result[1].substring(0, (result[1].length())-1) + ": " + result[2] + "\n");
                txtMessageArea.append("\n");

            //Mensagem para um destinatario apenas
            }else if (this.listOnline.getSelectedIndex() > -1) {
                destino = (String) this.listOnline.getSelectedValue();
                if(!(destino.equals(clientName))){
                    writer.println("mensagem:" + destino + ":" + texto_mensagem);
                    txtMessageArea.append("Para: " + destino + ": " + texto_mensagem + "\n");
                    txtMessageArea.append("\n");
                }else{
                    txtMessageArea.append("Voce nao pode enviar mensagens para voce mesmo!!\n");
                    txtMessageArea.append("\n");
                }

            //Mensagem para todos
            }else{
                writer.println("mensagem:" + "*: " + texto_mensagem);
                txtMessageArea.append("Para:*: " + texto_mensagem + "\n");
                txtMessageArea.append("\n");
            }
            txtSendArea.setText("");        
        }        
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnClsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClsActionPerformed
        txtMessageArea.setText("");   
        listOnline.clearSelection();
    }//GEN-LAST:event_btnClsActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        writer.println("sair");
        try {
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnClsSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClsSelectionActionPerformed
        listOnline.clearSelection();
    }//GEN-LAST:event_btnClsSelectionActionPerformed

    
    /**
     * @param args the command line arguments
     * @throws java.text.ParseException
     */
    public static void main(String args[]) throws ParseException {   
        
        UIManager.put("Synthetica.window.decoration", Boolean.TRUE);
        SyntheticaSimple2DLookAndFeel BlueLight = new SyntheticaSimple2DLookAndFeel();           
       
        try{
            UIManager.setLookAndFeel(BlueLight);
        }
        catch (UnsupportedLookAndFeelException e){}
                
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                new Client().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCls;
    private javax.swing.JButton btnClsSelection;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSend;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlblAdress;
    private javax.swing.JLabel jlblName;
    private javax.swing.JLabel jlblPort;
    private javax.swing.JPanel jpConnect;
    private javax.swing.JPanel jpMessages;
    private javax.swing.JScrollPane jscpScrollMensagens;
    private javax.swing.JList<String> listOnline;
    private javax.swing.JTextArea txtMessageArea;
    private javax.swing.JTextField txtSendArea;
    private javax.swing.JTextField txtServerIp;
    private javax.swing.JFormattedTextField txtServerPort;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
