import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/*
This is the exam for DM563 - Concurrent Programming, Spring 2022.
Your task is to implement the following methods of class Exam:
- findWordsCommonToAllLines;
- longestLine;
- wordWithVowels;
- wordsEndingWith.
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
https://github.com/fmontesi/cp2022/tree/main/exam
*/
public class Exam {
	// Do not change this method
	public static void main(String[] args) {
		checkArguments(args.length > 0,
				"You must choose a command: help, allLines, longestLine, vowels, or suffix.");
		switch (args[0]) {
			case "help":
				System.out.println(
						"Available commands: help, allLines, longestLine, vowels, or suffix.\nFor example, try:\n\tjava Exam allLines data");
				break;
			case "allLines":
				checkArguments(args.length == 2, "Usage: java Exam.java allLines <directory>");
				List<LocatedWord> uniqueWords = findWordsCommonToAllLines(Paths.get(args[1]));
				System.out.println("Found " + uniqueWords.size() + " words");
				uniqueWords.forEach( locatedWord ->
					System.out.println( locatedWord.word + ":" + locatedWord.filepath ) );
				break;
			case "longestLine":
				checkArguments(args.length == 2, "Usage: java Exam.java longestLine <directory>");
				Location location = longestLine(Paths.get(args[1]));
				System.out.println("Line with highest number of letters found at " + location.filepath + ":" + location.line );
				break;
			case "vowels":
				checkArguments(args.length == 3, "Usage: java Exam.java vowels <directory> <vowels>");
				int vowels = Integer.parseInt(args[2]);
				Optional<LocatedWord> word = wordWithVowels(Paths.get(args[1]), vowels);
				word.ifPresentOrElse(
						locatedWord -> System.out.println("Found " + locatedWord.word + " in " + locatedWord.filepath),
						() -> System.out.println("No word found with " + args[2] + " vowels." ) );
				break;
			case "suffix":
				checkArguments(args.length == 4, "Usage: java Exam.java suffix <directory> <suffix> <length>");
				int length = Integer.parseInt(args[3]);
				List<LocatedWord> words = wordsEndingWith(Paths.get(args[1]), args[2], length);
				if( words.size() > length ) {
					System.out.println( "WARNING: Implementation of wordsEndingWith computes more than " + args[3] + " words!" );
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
	 */
	private static List<LocatedWord> findWordsCommonToAllLines(Path dir) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
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
	 * The method should return the longest line (counting only letters) found among all text files.
	 * If multiple lines are identified as longest, the method should return
	 * the one that belongs to the file whose name precedes the filename of the other longest line
	 * lexicographically, or if the filename is the same, the line which comes first in the file.
	 * To compare strings lexicographically, you can use String::compareTo.
	 * See also https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#compareTo(java.lang.String)
	 *
	 * @param dir the directory to search
	 * @return the line with the highest number of letters found among all text files inside of dir
	 */
	private static Location longestLine(Path dir) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
	}

	/**
	 * Returns an Optional<LocatedWord> (see below) about a word found in the files
	 * of the given directory containing the given number of vowels.
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
	 * - the word found that contains as many vowels as specified by the parameter n (and no more);
	 * - the path to the file containing the word.
	 *
	 * You can consider a letter to be a vowel according to either English or Danish.
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
	 * @param vowels the number of vowels the word must contain
	 * @return an optional LocatedWord about a word containing exactly n vowels
	 */
	private static Optional<LocatedWord> wordWithVowels(Path dir, int vowels) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
	}

	/** Returns a list of words found in the given directory ending with the given suffix.
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
	 * - the word found that ends with the given suffix;
	 * - the path to the file containing the word.
	 *
	 * The size of the returned list must not exceed the given limit.
	 * Therefore, this method should return *as soon as possible*: if the list
	 * reaches the given limit at any point during the computation, no more
	 * elements should be added to the list and remaining files and/or other lines
	 * should not be analysed.
	 *
	 * @param dir the directory to search
	 * @param suffix the suffix to be searched for
	 * @param limit the size limit for the returned list
	 * @return a list of locations where the given suffix has been found
	 */
	private static List<LocatedWord> wordsEndingWith(Path dir, String suffix, int limit) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
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