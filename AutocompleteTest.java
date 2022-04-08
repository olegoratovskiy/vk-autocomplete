import java.io.IOException;
import java.util.ArrayList;

public class AutocompleteTest {
    public static void print(String wordToFind, int k, String filename, ArrayList<String> words) {
        System.out.println("Word to find: " + wordToFind);
        System.out.println("Number of relevant queries to find: " + k);
        System.out.println("Filename: " + filename);
        System.out.println("Relevant queries: ");
        for (String word : words) {
            System.out.println(word);
        }
        for (int i = 0; i < 50; ++i) {
            System.out.print('*');
        }
        System.out.println();
    }

    public static void main(String[] args) {
        try {
            // test1
            String filename = "initialization_text_1.txt";
            String wordToFind = "как";
            int k = 5;

            Autocomplete autocomplete = new Autocomplete(filename);
            ArrayList<String> result = autocomplete.getTopK(wordToFind, k);
            print(wordToFind, k, filename, result);


            // test2
            wordToFind = "капая";

            result = autocomplete.getTopK(wordToFind, k);
            print(wordToFind, k, filename, result);


            // test3
            wordToFind = "к";

            result = autocomplete.getTopK(wordToFind, k);
            print(wordToFind, k, filename, result);


            // test4
            filename = "initialization_text_2.txt";
            wordToFind = "hello";

            Autocomplete autocomplete2 = new Autocomplete(filename);
            result = autocomplete2.getTopK(wordToFind, k);
            print(wordToFind, k, filename, result);


            // test5
            wordToFind = "heloo";

            result = autocomplete2.getTopK(wordToFind, k);
            print(wordToFind, k, filename, result);


            // test6
            wordToFind = "h";

            result = autocomplete2.getTopK(wordToFind, k);
            print(wordToFind, k, filename, result);
        } catch (IOException e) {
            System.out.println("Error: File can't be read: " + e.getMessage());
        }
    }
}
