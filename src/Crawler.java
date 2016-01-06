import java.util.*;
import java.io.*;
import java.net.URL;

/*
 * public class for crawling website and sub pages for email addresses
 * 
 * to compile:
 * in terminal, navigate to the /src/ folder and type "javac Crawler.java"
 * 
 * to run:
 * in terminal, navigate to the /src/ folder and type "java Crawler [website]"
 * where [website] is replaced by the site you wish to crawl (in simple format, i.e. do not include http://)
 * 
 * java/online resources aided in understanding java url class
 * 
 */

public class Crawler {
	
	//declare global vars
	private HashSet<String> emails;
	private HashSet<String> visited;
	private Queue<String> domains;
	private String initDomain;
	
	//construct w/file name given
	public Crawler(String file) {
		//instantiate globals
		emails = new HashSet<String>();
		visited = new HashSet<String>();
		domains = new PriorityQueue<String>();
		
		//add init domain to queue
		if (file.contains("http://") || file.contains("https://")) {
			//record init domain
			initDomain = file;
			
			domains.add(file);
		}
		else {
			//record init domain
			initDomain = "http://" + file;
			
			domains.add("http://" + file);
		}
	}
	
	//adds email to set
	public void addEmail(String newArg) {
		emails.add(newArg);
	}
	
	//prints all emails in set
	public void printEmails() {
		for (String email : emails) {
			System.out.println(email);
		}
	}
	
	//method to crawl and read emails for init domain and subdomains
	public void readEmails() throws IOException {
		//init locals
		URL initURL;
		InputStream inputStream;
		BufferedReader inputHTML;
		
		//while the domain queue isn't empty
		while (!domains.isEmpty()) {
			//get first domain
			String file = domains.remove();
			//if it hasn't been visited before
			if (!visited.contains(file)) {
				try {
					//init url stream
					initURL = new URL(file);
					
						inputStream = initURL.openStream();
					inputHTML = new BufferedReader(new InputStreamReader(inputStream));
					
					int curr = inputHTML.read();
					//while not at end of page
					while (curr != -1) {
						String currWord = "";
						//cut white space
						while ((char)curr == ' ') {
							curr = inputHTML.read();
						}
						//while it's a single word, add onto current word
						while ((char)curr != ' ' && curr != 10 && curr != 9 && curr != -1) {
							currWord += (char)curr;
							curr = inputHTML.read();
						}
						//if it's a valid email
						if (validEmail(currWord)) {
							//cut off excess ("" or <> or :)
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
							//if it is still email address add it to set
							if (secondCheck(currWord)) {
								this.addEmail(currWord);
							}
						}
						else if (validDomain(currWord)) {
							//if it's not an email but is a valid domain, cut off excess
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
							if (!valid(currWord)) {
								currWord = initDomain + currWord;
							}
							//and add it domains list
							if (valid(currWord) && ((currWord.contains("http://") || currWord.contains("https://")))) {
								if (!visited.contains(currWord)) {
									domains.add(currWord);
								}
							}
							else if (valid(currWord)) {
								if (!visited.contains("http://" + currWord)) {
									domains.add("http://" + currWord);
								}
								
							}
						}
						//read next char
						curr = inputHTML.read();
					}
					//add file to visited
					visited.add(file);
					//close input stream
					inputStream.close();
				}
				catch (IOException e) {
					System.err.println("Error loading website: " + file);
				}
				catch(IllegalArgumentException e) {
					System.err.println("Error loading website: " + file);
				}
			}
		}
	}
	
	//check if it's a valid email
	public boolean validEmail(String word) {
		if (word.contains("@") && valid(word)) {
			return true;
		}
		else return false;
	}
	
	//check again after cutting excess
	public boolean secondCheck(String word) {
		if (word.endsWith(".com") || word.endsWith(".edu") ||
				word.endsWith(".org") || word.endsWith(".net") ||
				word.endsWith(".gov") || word.endsWith(".int") ||
				word.endsWith(".mil")) {
			return true;
		}
		else return false;
	}
	
	//check if valid domain
	public boolean validDomain(String currWord) {
		if (currWord.contains("href")) {
			if (valid(currWord) && !currWord.contains(initDomain)) {
				return false;
			}
			else return true;
		}
		else return false;
	}
	
	//check if it is a domain at all
	public boolean valid(String word) {
		if (word.contains(".com") || word.contains(".edu") ||
				word.contains(".org") || word.contains(".net") ||
				word.contains(".gov") || word.contains(".int") ||
				word.contains(".mil")) {
			return true;
		}
		else return false;
	}
	
	//main method call
	public static void main(String args[]) {
		
		//if there are no args provided, return error
		if (args.length == 0) {
			System.err.println("No website provided. Try again with a website.");
		}
		
		else {
			//instantiate crawler with arg
			Crawler myCrawler = new Crawler(args[0]);
			try {
				//read emails, print emails
				myCrawler.readEmails();
				myCrawler.printEmails();
			} catch (IOException e) {
				System.err.println("Reading emails failed. Please try again.");
			}
		}
		
	}
	
}
