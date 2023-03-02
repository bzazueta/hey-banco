package com.sycnos.heyvisitas.util;

import com.sycnos.heyvisitas.data.models.Deparments;

import java.io.File;
import java.util.ArrayList;

public class VariablesGlobales {

    public static File getImagen() {
        return imagen;
    }

    public static void setImagen(File imagen) {
        VariablesGlobales.imagen = imagen;
    }

    public  static File imagen = null;
    public static ArrayList<Deparments> arrayListDeptos = new ArrayList<>();
}


