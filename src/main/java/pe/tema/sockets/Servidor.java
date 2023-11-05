package pe.tema.sockets;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Servidor {

    public static void main(String[] args) {

        MarcoServidor mimarco = new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoServidor extends JFrame {

    public MarcoServidor() {
        setBounds(1000, 300, 280, 380);
        LaminaMarcoServidor laminamarcoservidor = new LaminaMarcoServidor();
        add(laminamarcoservidor);

        setVisible(true);
        setTitle("Servidor");

        // Iniciar el hilo del servidor
        Thread serverThread = new Thread(laminamarcoservidor);
        serverThread.start();
    }
}

class LaminaMarcoServidor extends JPanel implements Runnable {

    private final JTextArea areatexto;

    public LaminaMarcoServidor() {
        areatexto = new JTextArea(20, 25);
        areatexto.setEditable(false);
        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());
        add(milamina);
        milamina.add(areatexto, BorderLayout.CENTER);
    }

    @Override
    public void run() {
        try {
            //Server socket de escucha
            ServerSocket servidor = new ServerSocket(9999);
            areatexto.append("Servidor iniciado. Esperando conexiones...\n");
            PaqueteDatos paquetedatosrecibido;
            ArrayList<String> listaid = new ArrayList<>();
            while (true) {
                Socket misocket = servidor.accept();
                ObjectInputStream paquete = new ObjectInputStream(misocket.getInputStream());
                paquetedatosrecibido = (PaqueteDatos) paquete.readObject();
                String nick = paquetedatosrecibido.getNick();
                String ip = paquetedatosrecibido.getIp();
                String mensaje = paquetedatosrecibido.getMensaje();

                if (mensaje.equalsIgnoreCase("BYE")) {
                    areatexto.append("\nSE FINALIZÓ CONEXIÓN DE IP: "+ip);

                    // Mostrar un JOptionPane
                    JOptionPane.showMessageDialog(this, "La conexión se ha finalizado para: "+ip, "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                    servidor.close();
                   
                }

                if (!mensaje.equals("Conectado")) {
                    //Creamos socket para reenvio de paquetes
                    Socket enviarDatoDestinatario = new Socket(ip, 9090);
                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviarDatoDestinatario.getOutputStream());
                    paqueteReenvio.writeObject(paquetedatosrecibido);
                    areatexto.append("\n" + nick + ": " + mensaje + " para " + ip);

                    //Cerramos recursos
                    paqueteReenvio.close();
                    enviarDatoDestinatario.close();
                } else {
                    //Detectar la dirección IP del cliente conectado
                    InetAddress clienteAddress = misocket.getInetAddress();
                    String ipConectada = clienteAddress.getHostAddress();
                    areatexto.append("\nCliente conectado desde la IP: " + ipConectada + "\n");
                    listaid.add(ipConectada);
                    paquetedatosrecibido.setListaIds(listaid);
                    for (String z : listaid) {
                        Socket enviarDatoDestinatario = new Socket(z, 9090);
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviarDatoDestinatario.getOutputStream());
                        paqueteReenvio.writeObject(paquetedatosrecibido);
                        System.out.println(z);
                        paqueteReenvio.close();
                        enviarDatoDestinatario.close();
                    }
                }
                //cerramos socket
                misocket.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            areatexto.append("\nError en el servidor: " + e.getMessage() + "\n");
        }
    }
}
