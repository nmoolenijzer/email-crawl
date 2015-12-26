import java.util.*;
import java.io.*;

public class Crawler {
	
	private HashSet<String> emails;
	private String filePath;
	private FileReader inputHTML;
	
	public Crawler(String file) {
		emails = new HashSet<String>();
		filePath = file;
		try {
			inputHTML = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			System.err.println("Error loading website.");
		}
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
		try {
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
				if (currWord.contains("@")) {
					this.addEmail(currWord);
				}
				curr = inputHTML.read();
				
			}
		}
		finally {
			inputHTML.close();
		}
	}
	
	public static void main(String args[]) {
		
		if (args.length == 0) {
			System.err.println("No website provided. Try again with a website.");
		}
		
		else {
			Crawler myCrawler = new Crawler("../about.html");
			try {
				myCrawler.readEmails();
				myCrawler.printEmails();
			} catch (IOException e) {
				System.err.println("Reading emails failed. Please try again.");
			}
		}
		
	}
	
}
