package com.sycnos.heyvisitas.util;

import java.io.File;

public class VariablesGlobales {

    public static File getImagen() {
        return imagen;
    }

    public static void setImagen(File imagen) {
        VariablesGlobales.imagen = imagen;
    }

    public  static File imagen = null;
}


