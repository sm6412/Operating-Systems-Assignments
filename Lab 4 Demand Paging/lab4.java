import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

// Samira Mantri
// Lab 4, Paging
// 4/17/18

public class lab4 {
	// machine size
	private static int machineSize;
	// page size
	private static int pageSize;
	// the size of each process
	private static int proSize;
	// the job mix which determines the prob of A, B, and C
	private static int jobMix;
	// number of references per process 
	private static int refNum;
	// replacement algorithm
	private static String algo;
	// input holds all the random numbers 
	private static Scanner input;
	// the number of frames allowed at a time
	private static int frameNum;
	// contains what A, B, and C represents for J=1
	private static ArrayList<Integer> J1 = new ArrayList<Integer>();
	// contains what A, B, and C represents for J=2
	private static ArrayList<Float> J2 = new ArrayList<Float>();
	// contains what A, B, and C represents for J=3
	private static ArrayList<Float> J3 = new ArrayList<Float>();
	// contains what A, B, and C represents for J=4
	private static ArrayList<ArrayList<Float>> J4 = new ArrayList<ArrayList<Float>>();
	// the next word 
	private static int nextWord = 0;
	// keeps track of next word for J=2,3,4
	private static ArrayList<Integer> ref = new ArrayList<Integer>();
	// keeps track of frames 
	private static ArrayList<ArrayList<Integer>> frameTable = new ArrayList<ArrayList<Integer>>();
	// keeps track of faults per process
	private static ArrayList<Double> faults = new ArrayList<Double>();
	// keeps track of residency time for the different processes
	private static ArrayList<ArrayList<Double>> resident = new ArrayList<ArrayList<Double>>();



	
	public static void main(String[] args) throws FileNotFoundException {
		// assign command line arguments 
		machineSize= Integer.parseInt(args[0]);
		pageSize = Integer.parseInt(args[1]);
		proSize = Integer.parseInt(args[2]);
		jobMix = Integer.parseInt(args[3]);
		refNum = Integer.parseInt(args[4]);
		algo = args[5];
		
		// determine number of frames and add to frame table 
		frameNum = machineSize/pageSize;
		for(int x=0;x<frameNum;x++) {
			ArrayList<Integer> newFrame = new ArrayList<Integer>();
			frameTable.add(newFrame);
		}
		
		// set the fault and resident lists based on the job mix 
		if(jobMix==1) {
			ArrayList<Double> newList = new ArrayList<Double>();
			faults.add((double)0);
			resident.add(newList);
			
		}
		else {
			for(int x=0;x<4;x++) {
				ArrayList<Double> newList = new ArrayList<Double>();
				faults.add((double)0);
				resident.add(newList);
				
			}
		}
		
		// print key info 
		System.out.println("The machine size is "+machineSize);
		System.out.println("The page size is "+pageSize);
		System.out.println("The process size is "+proSize);
		System.out.println("The job mix number is "+jobMix);
		System.out.println("The number of references per process is "+refNum);
		System.out.println("The replacement algorithm is "+algo+"\n");
		
		// create input file holding the random numbers 
		File file = new File("random-numbers.txt");
		input = new Scanner(file);
		
		// set J1, J2, J3, and J4
		setProcesses();
		// start the program 
		start();
		
		// print results
		double totalAvg=0;
		int totalFaults=0;
		if(jobMix==1) {
			double evictions = (double) 0;
			double totalResidencies = (double)0;
			for(int x=0;x<resident.size();x++) {
				ArrayList<Double> current = resident.get(x);
				evictions += current.size();
				for(int y=0; y<current.size();y++) {
					totalResidencies += current.get(y);
				}
				
			}
			// print totals 
			totalAvg = totalResidencies/evictions;
			if(resident.get(0).isEmpty()==true) {
				System.out.println("Process 1 had "+faults.get(0)+" faults. With no evictions, the average residence is undefined.");
				System.out.println("The total number of faults is "+faults.get(0)+". With no evictions, the average residence is undefined.");
			}
			else {
				System.out.println("Process 1 had "+faults.get(0)+" faults and "+totalAvg+" average residency.");
				System.out.println("\nThe total number of faults is "+faults.get(0)+" and the overall average residency is "+totalAvg+".");
			}

		}
		else {
			for(int x=0;x<4;x++) {
				totalFaults+=faults.get(x);
				if(resident.get(x).isEmpty()==true) {
					totalAvg+=0;
					System.out.println("Process "+(x+1)+" had "+faults.get(x)+" faults. With no evictions, the average residence is undefined.");
				}
				else {
					double avg=0;
					for(int y=0; y<resident.get(x).size();y++) {
						ArrayList<Double> current = resident.get(x);
						avg+=current.get(y);
						
					}
					avg = avg/(resident.get(x).size());
					totalAvg+=avg;
					System.out.println("Process "+(x+1)+" had "+faults.get(x)+" faults and "+avg+" average residency.");
				}
			}
		}
		double evictions = (double) 0;
		double totalResidencies = (double)0;
		for(int x=0;x<resident.size();x++) {
			ArrayList<Double> current = resident.get(x);
			evictions += current.size();
			for(int y=0; y<current.size();y++) {
				totalResidencies += current.get(y);
			}
			
		}
		totalAvg = totalResidencies/evictions;
		

		if(jobMix!=1) {
			System.out.println("\nThe total number of faults is "+totalFaults+" and the overall average residency is "+totalAvg+".");
		}

		
		
	}
	
	// function is responsible for running the program 
	private static void start() {
		int time =1;
		// gather results when the job mix is 1
		if(jobMix==1) {
			// set the next word
			nextWord = (111)%proSize;
			for(int ref=0;ref<refNum;ref++) {
				// find page 
				int page = nextWord/pageSize;
				
				// check for fault or hit 
				boolean found = hasFault(page,nextWord,time,0,0);

				// evict and replace
				if(found==false) {
					// use correct algorithm 
					if(algo.equalsIgnoreCase("lru")) {
						lru(page,nextWord,time,0,0);
					}
					if(algo.equalsIgnoreCase("fifo")) {
						fifo(page,nextWord,time,0,0);
					}
					if(algo.equalsIgnoreCase("random")) {
						random(page,nextWord,time,0,0);
					}
				}
				
				// get next ref
				nextRef1(J1);
				// increment time 
				time++;
			}
			return;
		}
		
		
		// set next words for when the job mix is greater than 1
		for(int x=1; x<=4; x++) {
			ref.add((111*x)%proSize);
		}
		
		// handle job mix 2 & 3
		if(jobMix==2 || jobMix==3) {
			// determine which A, B, and C values to use based on the job mix 
			ArrayList<Float> usedlist = new ArrayList<Float>();
			if(jobMix==2) {
				usedlist = J2;
			}
			if(jobMix==3) {
				usedlist = J3;
			}
			
			int adjust1 = refNum % 3;
			int compare = (refNum*4)-(4*adjust1);
			int num = 0;
			for(int ref2=0; ref2<compare; ref2+=3) {
				// handle quantum
				for(int y=0; y<3; y++) {
					// find correct page 
					int page = ref.get(num)/pageSize;
					
					// check for a fault or hit 
					boolean found = hasFault(page,nextWord,time,num,num);

					// evict and replace
					if(found==false) {
						// determine which removal algorithm to use 
						if(algo.equalsIgnoreCase("lru")) {
							lru(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("fifo")) {
							fifo(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("random")) {
							random(page,nextWord,time,num,num);
						}
						
					}
					
					// find next word 
					nextRef(usedlist,num);
					// increment time 
					time++;
				}
				// increment process
				num++;
				if(num==4) {
					num=0;
				}
			}
			
			// iterate through the remaining words 
			for(int x=0; x<4; x++) {
				for(int y=0; y<adjust1; y++) {
					// find page 
					int page = ref.get(num)/pageSize;
			
					// check whether the word is found 
					boolean found = hasFault(page,nextWord,time,num,num);

					// evict and replace
					if(found==false) {
						// determine which removal algorithm to use 
						if(algo.equalsIgnoreCase("lru")) {
							lru(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("fifo")) {
							fifo(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("random")) {
							random(page,nextWord,time,num,num);
						}
						
					}
					// get next word 
					nextRef(usedlist,num);
					time++;
				}
				// increment process
				num++;
				if(num==4) {
					num=0;
				}
			}
		}
		
		// handle jobMix 4
		if(jobMix==4) {
			int adjust2 = refNum % 3;
			int compare = (refNum*4)-(4*adjust2);
			int num = 0;
			for(int ref2=0; ref2<compare; ref2+=3) {
				// handle quantum
				for(int y=0; y<3; y++) {
					// find correct page 
					int page = ref.get(num)/pageSize;
				
					// check whether the word is found 
					boolean found = hasFault(page,nextWord,time,num,num);

					// evict and replace
					if(found==false) {
						// determine which removal algorithm to use 
						if(algo.equalsIgnoreCase("lru")) {
							lru(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("fifo")) {
							fifo(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("random")) {
							random(page,nextWord,time,num,num);
						}
						
					}
					// gather next word 
					nextRef(J4.get(num),num);
					time++;
				}
				// increment process 
				num++;
				if(num==4) {
					num=0;
				}
			}
			
			// iterate through rest of words 
			for(int x=0; x<4; x++) {
				for(int y=0; y<adjust2; y++) {
					// discover page 
					int page = ref.get(num)/pageSize;
					
					// check whether the word is found 
					boolean found = hasFault(page,nextWord,time,num,num);

					// evict and replace
					if(found==false) {
						// determine which removal algorithm to use 
						if(algo.equalsIgnoreCase("lru")) {
							lru(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("fifo")) {
							fifo(page,nextWord,time,num,num);
						}
						if(algo.equalsIgnoreCase("random")) {
							random(page,nextWord,time,num,num);
						}
					}
					// find next word 
					nextRef(J4.get(num),num);
					time++;
				}
				// increment process
				num++;
				if(num==4) {
					num=0;
				}
			}
		}
	}
	
	// returns a boolean which determines the process is found in the frame table
	// or not
	private static boolean hasFault(int page, int word, int time, int num, int process) {
		// create to determine if the process is found or not
		boolean found = false;
		
		// search through frame table for the process
		for(int x=0;x<frameTable.size();x++) {
			ArrayList<Integer> current = frameTable.get(x);
			// we get a match when the word and page match
			if(current.isEmpty()==false && current.get(0)==page && current.get(4)==num) {
				found = true;
				// adjust time when the process is used 
				current.set(3, time);
			}
		}
		
		// if the list does not contain the element, see if there is 
		// room to add it
		if(found==false) {
			// iterate through frame table 
			for(int x = frameTable.size()-1; x >= 0; x--) {
				ArrayList<Integer> current = frameTable.get(x);
				if(current.isEmpty()==true) {
					found = true; 
					// add new frame
					addFrame(x,page,nextWord,time,num);
					// increment number of faults for process
					faults.set(process, (double)(faults.get(process)+1));
					break;
				}
			}
		}
		return found;
	}
	
	// least recently used replacement algorithm
	private static void lru(int page, int nextWord,int time,int pos, int process) {
		// set a variable to hold the process with the longest
		// time since it was last accessed 
		int oldest = 0;
		for(int x=1; x<frameTable.size();x++) {
			if(frameTable.get(x).get(3) <= frameTable.get(oldest).get(3)) {
				oldest = x;
			}
		}
		
		// add to resident list for the evicted process 
		double addVal = (double)time-((double)frameTable.get(oldest).get(2));
		resident.get(frameTable.get(oldest).get(4)).add(addVal);
		
		// evict
		frameTable.get(oldest).clear();
		
		// replace
		addFrame(oldest,page,nextWord,time,pos);
		
		// increment the process' faults 
		faults.set(process, (double)(faults.get(process)+1));
		
	}
	
	// first in first out replacement algorithm
	private static void fifo(int page, int nextWord, int time, int pos, int process) {
		// set earliest equal to the index of the process with the earliest arrival
		// into the frame table
		int earliest = 0;
		for(int x=1; x<frameTable.size();x++) {
			if(frameTable.get(x).get(2) <= frameTable.get(earliest).get(2)) {
				earliest = x;
			}
		}
		
		// add to resident list for the evicted process 
		double addVal = (double)time-((double)frameTable.get(earliest).get(2));
		resident.get(frameTable.get(earliest).get(4)).add(addVal);
		
		//evict
		frameTable.get(earliest).clear();
		
		// replace
		addFrame(earliest,page,nextWord,time,pos);
		
		// increment the process' faults 
		faults.set(process, (double)(faults.get(process)+1));
		
	}
	
	// random replacement algorithm uses random values to determine which process is removed from
	// the frame table 
	private static void random(int page, int nextWord, int time, int pos, int process) {
		// gather random number 
		int randomNum = input.nextInt();
		// use random number to determine which frame is evicted
		// from the page table
		int evictFrame = randomNum % frameNum;
	
		// add to resident list for the evicted process 
		double addVal = (double)time-((double)frameTable.get(evictFrame).get(2));
		resident.get(frameTable.get(evictFrame).get(4)).add(addVal);
		
		// evict
		frameTable.get(evictFrame).clear();
		
		// replace
		faults.set(process, (double)(faults.get(process)+1));
		
		// increment the process' faults 
		addFrame(evictFrame,page,nextWord,time,pos);
	
	}
	
	// add a frame to the frame table 
	private static void addFrame(int pos, int page, int word, int time, int proNum) {
		// page
		frameTable.get(pos).add(page);
		// word
		frameTable.get(pos).add(word);
		// when entered
		frameTable.get(pos).add(time);
		// when last used
		frameTable.get(pos).add(time);
		// add process number 
		frameTable.get(pos).add(proNum);
		
	}
	

	
	// function gets the next word when the job mix is 1
	private static void nextRef1(ArrayList<Integer> list) {
		long r = Long.parseLong(input.next());
	
		double y = r/(Integer.MAX_VALUE+1d);
		if(y<list.get(0)) {
			nextWord = (nextWord + 1 + proSize) % proSize;
		}
		else if(y<(list.get(0)+list.get(1))) {
			nextWord = (nextWord - 5 + proSize) % proSize;
		}
		else if(y<(list.get(0)+list.get(1)+list.get(2))) {
			nextWord = (nextWord + 4 + proSize) % proSize;
		}
		else{
			r = Long.parseLong(input.next());
			int randomRef = (int) (r % proSize);
			nextWord = randomRef;
		}
	}
	
	// function gets the next word when the job mix is greater than 1
	private static void nextRef(ArrayList<Float> list, int pos) {
		long r = Long.parseLong(input.next());
	
		double y = r/(Integer.MAX_VALUE+1d);

		if(y<list.get(0)) {
			ref.set(pos, (ref.get(pos) + 1 + proSize)%proSize);
		
		}
		else if(y<(list.get(0)+list.get(1))) {
			ref.set(pos, (ref.get(pos) - 5 + proSize)%proSize);
			
		}
		else if(y<(list.get(0)+list.get(1)+list.get(2))) {
			ref.set(pos, (ref.get(pos) + 4 + proSize)%proSize);
		}
		else{
			r = Long.parseLong(input.next());
			int randomRef = (int) (r % proSize);
			ref.set(pos, randomRef);
			
		}
	}
	
	// set A, B, and C values based on the job mix 
	private static void setProcesses() {
		// handle J1
		J1.add(1);
		J1.add(0);
		J1.add(3);
		
		// handle J2
		J2.add((float)1);
		J2.add((float)0);
		J2.add((float)0);
		
		// handle J3
		J3.add((float)0);
		J3.add((float)0);
		J3.add((float)0);
		
		// handle J4
		ArrayList<Float> pro1 = new ArrayList<Float>();
		pro1.add((float) .75);
		pro1.add((float) .25);
		pro1.add((float) 0);
		J4.add(pro1);
		
		ArrayList<Float> pro2 = new ArrayList<Float>();
		pro2.add((float) .75);
		pro2.add((float) 0);
		pro2.add((float) .25);
		J4.add(pro2);
		
		ArrayList<Float> pro3 = new ArrayList<Float>();
		pro3.add((float) .75);
		pro3.add((float) .125);
		pro3.add((float) .125);
		J4.add(pro3);
		
		ArrayList<Float> pro4 = new ArrayList<Float>();
		pro4.add((float) .5);
		pro4.add((float) .125);
		pro4.add((float) .125);
		J4.add(pro4);
		
	}
	
	

	


}




