package problem1a;

import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReutRead {

    private MongoDBConnectionUtil mongoDBConnectionUtil;

    private List<Document> newsArticles;

    public ReutRead() {
        newsArticles = new ArrayList<>();
        mongoDBConnectionUtil = new MongoDBConnectionUtil();
    }

    public void parseAndStoreData () {
        String[] fileNames = {
                "src/main/resources/reut2-009.sgm",
                "src/main/resources/reut2-014.sgm"
        };

        for (String fileName: fileNames) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // Use regex patterns to find the text between the specified tags
                String reutersPattern = "<REUTERS(?:[^>]+)?>([\\s\\S]*?)</REUTERS>";
                String textPattern = "<TEXT(?:[^>]+)?>([\\s\\S]*?)</TEXT>";
                String titlePattern = "<TITLE(?:[^>]+)?>([\\s\\S]*?)</TITLE>";
                String dateLinePattern = "<DATELINE(?:[^>]+)?>([\\s\\S]*?)</DATELINE>";
                String bodyPattern = "<BODY(?:[^>]+)?>([\\s\\S]*?)</BODY>";

                Pattern reutersRegex = Pattern.compile(reutersPattern);
                Pattern textRegex = Pattern.compile(textPattern);
                Pattern titleRegex = Pattern.compile(titlePattern);
                Pattern dateLineRegex = Pattern.compile(dateLinePattern);
                Pattern bodyRegex = Pattern.compile(bodyPattern);

                Matcher reutersMatcher = reutersRegex.matcher(content.toString());
                while (reutersMatcher.find()) {
                    String reutersText = reutersMatcher.group(1);

                    Matcher textMatcher = textRegex.matcher(reutersText);
                    String text = null;
                    String dateLine = null;
                    String body = null;
                    if (textMatcher.find()) {
                        text = textMatcher.group(1);
                        Matcher dateLineMatcher = dateLineRegex.matcher(text);
                        if(dateLineMatcher.find()) {
                            dateLine = cleanAndTransformData(dateLineMatcher.group(1)).trim().replaceAll("  "," ");
                        }

                        Matcher bodyMatcher = bodyRegex.matcher(text);
                        if(bodyMatcher.find()) {
                            body = cleanAndTransformData(bodyMatcher.group(1)).trim().replaceAll("  "," ");
                        }
                    }

                    Matcher titleMatcher = titleRegex.matcher(reutersText);
                    String title = null;
                    if (titleMatcher.find()) {
                        title = cleanAndTransformData(titleMatcher.group(1)).trim().replaceAll("  "," ");
                    }

                    if(title != null && !title.isEmpty()) {
                        newsArticles.add(new Document("title", title)
                                .append("text", new Document("dateLine", dateLine).append("body", body)));
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mongoDBConnectionUtil.addNewsArticles(newsArticles);
    }

    private String cleanAndTransformData (String data) {
        String pattern = "&.*;.*?>|<.*?>|&.*;";
        Pattern tagPattern = Pattern.compile(pattern);
        Matcher matcher = tagPattern.matcher(data);
        return matcher.replaceAll("");
    }
}
