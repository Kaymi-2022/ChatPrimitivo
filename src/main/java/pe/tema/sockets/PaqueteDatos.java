/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.tema.sockets;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author MICHAEL
 */
public class PaqueteDatos implements Serializable{

    private String mensaje, nick, ip;
    private ArrayList<String> listaIds;

    public ArrayList<String> getListaIds() {
        return listaIds;
    }

    public void setListaIds(ArrayList<String> listaIds) {
        this.listaIds = listaIds;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
