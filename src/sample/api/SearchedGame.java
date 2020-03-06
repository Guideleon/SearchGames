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

import java.util.ArrayList;

public class SearchedGame {

    public String nextURL;
    public String previousURL;
    private String name;
    private String slug;
    private String imageURL;
    private String releaseDate;
    private int metacritic;
    private JSONArray genres;

    public SearchedGame(JsonNode jsonNode, JSONArray jsonArray, int pos) {

        JSONObject values = jsonArray.getJSONObject(pos);
        this.name = values.optString("name");
        this.slug = values.optString("slug");
        this.imageURL = values.optString("background_image");
        this.releaseDate = values.optString("released");
        this.metacritic = values.optInt("metacritic");

        this.genres = values.optJSONArray("genres");

        if (pos == 0) {
            this.nextURL = jsonNode.getObject().optString("next");
            this.previousURL = jsonNode.getObject().optString("previous");
        } else {
            this.nextURL = null;
            this.previousURL = null;
        }

    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
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

    public int getMetacritic() {
        return metacritic;
    }

    public JSONArray getGenres() {
        return genres;
    }

    public ArrayList<String> getGenresName() {
        ArrayList<String> arrayList = new ArrayList<>();

        if (this.genres.length() <= 0) {
            arrayList.add("Null");
            return arrayList;
        }

        for (int i = 0; i < this.genres.length(); i++) {
            arrayList.add(this.genres.optJSONObject(i).optString("name"));
        }

        return arrayList;
    }

    public String getNextURL() {
        return nextURL;
    }

    public String getPreviousURL() {
        return previousURL;
    }

    public String getSlug() {
        return slug;
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

}
