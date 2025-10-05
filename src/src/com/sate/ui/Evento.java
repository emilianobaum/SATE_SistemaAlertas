package com.sate.ui;

public class Evento { // La clase debe ser PUBLIC
    private int idEvento;
    private String tipo;
    private String fechaHora;
    private String areaAfectada;

    public Evento(int idEvento, String tipo, String fechaHora, String areaAfectada) {
        this.idEvento = idEvento;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.areaAfectada = areaAfectada;
    }

    public int getIdEvento() { return idEvento; }
    public String getTipo() { return tipo; }
    public String getFechaHora() { return fechaHora; }
    public String getAreaAfectada() { return areaAfectada; }
    
    public String[] getCoordinates() {
        if (areaAfectada != null && areaAfectada.startsWith("POINT")) {
            String coords = areaAfectada.substring(6, areaAfectada.length() - 1);
            return coords.split(" ");
        }
        return new String[]{"0.0", "0.0"};
    }
}