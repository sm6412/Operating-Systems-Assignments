import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
// Samira Mantri

public final class Lab2 {
	private static int numPro=0;
	private static boolean flag=false;
	private static Map<Integer,ArrayList<Integer>> list = new HashMap<Integer,ArrayList<Integer>>();
	private static Map<Integer,ArrayList<Integer>> list2 = new HashMap<Integer,ArrayList<Integer>>();
	private static Scanner input1;
	private static Scanner input2;
	private static Scanner input3;
	private static Scanner input4;

	public static void main(String[] args) throws FileNotFoundException {
		File file;
		if(args.length==1) {
			// create a file with the entered file name
			file= new File(args[0]);
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
		}
		else {
			// create a file with the entered file name
			file= new File(args[1]);
			// check to see whether verbose flag appears
			Scanner input = new Scanner(args[0]);
			String flagString = input.next();
			
			// set flag to true if so
			if(flagString.equalsIgnoreCase("--verbose")) {
				flag=true;
			}
			// if the file does not exist inform the user and terminate the program
			if(file.exists()!=true){
					System.err.println("Usage Error: the file "+args[1]+" cannot be opened.");
					System.exit(-1);
			}
			// if the file cannot be read inform the user and terminate the program
			else if(file.canRead()!=true){
						System.err.println("Usage Error: the file "+args[1]+" cannot be opened.");
						System.exit(-1);
			}
			// if the file is not a file inform the user and terminate the program
			else if(file.isFile()!=true){
						System.err.println("Usage Error: the file "+args[1]+" cannot be opened.");
						System.exit(-1);
			}
		}
		

		
		// create arrayList
		splitFile(file);
		
		// create list of random numbers
		File random = new File("random-numbers.txt");
		
		// create inputs with the random numbers list 
		input1 = new Scanner(random);
		input2 = new Scanner(random);
		input3 = new Scanner(random);
		input4 = new Scanner(random);

		
		// run algorithms 
		fcfs();
		rr();
		uni();
		psjf();
		
		
		

	}
	
	private static void splitFile(File file) throws FileNotFoundException {
		// scan in file 
		Scanner input = new Scanner(file);
		
		// retrieve number of modules 
		numPro = input.nextInt();
		
		int key = 0;
		for(int x=0;x<numPro;x++) {
			// add line 1 of module to arrayList
			ArrayList<Integer> job = new ArrayList<Integer>();
		    for(int y=0;y<4;y++) {
				// gather symbol
				int next = input.nextInt();
                job.add(next);
			}
		    list.put(key, job);
			key++;
		}
		// sort input 
		sort();
	}
	
	// create random functions 
	private static int randomOS1(int U) {
		long X = input1.nextLong();
		int val = (int) (1+(X%U));
		return val;
	}
	
	private static int randomOS2(int U) {
		long X = input2.nextLong();
		int val = (int) (1+(X%U));
		return val;
	}
	
	private static int randomOS3(int U) {
		long X = input3.nextLong();
		int val = (int) (1+(X%U));
		return val;
	}
	
	private static int randomOS4(int U) {
		long X = input4.nextLong();
		int val = (int) (1+(X%U));
		return val;
	}
	
	// sort processes
	private static void sort() {
		// create arrayList processes' start times
		ArrayList<ArrayList<Integer>> start = new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<numPro;i++) {
			ArrayList<Integer> val = list.get(i);
			start.add(val);
			
		}
		
		// send to bubble sort to sort by start time 
		bubbleSort(start);
		
		
		// create new map with sorted processes
		for(int x=0; x<start.size();x++) {
			ArrayList<Integer> element = start.get(x);
			list2.put(x, element);
		}
	
	}
    
	// create bubblesort 
	public static ArrayList<ArrayList<Integer>> bubbleSort(ArrayList<ArrayList<Integer>> ar){
	   for (int i = (ar.size() - 1); i >= 0; i--){
	      for (int j = 1; j <= i; j++){
	         if (ar.get(j-1).get(0) > ar.get(j).get(0)){
	              ArrayList<Integer> temp = ar.get(j-1);
	              ar.set(j-1, ar.get(j));
	              ar.set(j, temp);
	         } 
	      } 
	   } 
	   return ar;
	}
	
	
	// first come first served 
	private static void fcfs() {
		// create queue
		Queue<Integer> pQ = new LinkedList<Integer>();
		// create list of blocked processes and their io bursts
		ArrayList<Integer> blocked1 = new ArrayList<Integer>();
		ArrayList<Integer> blocked2 = new ArrayList<Integer>();
		// create list of started processes
		ArrayList<Integer> startedPro = new ArrayList<Integer>();
		
		// create arrayList of starting times
		int[] start = new int[numPro];
		for(int i=0;i<start.length;i++) {
			int val = ((list2.get(i).get(0)));
			if(val>0) {
				start[i]=val+1;
			}
			else {
				start[i]=val;
			}
		}
		
		// keep track of terminations 
		Map<Integer,Integer> terminationList = new HashMap<Integer,Integer>();
		
		 // keep track of io time
		Map<Integer,Integer> io = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			io.put(x, 0);
		}
		
		// keep track of waiting times 
		Map<Integer,Integer> wait = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			wait.put(x, 0);
		}
		
		// keep track of turnaround times 
		Map<Integer,String> state = new HashMap<Integer,String>();
		for(int x=0;x<numPro;x++) {
			state.put(x, "unstarted");
		}
		
		// keep track of cpu and io utilization 
		float cpuUtil = 0;
		float ioUtil = 0;
		
		// create arrayList for cpu time
		ArrayList<Integer> timeLeft = new ArrayList<Integer>();
		for(int i=0;i<list2.size();i++) {
			timeLeft.add((list2.get(i).get(2)));
		}
		
		// keep track of num of terminations 
		int terminations=0;
		// cycle
		int cycle=0;
		// time for a new run?
		boolean newRun = true;
		// current cpu burst
		int cpuBurst=0;
		boolean started=false;
		// running process
		int head=0;
		int adder = 0;
		// detailed string 
		String detail = "";
		
		// print out info about the processes and sorted processes
		System.out.print("The original input was: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.print("\n");
		
		System.out.print("The (sorted) input is: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list2.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.println("\n");
		detail+="This detailed printout gives the state and remaining burst for each process\n";
		detail+="\n";
		
		while(true) {
			detail+="Before cycle "+cycle+":";
			// add processes that start at cycle to the queue
			for(int x=0;x<start.length;x++) {
				if(adder<start.length && !(startedPro.contains(adder))) {
					if(start[adder]<=cycle) {
						pQ.add(adder);
						startedPro.add(adder);
						adder++;
					}
				}
			}
			
			// create the detailed summary
			if(cycle==0) {
				for(int x=0; x<numPro;x++) {
					detail+=" unstarted 0";

				}
				detail+=".";
			}
			else {
				// get queue to see whats ready
				for(int x=0; x<numPro;x++) {
					if(pQ.contains(x)) {
						state.put(x, "ready");
					}
				}
				
				// get what's currently running
				state.put(head, "running");
				
				// see what's blocked 
				for(int x=0; x<blocked1.size();x++) {
					int val = blocked1.get(x);
					state.put(val,"blocked");
				}
				// retrieve detail
				for(int x=0;x<numPro;x++) {
					if(state.get(x).equalsIgnoreCase("running")) {
						detail+=" running "+cpuBurst;
					}
					else if(state.get(x).equals("unstarted")) {
						detail+=" unstarted 0";
					}
					else if(state.get(x).equalsIgnoreCase("ready")) {
						detail+=" ready 0";
					}
					else if(state.get(x).equalsIgnoreCase("terminating")) {
						detail+=" terminated 0";
					}
					else {
						int index = blocked1.indexOf(x);
						detail+=" blocked "+blocked2.get(index);
					}
				}
				detail+=".\n";
			}
			
			if(cycle>0) {
				// see what is waiting
				for(int x=0;x<numPro;x++) {
					if(pQ.contains(x)) {
						int cur = wait.get(x)+1;
						wait.put(x, cur);
					}
				}
			}

			// running starts when the queue has processes
			if(pQ.isEmpty()==false) {
				started = true;
			}
			
			// handle terminating
			if(timeLeft.get(head)-1==0 && !(blocked1.contains(head))) {
				terminations++;
				terminationList.put(head, cycle);
				newRun=true;
				cpuBurst=0;
				state.put(head, "terminating");
				
				// break if everything terminated 
				if(terminations==numPro) {
					break;
				}
			}

			
			// handle blocked state
			if(blocked1.isEmpty()==false) {
				ioUtil++;
				ArrayList<Integer> removeList = new ArrayList<Integer>();
				for (int x=0; x<blocked1.size();x++) {
				    int pro = blocked1.get(x);
				    int currentBurst = blocked2.get(x);
					int newBurst = currentBurst-1;
					int ioTime = (io.get(pro))+1;
					io.put(pro, ioTime);
					if(newBurst==0) {
						blocked1.remove(x);
						blocked2.remove(x);
						// add to list to remove and add to queue
						removeList.add(pro);
						if(x==0) {
							x=-1;
						}
					}
					else {
						// if io is not done keep in blocked state
						blocked2.set(x, newBurst);
					}
				}
				
				// remove processes and put in queue
				if(removeList.isEmpty()==false) {
					Collections.sort(removeList);
					for(int x=0;x<removeList.size();x++) {
						int addElement = removeList.get(x);
						pQ.add(addElement);
					}
				}
			}
			

			// time to run
			if(cpuBurst>0) {
				cpuUtil++;
				int cpuTime = timeLeft.get(head);
				int newVal = cpuTime-1;
				timeLeft.set(head, newVal);
				int num = 1;
				cpuBurst = cpuBurst-num;
				// if cpu burst is zero, it is time to block
				if(cpuBurst==0) {
					int origIoBurst = (list2.get(head)).get(3);
					int ioBurst = randomOS1(origIoBurst);
					// put in blocked state
					blocked1.add(head);
					blocked2.add(ioBurst);
					newRun = true;
				}
				// if the cpu burst is not finished, we continue 
				else {
					newRun=false;	
					
				}
			}
			
			// grab from queue when time to run new process
			if(newRun==true && pQ.isEmpty()==false) {
				head = pQ.poll();
				// find cpu burst time
				int origCpuBurst = (list2.get(head)).get(1);
				cpuBurst = randomOS1(origCpuBurst);
				if(cpuBurst>timeLeft.get(head)) {
					cpuBurst = timeLeft.get(head);
				}
			}
			
			// increase cycle
			cycle++;
		}
		
		// create map for turnaround times 
		Map<Integer,Integer> turn = new HashMap<Integer,Integer>();
		if(flag==true) {
			System.out.println(detail);
		}
		
		// show process info
		System.out.println("The scheduling algorithm used was First Come First Served\n");
		for(int x=0;x<numPro;x++) {
			System.out.println("Process "+x+":");
			System.out.println("   (A,B,C,IO) = "+list2.get(x));
			System.out.println(("   Finishing time: "+terminationList.get(x)));
			int turnaround = terminationList.get(x) - list2.get(x).get(0);
			turn.put(x, turnaround);
			System.out.println("   Turnaround time: "+turnaround);
			System.out.println("   I/O time: "+io.get(x));
			System.out.println("   Waiting time: "+wait.get(x)+"\n");
		}
		
		// show summary info
		System.out.println("Summary Data:");
		System.out.println("   Finishing time: "+cycle);
		System.out.printf("   CPU Utilization: %.6f\n", (cpuUtil+numPro)/cycle);
		System.out.printf("   I/O Utilization: %.6f\n", ioUtil/cycle);
		// calculate throughput
		System.out.printf("   Throughput: %.6f processes per hundred cycles\n", (((float)numPro/(float)cycle)*100));
		// calculate turnaround time 
		int sum=0;
		for(int x=0; x<numPro; x++) {
			sum = sum + turn.get(x);
		}
		System.out.printf("   Average turnaround time: %.6f\n", (float)sum/(float)numPro);
		// find average waiting time 
		int sum2=0;
		for(int x=0; x<numPro; x++) {
			sum2 = sum2 + wait.get(x);
		}
		System.out.printf("   Average waiting time: %.6f\n", (float)sum2/(float)numPro);
		System.out.println("\n");
		
	}
	
	// round robin
	private static void rr() {
		Queue<Integer> pQ = new LinkedList<Integer>();
		ArrayList<Integer> blocked1 = new ArrayList<Integer>();
		ArrayList<Integer> blocked2 = new ArrayList<Integer>();
		ArrayList<Integer> startedPro = new ArrayList<Integer>();
		

		// create arrayList of starting times
		int[] start = new int[numPro];
		for(int i=0;i<numPro;i++) {
			int val = ((list2.get(i).get(0)));
			if(val>0) {
				start[i]=val+1;
			}
			else {
				start[i]=val;
			}
		}
		
		// keep track of terminations 
		Map<Integer,Integer> terminationList = new HashMap<Integer,Integer>();
		
		// keep track of terminations 
		Map<Integer,Integer> burstList = new HashMap<Integer,Integer>();
		
			
		// create arrayList for cpu time
		ArrayList<Integer> timeLeft = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++) {
			timeLeft.add((list.get(i).get(2)));
		}
		 // keep track of io time
		Map<Integer,Integer> io = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			io.put(x, 0);
		}
		
		// keep track of waiting times 
		Map<Integer,Integer> wait = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			wait.put(x, 0);
		}
		
		// keep track of turnaround times 
		Map<Integer,String> state = new HashMap<Integer,String>();
		for(int x=0;x<numPro;x++) {
			state.put(x, "unstarted");
		}
		
		// keep track of cpu and io utilization 
		float cpuUtil = 0;
		float ioUtil = 0;
		
		
		// keep track of terminations 
		int terminations=0;
		// cycle
		int cycle=0;
		boolean newRun = true;
		boolean started=false;
		int head=0;
		int adder = 0;
		int q=2;
		boolean isZero = false;
		String detail = "";
		
		// print processes and sorted processes
		System.out.print("The original input was: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.print("\n");
		
		System.out.print("The (sorted) input is: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list2.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.println("\n");
		detail+="This detailed printout gives the state and remaining burst for each process\n";
		detail+="\n";
		
		
		while(true) {
			// create remove and add list
			ArrayList<Integer> removeList = new ArrayList<Integer>();
			ArrayList<Integer> addList = new ArrayList<Integer>();
			detail+="Before cycle "+cycle+":";
			
			// add processes that start at cycle to the queue
			for(int x=0;x<start.length;x++) {
				if(adder<start.length && !(startedPro.contains(adder))) {
					if(start[adder]<=cycle) {
						pQ.add(adder);
						startedPro.add(adder);
						adder++;
					}
				}
			}
			
			// add process data to detailed summary
			if(cycle==0) {
				for(int x=0; x<numPro;x++) {
					detail+=" unstarted 0";

				}
				detail+=".\n";
			}
			else {
				// get queue to see whats ready
				for(int x=0; x<numPro;x++) {
					if(pQ.contains(x)) {
						state.put(x, "ready");
					}
				}
				
				// get whats currently running
				state.put(head, "running");
				
				// see whats blocked 
				for(int x=0; x<blocked1.size();x++) {
					int val = blocked1.get(x);
					state.put(val,"blocked");
				}
				// print detail
				for(int x=0;x<numPro;x++) {
					if(state.get(x).equalsIgnoreCase("running")) {
						if(list2.get(head).get(1)==1) {
							detail+=" running 1";
						}
						else {
							detail+=" running "+q;
						}
						
					}
					else if(state.get(x).equals("unstarted")) {
						detail+=" unstarted 0";
					}
					else if(state.get(x).equalsIgnoreCase("ready")) {
						detail+=" ready 0";
					}
					else if(state.get(x).equalsIgnoreCase("terminating")) {
						detail+=" terminated 0";
					}
					else {
						int index = blocked1.indexOf(x);
						detail+=" blocked "+blocked2.get(index);
					}
				}
				detail+=".\n";
			}
			
			if(cycle>0) {
				// see what is waiting
				for(int x=0;x<numPro;x++) {
					if(pQ.contains(x)) {
						int cur = wait.get(x)+1;
						wait.put(x, cur);
					}
				}
			}
			
			// handle terminating
			if(timeLeft.get(head)-1==0 && !(blocked1.contains(head))) {
				terminations++;
				terminationList.put(head, cycle);
				newRun=true;
				state.put(head, "terminating");
				// set burst to 0
				burstList.put(head, 0);
				// reset quantum
				q=2;
				// break if everything terminated 
				if(terminations==numPro) {
					break;
				}
			}

			
			// handle blocked state
			if(blocked1.isEmpty()==false) {
				ioUtil++;
				for (int x=0; x<blocked1.size();x++) {
				    int pro = blocked1.get(x);
				    int currentBurst = blocked2.get(x);
					int newBurst = currentBurst-1;
					int ioTime = (io.get(pro))+1;
					io.put(pro, ioTime);
					if(newBurst==0) {
						blocked1.remove(x);
						blocked2.remove(x);
						//put back in queue
						addList.add(pro);
						if(x==0) {
							x=-1;
						}
					}
					else {
						// if io is not done keep in blocked state
						blocked2.set(x, newBurst);
					}
				}        
			
			}
			
		
			// time to run
			if(started==true) {
				if(burstList.get(head)>0) {
					cpuUtil++;
					int cpuTime = timeLeft.get(head);
					int newVal = cpuTime-1;
					timeLeft.set(head, newVal);
					int num = 1;
					int cpuBurst = burstList.get(head)-num;
					// if cpu burst is zero, it is time to block
					q--;
					if(cpuBurst==0) {
						int origIoBurst = (list2.get(head)).get(3);
						int ioBurst = randomOS2(origIoBurst);
						// put in blocked state
						blocked1.add(head);
						blocked2.add(ioBurst);
						// reset burst
						burstList.put(head, 0);
						newRun = true;
						q=2;
					}
					// if the cpu burst is not finished, we continue 
					else {
						// change burst
						burstList.put(head, cpuBurst);
						newRun=false;	
						
					}
					// handle quantum
					if(q==0) {
						if(newRun==false) {
							q=2;
							newRun=true;
							addList.add(head);

						}
					}
				}
			}
			
			// tie breaker 
			Collections.sort(addList);
			for(int x=0;x<addList.size();x++) {
				int element = addList.get(x);
				pQ.add(element);
			}
			

			// handle quantum
			if(q==0) {
				if(newRun==false) {
					q=2;
					newRun=true;
					pQ.add(head);

				}
			}
			
			isZero=false;
						

			// grab from queue when time to run new process
			if(newRun==true && pQ.isEmpty()==false && q==2) {
			    started = true; 
				head = pQ.poll();
				if(burstList.get(head)==null) {
					// find cpu burst time
					int origCpuBurst = (list2.get(head)).get(1);
					int cpuBurst = randomOS2(origCpuBurst);
					if(cpuBurst>timeLeft.get(head)) {
						cpuBurst = timeLeft.get(head);
					}
					// put cpuBurst in list
					burstList.put(head, cpuBurst);
				}
				if(burstList.get(head)==0) {
					// find cpu burst time
					int origCpuBurst = (list2.get(head)).get(1);
					int cpuBurst = randomOS2(origCpuBurst);
					if(cpuBurst>timeLeft.get(head)) {
						cpuBurst = timeLeft.get(head);
					}
					// put cpuBurst in list
					burstList.put(head, cpuBurst);
				}

			}
			
			if(newRun==true && q!=2) {
				q=2;
			}
			
			cycle++;
		}
		
		if(flag==true) {
			System.out.println(detail);
		}
		// create map to hold turnaround data 
		Map<Integer,Integer> turn = new HashMap<Integer,Integer>();
		// show process info
		System.out.println("The scheduling algorithm used was Round Robbin\n");
		for(int x=0;x<numPro;x++) {
			System.out.println("Process "+x+":");
			System.out.println("   (A,B,C,IO) = "+list2.get(x));
			System.out.println(("   Finishing time: "+terminationList.get(x)));
			int turnaround = terminationList.get(x) - list2.get(x).get(0);
			turn.put(x, turnaround);
			System.out.println("   Turnaround time: "+turnaround);
			System.out.println("   I/O time: "+io.get(x));
			System.out.println("   Waiting time: "+wait.get(x)+"\n");
		}
		
		// show summary info
		System.out.println("Summary Data:");
		System.out.println("   Finishing time: "+cycle);
		System.out.printf("   CPU Utilization: %.6f\n", (cpuUtil+numPro)/cycle);
		System.out.printf("   I/O Utilization: %.6f\n", ioUtil/cycle);
		// calculate throughput
		System.out.printf("   Throughput: %.6f processes per hundred cycles\n", (((float)numPro/(float)cycle)*100));
		// calculate turnaround time 
		int sum=0;
		for(int x=0; x<numPro; x++) {
			sum = sum + turn.get(x);
		}
		System.out.printf("   Average turnaround time: %.6f\n", (float)sum/(float)numPro);
		// find average waiting time 
		int sum2=0;
		for(int x=0; x<numPro; x++) {
			sum2 = sum2 + wait.get(x);
		}
		System.out.printf("   Average waiting time: %.6f\n", (float)sum2/(float)numPro);
		System.out.println("\n");
	}
	
	
    
	
	// uniprogrammed
	private static void uni() {
		// create queue
		Queue<Integer> pQ = new LinkedList<Integer>();
		// create blocked lists
		ArrayList<Integer> blocked1 = new ArrayList<Integer>();
		ArrayList<Integer> blocked2 = new ArrayList<Integer>();
		ArrayList<Integer> startedPro = new ArrayList<Integer>();
		

		
		// create arrayList of starting times
		int[] start = new int[numPro];
		for(int i=0;i<numPro;i++) {
			int val = ((list2.get(i).get(0)));
			if(val>0) {
				start[i]=val+1;
			}
			else {
				start[i]=val;
			}
			
		}
		
		// keep track of terminations 
		Map<Integer,Integer> terminationList = new HashMap<Integer,Integer>();
		
			
		// create arrayList for cpu time
		ArrayList<Integer> timeLeft = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++) {
			timeLeft.add((list.get(i).get(2)));
		}
		
		 // keep track of io time
		Map<Integer,Integer> io = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			io.put(x, 0);
		}

		
		// keep track of waiting times 
		Map<Integer,Integer> wait = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			wait.put(x, 0);
		}
		
		// keep track of turnaround times 
		Map<Integer,String> state = new HashMap<Integer,String>();
		for(int x=0;x<numPro;x++) {
			state.put(x, "unstarted");
		}
		
		// keep track of cpu and io utilization 
		float cpuUtil = 0;
		float ioUtil = 0;
		
		// set terminations originally equal to 0
		int terminations=0;
		// cycle
		int cycle=0;
		boolean newRun = true;
		int cpuBurst=0;
		boolean started=false;
		int head=0;
		int adder = 0;
		int a=0;
		boolean running=false;
		String detail="";
		
		// print out info
		System.out.print("The original input was: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.print("\n");
		
		System.out.print("The (sorted) input is: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list2.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.println("\n");
		detail+="This detailed printout gives the state and remaining burst for each process\n";
		detail+="\n";
		
		while(true) {
			detail+="Before "+cycle+":";
			// add processes that start at cycle to the queue
			for(int x=0;x<start.length;x++) {
				if(adder<start.length && !(startedPro.contains(adder))) {
					if(start[adder]<=cycle) {
						pQ.add(adder);
						startedPro.add(adder);
						adder++;
					}
				}
			}
			
			// create detailed summary
			if(cycle==0) {
				for(int x=0; x<numPro;x++) {
					detail+=" unstarted 0";

				}
				detail+=".\n";
			}
			else {
				// get queue to see whats ready
				for(int x=0; x<numPro;x++) {
					if(pQ.contains(x)) {
						state.put(x, "ready");
					}
				}
				
				// get whats currently running
				state.put(head, "running");
				
				// see whats blocked 
				for(int x=0; x<blocked1.size();x++) {
					int val = blocked1.get(x);
					state.put(val,"blocked");
				}
				// gather detail
				for(int x=0;x<numPro;x++) {
					if(state.get(x).equalsIgnoreCase("running")) {
						if(list2.get(head).get(1)==1) {
							detail+=" running 1";
						}
						else {
							detail+=" running "+cpuBurst;
						}
						
					}
					else if(state.get(x).equals("unstarted")) {
						detail+=" unstarted 0";
					}
					else if(state.get(x).equalsIgnoreCase("ready")) {
						detail+=" ready 0";
					}
					else if(state.get(x).equalsIgnoreCase("terminating")) {
						detail+=" terminated 0";
					}
					else {
						int index = blocked1.indexOf(x);
						detail+=" blocked "+blocked2.get(index);
					}
				}
				detail+=".\n";
			}
			
			if(cycle>0) {
				// see what is waiting
				for(int x=0;x<numPro;x++) {
					if(pQ.contains(x)) {
						int cur = wait.get(x)+1;
						wait.put(x, cur);
					}
				}
			}
			
			// running starts when the queue has processes
			if(pQ.isEmpty()==false) {
				started = true;
			}
			
			// handle terminating
			if(timeLeft.get(head)-1==0 && !(blocked1.contains(head))) {
				terminations++;
				terminationList.put(head, cycle);
				newRun=true;
				cpuBurst=0;
				state.put(head, "terminating");
				
				// break if everything terminated 
				if(terminations==numPro) {
					break;
				}
			}

			
			// handle blocked state
			if(blocked1.isEmpty()==false) {
				ioUtil++;
				for (int x=0; x<blocked1.size();x++) {
				    int pro = blocked1.get(x);
				    int currentBurst = blocked2.get(x);
					int newBurst = currentBurst-1;
					int ioTime = (io.get(pro))+1;
					io.put(pro, ioTime);
					if(newBurst==0) {
						blocked1.remove(x);
						blocked2.remove(x);
					}
					else {
						// if io is not done keep in blocked state
						blocked2.set(x, newBurst);
					}
				}
				
			}
			

			// time to run
			if(cpuBurst>0) {
				cpuUtil++;
				// process is running so we cannot run a new one
				// until it finishes 
				newRun=false;
				int cpuTime = timeLeft.get(head);
				int newVal = cpuTime-1;
				timeLeft.set(head, newVal);
				int num = 1;
				cpuBurst = cpuBurst-num;
				// if cpu burst is zero, it is time to block
				if(cpuBurst==0) {
					int origIoBurst = (list2.get(head)).get(3);
					int ioBurst = randomOS3(origIoBurst);
					// put in blocked state
					blocked1.add(head);
					blocked2.add(ioBurst);
					running=false;
				}
				else {
					running=true;
				}

			}
			
			
			// grab from queue when time to run new process
			if(newRun==true && pQ.isEmpty()==false) {
				head = pQ.poll();
				// find cpu burst time
				int origCpuBurst = (list2.get(head)).get(1);
				cpuBurst = randomOS3(origCpuBurst);
				if(cpuBurst>timeLeft.get(head)) {
					cpuBurst = timeLeft.get(head);
				}
				
			}
			
		    // if the same process is run set it up again 
			if(newRun==false && blocked1.isEmpty()==true && running==false) {
				int origCpuBurst = (list2.get(head)).get(1);
				cpuBurst = randomOS3(origCpuBurst);
				if(cpuBurst>timeLeft.get(head)) {
					cpuBurst = timeLeft.get(head);
				}
			}
			cycle++;

		}
		
		if(flag==true) {
			System.out.println(detail);
		}
		
		// create map to hold turnaround
		Map<Integer,Integer> turn = new HashMap<Integer,Integer>();
		// show process info
		System.out.println("The scheduling algorithm used was Uniprocessor\n");
		for(int x=0;x<numPro;x++) {
			System.out.println("Process "+x+":");
			System.out.println("   (A,B,C,IO) = "+list2.get(x));
			System.out.println(("   Finishing time: "+terminationList.get(x)));
			int turnaround = terminationList.get(x) - list2.get(x).get(0);
			turn.put(x, turnaround);
			System.out.println("   Turnaround time: "+turnaround);
			System.out.println("   I/O time: "+io.get(x));
			System.out.println("   Waiting time: "+wait.get(x)+"\n");
		}
		
		// show summary info
		System.out.println("Summary Data:");
		System.out.println("   Finishing time: "+cycle);
		System.out.printf("   CPU Utilization: %.6f\n", (cpuUtil+numPro)/cycle);
		System.out.printf("   I/O Utilization: %.6f\n", ioUtil/cycle);
		// calculate throughput
		System.out.printf("   Throughput: %.6f processes per hundred cycles\n", (((float)numPro/(float)cycle)*100));
		// calculate turnaround time 
		int sum=0;
		for(int x=0; x<numPro; x++) {
			sum = sum + turn.get(x);
		}
		System.out.printf("   Average turnaround time: %.6f\n", (float)sum/(float)numPro);
		// find average waiting time 
		int sum2=0;
		for(int x=0; x<numPro; x++) {
			sum2 = sum2 + wait.get(x);
		}
		System.out.printf("   Average waiting time: %.6f\n", (float)sum2/(float)numPro);
		System.out.println("\n");
	}
	
	
	
	private static void psjf() {
		// create queue
		ArrayList<Integer> pQ = new ArrayList<Integer>();
		// create blocked lists that hold blocked processes and their data
		ArrayList<Integer> blocked1 = new ArrayList<Integer>();
		ArrayList<Integer> blocked2 = new ArrayList<Integer>();
		ArrayList<Integer> startedPro = new ArrayList<Integer>();
		

		// create arrayList of starting times
		int[] start = new int[numPro];
		for(int i=0;i<numPro;i++) {
			int val = ((list2.get(i).get(0)));
			start[i]=val;
		}
		
		// keep track of terminations 
		Map<Integer,Integer> terminationList = new HashMap<Integer,Integer>();
		
			
		// create arrayList for cpu time
		ArrayList<Integer> timeLeft = new ArrayList<Integer>();
		for(int i=0;i<list2.size();i++) {
			timeLeft.add((list2.get(i).get(2)));
			
		}
		
		// keep track of terminations 
		Map<Integer,Integer> burstList = new HashMap<Integer,Integer>();
		
		 // keep track of io time
		Map<Integer,Integer> io = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			io.put(x, 0);
		}
		
		
		 // keep track of ready
		Map<Integer,Integer> ready = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			ready.put(x, 0);
		}
		
		// keep track of waiting times 
		Map<Integer,Integer> wait = new HashMap<Integer,Integer>();
		for(int x=0;x<numPro;x++) {
			wait.put(x, 0);
		}
		
		// keep track of turnaround times 
		Map<Integer,String> state = new HashMap<Integer,String>();
		for(int x=0;x<numPro;x++) {
			state.put(x, "unstarted");
		}
		
		// keep track of cpu and io utilization 
		float cpuUtil = 0;
		float ioUtil = 0;
		
		
		// keep track of terminations 
		int terminations=0;
		// cycle
		int cycle=0;
		boolean newRun = true;
		int cpuBurst=0;
		int head=0;
		int adder = 0;
		int a=0;
		int newHead=0;
		boolean started = false;
		String detail = "";
		
		// print out info
		System.out.print("The original input was: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.print("\n");
		
		System.out.print("The (sorted) input is: "+numPro+"  ");
		for(int x=0;x<numPro;x++) {
			ArrayList<Integer> position = list2.get(x);
			for(int y=0;y<4;y++) {
				System.out.print(position.get(y)+" ");
			}
			System.out.print("  ");
		}
		System.out.println("\n");
		detail+="This detailed printout gives the state and remaining burst for each process\n";
		detail+="\n";
		
		while(true) {
			detail+="Before cycle "+cycle+":";
			if(cycle==0) {
				for(int x=0; x<numPro;x++) {
					detail+=" unstarted 0";

				}
				detail+=".\n";
			}
			else {
				// get queue to see whats ready
				for(int x=0; x<numPro;x++) {
					if(pQ.contains(x)) {
						state.put(x, "ready");
					}
				}
				
				// get whats currently running
				state.put(head, "running");
				
				// see whats blocked 
				for(int x=0; x<blocked1.size();x++) {
					int val = blocked1.get(x);
					state.put(val,"blocked");
				}
				// print detail
				for(int x=0;x<numPro;x++) {
					if(state.get(x).equalsIgnoreCase("running")) {
						detail+=" running "+burstList.get(head);
					}
					else if(state.get(x).equals("unstarted")) {
						detail+=" unstarted 0";
					}
					else if(state.get(x).equalsIgnoreCase("ready")) {
						detail+=" ready "+ready.get(x);
					}
					else if(state.get(x).equalsIgnoreCase("terminating")) {
						detail+=" terminated 0";
					}
					else {
						int index = blocked1.indexOf(x);
						detail+=" blocked "+blocked2.get(index);
					}
				}
				detail+=".\n";
			}
			
			if(cycle>0) {
				// see what is waiting
				for(int x=0;x<numPro;x++) {
					if(pQ.contains(x)) {
						int cur = wait.get(x)+1;
						wait.put(x, cur);
					}
				}
			}

			// handle terminating
			if(timeLeft.get(head)-1==0 && !(blocked1.contains(head))) {
				terminations++;
				terminationList.put(head, cycle);
				state.put(head, "terminating");
				burstList.put(head, 0);
				timeLeft.set(head, 0);
				newRun=true;
				cpuBurst=0;
				
				// break if everything terminated 
				if(terminations==numPro) {
					break;
				}
			}

			
			// handle blocked state
			if(blocked1.isEmpty()==false) {
				ioUtil++;
				ArrayList<Integer> removeList = new ArrayList<Integer>();
				for (int x=0; x<blocked1.size();x++) {
					//System.out.println(x);
				    int pro = blocked1.get(x);
				    int currentBurst = blocked2.get(x);
					int newBurst = currentBurst-1;
					int ioTime = (io.get(pro))+1;
					io.put(pro, ioTime);
					if(newBurst==0) {
						blocked1.remove(x);
						blocked2.remove(x);
						// put in remove list
						removeList.add(pro);
						if(x==0) {
							x=-1;
						}
					}
					else {
						// if io is not done keep in blocked state
						blocked2.set(x, newBurst);
					}
				}
				
				// remove processes
				if(removeList.isEmpty()==false) {
					Collections.sort(removeList);
					for(int x=0;x<removeList.size();x++) {
						int addElement = removeList.get(x);
						pQ.add(addElement);
					}
				}
			}
			
			// add processes that start at cycle to the queue
			for(int x=0;x<start.length;x++) {
				if(adder<start.length && !(startedPro.contains(adder))) {
					if(start[adder]<=cycle) {
						pQ.add(adder);
						startedPro.add(adder);
						adder++;
					}
				}
			}
			
			// sort queue by time left
			if(pQ.size()>1) {
				ArrayList<Integer> copy = pQ;
				Collections.sort(copy);
				newHead = timeSort(copy,timeLeft);
			}
			
			if(pQ.size()==1) {
				newHead = 0;
			}
			
			
			// time to run
			if(burstList.get(head)!=null && burstList.get(head)>0) {
				cpuUtil++;
				int cpuTime = timeLeft.get(head);
				int newVal = cpuTime-1;
				timeLeft.set(head, newVal);
				int num = 1;
				cpuBurst = burstList.get(head)-num;
				// if cpu burst is zero, it is time to block
				if(cpuBurst==0) {
					int origIoBurst = (list2.get(head)).get(3);
					int ioBurst = randomOS4(origIoBurst);
					// put in blocked state
					blocked1.add(head);
					blocked2.add(ioBurst);
					burstList.put(head, 0);
					newRun = true;
					ready.put(head, 0);
				}
				// if the cpu burst is not finished, we continue 
				else {
					burstList.put(head, cpuBurst);
					// check to see if next run will have a new process
					if(pQ.isEmpty()==false) {
						// get the element in the queue with the shortest time
						int compare  = pQ.get(newHead);
						int time = timeLeft.get(compare);
						int currentTime = timeLeft.get(head);
						if(currentTime<time) {
							newRun = false;

						}
						else {
							ready.put(head, ready.get(head)+1);
							newRun = true;
							pQ.add(head);
						}
					}
					else {
						newRun = false;
					}
					
				}
			}
			
			// grab new process
			if(newRun==true && pQ.size()==1) {
				started = true;
				head = pQ.get(0);
				pQ.remove(0);
				if(burstList.get(head)==null || burstList.get(head)==0) {
					// find cpu burst time
					int origCpuBurst = (list2.get(head)).get(1);
					cpuBurst = randomOS4(origCpuBurst);
					if(cpuBurst>timeLeft.get(head)) {
						cpuBurst = timeLeft.get(head);
					}
					burstList.put(head, cpuBurst);
				}
			}
			
			// grab from queue when time to run new process
			if(newRun==true && pQ.size()>1) {
				head = pQ.get(newHead);
				pQ.remove(newHead);
				if(burstList.get(head)==null || burstList.get(head)==0) {
					// find cpu burst time
					int origCpuBurst = (list2.get(head)).get(1);
					cpuBurst = randomOS4(origCpuBurst);
					if(cpuBurst>timeLeft.get(head)) {
						cpuBurst = timeLeft.get(head);
					}
					burstList.put(head, cpuBurst);
				}
			}
			cycle++;
			
		}
		
		if(flag==true) {
			System.out.println(detail);
		}
		Map<Integer,Integer> turn = new HashMap<Integer,Integer>();
		// show process info
		System.out.println("The scheduling algorithm used was Preemptive Shortest Job First\n");
		for(int x=0;x<numPro;x++) {
			System.out.println("Process "+x+":");
			System.out.println("   (A,B,C,IO) = "+list2.get(x));
			System.out.println(("   Finishing time: "+terminationList.get(x)));
			int turnaround = terminationList.get(x) - list2.get(x).get(0);
			turn.put(x, turnaround);
			System.out.println("   Turnaround time: "+turnaround);
			System.out.println("   I/O time: "+io.get(x));
			System.out.println("   Waiting time: "+wait.get(x)+"\n");
		}
		
		// show summary info
		System.out.println("Summary Data:");
		System.out.println("   Finishing time: "+cycle);
		System.out.printf("   CPU Utilization: %.6f\n", (cpuUtil+numPro)/cycle);
		System.out.printf("   I/O Utilization: %.6f\n", ioUtil/cycle);
		// calculate throughput
		System.out.printf("   Throughput: %.6f processes per hundred cycles\n", (((float)numPro/(float)cycle)*100));
		// calculate turnaround time 
		int sum=0;
		for(int x=0; x<numPro; x++) {
			sum = sum + turn.get(x);
		}
		System.out.printf("   Average turnaround time: %.6f\n", (float)sum/(float)numPro);
		// find average waiting time 
		int sum2=0;
		for(int x=0; x<numPro; x++) {
			sum2 = sum2 + wait.get(x);
		}
		System.out.printf("   Average waiting time: %.6f\n", (float)sum2/(float)numPro);
	}
	
	private static int timeSort(ArrayList<Integer> q, ArrayList<Integer> t){
		int max = q.get(0);
		int pos=0;
		int timeL = t.get(max);
		for(int x=1; x<q.size();x++) {
			int val = q.get(x);
			int time = t.get(val);
			if(time<timeL) {
				pos = x;
				max = val;
				timeL = time;
			}

		}
		return pos;
	}
}

 















