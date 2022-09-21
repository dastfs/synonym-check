import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SynonymsValidator {
    private static final String synonym = "synonyms";
    private static final String different = "different";

    public static void main(String[] args) {

        var filePath = Path.of("example.in");
        var resultFile = Path.of("example.out");

        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<TestCase> testCases = testCasesBuilder(lines);
        var response = new StringBuilder();

        for (TestCase testCase : testCases) {
            String synOrNot;
            for (String testPairs : testCase.test.wordPairs) {
                var wordOne = testPairs.substring(0, testPairs.indexOf(" ")).toLowerCase();
                var wordTwo = testPairs.substring(testPairs.indexOf(" ") + 1).toLowerCase();

                if (checkForSynonym(testCase.dictionary, wordOne, wordTwo)) {
                    synOrNot = synonym;
                } else {
                    synOrNot = different;
                }
                response.append(synOrNot).append("\n");
            }
        }

        try {
            Files.writeString(resultFile, response,
                    StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.print("Invalid Path");
        }

    }

    private static List<TestCase> testCasesBuilder(List<String> lines) {
        var testCasesNumber = Integer.parseInt(lines.get(0));;
        List<TestCase> testCases = new ArrayList<>();
        var lineNumber = 1;

        for (int i = 0; i < testCasesNumber; i++) {
            var testCase = new TestCase();
            var dictionary = new Dictionary();

            dictionary.numberOfPairs = Integer.parseInt(lines.get(lineNumber));
            var dictCounterPairs = dictionary.numberOfPairs;

            dictionary.synonyms = new HashMap<>();
            while (dictCounterPairs != 0) {
                lineNumber++;
                String dictLine = lines.get(lineNumber);
                addToDictionaryMap(dictionary.synonyms, dictLine);
                dictCounterPairs--;
            }

            testCase.dictionary = dictionary;
            lineNumber++;

            var test = new Test();

            test.numberOfPairs = Integer.parseInt(lines.get(lineNumber));
            test.wordPairs = new ArrayList<>();
            var testPairsCounter = test.numberOfPairs;
            while (testPairsCounter != 0) {
                lineNumber++;
                test.wordPairs.add(lines.get(lineNumber));
                testPairsCounter--;
            }

            testCase.test = test;
            testCases.add(testCase);
            lineNumber++;
        }
        return testCases;
    }

    static boolean checkForSynonym(Dictionary dictionary, String wordOne, String wordTwo) {
        boolean isSynonym = false;
        List<String> firstWordList = null;
        List<String> secondWordList = null;
        if (wordOne.equals(wordTwo)) {
            isSynonym = true;
        }

        if (dictionary.synonyms.containsKey(wordOne)) {
            firstWordList = new ArrayList<>(dictionary.synonyms.get(wordOne));
            for (String str : firstWordList) {
                if (str.equals(wordTwo)) {
                    isSynonym = true;
                    break;
                }
            }
        }

        if (dictionary.synonyms.containsKey(wordTwo)) {
            secondWordList = new ArrayList<>(dictionary.synonyms.get(wordTwo));
            for (String str : secondWordList) {
                if (str.equals(wordOne)) {
                    isSynonym = true;
                    break;
                }
            }
        }

        if (firstWordList != null && secondWordList != null) {
            firstWordList.retainAll(secondWordList);
            if (firstWordList.size() > 0)
                isSynonym = true;
        }

        return isSynonym;
    }

    static void addToDictionaryMap(HashMap<String, HashSet<String>> dictMap, String line) {

        String wordOne = line.substring(0, line.indexOf(" ")).toLowerCase();
        String wordTwo = line.substring(line.indexOf(" ") + 1).toLowerCase();
        if (!dictMap.containsKey(wordOne)) {
            dictMap.put(wordOne, new HashSet<>());
        }

        if (!dictMap.containsKey(wordTwo)) {
            dictMap.put(wordTwo, new HashSet<>());
        }

        dictMap.get(wordOne).add(wordOne);
        dictMap.get(wordTwo).add(wordTwo);

        var set1 = Set.copyOf(dictMap.get(wordOne));
        for (String s : set1) {
            dictMap.get(s).addAll(dictMap.get(wordTwo));
        }

        var set2 = Set.copyOf(dictMap.get(wordTwo));
        for (String s : set2) {
            dictMap.get(s).addAll(dictMap.get(wordOne));
        }

        dictMap.get(wordOne).addAll(dictMap.get(wordTwo));
        dictMap.get(wordTwo).addAll(dictMap.get(wordOne));
    }

    static class TestCase {
        Dictionary dictionary;
        Test test;

        @Override
        public String toString() {
            return "TestCase{" +
                    "dictionary=" + dictionary +
                    ", testPairs=" + test +
                    '}';
        }
    }

    static class Dictionary {
        int numberOfPairs;
        HashMap<String, HashSet<String>> synonyms;

        @Override
        public String toString() {
            return "Dictionary{" +
                    "numberOfPairs=" + numberOfPairs +
                    ", synonyms=" + synonyms +
                    '}';
        }
    }

    static class Test {
        int numberOfPairs;
        List<String> wordPairs;

        @Override
        public String toString() {
            return "TestPairs{" +
                    "numberOfPairs=" + numberOfPairs +
                    ", testPairs=" + wordPairs +
                    '}';
        }
    }
}
