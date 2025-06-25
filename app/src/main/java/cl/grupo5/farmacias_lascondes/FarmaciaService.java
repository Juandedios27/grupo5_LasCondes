package cl.grupo5.farmacias_lascondes;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface para MIDAS v2:
 * https://midas.minsal.cl/farmacia_v2/WS/getLocalesTurnos.php
 */
public interface FarmaciaService {
    @GET("farmacia_v2/WS/getLocalesTurnos.php")
    Call<List<Farmacia>> getFarmacias(
            @Query("fecha") String fecha,
            @Query("fk_comuna") String fkComuna
    );
}
