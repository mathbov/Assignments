import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/*
This is the reexam for DM563 - Concurrent Programming, Spring 2022.
Your task is to implement the following methods of class Exam:
- findWordsCommonToAllLines;
- shortestLine;
- wordWithConsonants;
- wordsStartingWith.
These methods search text files for particular words.
You must use a BreakIterator to identify words in a text file,
which you can obtain by calling BreakIterator.getWordInstance().
For more details on the usage of BreakIterator, please see the corresponding video lecture in the
course.
The implementations of these methods must exploit concurrency to achieve improved performance.
The only code that you can change is the implementation of these methods.
In particular, you cannot change the signatures (return type, name, parameters) of any method, and
you cannot edit method main.
The current code of these methods throws an UnsupportedOperationException: remove that line before
proceeding on to the implementation.
You can find a complete explanation of the exam rules at the following webpage.
https://github.com/fmontesi/cp2022/tree/main/reexam
*/
public class Exam {
    // Do not change this method
    public static void main(String[] args) {
        checkArguments(args.length > 0,
                "You must choose a command: help, allLines, shortestLine, consonants, or prefix.");
        switch (args[0]) {
            case "help":
                System.out.println(
                        "Available commands: help, allLines, shortestLine, consonants, or prefix.\nFor example, try:\n\tjava Exam allLines data");
                break;
            case "allLines":
                checkArguments(args.length == 2, "Usage: java Exam.java allLines <directory>");
                List<LocatedWord> uniqueWords = findWordsCommonToAllLines(Paths.get(args[1]));
                System.out.println("Found " + uniqueWords.size() + " words");
                uniqueWords.forEach( locatedWord ->
                        System.out.println( locatedWord.word + ":" + locatedWord.filepath ) );
                break;
            case "shortestLine":
                checkArguments(args.length == 2, "Usage: java Exam.java shortestLine <directory>");
                Location location = shortestLine(Paths.get(args[1]));
                System.out.println("Line with highest number of letters found at " + location.filepath + ":" + location.line );
                break;
            case "consonants":
                checkArguments(args.length == 3, "Usage: java Exam.java consonants <directory> <consonants>");
                int consonants = Integer.parseInt(args[2]);
                Optional<LocatedWord> word = wordWithConsonants(Paths.get(args[1]), consonants);
                word.ifPresentOrElse(
                        locatedWord -> System.out.println("Found " + locatedWord.word + " in " + locatedWord.filepath),
                        () -> System.out.println("No word found with " + args[2] + " consonants." ) );
                break;
            case "prefix":
                checkArguments(args.length == 4, "Usage: java Exam.java prefix <directory> <prefix> <length>");
                int length = Integer.parseInt(args[3]);
                List<LocatedWord> words = wordsStartingWith(Paths.get(args[1]), args[2], length);
                if( words.size() > length ) {
                    System.out.println( "WARNING: Implementation of wordsStartingWith computes more than " + args[3] + " words!" );
                }
                words.forEach(loc -> System.out.println(loc.word + ":" + loc.filepath));
                break;
            default:
                System.out.println("Unrecognised command: " + args[0] + ". Try java Exam.java help.");
                break;
        }
    }

    // Do not change this method
    private static void checkArguments(Boolean check, String message) {
        if (!check) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Returns the words that appear on every line of a text file contained in the given directory.
     *
     * This method recursively visits a directory to find text files contained in it
     * and its subdirectories (and the subdirectories of these subdirectories,
     * etc.).
     *
     * You must consider only files ending with a ".txt" suffix. You are guaranteed
     * that they will be text files.
     *
     * The method should return a list of LocatedWord objects (defined by the class
     * at the end of this file), where each LocatedWord object should consist of:
     * - a word appearing in every line of a file
     * - the path to the file containing such word.
     *
     * All words appearing on every line of some file must appear in the list: words
     * that can be in the list must be in the list.
     *
     * Words must be considered equal without considering differences between
     * uppercase and lowercase letters. For example, the words "Hello", "hEllo" and
     * "HELLo" must be considered equal to the word "hello".
     *
     * @param dir the directory to search
     * @return a list of words that, within a file inside dir, appear on every line
     *
     * This method uses a BlockingDeque to keep track of the tasks present and then each thread takes a task and goes
     * to the commonWords method I created to do the actual work.
     */
    private static List<LocatedWord> findWordsCommonToAllLines(Path dir) {
        List<LocatedWord> listCommonWords = new ArrayList<>();
        int maxThreads = Runtime.getRuntime().availableProcessors() -1;
        CountDownLatch latch = new CountDownLatch(maxThreads);
        final BlockingDeque <Optional<Path>> tasks = new LinkedBlockingDeque<>();

        IntStream.range(0, maxThreads).forEach(i -> {
            new Thread(() -> {
                try {
                    Optional<Path> task;
                    do {
                        task = tasks.take();
                        task.ifPresent(t -> commonWords(t, listCommonWords));
                    } while (task.isPresent());
                    tasks.add(task);
                }catch (InterruptedException e) {}
                latch.countDown();
            }).start();
        });

        try {
            Files
                    .walk(dir)
                    .filter( Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(path -> tasks.add(Optional.of(path)));
        } catch (IOException e){
            e.printStackTrace();
        }
        tasks.add(Optional.empty());

        try {
            latch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        return listCommonWords;
    }

    /**
     * commonWords walks through a file and splits it into lines which it then reduces to the words there are in
     * common for every line. Last if any words are found they get added to the list it is called with.
     * @param path Path to a file as parameter to work on.
     * @param listCommonWords a list to add the words found to.
     */
    private static void commonWords(Path path, List<LocatedWord> listCommonWords){
        try {
            Files.lines(path)
                   // .peek(t -> System.out.println(Thread.currentThread().toString()))
                    .map(fileLine -> {
                        BreakIterator it = BreakIterator.getWordInstance();
                        it.setText( fileLine );

                        int start = it.first();
                        int end = it.next();
                        HashSet<String> lineSet = new HashSet<>();

                        while( end != BreakIterator.DONE) {
                            String word = fileLine.substring(start, end);
                            if (Character.isLetterOrDigit(word.charAt(0))){

                                lineSet.add(word.toLowerCase());
                            }
                            start = end;
                            end = it.next();
                        }
                        return lineSet;

                    })
                    .reduce((hashSet1, hashSet2) -> wordsInCommon(hashSet1, hashSet2))
                    .ifPresent(reducedHashSet -> {
                            synchronizedArrayListAdd(listCommonWords, reducedHashSet, path);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * My wordsInCommon method takes two sets as parameters and returns a commonSet consisting of the words those two
     * sets have in common.
     */
    private static HashSet<String> wordsInCommon(Set<String> set1, Set<String> set2) {
        HashSet<String> commonSet = new HashSet<>();
        for (String s : set1) {
            if (set2.contains(s)) {
                commonSet.add(s);
            }
        }
        return commonSet;
    }
    //Synchronized method to add the LocatedWords one by one avoiding race conditions
    private static synchronized void synchronizedArrayListAdd(List<LocatedWord> list, HashSet<String> reducedHashSet, Path path) {
        for (String word : reducedHashSet) {
            list.add(new LocatedWord(word, path));
        }
    }

    /** Returns the line with the highest number of letters among all the lines
     * present in the text files contained in a directory.
     *
     * This method recursively visits a directory to find all the text files
     * contained in it and its subdirectories (and the subdirectories of these
     * subdirectories, etc.).
     *
     * You must consider only files ending with a ".txt" suffix. You are
     * guaranteed that they will be text files.
     *
     * The method should return the shortest line (counting only letters) found among all text files.
     * If multiple lines are identified as shortest, the method should return
     * the one that belongs to the file whose name precedes the filename of the other shortest line
     * lexicographically, or if the filename is the same, the line which comes first in the file.
     * To compare strings lexicographically, you can use String::compareTo.
     * See also https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#compareTo(java.lang.String)
     *
     * @param dir the directory to search
     * @return the line with the highest number of letters found among all text files inside of dir
     *
     * I use a BlockingDeque to keep track of the tasks. Each thread then take a task and send to the lineCounter method
     * which separates the file into lines and count them. lineCounter return each file a possible candidate which it
     * then tries to update the tuple bestCandidate which to see if it is the new shortest found.
     */
    private static Location shortestLine(Path dir) {
        Tuple bestCandidate = new Tuple(null, Integer.MAX_VALUE);
        int maxThreads = Runtime.getRuntime().availableProcessors() - 1;
        CountDownLatch latch = new CountDownLatch(maxThreads);
        final BlockingDeque<Optional<Path>> tasks = new LinkedBlockingDeque<>();

        IntStream.range(0, maxThreads).forEach(i -> {
            new Thread(() -> {
                try {
                    Optional<Path> task;
                    do {
                        task = tasks.take();
                        task.ifPresent(t -> {
                            Tuple possibleCandidate = lineCounter(t);
                            bestCandidate.update(possibleCandidate);
                        });
                    } while (task.isPresent());
                    tasks.add(task);
                } catch (InterruptedException e) {
                }
                latch.countDown();
            }).start();
        });
        try {
            Files
                    .walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(path -> tasks.add(Optional.of(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tasks.add(Optional.empty());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return bestCandidate.location;
    }

    /** The Tuple class containing a Location which is the path and linenumber and a value which is the number of letters
     * on that line.
     */
    private static class Tuple{
        Location location;
        int value;
        public Tuple(Location location, int value){
            this.location = location;
            this.value = value;
        }

        /** The synchronized method update which updates the tuple with the shortest line avoiding race conditions.
         * It also checks which file names comes first lexicographically if the lines are equal.
         */
        synchronized public void update(Tuple tuple) {
            if(this.value > tuple.value) {
                this.value = tuple.value;
                this.location = tuple.location;
            }
            if (this.value == tuple.value){
                if (this.location.filepath.getFileName().toString().compareTo(tuple.location.filepath.getFileName().toString()) > 0){
                    this.value = tuple.value;
                    this.location = tuple.location;
                }
            }
        }
    }

    /** My lineCounter method which counts the letters in the line. First I initialize some variables
     * to keep track of which line is the shortest so far in the file given.
     * Then I create an arrayList of type String which is given the lines one by one by the Files.readAllLines.
     * It uses the BreakIterator to run through the lines one by one and counting only letters. The shortest line found
     * is then returned. I am counting only letters, but if a line has 0 letters I chose that counts aswell.
     */
    private static Tuple lineCounter(Path path){
        int bestShortestLineValue = Integer.MAX_VALUE;
        int shortestLineNr = 0;
        int currentShortestLineValue;
        List<String> listOfLines = new ArrayList<>();
        try {
            listOfLines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < listOfLines.size(); i++){
            String line = listOfLines.get(i);
            BreakIterator it = BreakIterator.getWordInstance();
            it.setText( line);
            int start = it.first();
            int end = it.next();
            currentShortestLineValue = 0;

            while( end != BreakIterator.DONE) {
                String word = line.substring(start, end);
                if (Character.isLetterOrDigit(word.charAt(0))) {
                    currentShortestLineValue = currentShortestLineValue + word.length();
                }
                start = end;
                end = it.next();
            }
            if ( currentShortestLineValue < bestShortestLineValue){
                bestShortestLineValue = currentShortestLineValue;
                shortestLineNr = i+1;
            }

        }
        return new Tuple(new Location(path,shortestLineNr),bestShortestLineValue);
    }


    /**
     * Returns an Optional<LocatedWord> (see below) about a word found in the files
     * of the given directory containing the given number of consonants.
     *
     * This method recursively visits a directory to find text files contained in it
     * and its subdirectories (and the subdirectories of these subdirectories,
     * etc.).
     *
     * You must consider only files ending with a ".txt" suffix. You are guaranteed
     * that they will be text files.
     *
     * The method should return an (optional) LocatedWord object (defined by the
     * class at the end of this file), consisting of:
     * - the word found that contains as many consonants as specified by the parameter n (and no more);
     * - the path to the file containing the word.
     *
     * You can consider a letter to be a consonant according to either English or Danish.
     *
     * If a word satisfying the description above can be found, then the method
     * should return an Optional containing the desired LocatedWord. Otherwise, if
     * such a word cannot be found, the method should return Optional.empty().
     *
     * This method should return *as soon as possible*: as soon as a satisfactory
     * word is found, the method should return a result without waiting for the
     * processing of remaining files and/or other data.
     *
     * @param dir the directory to search
     * @param consonants the number of consonants the word must contain
     * @return an optional LocatedWord about a word containing exactly n consonants
     *
     * This method is a little different than the rest because it has to return as soon as possible. So I chose not
     * to use a latch, blockingdeque and a maximum of threads.
     * Instead I create a function that takes a path as input and returns an Optional<LocatedWord>
     * In this function I first read each lines of the file from the path and maps each line to a BreakIterator
     * Which can then call my countingConsonants method on each word.
     * It does so by using a while loop to itereate over all the words in the line and because countingVowels returns
     * a boolean if that if statement is true I can just return the new LocatedWord with the word found and the path
     * the thread is currently working on and exit.
     *
     */

    private static Optional<LocatedWord> wordWithConsonants(Path dir, int consonants) {
        Function<Path, Optional<LocatedWord>> wordFinder = path -> {
            try {
                return Files.lines(path)
                        .map(line -> {
                            BreakIterator it = BreakIterator.getWordInstance();
                            it.setText(line);
                            int start = it.first();
                            int end = it.next();

                            while (end != BreakIterator.DONE) {
                                String word = line.substring(start, end);
                                if (Character.isLetterOrDigit(word.charAt(0))) {
                                    if (countingConsonants(consonants, word)){
                                        return new LocatedWord(word, path);
                                    }
                                }
                                start = end;
                                end = it.next();
                            }
                            return null;
                        })
                        .filter(word -> word != null)
                        .findAny();
            } catch (IOException e) {
                return Optional.empty();
            }
        };

        try {
            return Files.walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .collect(Collectors.toList())
                    .parallelStream()
                    .map (wordFinder)
                    .filter(opt -> opt.isPresent())
                    .findAny()
                    .orElseGet( () -> Optional.empty());

        } catch(IOException e){
            return Optional.empty();
        }

    }

    /** countingConsonants counts the number of consonants in a word by using a for loop and then checks if
     * the count matches the required number given.
     */
    private static boolean countingConsonants( int consonants, String word) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if (letter == 'b' || letter == 'c' || letter == 'd' || letter == 'f' || letter == 'g' || letter == 'h' || letter == 'j'
                    || letter == 'k' || letter == 'l' || letter == 'm' || letter == 'n' || letter == 'p' || letter == 'q'
                    || letter == 'r' || letter == 's' || letter == 't' || letter == 'v' || letter == 'w' || letter == 'x'
                   || letter == 'z') {
                count++;
            }
        }
        if (count == consonants) {
            return true;
        }

        return false;
    }



    /** Returns a list of words found in the given directory starting with the given prefix.
     *
     * This method recursively visits a directory to find text files
     * contained in it and its subdirectories (and the subdirectories of these
     * subdirectories, etc.).
     *
     * You must consider only files ending with a ".txt" suffix. You are
     * guaranteed that they will be text files.
     *
     * The method should return a list of LocatedWord objects (defined by the
     * class at the end of this file), consisting of:
     * - the word found that starts with the given prefix;
     * - the path to the file containing the word.
     *
     * The size of the returned list must not exceed the given limit.
     * Therefore, this method should return *as soon as possible*: if the list
     * reaches the given limit at any point during the computation, no more
     * elements should be added to the list and remaining files and/or other lines
     * should not be analysed.
     *
     * @param dir the directory to search
     * @param prefix the prefix to be searched for
     * @param limit the size limit for the returned list
     * @return a list of locations where the given prefix has been found
     *
     * I use a BlockingDeque to keep track of the tasks available and let each thread take a task which consist of a
     * file which it then uses a BreakIterator to split into words and check if the word begins with the given prefix
     * if so it tries to add it to the list using the synchronized method addToList.
     */
    private static List<LocatedWord> wordsStartingWith(Path dir, String prefix, int limit) {
        int maxThreads = Runtime.getRuntime().availableProcessors() - 1;
        CountDownLatch latch = new CountDownLatch(maxThreads);
        final BlockingDeque<Optional<Path>> tasks = new LinkedBlockingDeque<>();

        IntStream.range(0, maxThreads).forEach(i -> {
            new Thread(() -> {
                try {
                    Optional<Path> task;
                    do {
                        task = tasks.take();
                        task.ifPresent(t -> {
                            try {
                                Files.lines(t)
                                        .forEach(line -> {
                                            BreakIterator it = BreakIterator.getWordInstance();
                                            it.setText(line);
                                            int start = it.first();
                                            int end = it.next();

                                            while (end != BreakIterator.DONE) {
                                                String word = line.substring(start, end);
                                                if (Character.isLetterOrDigit(word.charAt(0))){
                                                    if ( word.startsWith(prefix)){
                                                        LocatedWord locatedWord = new LocatedWord(word, t);
                                                        if( !addToList(locatedWord, limit)){
                                                            return;
                                                        }
                                                    }
                                                }
                                                start = end;
                                                end = it.next();
                                            }

                                        });

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } while (task.isPresent() && listOfWordsBeginsWith.size() < limit);
                    tasks.add(task);
                } catch (InterruptedException e) {
                }
                latch.countDown();
            }).start();
        });

        try {
            Files
                    .walk(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .forEach(path -> tasks.add(Optional.of(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tasks.add(Optional.empty());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return listOfWordsBeginsWith;
    }

    /**
     * The list created outside of the method to be able to make the synchronized addToList method on it.
     */
    private static List<LocatedWord> listOfWordsBeginsWith = new ArrayList();

    /**
     * Synchronized method that adds the word to the list if the length of the list
     * has not surpassed the limit.
     */
    private static synchronized boolean addToList(LocatedWord word, int maxLength){
        if (listOfWordsBeginsWith.size() >= maxLength){
            return false;
        }
        return listOfWordsBeginsWith.add(word);

    }

    // Do not change this class
    private static class LocatedWord {
        private final String word; // the word
        private final Path filepath; // the file where the word has been found

        private LocatedWord(String word, Path filepath) {
            this.word = word;
            this.filepath = filepath;
        }
    }

    // Do not change this class
    private static class Location {
        private final Path filepath; // the file where the word has been found
        private final int line; // the line number at which the word has been found

        private Location(Path filepath, int line) {
            this.filepath = filepath;
            this.line = line;
        }
    }
    // Do not change this class
    private static class InternalException extends RuntimeException {
        private InternalException(String message) {
            super(message);
        }
    }

}


