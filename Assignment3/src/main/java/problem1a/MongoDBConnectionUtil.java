package problem1a;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;

import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class MongoDBConnectionUtil {

    private static final String CONNECTION_STRING = "mongodb+srv://arihant0996:Cm2dkFHYgpQCXxkS@cluster5408.8owfjoj.mongodb.net/?retryWrites=true&w=majority";
    private static final String DATABASE_NAME = "ReuterDb";

    private static MongoDatabase mongoDatabase;

    public boolean addNewsArticles(List<Document> document) {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
            mongoDatabase = mongoClient.getDatabase(DATABASE_NAME);
            MongoCollection articles = mongoDatabase.getCollection("news");
            articles.insertMany(document);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

