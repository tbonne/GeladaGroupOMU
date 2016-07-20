package LHP;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import cern.jet.random.VonMises;

import com.vividsolutions.jts.geom.Coordinate;

public class Primate {

	//primate level variables
	Coordinate coordinate,foodTarget;
	ArrayList<Baboon> primateList;
	List<Cell> visualPatches;
	List<Primate> visualPartners;
	Primate followMate;
	Cell myPatch;
	int id;
	boolean blocked;
	private double facing;
	int sex;
	Coordinate destination;
	int myGroup;
	
	VonMises vm;
	RealVector myVector;


	/****************************
	 * 							*
	 * Behaviours				*
	 * 							*
	 ****************************/

	public void getInputs(){

	}

	public void behaviouralResponse(){

	}

	public void energyUpdate(){

	}


	/****************************
	 * 							*
	 * Get and set parameters	*
	 * 							*
	 ****************************/

	public void setPrimateList(ArrayList<Baboon> allPrimates){
		this.primateList=allPrimates;
	}
	
	public void setBaboonFollowerRand(Primate primate){
		followMate=null;
		System.out.println("I'm "+primate.id+" trying to find someone");
		while(followMate == null){
			followMate = ModelSetup.getAllPrimateAgents().iterator().next();
			System.out.println("...looking at "+followMate.id);
			if(followMate.id==primate.id)followMate=null;
		}
	}
	public Primate getBaboonFollower(){
		return followMate;
	}
	public int getId(){
		return id;
	}
	public void setCoord(Coordinate c){
		coordinate = c;
	}
	public Coordinate getCoord(){
		return coordinate;
	}
	public List<Cell> getVisualPatches(){
		return visualPatches;
	}
	public List<Primate> getVisualPartners(){
		return visualPartners;
	}
	public double getFacing(){
		return facing;
	}
	public void setFacing(double d){
		facing = d;
		if(d>360*2){
			System.out.println("something in facing methods");
		}
		if(facing>360)facing=facing-360;
		if(facing<0)  facing=facing+360;
	}
	public boolean blocked(){
		return blocked;
	}
	public void setBlocked(boolean i){
		blocked = i;
	}
	public void setFoodTarget(Coordinate c){
		foodTarget=c;
	}
	public int getMyGroup(){
		return this.myGroup;
	}
}
