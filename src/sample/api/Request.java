package sample.api;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONException;
import sample.Informacoes.GameInformation;
import sample.Informacoes.Generos;
import sample.Informacoes.Plataformas;
import sample.Informacoes.Stores;

import java.util.ArrayList;
import java.util.Arrays;

public class Request {

    public static final String APPLICATION_NAME = "SearchGames Application";
    public static String APISearch = "https://api.rawg.io/api/games?page_size=";
    public static String APIExactlySearch = "https://api.rawg.io/api/games/";

    public static HttpResponse<JsonNode> searchGame(String gameName) throws UnirestException {
        gameName = gameName.replaceAll(" ", "-");
        HttpResponse<JsonNode> response = Unirest.get(APIExactlySearch + gameName)
                .header("User-agent", APPLICATION_NAME)
                .asJson();


        return response;
    }

    public static ExactlyGame searchExactly(String gameName) throws UnirestException {
        gameName = gameName.replace(" ", "-").toLowerCase();

        HttpResponse<JsonNode> response = Unirest.get(APIExactlySearch + gameName)
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        return new ExactlyGame(response.getBody());
    }

    public static SearchedGame[] searchGameAndReturnGameArray(String gameName, String pageSize) throws UnirestException {
        gameName = gameName.replace(" ", "%20");

        HttpResponse<JsonNode> response = Unirest.get(APISearch + pageSize + "&search=" + gameName)
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        if (response.getBody().getObject().getJSONArray("results").length() <= 0) {
            return topTrendingGames();
        }
        SearchedGame[] array = new SearchedGame[response.getBody().getObject().optString("results").length()];

        int realLength = 0;

        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = new SearchedGame(response.getBody(), response.getBody().getObject().getJSONArray("results"), i);

                if (array[i].getName() == null) {
                    array[i] = null;
                    break;
                } else {
                    realLength++;
                }
            } catch (JSONException ex) {
                break;
            }
        }
        SearchedGame[] returnable = Arrays.copyOf(array, realLength);

        return returnable;
    }

    public static SearchedGame[] requestPagina(String paginaURL) throws UnirestException {

        HttpResponse<JsonNode> response = Unirest.get(paginaURL)
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        if (response.getBody().getObject().getJSONArray("results").length() <= 0) {
            return topTrendingGames();
        }
        SearchedGame[] array = new SearchedGame[response.getBody().getObject().getJSONArray("results").length()];


        int realLength = 0;

        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = new SearchedGame(response.getBody(), response.getBody().getObject().getJSONArray("results"), i);

                if (array[i].getName() == null) {
                    array[i] = null;
                    break;
                } else {
                    realLength++;
                }


            } catch (JSONException ex) {
                break;
            }
        }

        SearchedGame[] returnable = Arrays.copyOf(array, realLength);

        return returnable;
    }

    public static SearchedGame[] requestGamesLikeThis(String gameSlug, int page_size) throws UnirestException {

        HttpResponse<JsonNode> response = Unirest.get("https://api.rawg.io/api/games/" + gameSlug + "/suggested")
                .header("User-agent", APPLICATION_NAME)
                .asJson();


        SearchedGame[] array = new SearchedGame[page_size];


        int realLength = 0;

        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = new SearchedGame(response.getBody(), response.getBody().getObject().getJSONArray("results"), i);

                if (array[i].getName() == null) {
                    array[i] = null;
                    break;
                } else {
                    realLength++;
                }

            } catch (JSONException ex) {
                break;
            }
        }

        SearchedGame[] returnable = Arrays.copyOf(array, realLength);

        return returnable;
    }

    public static SearchedGame[] topTrendingGames() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.rawg.io/api/games?dates=2019-10-10,2020-10-10&ordering=-added&page_size=20")
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        SearchedGame[] array = new SearchedGame[response.getBody().getObject().optJSONArray("results").length()];

        int realLength = 0;

        for (int i = 0; i < array.length; i++) {
            try {
                array[i] = new SearchedGame(response.getBody(), response.getBody().getObject().getJSONArray("results"), i);
                if (array[i].getName() == null) {
                    array[i] = null;
                    break;
                } else {
                    realLength++;
                }
            } catch (JSONException ex) {
                break;
            }
        }

        SearchedGame[] returnable = Arrays.copyOf(array, realLength);

        return returnable;

    }

    public static ArrayList<Generos> getPrincipaisGeneros() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.rawg.io/api/genres")
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        JSONArray array = response.getBody().getObject().getJSONArray("results");
        ArrayList<Generos> generosArrayList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            String img = array.optJSONObject(i).optString("image_background");
            String name = array.optJSONObject(i).optString("name");
            int ID = array.optJSONObject(i).optInt("id");

            generosArrayList.add(new Generos(img, name, ID));
        }

        return generosArrayList;
    }

    public static ArrayList<Plataformas> getPrincipaisPlataformas() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.rawg.io/api/platforms")
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        JSONArray array = response.getBody().getObject().getJSONArray("results");
        ArrayList<Plataformas> plataformasArrayList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            String name = array.optJSONObject(i).optString("name");
            int ID = array.optJSONObject(i).optInt("id");
            int games_count = array.optJSONObject(i).optInt("games_count");

            plataformasArrayList.add(new Plataformas(name, ID, games_count));
        }

        return plataformasArrayList;
    }

    public static ArrayList<GameInformation> getStoresInformation() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("https://api.rawg.io/api/stores")
                .header("User-agent", APPLICATION_NAME)
                .asJson();

        JSONArray array = response.getBody().getObject().getJSONArray("results");

        ArrayList<GameInformation> arrayList = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            String name = array.optJSONObject(i).optString("name");
            String domain = array.optJSONObject(i).optString("domain");
            int ID = array.optJSONObject(i).optInt("id");

            arrayList.add(new Stores(ID, name, domain));
        }

        return arrayList;
    }
}