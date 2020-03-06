package sample.api;

import com.mashape.unirest.http.JsonNode;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.json.JSONArray;
import org.json.JSONObject;
import sample.Informacoes.GameInformation;
import sample.Informacoes.Stores;

import java.util.ArrayList;

public class ExactlyGame {

    private String name;
    private String imageURL;
    private String additionalImageURL;
    private String releaseDate;
    private String updated;
    private String description;
    private String website;
    private int metacritic;
    private int rating;
    private int rating_top;
    private JSONArray genres;
    private ArrayList<String> screenshot;
    private ArrayList<Stores> stores;

    public ExactlyGame(JsonNode jsonNode) {

        JSONObject values = jsonNode.getObject();

        stores = new ArrayList<>();

        this.name = values.optString("name");
        this.imageURL = values.optString("background_image");
        this.additionalImageURL = values.optString("background_image_additional");
        this.releaseDate = values.optString("released");
        this.updated = values.optString("updated");
        this.description = values.optString("description_raw");
        this.website = values.optString("website");
        this.metacritic = values.optInt("metacritic");
        this.rating = values.optInt("rating");
        this.rating_top = values.optInt("rating_top");
        this.genres = values.optJSONArray("genres");

        JSONArray storesArray = values.optJSONArray("stores");
        if (storesArray.length() > 0) {
            for (int i = 0; i < storesArray.length(); i++) {
                JSONObject insideValues = storesArray.getJSONObject(i);
                stores.add(new Stores(insideValues.getJSONObject("store").optInt("id"), insideValues.getJSONObject("store").optString("name"), insideValues.optString("url")));
            }
        } else {
            //stores.add(new Stores(0,"Null","Null"));
        }

    }

    public int getRating() {
        return rating;
    }

    public int getRating_top() {
        return rating_top;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getAdditionalImageURL() {
        return additionalImageURL;
    }

    public String getUpdated() {
        return updated;
    }

    public String getDescription() {
        return description;
    }

    public String getWebsite() {
        return website;
    }

    public int getMetacritic() {
        return metacritic;
    }

    public JSONArray getGenres() {
        return genres;
    }

    public ArrayList<String> getScreenshot() {
        return screenshot;
    }

    public ArrayList<Stores> getStores() {
        return stores;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getBrazilianReleaseDate() {
        if (releaseDate.isEmpty()) return "";
        return releaseDate.substring(releaseDate.length() - 2, releaseDate.length()) +
                "/" + releaseDate.substring(5, 7) +
                "/" + releaseDate.substring(0, 4);
    }

    public ImageView imagemPronta(int layoutLength) {
        ImageView imageView = new ImageView();


        imageView.setFitWidth(layoutLength);
        imageView.setFitHeight(layoutLength);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);

        Rectangle clip = new Rectangle(
                imageView.getFitWidth(), imageView.getFitHeight()
        );

        clip.setArcWidth(20);
        clip.setArcHeight(20);

        imageView.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            imageView.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.8)));
        }

        imageView.setClip(null);

        if (getImageURL().isEmpty()) {
            Image img = new Image("sample/imagens/noimage.jpg");
            return new ImageView(img);
        }

        Image image = new Image(getImageURL(), true);

        imageView.setImage(image);

        return imageView;
    }

    public ImageView additionalImagemPronta(int layoutLength) {
        ImageView imageView = new ImageView();


        imageView.setFitWidth(layoutLength);
        imageView.setFitHeight(layoutLength);
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setCacheHint(CacheHint.SPEED);

        Rectangle clip = new Rectangle(
                imageView.getFitWidth(), imageView.getFitHeight()
        );

        clip.setArcWidth(20);
        clip.setArcHeight(20);

        imageView.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        if (Platform.isSupported(ConditionalFeature.EFFECT)) {
            imageView.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.8)));
        }

        imageView.setClip(null);

        if (getImageURL().isEmpty()) {
            Image img = new Image("sample/imagens/noimage.jpg");
            return new ImageView(img);
        }

        Image image = new Image(getAdditionalImageURL(), true);

        imageView.setImage(image);

        return imageView;
    }
}
