package LHP;

import cern.jet.random.VonMises;
import cern.jet.random.engine.MersenneTwister64;
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
	public static MersenneTwister64 mt = new MersenneTwister64();	// random number generator
	
	//Watcher agent (for observations)
	public final static double stepsPerDay = 60*60*8;	//1 sec interval //each step represents 1 min (1min*60*12), for a total of 12hours of active time per day  //48*4
	public final static double recordingFreq = 60*10;	
	public final static int endDay = 1;		 			// will run the simulation until it has completed this many days
	
	//primate: population
	public static int groupSize = 5;						//number of individuals in a group
	public static int numbOfGroups=20;						//number of groups
	public static int maxInitialGroupDistance = 100;         //radius of the initial group (m) (starting conditions)
	
	//primate: movement
	public static double visual_range = 50;					//range at which individuals can see food patches
	public static double attractionWeight = 2;
	public static double repulsionWeight = 2;
	public static double bearingWeight = 1;
	public static double foodWeight = 0.5;
	public static double attractionDistMax = 10;
	public static double repulsionDistMax = 10;
	public static double followMateProb = 0.001; 		//this is the probability that the individual will switch who they are attracted to in the group (random for now). binomial with p=0.01, then mean time attracted to followMate is 1000 steps.
	
	//Food landscape
	public static double envHomogen = 1;
	public static double biteSize = 0.01;					//Amount of food reduced per time step
	public static int landscapeWidth = 500;					//width and length of the landscape meters /10000
	public static int landscapeHeight = 1000;				//width and length of the landscape meters /10000
	public static double regrowthRate = 0.0;				//rate at which regrowth occurs 
	public static double cellChangeRate = 0.001;			//rate of decrease in cell familiarity over time
	
	//public static double food = 4;						//Amount of food within a food site (homogenous for now)
	public static double foodBuffer = 1;						//size of the food buffer in meters
	public static double foodDensity = 0.01;              //avg number of food patches within 1m2 area


	//Constructor: used to set values from batch runs or the GUI
	public Parameter(){
		
		randomSeed = (Integer)p.getValue("randomSeed");

		//landscape
		visual_range = (Double)p.getValue("visual_range");//
		attractionWeight = (Double)p.getValue("attractionWeight");//
		repulsionWeight = (Double)p.getValue("repulsionWeight");//
		bearingWeight = (Double)p.getValue("bearingWeight");//
		foodWeight = (Double)p.getValue("foodWeight");//
		attractionDistMax = (Double)p.getValue("attractionDistMax");//
		repulsionDistMax = (Double)p.getValue("repulsionDistMax");//
		landscapeWidth = (Integer)p.getValue("landscapeWidth"); //
		landscapeHeight = (Integer)p.getValue("landscapeHeight");//
		numbOfGroups = (Integer)p.getValue("numbOfGroups");//
		groupSize = (Integer)p.getValue("groupSize");//
		foodBuffer = (Double)p.getValue("foodBuffer"); //
		foodDensity = (Double)p.getValue("foodDensity"); //
		biteSize = (Double)p.getValue("biteSize"); //
		
	}
	
	//public static double foodAvoidanceDistance = 10;		//radius in which food is not selected for when a stranger is nearby (m)
		//public final static double maxDistancePerStep = 0.5; 	//distance (meter) that an individual can travel within one time-step (1sec)
		//public final static double bodyRadius = 4; 				//this is the physical space taken up by an agent (meters)
}
