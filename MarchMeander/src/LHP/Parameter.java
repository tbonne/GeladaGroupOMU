package LHP;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;

public class Parameter {
	
	final static Parameters p = RunEnvironment.getInstance().getParameters();
	
	//simulation settings
	public final static String agent_context = "cellContext";
	public final static String geog = "geog";
	public static int randomSeed = 1847620; 			//adjusted in constructor
	public static int numbOfThreads = 8;				//multipule of # of cores (e.g. 8 * 2 = 16)
	public static boolean groupProcess = false;			//sub-groups of agents processed separately, or all agents passed through each separate process
	
	//Watcher agent (for observations)
	public final static double stepsPerDay = 60*60*48;	//1 sec interval //each step represents 1 min (1min*60*12), for a total of 12hours of active time per day  //48*4
	public final static int endDay = 1;		 			// will run the simulation until it has completed this many days
	
	//primate: population
	public static int groupSize = 10;						//number of individuals in a group
	public static int numbOfGroups=10;						//number of groups
	public static int maxInitialGroupDistance = 60;         //radius of the initial group (m) (starting conditions)
	
	//primate: capabilities
	public static double foodAvoidanceDistance = 10;		//radius in which food is not selected for when a stranger is nearby (m)
	public final static double maxDistancePerStep = 0.5; 	//distance (meter) that an individual can travel within one time-step (1sec)
	public final static double bodyRadius = 4; 				//this is the physical space taken up by an agent (meters)
	public static double visual_range = 50;					//range at which individuals can see food patches
	
	//Food landscape
	public static double foodDensity = 0.01;                 //avg number of food patches within 1m2 area
	public static double biteSize = 1;					    //Amount of food reduced per time step
	public static double food = 4;							//Amount of food within a food site (homogenous for now)
	public static int foodBuffer = 1;						//size of the food buffer in meters
	public static int landscapeWidth = 1000;				//width and length of the landscape meters /10000
	public static int regrowthRate = 999999;				//time until regrowth iccurs (this is turned off for now)


	//Constructor: used to set values from batch runs or the GUI
	public Parameter(){
		
		randomSeed = (Integer)p.getValue("randomSeed");

		//landscape
		//cellSize = (Integer)p.getValue("cellSize");
		//foodDensity = (Double)p.getValue("foodDensity");
		//foodSD = (Double)p.getValue("envHetero");
		//regrowthRate = (Double)p.getValue("regrowthRate");
		
		//energy balance
		//biteSize = (Double)p.getValue("biteSize");
		//targetEnergy_low = (Double)p.getValue("targetEnergy_low");
		//foodDigestionRate = (Double)p.getValue("foodDigestionRate");
		
		//primate capabilities
		//visual_range = (Integer)p.getValue("foodSearchRange");
		
	}
}