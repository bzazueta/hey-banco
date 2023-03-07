package com.sycnos.heyvisitas.util;

import com.sycnos.heyvisitas.data.models.Deparments;

import java.io.File;
import java.util.ArrayList;

public class VariablesGlobales {

    public  static File imagen = null;
    public  static String user = "";
    public  static String idUser = "";
    public  static String pasw = "";

    public  static String urlArchivo = "";

    public  static String urlArchivoMessage = "";

    public  static String urlFotoMessage = "";

    public static String getIdUser() {
        return idUser;
    }

    public static void setIdUser(String idUser) {
        VariablesGlobales.idUser = idUser;
    }
    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        VariablesGlobales.user = user;
    }

    public static String getPasw() {
        return pasw;
    }

    public static void setPasw(String pasw) {
        VariablesGlobales.pasw = pasw;
    }


    public static File getImagen() {
        return imagen;
    }

    public static void setImagen(File imagen) {
        VariablesGlobales.imagen = imagen;
    }

    public static String getUrlArchivo() {
        return urlArchivo;
    }

    public static void setUrlArchivo(String urlArchivo) {
        VariablesGlobales.urlArchivo = urlArchivo;
    }


    public static String getUrlArchivoMessage() {
        return urlArchivoMessage;
    }

    public static void setUrlArchivoMessage(String urlArchivoMessage) {
        VariablesGlobales.urlArchivoMessage = urlArchivoMessage;
    }

    public static String getUrlFotoMessage() {
        return urlFotoMessage;
    }

    public static void setUrlFotoMessage(String urlFotoMessage) {
        VariablesGlobales.urlFotoMessage = urlFotoMessage;
    }

    public static ArrayList<Deparments> arrayListDeptos = new ArrayList<>();
}


