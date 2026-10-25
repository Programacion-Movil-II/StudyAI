package com.example.studyai;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
public interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    Call<JsonObject> generarRespuesta(
            @Path("model") String modelo,
            @Header("x-goog-api-key") String apiKey,
            @Body JsonObject solicitud
    );
}
