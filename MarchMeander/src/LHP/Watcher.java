package LHP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.space.gis.ShapefileWriter;

public class Watcher {

	Executor executor;
	RunEnvironment runEnvironment;
	BufferedWriter summaryStats_out,individualMovements_out,foodQuant_out,foodPos_out;//locations
	List<Baboon> primateList;
	ArrayList<Double> speeds,spreads;
	ArrayList<Coordinate> centers;
	static ArrayList<ArrayList> indPositions,foodQuant;
	static ArrayList<Double> foodPosX,foodPosY;
	double hour;
	

	//output stats
	Coordinate lastCenter;
	
	//resolution reduction 
	//final static double scale = 6.8411;//6.687;//3.800;//6.505;//6.2800;//5.586;//4.895;//6.0980;  //6
	//final static double shape = 0.5955;  //0.6
	//static LogNormalDistribution logNDist;
	//int countSteps=0;
	//Long revisit=null;

	
	public Watcher(Executor exe){

		executor = exe;
		runEnvironment = RunEnvironment.getInstance();
		primateList = ModelSetup.primatesAll;
		speeds = new ArrayList<Double>();
		spreads = new ArrayList<Double>();
		centers = new ArrayList<Coordinate>();
		indPositions = new ArrayList<ArrayList>();
		hour = 1;
		foodPosX = new ArrayList<Double>();
		foodPosY = new ArrayList<Double>();
		foodQuant = new ArrayList<ArrayList>();
		

		Collections.sort(primateList,new CustomComparator());
		for(Primate p : primateList){
			//System.out.println("id "+ p.getId());
		}

		for(Primate p : primateList){
			//System.out.println("id "+ p.getId());
		}

		//creating a file to store the output of the counts
		try {
			//locations = new BufferedWriter(new FileWriter("C:/Users/t-work/Dropbox/Projects_with_LouisePeter/Project_Babbon_group_cordination_models_(specificFocus)/Project_simple_to_complex_models_min_requirments/runingABM/Primate_locations"+Parameter.decisionMaking+"_"+Parameter.associationSize+".csv",false));
			summaryStats_out = new BufferedWriter(new FileWriter("C:/Users/t-work/Dropbox/Projects_with_LouisePeter/Project_Babbon_group_cordination_models_(specificFocus)/Project_simple_to_complex_models_min_requirments/runingABM/summary_stats.csv",false));
			individualMovements_out = new BufferedWriter(new FileWriter("C:/Users/t-work/Dropbox/Projects_with_LouisePeter/Project_Babbon_group_cordination_models_(specificFocus)/Project_simple_to_complex_models_min_requirments/runingABM/movement_stats.csv",false));
			foodQuant_out = new BufferedWriter(new FileWriter("C:/Users/t-work/Dropbox/Projects_with_LouisePeter/Project_Babbon_group_cordination_models_(specificFocus)/Project_simple_to_complex_models_min_requirments/runingABM/food_quant.csv",false));
			foodPos_out = new BufferedWriter(new FileWriter("C:/Users/t-work/Dropbox/Projects_with_LouisePeter/Project_Babbon_group_cordination_models_(specificFocus)/Project_simple_to_complex_models_min_requirments/runingABM/food_pos.csv",false));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//logNDist = new LogNormalDistribution(scale,shape);
	}


	/********************************	
	 * 								*
	 *         Stepper				*
	 * 								*
	 *******************************/

	@ScheduledMethod(start=26, interval = 1,priority=0)
	public void step(){

		//	to do every tick
		
		//countSteps=countSteps+1;
		//if(revisit==null)revisit=Math.round(logNDist.sample()); can't down-sample this way as it assumes that all positions are recorded at the same time... no interpolation error introduced.
		//if(countSteps==revisit){
			//recordIndividualPositions();
			//recordFood();
		//	revisit=null;
		//	countSteps=0;
		//}
		

		//	to do every minute
		if(RunEnvironment.getInstance().getCurrentSchedule().getTickCount()%(60)==0){
		//	Coordinate groupCenter = calculateGroupCenter();
		//	centers.add(groupCenter);
		//	speeds.add(calculateSpeed(groupCenter));
		//	spreads.add(calculateSpread(groupCenter));
		}

		//	to do every hour
		if(RunEnvironment.getInstance().getCurrentSchedule().getTickCount()%(60*60)==0){
			//System.out.println("End of hour "+ hour);
		//	System.out.println(" ");
			hour++;
		}

		//	to do at the end of the simulation
		if(RunEnvironment.getInstance().getCurrentSchedule().getTickCount()>=Parameter.stepsPerDay*Parameter.endDay){
			executor.shutdown();
			RunEnvironment.getInstance().endAt(this.runEnvironment.getCurrentSchedule().getTickCount());
		//	recordSummaryStats();
		//	recordIndPosisions();
		//	recordFoodPo();
		//	recordFood_txt();
		//	closeBufferWiter();
		//	closeBufferWiter2();
		//	printSocialCounts();
			System.out.println("End of day");
			//exportLandscape();
		}
	}
	
	//private void printSocialCounts(){
	//	
	//	for(Primate p : ModelSetup.orderedP){
	//		System.out.println(p.getId()+" "+p.socialCount);
	//	}
	//	
	//}

	private double calculateSpeed(Coordinate groupCenter){

		double speed=-1;
		
		if(lastCenter!=null){
			speed = groupCenter.distance(lastCenter)/1;
		} 
		
		lastCenter = groupCenter;
				
		return speed;
	}
	
	
	private static void recordFood(){
		ArrayList foodT = new ArrayList<Double>();
		for(Cell c: ModelSetup.allCells){
			foodT.add(c.resources);
		}
		foodQuant.add(foodT);
	}
	
	private static void recordFoodPo(){
		
		for(Cell c: ModelSetup.allCells){
			foodPosX.add(c.getCoord().x);
			foodPosY.add(c.getCoord().y);
		}
	}
	
	private void recordFood_txt(){
		try {
			int count=0;
			for(int i = 0 ; i<foodQuant.size();i++){
				ArrayList fq = foodQuant.get(i);
				for(int j = 0 ; j < fq.size() ; j++ ){
					foodQuant_out.append(((Double)fq.get(j)).toString());
					foodQuant_out.append(",");
				}
				foodQuant_out.newLine();
			}
			
			for(int k = 0 ; k < foodPosX.size();k++){
				foodPos_out.append(((Double)foodPosX.get(k)).toString());
				foodPos_out.append(",");
				foodPos_out.append(((Double)foodPosY.get(k)).toString());
				foodPos_out.newLine();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			
			foodQuant_out.flush();
			foodQuant_out.close();
			
			foodPos_out.flush();
			foodPos_out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Coordinate calculateGroupCenter(){
		//find the center of this group
		double xCoordAvg=0;
		double yCoordAvg=0;
		double numbPrimates=0;

		//calculate their average x and y coordinates
		for (Primate p: ModelSetup.primatesAll){
			xCoordAvg = xCoordAvg + p.getCoord().x;
			yCoordAvg = yCoordAvg + p.getCoord().y;
			numbPrimates++;
		}

		xCoordAvg = xCoordAvg/numbPrimates;
		yCoordAvg = yCoordAvg/numbPrimates;

		//return the center coordinate
		return new Coordinate(xCoordAvg,yCoordAvg);
	}
	
	private static void recordIndividualPositions(){
		ArrayList<Double> positions = new ArrayList<Double>();
		for (Primate p: ModelSetup.orderedP){
			positions.add(p.getCoord().x);
			positions.add(p.getCoord().y);
		}
		indPositions.add(positions);
	}

	private double calculateSpread(Coordinate groupCenter){
		
		double MSE_dist=-1;
		int numbPrimates=0;
		
		for (Primate p: ModelSetup.primatesAll){
			MSE_dist = MSE_dist + Math.pow(p.getCoord().distance(groupCenter),2);
			numbPrimates++;
		}
		
		return MSE_dist/numbPrimates;
	}

	private void recordIndPosisions(){
		try {
			int count=0;
			for(int i = 0 ; i<indPositions.size();i++){
				ArrayList<Double> pos = indPositions.get(i);
				for(int j = 0 ; j<pos.size();j++){
					//x
					individualMovements_out.append(((Double)pos.get(j)).toString());
					individualMovements_out.append(",");
					j++;
					//y
					individualMovements_out.append(((Double)pos.get(j)).toString());
					individualMovements_out.append(",");
					//id
					individualMovements_out.append(((Integer)count).toString());
					individualMovements_out.append(",");
					count++;
					//time
					individualMovements_out.append(((Integer)i).toString());
					
					//new line
					individualMovements_out.newLine();
				}
				count=0;
				//individualMovements_out.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		flushBufferWriter2();
	}
	
	
	
	private void recordSummaryStats() {

		double avg_speed=0,avg_spread=0;

		for(Double d:spreads){
			avg_spread = avg_spread + d;
		}
		avg_spread = avg_spread/spreads.size();
		for(Double d:speeds){
			avg_speed = avg_speed + d;
		}
		avg_speed = avg_speed / speeds.size();
		
		System.out.println("speed = "+avg_speed+"  spread = "+avg_spread);
		
		try {
			for(int i = 0 ; i<centers.size();i++){
				summaryStats_out.append(((Double)centers.get(i).x).toString());
				summaryStats_out.append(",");
				summaryStats_out.append(((Double)centers.get(i).y).toString());
				summaryStats_out.append(",");
				summaryStats_out.append(((Double)speeds.get(i)).toString());
				summaryStats_out.append(",");
				summaryStats_out.append(((Double)spreads.get(i)).toString());
				summaryStats_out.append(",");
				summaryStats_out.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		flushBufferWriter();
	}


	private void flushBufferWriter(){
		try {
			summaryStats_out.newLine();
			summaryStats_out.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void flushBufferWriter2(){
		try {
			individualMovements_out.newLine();
			individualMovements_out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeBufferWiter(){
		try {
			summaryStats_out.flush();
			summaryStats_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void closeBufferWiter2(){
		try {
			individualMovements_out.flush();
			individualMovements_out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class CustomComparator implements Comparator<Primate> {
		@Override
		public int compare(Primate o1, Primate o2) {
			int retval = 1;
			if(o1.getId()<o2.getId())retval=0;
			return retval;
		}
	}

	private void exportLandscape(){
		System.out.println("Exporting landscape to be analyzed");

		File point = new File("C:/Users/t-work/Desktop/GIS/BehaviourExtraction/Landscape"+".shp");

		ShapefileWriter shapeOut = new ShapefileWriter(ModelSetup.getGeog());
		try {
			shapeOut.write(ModelSetup.getGeog().getLayer(Cell.class).getName(), point.toURI().toURL());
		} catch (MalformedURLException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException n){
			n.printStackTrace();
		}

	}

}
