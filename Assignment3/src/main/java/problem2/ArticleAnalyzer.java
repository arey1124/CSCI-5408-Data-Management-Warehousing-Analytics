package problem2;

import org.bson.Document;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArticleAnalyzer {
    public void parseTitleData () {
        String[] fileNames = {
                "src/main/resources/reut2-009.sgm",
                "src/main/resources/reut2-014.sgm"
        };

        String outputFile = "src/main/resources/titles.txt";
        try (FileWriter fileWriter = new FileWriter(outputFile);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (String fileName: fileNames) {
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }

                    // Use regex patterns to find the text between the specified tags
                    String reutersPattern = "<REUTERS(?:[^>]+)?>([\\s\\S]*?)</REUTERS>";
                    String titlePattern = "<TITLE(?:[^>]+)?>([\\s\\S]*?)</TITLE>";

                    Pattern reutersRegex = Pattern.compile(reutersPattern);
                    Pattern titleRegex = Pattern.compile(titlePattern);

                    Matcher reutersMatcher = reutersRegex.matcher(content.toString());
                    while (reutersMatcher.find()) {
                        String reutersText = reutersMatcher.group(1);

                        Matcher titleMatcher = titleRegex.matcher(reutersText);
                        String title = null;
                        if (titleMatcher.find()) {
                            title = cleanAndTransformData(titleMatcher.group(1)).trim().replaceAll("  ", " ");
                        }

                        if(title != null && !title.isEmpty()) {
                           bufferedWriter.append(title);
                           bufferedWriter.newLine();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String cleanAndTransformData (String data) {
        String pattern = "&.*;.*?>|<.*?>|&.*;";
        Pattern tagPattern = Pattern.compile(pattern);
        Matcher matcher = tagPattern.matcher(data);
        return matcher.replaceAll("");
    }

    public void analyseData () {
        String fileName = "src/main/resources/titles.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                index++;
                String[] words = line.split(" ");
                int tileLen = line.length();
                int largestWordLen = Arrays.stream(words).max(Comparator.comparingInt(String::length)).orElse("").length();
                System.out.println(String.format("| %-5s | %-"+ tileLen +"s | %-"+ largestWordLen +"s | %-15s |",
                        "News", "Title Content", "Match", "Polarity"));
                int polarity = 0;
                Map<String, Integer> frequency = getWordsFrequency(words);
                for (Map.Entry<String, Integer> entry : frequency.entrySet()) {
                    String word = entry.getKey();
                    int count = entry.getValue();
                    if(isNegativeWord(word.toLowerCase())) {
                        polarity += (-1*count);
                        System.out.println(String.format("| %-5s | %-"+ tileLen +"s | %-"+ largestWordLen +"s | %-15s |",
                                index, line, word, "Negative"));
                    } else if (isPositiveWord(word.toLowerCase())) {
                        polarity += count;
                        System.out.println(String.format("| %-5s | %-"+ tileLen +"s | %-"+ largestWordLen +"s | %-15s |",
                                index, line, word, "Positive"));
                    } else {
                        System.out.println(String.format("| %-5s | %-"+ tileLen +"s | %-"+ largestWordLen +"s | %-15s |",
                                index, line, word, "Neutral"));
                    }
                }
                System.out.println("Overall Score for the article: " + ((polarity > 0) ? "+" + polarity : polarity));
                System.out.println("Title polarity: " + ((polarity > 0) ? "Positive" : (polarity == 0) ? "Neutral" : "Negative"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Integer> getWordsFrequency (String[] words) {
        Map<String, Integer> wordFrequencyMap = new HashMap<>();

        for (String word : words) {
            if (wordFrequencyMap.containsKey(word)) {
                wordFrequencyMap.put(word, wordFrequencyMap.get(word) + 1);
            } else {
                wordFrequencyMap.put(word, 1);
            }
        }

        return wordFrequencyMap;
    }

    private boolean isNegativeWord(String word) {
        String fileName = "src/main/resources/negative-words.txt";
        return checkIfWordExistsInFile(word, fileName);
    }

    private boolean isPositiveWord(String word) {
        String fileName = "src/main/resources/positive-words.txt";
        return checkIfWordExistsInFile(word, fileName);
    }

    private boolean checkIfWordExistsInFile (String word, String fileName) {
        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            boolean wordFound = false;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals(word)) {
                    wordFound = true;
                    break;
                }
            }

            if (wordFound) {
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return false;
    }
}
