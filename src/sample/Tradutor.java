package sample;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

public class Tradutor {

    private static Tradutor tradutor;

    public Tradutor() {
    }

    public static synchronized Tradutor getInstance() {
        if (tradutor == null) {
            tradutor = new Tradutor();
        }

        return tradutor;
    }

    public String traduzir(String txt, Linguagem sourceLenguage, Linguagem lenguageToTranslate) throws IOException {

        if (txt == null || txt == "" || txt == " " || txt.isEmpty()) {
            return "Nenhuma informacao";
        }

        String url = "https://api.gotit.ai/Translation/v1.1/Translate";

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        String authString = "1206-6A5SgdSh" + ":" + "a53JpMsicIX1GiRiQbfBqc0YLbZhneE130W8";
        String authStringEnc = Base64.getEncoder().encodeToString(authString.getBytes());

        post.addHeader("Content-Type", "application/json");
        post.addHeader("Authorization", "Basic " + authStringEnc);

        JSONObject data = new JSONObject();
        data.put("T", txt);
        data.put("SL", sourceLenguage.origin);
        data.put("TL", lenguageToTranslate.origin);

        post.setEntity(new StringEntity(data.toString(), ContentType.APPLICATION_JSON));

        HttpResponse response = client.execute(post);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        String newResult = result.substring(11, result.length() - 2);

        return newResult.replace(".", ".\n");
    }


    enum Linguagem {
        PORTUGUES("PtBr"),
        INGLES("EnUs");

        String origin;

        Linguagem(String origin) {
            this.origin = origin;
        }
    }

}
