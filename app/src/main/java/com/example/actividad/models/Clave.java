package com.example.actividad.models;

import com.example.actividad.utils.EncryptionUtil;

public class Clave {
    private String idClave;
    private String propietario;
    private String sitioWeb;
    private String nombreUsuario;
    private String clave;
    private String notas;

    public Clave() {}

    public Clave(String idClave, String propietario, String sitioWeb, String nombreUsuario, String clave, String notas) {
        this.idClave = idClave;
        this.propietario = propietario;
        this.sitioWeb = sitioWeb;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.notas = notas;
    }

    public String getIdClave() {
        return idClave;
    }

    public void setIdClave(String idClave) {
        this.idClave = idClave;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public void encriptarClave() {
        this.clave = EncryptionUtil.encrypt(this.clave);
    }
}
