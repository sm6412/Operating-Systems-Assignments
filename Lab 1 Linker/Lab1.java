import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

// Samira Mantri
// Linker lab 1
// 2/6/2018
public class Lab1 {
	private static ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	private static  Map<String, Integer> map = new HashMap<String, Integer>();
	private static ArrayList<String> symbolList = new ArrayList<String>();
	private static ArrayList<String> undefSymbol = new ArrayList<String>();
	private static Map<Integer,Integer> Addresses = new HashMap<Integer,Integer>();
	private static Map<Integer,Integer> baseAddresses = new HashMap<Integer,Integer>();
	private static Map<String,Integer> symbolLoc = new HashMap<String,Integer>();
	private static Map<ArrayList<String>,Integer> memoryMap = new HashMap<ArrayList<String>,Integer>();
	private static ArrayList<String> errors = new ArrayList<String>();

	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
		// if the user enters no file name the program should print a message
		// explaining that a file name is necessary before terminating
		if (args.length == 0) {
			  System.err.println("Usage Error: the program expects a file name as an argument");
			  System.exit(-1);
		}
		
		// create a file with the entered file name
		File file= new File(args[0]);
		
		// if the file does not exist inform the user and terminate the program
		if(file.exists()!=true){
				System.err.println("Usage Error: the file "+args[0]+" cannot be opened.");
				System.exit(-1);
		}
		// if the file cannot be read inform the user and terminate the program
		else if(file.canRead()!=true){
					System.err.println("Usage Error: the file "+args[0]+" cannot be opened.");
					System.exit(-1);
		}
		// if the file is not a file inform the user and terminate the program
		else if(file.isFile()!=true){
					System.err.println("Usage Error: the file "+args[0]+" cannot be opened.");
					System.exit(-1);
		}
		
		// arrange the input 
		splitline(file);
		
		// conduct pass 1
		pass1(table);
		
		// conduct pass 2
		pass2(table);
	}
	
	static void pass1(ArrayList<ArrayList<String>> list) {
		
		//create symbol table
		// loc will keep track of module
		int loc = 0;
		for(int x=0;x<list.size();x+=3) {
			ArrayList<String> pos = list.get(x);
			if(!(pos.get(0).equals("0"))) {
				for(int y=1;y<pos.size();y++) {
						try {
							// if the element at the position is a number 
							int val = Integer.parseInt(pos.get(y));
							String key = pos.get(y-1);
							
							// check to see if the key exists already
							if(!(map.containsKey(key))) {
								if(!(symbolList.contains(key))) {
									// if we don't have the key add it 
									symbolList.add(key);
									
									// check to see if the definition exceeds the module size
									ArrayList<String> defCheck = list.get(x+2);
									int wordNum = Integer.parseInt(defCheck.get(0));
									if(wordNum<=val) {
										// print error
										System.out.println("Error: In module "+loc+" the def of "+key+" exceeds the module size; zero (relative) used.");
										map.put(key,0);
										symbolLoc.put(key,loc);
										
									}
									else {
										// check to see if the definition exceeds the module size
										ArrayList<String> defCheck2 = list.get(x+2);
										int wordNum2 = Integer.parseInt(defCheck2.get(0));
										if(wordNum2<=val) {
											// print error
											System.out.println("Error: In module "+loc+" the def of "+key+" exceeds the module size; zero (relative) used.");
											
										}
										map.put(key,val);
										symbolLoc.put(key,loc);
									}
								}
								
							}
							else {
								// check to see if the definition exceeds the module size
								ArrayList<String> defCheck2 = list.get(x+2);
								int wordNum2 = Integer.parseInt(defCheck2.get(0));
								if(wordNum2<=val) {
									// print error
									System.out.println("Error: In module "+loc+" the def of "+key+" exceeds the module size; zero (relative) used.");
									
								}
								// print error is variable is multiply defined
								System.out.println("Error: variable "+key+" is multiply defined; first value used.");
							}
						}
						catch(Exception e) {
							// we are dealing with a string, look ahead to see if it will be defined
							try {
								int val = Integer.parseInt(pos.get(y+1));
							}
							catch(Exception e1) {
								// keep tracked of undefined symbols 
								undefSymbol.add(pos.get(y));
							}
							continue;
						}
					}
			}
			loc++;
		}
		
		int addressKey = 1;
		// obtain the moldule size at each of the modules
		for(int x=2; x<list.size();x+=3) {
			ArrayList<String> currentList = list.get(x);
			int num = Integer.parseInt(currentList.get(0));
			Addresses.put(addressKey, num);
			addressKey++;
			
		}
		
		//create map of base addresses that match to module number
		baseAddresses.put(0, 0);
		int c=0;
		for(int x=1;x<Addresses.size()+1;x++) {
			int pos = Addresses.get(x);
			c+=pos;
			baseAddresses.put(x, c);
		}
		
		
		
		// print and update symbol table 
		Iterator iterator = map.keySet().iterator();
		System.out.println("\nSymbol Table");
		while (iterator.hasNext()) {
		   String key = iterator.next().toString();
		   int location = symbolLoc.get(key);
		   int adder = baseAddresses.get(location);
		   map.put(key, (adder+map.get(key)));
		   String value = map.get(key).toString();
		  System.out.println(key + "=" + value);
		}
	}
		

	
	static void pass2(ArrayList<ArrayList<String>> list) {
		System.out.println("\nMemory Map");
		
		// gather uses 
		int loc=0;
		for(int x=2;x<table.size();x+=3) {
			// add uses
			ArrayList<String> currentList = table.get(x);
			// error check
			memoryMap.put(currentList, loc);
			loc++;
		}
		
	
		
		// keep track of which symbols are used 
		Boolean[] used = new Boolean[symbolList.size()];
		for(int a=0; a<symbolList.size();a++) {
			used[a] = false;
		}
		
		
		// create memory map
		int tracker=0;
		int useSize=0;
		int modNum=0;
		for(int x=2;x<table.size();x+=3) {
			ArrayList<String> useList = new ArrayList<String>();
			ArrayList<String> currentPos = table.get(x);
			ArrayList<Boolean> unused = new ArrayList<Boolean>();
			int numOfModules = Integer.parseInt(currentPos.get(0));
			
			
			// create use list
			ArrayList<String> prevPos = table.get(x-1);
			boolean isZero = true;
			
			if(Integer.parseInt(prevPos.get(0))!=0) {
				for(int w=1;w<prevPos.size();w++) {
					// update list to keep track of used symbols
					unused.add(false);
					useList.add(prevPos.get(w));
				}
				useSize = useList.size()-1;
				isZero=false;
				
			}
			else {
				useSize=-1;
				
			}
			
			
			
			
			// check for errors and print final addresses
			for(int y=1;y<currentPos.size();y++) {
				// create case for immediate address
				if(currentPos.get(y).equals("I")) {
					System.out.println(tracker+": "+currentPos.get(y+1));
					tracker++;
				}
				// create case for Absolute address
				if(currentPos.get(y).equals("A")) {
					// make sure absolute address does not exceed machine size
					int num = Integer.parseInt(currentPos.get(y+1));
					int check = num%1000;
					// make sure address does not exceed machine size of 200
					if(check>199) {
						int finalNum = ((Integer.parseInt(currentPos.get(y+1)))/1000)*1000;
						System.out.println(tracker+": "+finalNum+" Error: Absolute address exceeds machine size; zero used.");
					}
					else {
						System.out.println(tracker+": "+currentPos.get(y+1));
					}
					tracker++;
				}
				// create case for relative address
				if(currentPos.get(y).equals("R")) {
					int num = Integer.parseInt(currentPos.get(y+1));
					int check = num%1000;
					// make sure address does not exceed size of module
					if(check>=numOfModules) {
						int finalNum = ((Integer.parseInt(currentPos.get(y+1)))/1000)*1000;
						System.out.println(tracker+": "+finalNum+" Error: Relative address exceeds module size; zero used.");
					}
					else {
						int location = memoryMap.get(table.get(x));
						int adder = baseAddresses.get(location);
						System.out.println(tracker+": "+(num+adder));
					}
					tracker++;
				}
				// create case for external address
				if(currentPos.get(y).equals("E")) {
					int num = Integer.parseInt(currentPos.get(y+1));
					int check = num%10;
					// make sure external address is not too large to reference an entry in use list
					if(check>useSize) {
						System.out.println(tracker+": "+((num/1000)*1000)+" Error: External address exceeds length of use list; treated as immediate.");
					}
					else {
						// add the definition of the symbol to the external address 
						String symbol = useList.get(check);
						if(symbolList.contains(symbol)) {
							int def = map.get(symbol);
							System.out.println(tracker+": "+(((num/1000)*1000)+def));
							
							// record the symbol was used
							int symbolLoc = symbolList.indexOf(symbol);
							used[symbolLoc]=true;
							int symbolLoc2 = useList.indexOf(symbol);
							unused.set(symbolLoc2, true);
						}
						else {
							int symbolLoc2 = useList.indexOf(symbol);
							unused.set(symbolLoc2, true);
							System.out.println(tracker+": "+num+" Error: "+symbol+" is used but not defined; zero used.");
						}
					}
					tracker++;
					
				}
				
			}
			
			// print unused symbols 
			for(int q=0;q<unused.size();q++) {
				if(unused.get(q)==false) {
					String symbol = useList.get(q);
					errors.add("\nWarning: In module "+modNum+" "+symbol+" appeared in the use list but was not actually used.");
				}
			}
			
			modNum++;
			
		}
		
		if(!(errors.isEmpty())) {
			for(int x=0;x<errors.size();x++) {
				System.out.print(errors.get(x));
			}
		}
		
		System.out.println("");
		// print symbols that were defined but never used 
		for(int x=0;x<used.length;x++) {
			if(used[x]==false) {
				String symbol = symbolList.get(x);
				int mod = symbolLoc.get(symbol);
				System.out.println("Warning: "+symbol+" was defined in module "+mod+" but never used.");
			}
		}
		
		
		
			
		
		
	}
	
	
	static void splitline(File file) throws FileNotFoundException{
		// scan in file 
		Scanner input = new Scanner(file);
		
		// retrieve number of modules 
		int numMods = input.nextInt();
		
		for(int x=0;x<numMods;x++) {
			// add line 1 of module to arrayList
			ArrayList<String> line1 = new ArrayList<String>();
			
			// gather number of definitions
			int numDefs = input.nextInt();
			line1.add(Integer.toString(numDefs));
			
			for(int y=0;y<numDefs;y++) {
				// gather symbol
				String symbol = input.next();
				
				// gather definition
				int val = input.nextInt();
				String stringVal = Integer.toString(val);
				line1.add(symbol);
				line1.add(stringVal);
			}
			// add to linker
			table.add(line1);
			
			// add line 2 of module to arrayList
			ArrayList<String> line2 = new ArrayList<String>();
			
			// gather number of uses 
			int numUses= input.nextInt();
			line2.add(Integer.toString(numUses));
			
			for(int q=0;q<numUses;q++) {
				// retrieve use
				String use = input.next();
				line2.add(use);
			}
			
			// add to table of input
			table.add(line2);
			
			// create line 3 and add line 3 of module
			ArrayList<String> line3 = new ArrayList<String>();
			
			// find number of text addresses
			int numText = input.nextInt();
			line3.add(Integer.toString(numText));
			
			for(int a=0;a<numText;a++) {
				// gather type
				String type = input.next();
				// gather address
				int address = input.nextInt();
				line3.add(type);
				line3.add(Integer.toString(address));
			}
			table.add(line3);
			
		}
				
		
	}
		
		









}
	
	
	
	

	



