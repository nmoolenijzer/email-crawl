import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Crawler {

	private HashSet<String> emails;
	private HashSet<String> visited;
	private Queue<String> domains;
	private String initDomain;
	
	public Crawler(String file) {
		initDomain = file;
		emails = new HashSet<String>();
		visited = new HashSet<String>();
		domains = new PriorityQueue<String>();
		domains.add("http://" + file);
	}
	
	public void addEmail(String newArg) {
		emails.add(newArg);
	}
	
	public void printEmails() {
		for (String email : emails) {
			System.out.println(email);
		}
	}
	
	public void readEmails() throws IOException {
		URL initURL;
		InputStream inputStream;
		BufferedReader inputHTML;
		while (!domains.isEmpty()) {
			String file = domains.remove();
			if (!visited.contains(file)) {
				try {
					initURL = new URL(file);
					inputStream = initURL.openStream();
					inputHTML = new BufferedReader(new InputStreamReader(inputStream));
					
					int curr = inputHTML.read();
					
					while (curr != -1) {
						String currWord = "";
						while ((char)curr == ' ') {
							curr = inputHTML.read();
						}
						while ((char)curr != ' ' && curr != 10 && curr != 9 && curr != -1) {
							currWord += (char)curr;
							curr = inputHTML.read();
						}
						if (validEmail(currWord)) {
							if (currWord.length() > 0 && currWord.contains("\"")) {
								while (currWord.length() > 1 && currWord.charAt(0) != '"') {
									currWord = currWord.substring(1, currWord.length());
								}
								currWord = currWord.substring(1, currWord.length());
								int i = 0;
								while (i < currWord.length() && currWord.charAt(i) != '"') { i++; }
								currWord = currWord.substring(0, i);
							}
							if (currWord.length() > 0 && currWord.contains("<")) {
								while (currWord.length() > 1 && currWord.charAt(0) != '<') {
									currWord = currWord.substring(1, currWord.length());
								}
								currWord = currWord.substring(1, currWord.length());
								int i = 0;
								while (i < currWord.length() && currWord.charAt(i) != '>') { i++; }
								currWord = currWord.substring(0, i);
							}
							if (currWord.length() > 0 && currWord.contains(":")) {
								while (currWord.length() > 1 && currWord.charAt(0) != ':') {
									currWord = currWord.substring(1, currWord.length());
								}
								currWord = currWord.substring(1, currWord.length());
							}
							if (secondCheck(currWord)) {
								this.addEmail(currWord);
							}
						}
						else if (validDomain(currWord)) {
							if (currWord.length() > 0 && currWord.contains("\"")) {
								while (currWord.length() > 1 && currWord.charAt(0) != '"') {
									currWord = currWord.substring(1, currWord.length());
								}
								currWord = currWord.substring(1, currWord.length());
								int i = 0;
								while (i < currWord.length() && currWord.charAt(i) != '"') { i++; }
								currWord = currWord.substring(0, i);
							}
							if (currWord.length() > 0 && currWord.contains("<")) {
								while (currWord.length() > 1 && currWord.charAt(0) != '<') {
									currWord = currWord.substring(1, currWord.length());
								}
								currWord = currWord.substring(1, currWord.length());
								int i = 0;
								while (i < currWord.length() && currWord.charAt(i) != '>') { i++; }
								currWord = currWord.substring(0, i);
							}
							if (validDomain(currWord) && (currWord.contains("http://") || currWord.contains("https://"))) {
								domains.add(currWord);
							}
							else if (validDomain(currWord)) {
								domains.add("http://" + currWord);
							}
						}
						curr = inputHTML.read();
					}
					visited.add(file);
					inputStream.close();
				}
				catch (IOException e) {
					System.err.println("Error loading website: " + file);
				}
			}
		}
	}
	
	public boolean validEmail(String word) {
		if (word.contains("@") && valid(word)) {
			return true;
		}
		else return false;
	}
	
	public boolean secondCheck(String word) {
		if (word.endsWith(".com") || word.endsWith(".edu") ||
				word.endsWith(".org") || word.endsWith(".net") ||
				word.endsWith(".gov") || word.endsWith(".int") ||
				word.endsWith(".mil")) {
			return true;
		}
		else return false;
	}
	
	public boolean validDomain(String currWord) {
		if (currWord.contains(initDomain) && !currWord.contains("." + initDomain)) {
			return true;
		}
		else return false;
	}
	
	public boolean valid(String word) {
		if (word.contains(".com") || word.contains(".edu") ||
				word.contains(".org") || word.contains(".net") ||
				word.contains(".gov") || word.contains(".int") ||
				word.contains(".mil")) {
			return true;
		}
		else return false;
	}
	
	public static void main(String args[]) {
		
		if (args.length == 0) {
			System.err.println("No website provided. Try again with a website.");
		}
		
		else {
			Crawler myCrawler = new Crawler(args[0]);
			try {
				myCrawler.readEmails();
				myCrawler.printEmails();
			} catch (IOException e) {
				System.err.println("Reading emails failed. Please try again.");
			}
		}
		
	}
	
}
