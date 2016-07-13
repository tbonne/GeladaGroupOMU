package LHP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.geotools.referencing.GeodeticCalculator;

import jsc.distributions.Lognormal;
import jsc.distributions.Normal;

import cern.jet.random.Uniform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.context.space.gis.GeographyFactory;
import repast.simphony.context.space.gis.GeographyFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.gis.Geography;
import repast.simphony.space.gis.GeographyParameters;
import tools.SimUtils;

//This file builds the model: creating the environment and populating it with primates
public class ModelSetup implements ContextBuilder<Object>{

	private static Context mainContext;
	private static Geography geog;
	private static int sitesAdded;
	private static int previousListSize;
	private static double resAdded;
	public static int primatesAdded;
	public static ArrayList<Baboon> primatesAll,orderedP;
	public static Iterable<Primate> primateIterator;
	public static ArrayList<Cell> cellsToUpdate;
	public static ArrayList<Cell> removeCellsToUpdate;
	public static ArrayList<Cell> allCells;
	public static double timeRecord;
	public static double timeRecord_start;
	public static GeodeticCalculator gc;

	public Context<Object> build(Context<Object> context){

		System.out.println("Running movement pattern simulation model");

		/********************************
		 * 								*
		 * initialize model parameters	*
		 * 								*
		 * ******************************/

		sitesAdded = 0;
		resAdded=0;
		primatesAdded = 0;
		primatesAll= new ArrayList<Baboon>();
		orderedP = new ArrayList<Baboon>();
		mainContext = null;
		Parameter parameter = new Parameter();
		geog=null;
		cellsToUpdate = new ArrayList<Cell>();
		removeCellsToUpdate = new ArrayList<Cell>();
		allCells = new ArrayList<Cell>();
		mainContext = context; //static link to context
		timeRecord = System.currentTimeMillis();
		timeRecord_start = System.currentTimeMillis();

		/****************************
		 * 							*
		 * Building the landscape	*
		 * 							*
		 * *************************/

		//Create Geometry factory; used to create gis shapes (points=primates; polygons=resources)
		GeometryFactory fac = new GeometryFactory();

		//x and y dims of the map file
		int xdim = Parameter.landscapeWidth;
		int ydim = Parameter.landscapeWidth;

		//Create Geography/GIS 
		GeographyParameters<Object> params= new GeographyParameters<Object>();
		GeographyFactory factory = GeographyFactoryFinder.createGeographyFactory(null);
		geog = factory.createGeography(Parameter.geog, context, params);
		geog.setCRS("EPSG:32636"); //WGS 84 / UTM zone 36N EPSG:32636
		gc = new GeodeticCalculator(geog.getCRS());

		//Add Resources to the environment *****************************************//
		System.out.println("adding resources to the environment");
		Uniform xDist = RandomHelper.createUniform(0, xdim);
		Uniform yDist = RandomHelper.createUniform(0, ydim);
		int count=0;
		for(int i=0;i<Parameter.foodDensity*xdim*ydim;i++){
			Cell cell = new Cell(context,xDist.nextDouble(),yDist.nextDouble(),Parameter.food,count++);
			resAdded=resAdded+Parameter.food;
			sitesAdded++;
			allCells.add(cell);
		}

		/************************************
		 * 							        *
		 * Adding hosts to the landscape	*
		 * 							        *
		 * *********************************/

		//keep track of groups being added
		ArrayList<Primate> group = new ArrayList<Primate>();

		//center of group (fixed)
		double xCenter =xdim/2;//0;//xDist.nextDouble();//75;//1000;// 75;
		double yCenter =0;//0;//yDist.nextDouble();// -75;//-1000;//-75;

		for(int i = 0 ;i<Parameter.numbOfGroups;i++ ){

			//select the number of primates in this group (fixed)
			int groupSize = Parameter.groupSize;

			boolean isMale = true;
			
			//add individuals
			for (int j = 0; j < groupSize; j++){
				
				//add individual
				Coordinate coord=SimUtils.generateCoordAround(xCenter,yCenter);
				Baboon rc = new Baboon(primatesAdded++,coord,groupSize,isMale,i);
				isMale=false;
				context.add(rc);
				primatesAll.add(rc);
				orderedP.add(rc);
				group.add(rc);
				Point geom = fac.createPoint(coord);
				geog.move(rc, geom);
				rc.myPatch = null;
			}
		}

		//Add groupMates list (for simulation control)
		for(Baboon p:primatesAll){
			p.setPrimateList(primatesAll);
		}


		/************************************
		 * 							        *
		 * create the scheduling			*
		 * 							        *
		 * *********************************/

		// Ordering processes
		// (1) get inputs for agents						- threaded
		// (2) behavioural responses of agents (movement) 	- threaded
		// (3) behavioural responses of agents (energy)		- threaded
		// (4) environment growback							- threaded
		// (5) add/remove cells to modify					- not threaded

		//executor takes care of the processing of the schedule
		Executor executor = new Executor();
		createSchedule(executor);

		/************************************
		 * 							        *
		 * Adding watcher agent				*
		 * 							        *
		 * *********************************/

		//watcher agent records all output
		Watcher w = new Watcher(executor);
		context.add(w);

		return context;

	}

	private void createSchedule(Executor executor){

		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();

		if(Parameter.groupProcess==true){ //subgroups of agents passed through each separate process, then next subgroup processed

			ScheduleParameters agentStepParamsPrimate = ScheduleParameters.createRepeating(1, 1, 6); //start, interval, priority (high number = higher priority)
			schedule.schedule(agentStepParamsPrimate,executor,"processPrimates");

		} else {  //all agents passed through separate process, one process at a time

			ScheduleParameters agentStepParamsPrimate = ScheduleParameters.createRepeating(1, 1, 6); //start, interval, priority (high number = higher priority)
			schedule.schedule(agentStepParamsPrimate,executor,"getInputs");

			ScheduleParameters agentStepParamsPrimateBehaviour = ScheduleParameters.createRepeating(1, 1, 5); //start, interval, priority (high number = higher priority)
			schedule.schedule(agentStepParamsPrimateBehaviour,executor,"behaviour");

			ScheduleParameters agentStepParamsPrimateEnergy = ScheduleParameters.createRepeating(1, 1, 4); //start, interval, priority (high number = higher priority)
			schedule.schedule(agentStepParamsPrimateEnergy,executor,"energyUpdate");

		}

		ScheduleParameters agentStepParams = ScheduleParameters.createRepeating(1, 1, 2);
		schedule.schedule(agentStepParams,executor,"envUpdate");
	}


	//used to update only the cells which have been modified, or are growing back
	@ScheduledMethod(start=1, interval = 1,priority=1)
	public static void removeCellsUpdated(){
		for(Cell c : removeCellsToUpdate){
			cellsToUpdate.remove(c);
		}
		removeCellsToUpdate.clear();
	}

	public static void stopSim(Exception ex, Class<?> clazz) {
		ISchedule sched = RunEnvironment.getInstance().getCurrentSchedule();
		sched.setFinishing(true);
		sched.executeEndActions();
	}

	public static Iterable<Cell> getAllCellAgents() {
		return allCells;
	}

	public static Iterable<Baboon> getAllPrimateAgents(){
		Collections.shuffle(primatesAll, new Random(System.currentTimeMillis()));
		Iterable<Baboon> agents = primatesAll;
		
		if(primatesAll.size()!=previousListSize){
			System.out.println("what!?!");
		}
		previousListSize=primatesAll.size();
		
		for(Baboon p: primatesAll){
			//if(p.getId()==43)System.out.println("found you");
		}
		
		return agents;
	}

	public static Context<Cell> getContext() {
		return ModelSetup.mainContext;
	}
	public static Geography<Primate> getGeog(){
		return geog;
	}
	public synchronized static void addToCellUpdateList(Cell c){  //need to read up on the synchronized implimentation
		if(cellsToUpdate.contains(c)==false)cellsToUpdate.add(c);
	}
	public synchronized static void removeCellToUpdate(Cell c){
		removeCellsToUpdate.add(c);
	}
	public static ArrayList<Cell> getCellsToUpdate(){
		return cellsToUpdate;
	}
	public static <T> Geometry getAgentGeometry(T agent) {
		return getGeog().getGeometry(agent);
	}
	public static GeodeticCalculator getGC(){
		return gc;
	}

}
