package com.example.RealFilm.service;

import com.example.RealFilm.model.ApiResponse;
import com.example.RealFilm.model.Comment;
import com.example.RealFilm.model.Movie;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface MovieService {

    @FormUrlEncoded
    @POST("movies")
    Call<ApiResponse>
    addMovie(@Field("title") String title,
             @Field("description") String description,
             @Field("director") String director,
             @Field("releaseYear") String releaseYear,
             @Field("duration") Integer duration,
             @Field("country") String country,
             @Field("actors") String actors,
             @Field("trailerURL") String trailerURL,
             @Field("genre") List<String> genre,
             @Field ("videoURL") List<String> videoURL,
             @Field("posterVertical") String posterVertical,
             @Field("posterHorizontal") String posterHorizontal
//             @Part MultipartBody.Part posterVertical,
//             @Part MultipartBody.Part posterHorizontal
    );

    @GET("movies/search/{query}")
    Call<ApiResponse<List<Movie>>>
    searchMovie(@Path("query") String query);

    @GET("movies/{id}")
    Call<ApiResponse<Movie>>
    getMovie(@Path("id") Integer id);

    @GET("movies/{movieId}/recommendations")
    Call<ApiResponse<List<Movie>>>
    getMoviesRecommendation(@Path("movieId") Integer movieId);

    @POST("movies/{movieId}/favorites")
    Call<ApiResponse>
    addFavorite(@Path("movieId") Integer movieId);

    @DELETE("movies/{movieId}/favorites")
    Call<ApiResponse>
    removeFavorite(@Path("movieId") Integer movieId);

    @GET("movies/latest")
    Call<ApiResponse<List<Movie>>>
    getMoviesLatest();

    @GET("movies/genre/{genreId}")
    Call<ApiResponse<List<Movie>>>
    getMoviesByGenre(@Path("genreId") String genreId);

    @GET("movies/country/{countryId}")
    Call<ApiResponse<List<Movie>>>
    getMoviesByCountry(@Path("countryId") String countryId);

    @GET("movies/favorites")
    Call<ApiResponse<List<Movie>>>
    getFavoriteMovies();

    @GET("movies/comments")
    Call<ApiResponse<List<Movie>>>
    getCommentedMovies();

    @GET("movies/ratings")
    Call<ApiResponse<List<Movie>>>
    getRatedMovies();


}
