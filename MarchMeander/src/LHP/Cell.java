package LHP;

import java.util.ArrayList;
import java.util.Random;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;

import repast.simphony.context.Context;
import repast.simphony.space.gis.Geography;

import tools.SimUtils;

public class Cell {
	
	//geometry
	Context context;
	Geography geog;
	Geometry geom;
	Coordinate coord; //centroid
	
	//resources
	double resources;
	double maxResources;
	double regrowRate =Parameter.regrowthRate;
	double timeCounter=0;
	
	//memory
	int remembered =0;
	
	//visit counts
	double visit=0;
	int id;
	
	public Cell(Context con,double x,double y, double r, int i){
		
		//set initial variables
		id=i;
		resources = r;
		maxResources = resources; //maintains initial conditions
		con.add(this);
		
		//place the cell on a gis landscape
		geog = (Geography)con.getProjection("geog");
		geom = getCircle(x,y);
		
		geog.move(this, geom);
		this.setCoord(geom.getCentroid().getCoordinate());
		timeCounter=0;

	}
	
	private Geometry getCircle(double x, double y){
		
		GeometryFactory fac = new GeometryFactory();
		Geometry shape = null;
		Coordinate centroid = new Coordinate(x,y);
		
		Point point = fac.createPoint(centroid);
		shape = point.buffer(Parameter.foodBuffer);
		
		return shape;
	}
	
	//@ScheduledMethod(start=0, interval = 1,priority=2,shuffle=true)
	public void stepThreaded(){
		
		try{
		//regrow();
		}catch (NullPointerException e){
			System.out.println("problem in the regrow method");
		}
		
	}
	
	private void regrow(){
		
		if(timeCounter<regrowRate){
			timeCounter++;
		} else {
			resources = maxResources;
		}
	}
	
	public synchronized double eatMe(double bite){
		double biteSize =0;
		if (this.getResourceLevel() - bite > 0){
				setResourceLevel( (this.getResourceLevel() - bite));
				biteSize = bite;
		}else{
			biteSize = this.getResourceLevel();
			setResourceLevel(0);
		}
		
		//biteSize = this.getResourceLevel();
		//setResourceLevel(0);
		
		//ModelSetup.addToCellUpdateList(this);
		timeCounter=0;
		return biteSize;
	}
	
	/****************************Get and set methods************************************/
	
	public double getResourceLevel(){
		return resources;
	}
	public void setResourceLevel(double r){
		resources = r;
	}
	public double getMaxResourceLevel(){
		return maxResources;
	}
	public void setMaxResourceLevel(double rm){
		maxResources = rm;
	}
	public Coordinate getCoord(){
		return coord;
	}
	private void setCoord(Coordinate c){
		coord = c;
	}
	public int getID(){
		return id;
	}
}