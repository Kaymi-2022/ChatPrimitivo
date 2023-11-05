package pe.tema.sockets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.*;

public class Cliente {

    public static void main(String[] args) {
        MarcoCliente mimarco = new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {

    public MarcoCliente() {
        setBounds(200, 300, 395, 450);
        LaminaMarcoCliente milamina = new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        setTitle("Cliente");
        addWindowListener(new EnvioAlertaConeccion());
    }
}

class EnvioAlertaConeccion extends WindowAdapter {
    
    //COLOCAR ID DE CONEXION DE SERVIDOR

    String id_Servidor="192.168.0.4";
    @Override
    public void windowOpened(WindowEvent w) {
        try {
            Socket socket = new Socket(id_Servidor, 9999);
            PaqueteDatos dato = new PaqueteDatos();
            dato.setMensaje("Conectado");
            ObjectOutputStream paquete_datos = new ObjectOutputStream(socket.getOutputStream());
            paquete_datos.writeObject(dato);
            socket.close();
            paquete_datos.close();
        } catch (IOException e) {
            System.out.println("Error envio de Coneccion " + e.getMessage());
        }
    }
}

class LaminaMarcoCliente extends JPanel {

    JButton miboton;
    JLabel nick, IPS;
    private final JTextField campo1;
    private final JTextArea campoChat;
    private final JList<String> listaContactos;
    private final JLabel nombre;

    DefaultListModel<String> model = new DefaultListModel<>();

    public LaminaMarcoCliente() {

        //INGRESO DE NOMBRE DE USUARIO
        String usuarion_name = JOptionPane.showInputDialog("Nick: ");

        if (usuarion_name == null) {
            System.exit(0);
        }

        //CREACION DE JLABEL CON NOMBRE USUARIO
        nick = new JLabel("Usuario:");
        nick.setForeground(Color.red);
        add(nick);

        //CREACION DE JLABEL CON NOMBRE IPS
        IPS = new JLabel("IPS:");
        IPS.setForeground(Color.red);

        //INGRESO DEL DATO DEL USUARIO CAPTURADO EN LA VARIABLE usuarion_name
        nombre = new JLabel(usuarion_name);
        nombre.setForeground(Color.BLUE);
        add(nombre);
        add(IPS);

        escucharMensaje();
        listaContactos = new JList<>(model);
        listaContactos.setVisibleRowCount(1);
        JScrollPane desplazamiento = new JScrollPane(listaContactos);
        JPanel laminaLista = new JPanel();

        // Definir laminaLista aquí
        laminaLista.add(desplazamiento);
        add(laminaLista, BorderLayout.NORTH);

        campoChat = new JTextArea(20, 30);
        add(campoChat);
        campoChat.setEditable(false);

        campo1 = new JTextField(20);
        add(campo1);

        miboton = new JButton("Enviar");
        // Agregar un ActionListener al botón||
        miboton.addActionListener(e -> enviarMensaje());

        add(miboton);

    }

    private void enviarMensaje() {
        // Obtener los datos del usuario
        String usuario = nombre.getText();
        String ipDestino = listaContactos.getSelectedValue();
        String mensaje = campo1.getText();

        if (usuario.isEmpty() || ipDestino == null || mensaje.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.");
            return;
        } else {
            campoChat.append("\nYO: " + campo1.getText());
            campo1.setText("");
        }

        // Crear un nuevo hilo para realizar la comunicación de red
        Thread thread = new Thread(() -> {
            try {
                // Establecer la conexión con el servidor
                Socket SocketEnvio = new Socket(ipDestino, 9999);

                // Crear un objeto PaqueteDatos y enviarlo
                PaqueteDatos datos = new PaqueteDatos();
                datos.setNick(usuario);
                datos.setIp(ipDestino);
                datos.setMensaje(mensaje);

                ObjectOutputStream flujoDatos = new ObjectOutputStream(SocketEnvio.getOutputStream());
                flujoDatos.writeObject(datos);

                // Cerrar recursos
                SocketEnvio.close();
                flujoDatos.close();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(LaminaMarcoCliente.this, "Error al enviar el mensaje: " + ex.getMessage());
            }
        });

        // Iniciar el hilo de comunicación
        thread.start();
    }

    private void escucharMensaje() {

        // Crear un nuevo hilo para realizar la comunicación de red
        Thread thread = new Thread(() -> {
            try {
                ServerSocket servidorcliente = new ServerSocket(9090);
                Socket cliente;
                PaqueteDatos paqueteRecibido;
                List<String> listaid;

                while (true) {
                    //ACEPTAR LA CONEXION Y LO ALMACENAMOS EN LA VARIABLE CLIENTE
                    cliente = servidorcliente.accept();
                    //Obtenecion de datos del paquete de la variable cliente
                    ObjectInputStream flujo = new ObjectInputStream(cliente.getInputStream());
                    //ASIGNAMOS AL OBJETO EL VALOR DEL DATO DE FLUJOS
                    paqueteRecibido = (PaqueteDatos) flujo.readObject();
                    listaid = paqueteRecibido.getListaIds();
                    //EVAÑUA SI CONTIENE EL ATRIBUTO MENSAJE DEL OBJETO PAQUETE RECIBIDO LA PALABRA CONECTADO
                    if (!paqueteRecibido.getMensaje().equals("Conectado")) {
                        campoChat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
                    } else {
                        for (String id : listaid) {
                            model.addElement(id);
                        }
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(LaminaMarcoCliente.this, "Error de envio de paquete" + e.getMessage());
            }
        });

        // Iniciar el hilo de comunicación
        thread.start();
    }

}
