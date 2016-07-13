package LHP;

import java.util.Collections;

import repast.simphony.random.RandomHelper;
import tools.SimUtils;
import tools.MoveUtils;

import com.vividsolutions.jts.geom.Coordinate;

public class Baboon extends Primate{

	/****************************
	 * 							*
	 * Building a red colobus 	*
	 * 							*
	 * *************************/

	//initialize a primate agent
	public Baboon(int ID, Coordinate c, int groupSize, boolean isMale, int group){
		this.id = ID;
		this.coordinate = c;
		this.myGroup=group;
		if (isMale == true){
			this.sex =1; 
		} else {
			this.sex = 0;
		}
		//groupMates = new ArrayList<Primate>();
		destination = null;
		this.setFacing(RandomHelper.nextDoubleFromTo(0, 360));
		blocked=false;
		foodTarget=null;
		followMate=null;
	}


	/************************************
	 * 									*
	 * Stimuli (internal + external) 	*
	 * 									*
	 * *********************************/

	public void getInputs(){
		
		if(this.getId()==3){
			//System.out.println("Something here... id 3");
		}

		//update where the agent is on the landscapes
		this.coordinate = ModelSetup.getAgentGeometry(this).getCoordinates()[0];

		//what food/groupmates can i see?
		try{
			//visualPatches = SimUtils.getFoodInFrontOfMe(this, Parameter.visual_range);
			visualPatches = SimUtils.getFoodAroundMe(this, Parameter.visual_range);
			//visualPartners = SimUtils.getIndividualsInFrontOfMe(this, Parameter.visual_range);
		}catch (NullPointerException e){
			System.out.println("herin lies the problem 1");
		}

		try{
			//Make a decision as to where to move based on above information
			destination = attractionRepulsion();
		} catch (NullPointerException e){
			System.out.println("herin lies the problem 3,  ID:"+ this.id);
		}
	}

	/****************************
	 * 							*
	 * Behavioural response 	*
	 * 							*
	 * *************************/

	public void behaviouralResponse(){

		if(destination!=null){

			//move towards destination
			move(destination,true); 

		} else {
			//rest
		}

		destination = null;
	}

	/****************************
	 * 							*
	 * 		Energy updates		*
	 * 							*
	 * *************************/

	public void energyUpdate(){

		//try to eat
		double distMin=Parameter.foodBuffer;
		for(Cell c:visualPatches){
			double dist = c.getCoord().distance(this.getCoord());
			if(dist<distMin){
				myPatch =c;
				distMin=dist;
			}
		}
		//attempt to eat from current location
		if(myPatch!=null){
			double bite = myPatch.eatMe(Parameter.biteSize);
			followMate=null;
		}
		myPatch=null;
	}

	/****************************
	 * 							*
	 * 	   Behavioural model	*
	 * 							*
	 * *************************/

	private Coordinate attractionRepulsion(){

		Coordinate dest = null;
		
		if(this.getId()==43){
			//System.out.println("Something here... ID 43");
		}

		//choose best food site
		double distMin = 9999;
		for (Cell food : visualPatches){
			if(nearStranger(food.coord)==false && food.getResourceLevel()>0 && occupied(food.coord)==false){
				double dist = this.coordinate.distance(food.coord);
				if(distMin>dist){
					distMin=dist;
					dest = food.coord;
				}
			}
		}

		//if no food site is available move to social partner
		if(dest == null){
			if(followMate==null){
				Collections.shuffle(this.primateList);
				for(Baboon b:primateList){
					if(this.id!=b.id && b.myGroup == this.myGroup){
						followMate=b;
						break;
					}
				}
			} else {
				dest = followMate.coordinate;
				if(this.coordinate.distance(dest)<=Parameter.bodyRadius)followMate=null;
			}
		}

		return dest;

	}


	/****************************
	 * 							*
	 * 	   Methods				*
	 * 							*
	 * *************************/

	
	private boolean nearStranger(Coordinate coord){
		boolean retval = false;
		for(Baboon bab : this.primateList){
			if(coord.distance(bab.coordinate)<Parameter.foodAvoidanceDistance && bab.myGroup!=this.myGroup){
				retval = true;
				break;
			}
		}
		return retval;
	}
	
	private boolean occupied(Coordinate coord){
		boolean retval = false;
		for(Baboon bab : this.primateList){
			if(coord.distance(bab.coordinate)<Parameter.foodBuffer && bab.getId()!=this.getId()){
				retval = true;
				break;
			}
		}
		return retval;
	}


	private synchronized void move(Coordinate destination,boolean isCellSite){
		//MoveUtils.moveToS((Primate)this, destination,0);
		if(destination.distance(this.getCoord())>0.01){
			MoveUtils.moveTo((Primate)this, destination);
			this.setCoord(ModelSetup.getAgentGeometry(this).getCoordinates()[0]);
		}
	}

}