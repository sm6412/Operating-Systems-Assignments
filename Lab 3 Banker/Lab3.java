import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Samira Mantri
// Banker Lab 

public class Lab3 {
	// keep track of tasks
	private static int tasks;
	// keep track of resources 
	private static int resources;
	// create arrayList to keep track of number of resources 
	private static ArrayList<Integer> origUnits = new ArrayList<Integer>();
	private static ArrayList<Integer> units = new ArrayList<Integer>();
	private static ArrayList<Integer> units2 = new ArrayList<Integer>();
	private static ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
	// create arrayList to keep track of where initiate occurs
	private static ArrayList<Integer> initiate = new ArrayList<Integer>();
	private static ArrayList<Integer> initiate2 = new ArrayList<Integer>();
	// create arrayList to keep track of where initiate positions occur
	private static ArrayList<ArrayList<Integer>> initiatePos = new ArrayList<ArrayList<Integer>>();
	// create arrayList to keep track of resources permitted 
	private static ArrayList<ArrayList<Integer>> amountPermitted = new ArrayList<ArrayList<Integer>>();
	
	public static void main(String[] args) throws FileNotFoundException {
		
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
		// read information from the file
		read(file);
		
		// run first come first served
		fcfs();
		// run banker 
		banker();

	}
	
	// this function is responsible for reading the input file and assigning its information
	// to the appropriate variables that will be utilized by the fcfs and banker function
	// it does so with the help of the scanner 
	private static void read(File file) throws FileNotFoundException {
		// scan in file 
		Scanner input = new Scanner(file);
		
		// collect tasks
		tasks= input.nextInt();
		
		// collect number of resource types
		resources = input.nextInt();
		
		// put resources into an array that will hold 
		// how many resources of what type we have at each position
		if(resources>1) {
			for(int x=0;x<resources;x++) {
				//collect number of units
				units.add(input.nextInt());
				
			}
		}
		else {
			units.add(input.nextInt());
	
		}
		
		// copy the info from units into an array that will hold
		// the resources no matter what changes occur
		// copy into units2 that will be used by the banker 
		for(int x=0;x<units.size();x++) {
			origUnits.add(units.get(x));
			units2.add(units.get(x));
		}
		
	
		
		// put each line of the input file into a large arraylist 
		while(input.hasNext()) {
			ArrayList<String> mini = new ArrayList<String>();
			String activity = input.next();
			if(activity.equalsIgnoreCase("initiate")) {
				mini.add("initiate");
				for(int x=0;x<4;x++) {
					mini.add(input.next());
				}
			}
			else if(activity.equalsIgnoreCase("request")) {
				mini.add("request");
				for(int x=0;x<4;x++) {
					mini.add(input.next());
				}
			}
			else if(activity.equalsIgnoreCase("release")) {
				mini.add("release");
				for(int x=0;x<4;x++) {
					mini.add(input.next());
				}
			}
			else if(activity.equalsIgnoreCase("terminate")){
				mini.add("terminate");
				for(int x=0;x<4;x++) {
					mini.add(input.next());
				}
			}
			list.add(mini);
			
		}
		
		// gather positions where initiate is called 
		for(int x=0;x<tasks;x++) {
			ArrayList<Integer> newList = new ArrayList<Integer>();
			initiatePos.add(newList);
			
		}
		
		int inCounter=0;
		// fill initiate arrayList with positions when they occur
		// handle when multiple resources 
		if(resources>1) {
			for(int x=0;x<list.size()-1;x++) {
				// grab arrayList
				ArrayList<String> current = list.get(x);
				String current_action = current.get(0);
				if(current_action.equalsIgnoreCase("initiate")) {
					int claim = Integer.parseInt(current.get(4));
					int taskNum = Integer.parseInt(current.get(1))-1;
					// get correct array
					ArrayList<Integer> currentList = initiatePos.get(taskNum);
					currentList.add(claim);
					inCounter++;
					if(inCounter==1) {
						initiate.add(x);
						initiate2.add(x);
					}
					else if(inCounter==resources) {
						inCounter=0;
					}
				}

			}
		}
		// handle when only one resource 
		else {
			for(int x=0;x<list.size()-1;x++) {
				// grab arrayList
				ArrayList<String> current = list.get(x);
				String current_action = current.get(0);

				if(current_action.equalsIgnoreCase("initiate")) {
					initiate.add(x);
					initiate2.add(x);
					int claim = Integer.parseInt(current.get(4));
					int taskNum = Integer.parseInt(current.get(1))-1;
					
					// get correct array
					ArrayList<Integer> currentList = initiatePos.get(taskNum);
					currentList.add(claim);
				}
			}
		}
		
		// handle amount permitted
		for(int x=0;x<units.size();x++) {
			ArrayList<Integer> newList = new ArrayList<Integer>();
			amountPermitted.add(newList);
		}
		
	
		
		

		
	}
	
	// fcfs does not check for safe states and instead grants requests as they come
	// by checking how many resources are currently available
	private static void fcfs() {
		// create a queue to hold info
  		ArrayList<Integer> q = new ArrayList<Integer>();
  		// creating arrayList that will hold elements to be
  		// added to the queue
  		ArrayList<Integer> addQ = new ArrayList<Integer>();
  		// holds elements to be removed from the blocked list
  		ArrayList<Integer> removeB = new ArrayList<Integer>();
  		// hold blocked tasks
		ArrayList<Integer> blocked = new ArrayList<Integer>();
		// list that contains when tasks terminate
		ArrayList<Integer> terminated = new ArrayList<Integer>();
		for(int x=0;x<tasks;x++) {
			terminated.add(0);
		}
		// list that contains how long blocked tasks have waited 
		ArrayList<Integer> waiting = new ArrayList<Integer>();
		for(int x=0;x<tasks;x++) {
			waiting.add(0);
		}
		// holds tasks to be aborted 
		ArrayList<Integer> abort = new ArrayList<Integer>();
		// collects resources to be added back 
		Map<Integer,Integer> addUnits = new HashMap<Integer,Integer>();
	
		// list to remove elements from the queue
		ArrayList<Integer> removeList = new ArrayList<Integer>();
		// how many resources every task has
		Map<Integer,Integer> unitAmount = new HashMap<Integer,Integer>();
		for(int x=0; x<tasks;x++) {
			unitAmount.put(x, 0);
		}
		
		// handle wait time 
		Map<Integer,Integer> wait = new HashMap<Integer,Integer>();
		for(int x=0; x<tasks;x++) {
			int initiate_pos = initiate.get(x);
			ArrayList<String> element = list.get(initiate_pos);
			int delay = Integer.parseInt(element.get(2));
			wait.put(x, delay);
		}
		
		// num of terminations
		 int terminations = 0;
		 // current cycle 
		 int cycle = 0;
		 // can run?
		 boolean run = true;
		 // deadlock?
		 boolean deadlock = false;
		 int deadlockNum = 0;
		 while(run) {
			
			 // check whether if the abort list is not empty,
			 // if so, tasks are aborted 
			 if(abort.isEmpty()==false) {
				 run=false;
				 if(q.isEmpty()==false) {
					 for(int x=0;x<q.size();x++) {
						 int position = q.get(x);
						 terminated.set(position, cycle+1);
					 }
				 }
				 if(blocked.isEmpty()==false) {
					 for(int x=0;x<blocked.size();x++) {
						 int position = blocked.get(x);
						 terminated.set(position, cycle+1);
					 }
				 }

			}
			 
			// checked blocked tasks first
			if(blocked.isEmpty()==false) {
				// check elements in blocked list
				for(int x=0;x<blocked.size();x++) {
						int position = blocked.get(x);
						// increment waiting time for position
						 waiting.set(position, waiting.get(position)+1);
						 // remove from queue for time being 
						 q.remove(blocked.get(x));
						 int initiate_pos = initiate.get(position);
						 ArrayList<String> element = list.get(initiate_pos);
						 // check for if the action is a request
						 if(element.get(0).equalsIgnoreCase("request")) {
								int delay = wait.get(position);
								// check the wait time 
								if(delay>0) {
									// adjust wait time
									wait.put(position, delay-1);
								}
								else {
									int type= (Integer.parseInt(element.get(3))-1);
									int amount = Integer.parseInt(element.get(4));
									int units_left = units.get(type);
									// make sure there are enough units for the request
									if(units_left>=amount) {
										// adjust position to read for the task 
										initiate.set(position, initiate_pos+1);
										// adjust resources left 
										units.set(type, units_left-amount);
										// adjust how many resources the task has 
										unitAmount.put(position, unitAmount.get(position)+amount);
										// remove from blocked 
										removeB.add(position);
										// look at the next position
										 initiate_pos = initiate.get(position);
										 element = list.get(initiate_pos);
										 // gather the next wait time 
										 int delay2 = Integer.parseInt(element.get(2));
										 wait.put(position, delay2);
										 int compare = Integer.parseInt(element.get(1));
										 // make sure the task at the next position matches
										 // the current task, if not, abort 
										 if(compare!=(position+1)) {
											 abort.add(position);
										}
									}
									else {
										// increment deadlock
										deadlockNum++;
									}
								}
							}
						
						 // check whether the action is release
						 else if(element.get(0).equalsIgnoreCase("release")) {
								// check wait time
								int delay = wait.get(position);
								if(delay>0) {
									wait.put(position, delay-1);
								}
								else {
									int type= (Integer.parseInt(element.get(3))-1);
									int amount = Integer.parseInt(element.get(4));
									int units_left = units.get(type);
									initiate.set(position, initiate_pos+1);
									initiate_pos = initiate.get(position);
									element = list.get(initiate_pos);
									int compare = Integer.parseInt(element.get(1));
									// make sure the requests do not demand more resources
									// than the initial claim 
									 if(compare!=(position+1)) {
										 abort.add(position);
										 continue;
										 
									 }
									 // add back into queue
									addQ.add(position);
									// add units 
									unitAmount.put(position, 0);
									addUnits.put(type, units_left+amount);
									removeB.add(position);
									
									if(addUnits.get(type)==null) {
										addUnits.put(type, amount);
									}
									else {
										addUnits.put(type, (addUnits.get(type))+amount);
									}
									
									// find new delay
									 initiate_pos = initiate.get(position);
									 element = list.get(initiate_pos);
									int delay2 = Integer.parseInt(element.get(2));
									 // put delay as the new wait time 
									 wait.put(position, delay2);
									
								}
							}
					 }
				}
			
			// find whether the queue is empty 
			if(q.isEmpty()==false) {
				for(int x=0;x<q.size();x++) {
					int position = q.get(x);
					int initiate_pos = initiate.get(position);
					ArrayList<String> element = list.get(initiate_pos);
					if(element.get(0).equalsIgnoreCase("request")) {
						int delay = wait.get(position);
						// check wait time 
						if(delay>0) {
							// wait if there is still wait time 
							wait.put(position, delay-1);
						}
						else {
							// resource type 
							int type= (Integer.parseInt(element.get(3))-1);
							// resource amount demanded 
							int amount = Integer.parseInt(element.get(4));
							// how many resources of that type left 
							int units_left = units.get(type);
							// make sure enough resources are left 
							if(units_left>=amount) {
								// adjust position for task and number of resources 
								initiate.set(position, initiate_pos+1);
								units.set(type, units_left-amount);
								unitAmount.put(position, unitAmount.get(position)+amount);
								
								 // add new wait 
								 initiate_pos = initiate.get(position);
								 element = list.get(initiate_pos);
								 int delay2 = Integer.parseInt(element.get(2));
								 wait.put(position, delay2);
								 int compare = Integer.parseInt(element.get(1));
								 // make sure tasks match the task at the next position 
								 if(compare!=(position+1)) {
									abort.add(position);
								}
								
							}
							else {
								// add to blocked 
								blocked.add(position);
								deadlockNum++;
							}
						}
					}
					else if(element.get(0).equalsIgnoreCase("release")) {
						int delay = wait.get(position);
						if(delay>0) {
							wait.put(position, delay-1);
						}
						else {
							int type= (Integer.parseInt(element.get(3))-1);
							int amount = Integer.parseInt(element.get(4));
							initiate.set(position, initiate_pos+1);
							initiate_pos = initiate.get(position);
							element = list.get(initiate_pos);
							int compare = Integer.parseInt(element.get(1));
							 if(compare!=(position+1)) {
								 abort.add(position);
								 continue;
								 
							 }
							
							// add units 
							unitAmount.put(position, 0);
							if(addUnits.get(type)==null) {
								addUnits.put(type, amount);
							}
							else {
								addUnits.put(type, (addUnits.get(type))+amount);
							}
							
							// add new wait 
							 initiate_pos = initiate.get(position);
							 element = list.get(initiate_pos);
							 
							 int delay2 = Integer.parseInt(element.get(2));
							 wait.put(position, delay2);
							
						}
					}
				}
			 }
			
		
			 
			 // return resources release 
			 if(addUnits.isEmpty()==false) {
				for(int x=0;x<addUnits.size();x++) {
					 int current = units.get(x);
					 units.set(x,current+addUnits.get(x));
					 addUnits.put(x, 0);
				 } 
			 }

			 
			 

			 // check whether all the task's requests were not granted, if so,
			 // we have a deadlock
			 if((deadlockNum==(tasks-terminations))&& ((tasks-terminations)!=1)) {
				 deadlock=true;
			 }
			 // clear deadlock num
			 deadlockNum = 0;
			 
			
			 // handle deadlock, keep aborting tasks until we have enough
			 // resources for a task's request to be granted 
			 if(deadlock==true) {
				 for(int x=0;x<tasks;x++) {
					 if(unitAmount.containsKey(x)) {
						 int addAmount = unitAmount.get(x);
						 int initiate_pos = initiate.get(x);
						 ArrayList<String> element = list.get(initiate_pos);
						 int amount = Integer.parseInt(element.get(4));
						 int type= (Integer.parseInt(element.get(3))-1);
						 int units_left = units.get(type);
						 if(units_left>=amount) {
							deadlock=false;
							break;
						 }
						 else {
							removeList.add(x);
							terminations++;
							unitAmount.remove(x);
							removeB.add(x);
							units.set(type, units_left+addAmount);
							terminated.set(x, -1);
							if(q.contains(x)) {
								int place = q.indexOf(x);
								q.remove(place);
							}
							 
						 }
					 }
				 }
			 }
			 
			 
			 // handle terminations
			 for(int x=0;x<q.size();x++) {
				 int origPos = q.get(x);
				 int initiate_pos = initiate.get(origPos);
				 ArrayList<String> element = list.get(initiate_pos);
				 // queue items to remove if action is terminate 
					if(element.get(0).equalsIgnoreCase("terminate")) {
						// check wait time 
						int delay = wait.get(origPos);
						if(delay>0) {
							wait.put(origPos, delay-1);
						}
						else {
							// if wait time is 0 the task terminates 
							int position = q.get(x);
							terminated.set(position, cycle+1);
							removeList.add(position);
							unitAmount.remove(position);
							terminations++;
							// if terminations equal the tasks we break the loop 
							if(terminations==tasks) {
								 run=false;
							 }
						}
					}
			 }
			 
			 // remove elements from blocked list 
			 if(removeB.isEmpty()==false) {
				 for(int x=0;x<removeB.size();x++) {
					 int element = removeB.get(x);
					 
					 int position = blocked.indexOf(element);
					 blocked.remove(position);
					 q.add(element);
				 }
				 removeB.clear();
			 }
			 
			 
			 
				// take elements out of queue with the removeList
				for(int y=0;y<removeList.size();y++) {
					q.remove(removeList.get(y));
					initiate.remove(removeList.get(y));
				}
				// clear remove list
				removeList.clear();
			 
			 
			 // lets run add to the queue
			 for(int x=0;x<initiate.size();x++) {
				 int initiate_pos = initiate.get(x);
				ArrayList<String> element = list.get(initiate_pos);
				// see whether the action is initiate 
				 if(element.get(0).equalsIgnoreCase("initiate")) {
					int delay = wait.get(x);
					// check wait time 
					if(delay>0) {
						wait.put(x, delay-1);
					}
					else {
						// add to queue
						if(q.contains(x)==false) {
							q.add(x);
						}
						
						// shift where to look for the task 
						initiate.set(x, initiate_pos+1 );
						 
						// add new wait time for next action 
						initiate_pos = initiate.get(x);
						element = list.get(initiate_pos);
						delay = Integer.parseInt(element.get(2));
						wait.put(x, delay);
					}
					 
				 }
			 }
			 
			 // increment cycle 
			 cycle++;

		 }
		 
		 // total terminated times 
		 int terminatedCount = 0;
		 // total waiting time 
		 int waitingCount = 0;
		 
		 // gather totals 
		 for(int x=0;x<tasks;x++) {
			 int num = terminated.get(x);
			 if(num!=-1) {
				 terminatedCount+=num;
				 waitingCount += waiting.get(x);
			 }
		 }

		 System.out.println("FIFO");
		 System.out.println("----");
		 
		// print task info 
		 for(int x=0;x<tasks;x++) {
			 int num = terminated.get(x);
			 if(num!=-1) {
				 float terminate = terminated.get(x);
				 float waitNum = waiting.get(x);
				 System.out.print("Task "+(x+1)+"   "+terminated.get(x));
				 System.out.print("  "+waiting.get(x));
				 System.out.printf("   %.0f",(waitNum/terminate)*100);
				 System.out.println("%");
			 }
			 else {
				 System.out.println("Task "+(x+1)+"  aborted");
			 }
		 }
		 // print totals 
		 System.out.print("Total   "+terminatedCount);
		 System.out.print("  "+waitingCount);
		 System.out.printf("   %.0f",((float)waitingCount/(float)terminatedCount)*100);
		 System.out.println("%\n");
		 
		
}
	
	// banker checks whether a state is safe before granting a request
	// in order to avoid a deadlock. It compares the initial claims
	// with the resource amounts in order to determine whether the state
	// is safe 
	private static void banker() {
		// queue to keep track of tasks
  		ArrayList<Integer> q = new ArrayList<Integer>();
  		// removes elements from the blocked list 
  		ArrayList<Integer> removeB = new ArrayList<Integer>();
  		// holds elements of blocked list 
  		ArrayList<Integer> blocked = new ArrayList<Integer>();
  		// holds resources to be added back after they are release 
		Map<Integer,Integer> addUnits = new HashMap<Integer,Integer>();
		// holds terminated tasks 
		ArrayList<Integer> terminated = new ArrayList<Integer>();
		// holds tasks to be aborted 
		ArrayList<Integer> abort = new ArrayList<Integer>();
		
		// holds elements to be removed from the queue
		ArrayList<Integer> removeList = new ArrayList<Integer>();
		// holds resources each tasks has 
		ArrayList<ArrayList<Integer>> unitAmount = new ArrayList<ArrayList<Integer>>();
		for(int x=0; x<tasks;x++) {
			ArrayList<Integer> addList = new ArrayList<Integer>();
			for(int a=0; a<units2.size();a++) {
				addList.add(0);
			}
			unitAmount.add(addList);
		}

		
		// handle wait times 
		Map<Integer,Integer> wait = new HashMap<Integer,Integer>();
		for(int x=0; x<tasks;x++) {
			int initiate_pos = initiate2.get(x);
			ArrayList<String> element = list.get(initiate_pos);
			int delay = Integer.parseInt(element.get(2));
			wait.put(x, delay);
		}
		
		// number of terminations 
		 int terminations = 0;
		 
		 
		// handle termination and waiting time 
		 ArrayList<Integer> terminate = new ArrayList<Integer>();
		for(int x=0;x<tasks;x++) {
			terminate.add(0);
		}
		ArrayList<Integer> waiting = new ArrayList<Integer>();
		for(int x=0;x<tasks;x++) {
			waiting.add(0);
		}

		// keep track of the task at the head of the queue
		 int currentPro = 0;
		 // keep track of cycles 
		 int cycle = 0;
		 boolean run = true;
		 
		 // run
		 while(run) {
			 // see whether we change the current Process 
			 boolean change = false;
			// see whether it is time for the wait time in terminations to start 
			 boolean runDelay = true;

			// check abort list, and abort tasks it contains 
			// remove tasks from other appropriate lists 
			 if(abort.isEmpty()==false) {
				for(int x=0; x<abort.size();x++) {
					int position = abort.get(x);
					terminations++;
					 if(terminations==tasks) {
						 run=false;
					}
					terminated.add(position);
					if(q.contains(position)) {
						removeList.add(position);
					}
					if(blocked.contains(position)) {
						removeB.add(position);
					}
					for(int y=0;y<units2.size();y++) {
						if(addUnits.get(y)==null) {
							addUnits.put(y, unitAmount.get(position).get(y));
						}
						else {
							addUnits.put(y, unitAmount.get(position).get(y));
						}
						unitAmount.get(position).set(y, 0);
					}
					terminate.set(position, cycle+1);
				}
				change=true;
				abort.clear();

			}
			 
			// take elements out of queue with the removeList
			for(int y=0;y<removeList.size();y++) {
				q.remove(removeList.get(y));
			}
			// clear remove list
			removeList.clear();
				
			
			 
			 // check blocked list first 
			 if(blocked.isEmpty()==false) {
					// move through blocked list 
					 for(int x=0;x<blocked.size();x++) {
						// gather task
						 int position = blocked.get(x);
						 // set waiting 
						 waiting.set(position, waiting.get(position)+1);
						 // remove from queue for time being 
						 q.remove(blocked.get(x));
						 int initiate_pos = initiate2.get(position);
						 ArrayList<String> element = list.get(initiate_pos);
						 // see if action is request  
						 if(element.get(0).equalsIgnoreCase("request")) {
							 		// check wait time 
									int delay = wait.get(position);
									if(delay>0) {
										// decrement wait 
										wait.put(position, delay-1);
									}
									else {
										int type= (Integer.parseInt(element.get(3))-1);
											// gather whether safe 
											int amount = Integer.parseInt(element.get(4));
											boolean go = checkSafe(type,position,amount);
											// make sure request is not larger than the initial claim, if so
											// terminate 
											if(unitAmount.get(position).get(type)+amount>initiatePos.get(position).get(type)) {
												terminations++;
												terminated.add(position);
												terminate.set(position, cycle+1);
												removeB.add(position);
												int returnAmount = 0;
												for(int y=0;y<units2.size();y++) {
													returnAmount += unitAmount.get(position).get(y);
													if(addUnits.get(y)==null) {
														addUnits.put(y, unitAmount.get(position).get(type));
													}
													else {
														addUnits.put(type, (addUnits.get(type))+amount);
													}
													unitAmount.get(position).set(y, 0);
												}
												// print informative statement 
												System.out.println("Task "+(position+1)+" requests exceeds its claim; aborted;");
												System.out.println(returnAmount+" units available next cycle");
												terminate.set(position, -1);
												continue;
											}
											
											// check whether the state is safe 
											if(go==true || currentPro==position) {
												int units_left = units2.get(type);
												// ensure enough resources for the request 
												if(units_left>=amount) {
													initiate2.set(position, initiate_pos+1);
													units2.set(type, units_left-amount);
													unitAmount.get(position).set(type, unitAmount.get(position).get(type)+amount);
													// remove from blocked list 
													removeB.add(position);
													// add new wait
													initiate_pos = initiate2.get(position);
													element = list.get(initiate_pos);
													int delay2 = Integer.parseInt(element.get(2));
													wait.put(position, delay2);
													int compare = Integer.parseInt(element.get(1));
													// ensure task at the next position matches, if not,
													// add to the abort list 
													if(compare!=(position+1)) {
															abort.add(position);
															 
													}
												}
												else {
													// change current pro if the task it is currently
													// is blocked 
													if(position==currentPro) {
														currentPro = blocked.get(0);
													}
												}
											}
										else {
											if(position==currentPro) {
												currentPro = blocked.get(0);
											}
										}
									}
						 		}
						 	}
			 			}
			 
			// check whether the queue is empty 
			 if(q.isEmpty()==false) {
				for(int x=0;x<q.size();x++) {
					int position = q.get(x);
					int initiate_pos = initiate2.get(position);
					ArrayList<String> element = list.get(initiate_pos);
					// check whether the action is a request 
					if(element.get(0).equalsIgnoreCase("request")) {
							int delay = wait.get(position);
							// check wait 
							if(delay>0) {
								// decrement wait 
								wait.put(position, delay-1);
							}
							else {
									int type= (Integer.parseInt(element.get(3))-1);
									int amount = Integer.parseInt(element.get(4));
									// check whether safe 
									boolean go = checkSafe(type,position,amount);
									int returnAmount = 0;
									// ensure the task does not ask for more resources than what was in the initial claim 
									if((unitAmount.get(position).get(type)+amount)>initiatePos.get(position).get(type)) {
										for(int y=0;y<units2.size();y++) {
											returnAmount += unitAmount.get(position).get(y);
											if(addUnits.get(y)==null) {
												addUnits.put(y, unitAmount.get(position).get(type));
											}
											else {
												addUnits.put(type, (addUnits.get(type))+amount);
											}
											unitAmount.get(position).set(y, 0);
										}
										// print informative statement 
										System.out.println("Task "+(position+1)+" requests exceeds its claim; aborted;");
										System.out.println(returnAmount+" units available next cycle");
										terminate.set(position, -1);
										terminations++;
										terminated.add(position);
										removeList.add(position);
										continue;
									}
									// ensure the state is safe before continuing 
									if(go==true || currentPro==position) {
										// gather units left 
										int units_left = units2.get(type);
										// ensure enough resources are left 
										if(units_left>=amount) {
												// adjust resources and the task's position 
												initiate2.set(position, initiate_pos+1);
												units2.set(type, units_left-amount);
												//addUnits.put(type, units_left+amount);
												unitAmount.get(position).set(type, unitAmount.get(position).get(type)+amount);
												
												// add new wait 
												initiate_pos = initiate2.get(position);
												element = list.get(initiate_pos);
												int delay2 = Integer.parseInt(element.get(2));
												wait.put(position, delay2);
												int compare = Integer.parseInt(element.get(1));
												// ensure the task at the next position matches, if not, add
												// to the abort list 
												if(compare!=(position+1)) {
													 abort.add(position);
												}
												
										}
										else {
											// add to block list 
											blocked.add(position);
											// take out of queue
											removeList.add(position);
											if(position==currentPro) {
												currentPro = blocked.get(0);
											}
										}
									}
									else {
										removeList.add(position);
										blocked.add(position);
										if(position==currentPro) {
											currentPro = blocked.get(0);
										}
							
									}
								}
							}
					
						// check whether the action is release 
						else if(element.get(0).equalsIgnoreCase("release")) {
							// check wait
							int delay = wait.get(position);
							if(delay>0) {
								// decrement wait 
								wait.put(position, delay-1);
							}
							else {
								int type= (Integer.parseInt(element.get(3))-1);
								int amount = Integer.parseInt(element.get(4));
								// adjust task position 
								initiate2.set(position, initiate_pos+1);
								
								// check next position
								initiate_pos = initiate2.get(position);
								element = list.get(initiate_pos);
								int compare = Integer.parseInt(element.get(1));
								// ensure the task at the next position is the same
								// if not, abort 
								if(compare!=(position+1)) {
									 abort.add(position);
									 continue;
								 }
								
								// if resources are release we must ensure that the current Process is still 
								// valid by setting change to true
								if(!element.get(0).equalsIgnoreCase("release")) {
									change = true;
								}

								// add resources back 
								unitAmount.get(position).set(type, 0);
								if(addUnits.get(type)==null) {
									addUnits.put(type, amount);
								}
								else {
									addUnits.put(type, (addUnits.get(type))+amount);
								}
								
								// add new wait 
								 initiate_pos = initiate2.get(position);
								 element = list.get(initiate_pos);
								 int delay2 = Integer.parseInt(element.get(2));
								 wait.put(position, delay2);
								 
								 // if there is a wait larger than 0, we adjust 
								 if(delay2>0) {
									 wait.put(position, delay2+1); 
								 }
							}
						}
					}
				 }
			

			 // add resources released back
			 if(addUnits.isEmpty()==false) {
				 for(int x=0;x<addUnits.size();x++) {
					 int current = units2.get(x);
					 units2.set(x,current+addUnits.get(x));
					 addUnits.put(x, 0);
				 } 
			 }
			
			 
			// remove elements from the blocked list 
			 if(removeB.isEmpty()==false) {
				 for(int x=0;x<removeB.size();x++) {
					 int element = removeB.get(x);
					 
					 int position = blocked.indexOf(element);
					 blocked.remove(position);
					 q.add(element);
				 }
				 removeB.clear();
			 }
			 
			 // handle terminations
			 for(int x=0;x<q.size();x++) {
				 int origPos = q.get(x);
				 int initiate_pos = initiate2.get(origPos);
				 ArrayList<String> element = list.get(initiate_pos);
				// check whether the action is to terminate 
					if(element.get(0).equalsIgnoreCase("terminate")) {
						// check wait
						int delay = wait.get(origPos);
						if(delay>0 && runDelay==true) {
							// decrement wait 
							wait.put(origPos, delay-1);
						// terminate if wait is 0
						if(delay-1==0) {
							int position = q.get(x);
							terminate.set(position, cycle+1);
							terminated.add(position);
							removeList.add(position);
							for(int t=0;t<units2.size();t++) {
								unitAmount.get(position).set(t, 0); 
							}
							terminations++;
							if(terminations==tasks) {
								run=false;
							}
						}
					}
						else {
							// if no terminate wait, we immediately terminate 
							if(runDelay!=false) {
								int position = q.get(x);
								 terminate.set(position, cycle+1);
								 terminated.add(position);
								 removeList.add(position);
								 for(int t=0;t<units2.size();t++) {
									 unitAmount.get(position).set(t, 0); 
								 }
								 terminations++;
								 if(terminations==tasks) {
									 run=false;
								}
							}
						}
					}
			 }
				
			 // take elements out of queue with the removeList
				for(int y=0;y<removeList.size();y++) {
					q.remove(removeList.get(y));
				}
				// clear remove list
				removeList.clear();
				
				// gather new current process if change is true
				if(change==true) {
					if(initiatePos.get(currentPro).get(0)==origUnits.get(0)) {
						if(terminations!=tasks) {
							if(blocked.size()!=0) {
								currentPro = blocked.get(0);
							}
						}
					}
					else if(q.isEmpty()==false) {
						currentPro = q.get(0);
					}
					else {
						if(terminations!=tasks) {
							if(blocked.isEmpty()==false) {
								currentPro = blocked.get(0);
							}
						}
					}
				}
			 

			 
			 
			 // lets run add to the queue
			 for(int x=0;x<initiate2.size();x++) {
				 int initiate_pos = initiate2.get(x);
				ArrayList<String> element = list.get(initiate_pos);
				// check whether the action is initiate 
				 if(element.get(0).equalsIgnoreCase("initiate")) {
					 // check wait
					int delay = wait.get(x);
					if(delay>0) {
						wait.put(x, delay-1);
					}
					else {
						// if no wait, we initiate 
						int task = (Integer.parseInt(element.get(1)));
						if(task==(x+1)) {
							int type = (Integer.parseInt(element.get(3))-1);
							int claim= (Integer.parseInt(element.get(4)));
							if(claim<=units2.get(type)) {
								// make sure task is not terminated 
								if(terminated.isEmpty()==false && terminated.contains(x)==true) {
									continue;
								}
								// add to queue
								if(q.contains(x)==false) {
									q.add(x);
								}
								
								// shift where to look
								initiate2.set(x, initiate_pos+1 );
								
								// add new wait 
								initiate_pos = initiate2.get(x);
								element = list.get(initiate_pos);
								delay = Integer.parseInt(element.get(2));
								wait.put(x, delay);
							}
							else {
								if(terminated.contains(initiate_pos)==false) {
									// if claim is larger than the resource amount 
									// we abort 
									int resource = (Integer.parseInt(element.get(3)));
									int requested = (Integer.parseInt(element.get(4)));
									int unitsHad = origUnits.get(resource-1);
									System.out.println("Banker aborts task "+(x+1)+" before run begins:");
									System.out.println("claim for resource "+resource+" ("+requested+") exceeds number");
									System.out.println("of units present ("+unitsHad+")");
									terminate.set(task-1, -1);
									terminations++;
									terminated.add(initiate_pos);
								}

							}
						}
					}
				 }
			 }
			 
			 // increment cycle 
			 cycle++;

		 
		 
	}
		 // gather termination and waiting time totals 
		 int terminatedCount = 0;
		 int waitingCount = 0;
		 for(int x=0;x<tasks;x++) {
			 int num = terminate.get(x);
			 if(num!=-1) {
				 terminatedCount+=num;
				 waitingCount += waiting.get(x);
			 }
		 }

		 System.out.println("\nBANKER'S");
		 System.out.println("--------");
		 
		// print task info 
		 for(int x=0;x<tasks;x++) {
			 int num = terminate.get(x);
			 if(num!=-1) {
				 float terminateNum = terminate.get(x);
				 float waitNum = waiting.get(x);
				 System.out.print("Task "+(x+1)+"   "+terminate.get(x));
				 System.out.print("  "+waiting.get(x));
				 System.out.printf("   %.0f",(waitNum/terminateNum)*100);
				 System.out.println("%");
			 }
			 else {
				 System.out.println("Task "+(x+1)+"  aborted");
			 }
		 }
		 // print total info
		 System.out.print("Total    "+terminatedCount);
		 System.out.print("  "+waitingCount);
		 System.out.printf("   %.0f",((float)waitingCount/(float)terminatedCount)*100);
		 System.out.println("%");
}
	

	// this function is responsible for checking whether a state is safe 
	// based on its initial claim, and the initial resources.
	// if there are enough resources to satisfy the claim we decide the 
	// state is safe 
	private static boolean checkSafe(int type, int position, int amount) {
		int allowed = origUnits.get(type)-initiatePos.get(position).get(type);
		int compare = allowed-amount;
		if(compare<0) {
			return false;
		}
		return true;
	}
	
}
