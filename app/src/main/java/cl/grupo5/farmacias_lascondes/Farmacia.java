package cl.grupo5.farmacias_lascondes;

import com.google.gson.annotations.SerializedName;

/**
 * Representa un local de turno según la API MIDAS v2
 */
public class Farmacia {
    @SerializedName("fecha")
    private String fecha;

    @SerializedName("local_id")
    private String localId;

    @SerializedName("fk_region")
    private String fkRegion;

    @SerializedName("fk_comuna")
    private String fkComuna;

    @SerializedName("fk_localidad")
    private String fkLocalidad;

    @SerializedName("local_nombre")
    private String nombre;

    @SerializedName("comuna_nombre")
    private String comuna;

    @SerializedName("localidad_nombre")
    private String localidad;

    @SerializedName("local_direccion")
    private String direccion;

    @SerializedName("funcionamiento_hora_cierre")
    private String cierre;

    @SerializedName("local_telefono")
    private String telefono;

    @SerializedName("local_lat")
    private double latitud;

    @SerializedName("local_lng")
    private double longitud;

    @SerializedName("funcionamiento_dia")
    private String dia;

    // Getters
    public String getFecha() { return fecha; }
    public String getLocalId() { return localId; }
    public String getFkRegion() { return fkRegion; }
    public String getFkComuna() { return fkComuna; }
    public String getFkLocalidad() { return fkLocalidad; }
    public String getNombre() { return nombre; }
    public String getComuna() { return comuna; }
    public String getLocalidad() { return localidad; }
    public String getDireccion() { return direccion; }
    public String getCierre() { return cierre; }
    public String getTelefono() { return telefono; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getDia() { return dia; }
}
