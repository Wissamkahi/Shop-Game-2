/*
 * File: Shopgame.java
 * -------------------
 * Name: Wissam Kahi
 * Game simulates the equipment cycle (Shop --> Available --> On Rent ---> Waiting for Pickup
 * With a particular focus on the shop
 */


import acm.graphics.*;
import acm.program.*;
import acm.util.*;
import static java.lang.Math.*; 

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.*;
import java.util.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class ShopGame extends GraphicsProgram {

	public class Equipment {
		private int state=0;
		private int type=0;
		private int order=0;
		private int ident=0; /*identifier */
		private double timeIn=0; /*Time equipment entered into the state */
		private double timeOut=0; /*Time equipment is supposed to leave the state */
		private Color c=Color.WHITE;
		private GLabel label;
		private GRect shape;
	
	}
	
	/* Run the program */
	public void run () {
		setup ();
		play (); 
	}
	
	/*setup the game */
	private void setup() {
		t0=System.currentTimeMillis(); /*Times the beginning of the game */
		//int size;
		Scanner in = new Scanner(System.in);
	    //System.out.println("Enter the size of available");
	    //size = in.nextInt();
		initiateScores();
	    placeWalls ();	
		placeStates ();
		placeLabels();
		randomizeOrderRent (); /*randomizes the order of the equipment for the On Rent state */
		System.out.println("How many High Runners at price $"+EQUIPMENTCOSTS[0]+" do you want to buy?");
		int type1Equip=in.nextInt();
		System.out.println("How many Medium Runners at price $"+EQUIPMENTCOSTS[1]+" do you want to buy?");
		int type2Equip=in.nextInt();
		System.out.println("How many Low Runners at price $"+EQUIPMENTCOSTS[2]+" do you want to buy?");
		int type3Equip=in.nextInt();
		capitalInvested=EQUIPMENTCOSTS[0]*type1Equip+EQUIPMENTCOSTS[1]*type2Equip+EQUIPMENTCOSTS[2]*type3Equip;
		capitalLabel.setLabel("Capital Invested: $"+capitalInvested);
		fillAvailable(type1Equip, type2Equip, type3Equip); // fills with the proper number of equipment 
		//fillStates(size,INITRENT,INITSHOP);
		placeEquipments ();
		
	}
	
	/* play the game */
	private void play () {
		t0=System.currentTimeMillis(); /*Times the beginning of the game */
		currentTime=System.currentTimeMillis()-t0;
		generateRandomOrderTimes ();
		System.out.println("next t1 is in "+orderTimes.get(0));
		System.out.println("next t2 is in "+orderTimes.get(1));
		System.out.println("next t3 is in "+orderTimes.get(2));
		
		while (System.currentTimeMillis()-t0<ENDOFGAMETIME) {
			pause (100);
			/*Update the days label*/
			daysElapsed=(int)((System.currentTimeMillis()-t0)/(LENGTHOFADAY*1000));
			daysElapsedLabel.setLabel("DAYS "+daysElapsed);
			scanEquipments();
		}
	} 
	
	/*Initiates the scores */
	private void initiateScores() {
		for (int counter=0; counter<=TYPES; counter++){
			lostSales.add(0);
		}
		capitalInvested=0;
		sales=0;
	}
	
	
	/** places the 4 walls */
	private void placeWalls() {
		GRect walls = new GRect (START_X,START_Y,GWIDTH,GHEIGHT);
		add (walls);
	}
	
	/**places the boxes representing the various states */
	private void placeStates() {
		placeShop (SHOPX, SHOPY, SHOPWIDTH, SHOPHEIGHT, SHOPCOLOR);
		place(AVAILX, AVAILY,AVAILWIDTH,AVAILHEIGHT, AVAILCOLOR);
		place (RENTX, RENTY,RENTWIDTH,RENTHEIGHT, RENTCOLOR);
	}
	
	
	/*Place the labels on top */
	private void placeLabels(){
		/*Place label On Rent */
		GLabel onRentLabel = new GLabel("ON RENT");
		onRentLabel.setFont(new Font("Serif", Font.BOLD, BIGFONTSIZE));
		onRentLabel.setColor(FONTCOLOR);
		add(onRentLabel, RENTX+RENTWIDTH/2-onRentLabel.getWidth()/2, RENTY-EQUIPHEIGHT);
					
		/*Place label Available */
		GLabel availForRentLabel = new GLabel("AVAILABLE FOR RENT");
		availForRentLabel.setFont(new Font("Serif", Font.BOLD, BIGFONTSIZE));
		availForRentLabel.setColor(FONTCOLOR);
		add(availForRentLabel, AVAILX+AVAILWIDTH/2-availForRentLabel.getWidth()/2, AVAILY-EQUIPHEIGHT);
		
		/*Place label Shop */
		GLabel shopLabel = new GLabel("SHOP");
		shopLabel.setFont(new Font("Serif", Font.BOLD, BIGFONTSIZE));
		shopLabel.setColor(FONTCOLOR);
		add(shopLabel, SHOPX+SHOPWIDTH/2-shopLabel.getWidth()/2, SHOPY-EQUIPHEIGHT*14);
		
		/*Place the lost sales label */
		lostSalesLabel1 = new GLabel("Lost Sales HR = "+lostSales.get(0));
		lostSalesLabel2 = new GLabel("Lost Sales MR = "+lostSales.get(1));
		lostSalesLabel3 = new GLabel("Lost Sales LR = "+lostSales.get(2));
		lostSalesLabel1.setFont(new Font("Serif", Font.BOLD, BIGFONTSIZE));
		lostSalesLabel2.setFont(new Font("Serif", Font.BOLD, BIGFONTSIZE));
		lostSalesLabel3.setFont(new Font("Serif", Font.BOLD, BIGFONTSIZE));
		lostSalesLabel1.setColor(FONTCOLOR);
		lostSalesLabel2.setColor(FONTCOLOR);
		lostSalesLabel3.setColor(FONTCOLOR);
		add(lostSalesLabel1,START_X+4*EQUIPWIDTH, 4*EQUIPHEIGHT);
		add(lostSalesLabel2, START_X+4*EQUIPWIDTH, 4*EQUIPHEIGHT+lostSalesLabel1.getHeight()*1.5);
		add(lostSalesLabel3, START_X+4*EQUIPWIDTH, 4*EQUIPHEIGHT+lostSalesLabel2.getHeight()*3);
	
		/*Place the days elapsed label */
		daysElapsed=0;
		daysElapsedLabel=new GLabel ("DAY" +daysElapsed);
		daysElapsedLabel.setFont(new Font("Serif", Font.BOLD, 24));
		daysElapsedLabel.setColor(Color.red.darker());
		add(daysElapsedLabel,APPLICATION_WIDTH/2,START_Y/2);
		
		/*Place the sales Label */
		sales=0;
		salesLabel=new GLabel ("SALES: $" +sales);
		salesLabel.setFont(new Font("Serif", Font.BOLD, 24));
		salesLabel.setColor(Color.GREEN.darker());
		add(salesLabel,APPLICATION_WIDTH/2,daysElapsedLabel.getY()+daysElapsedLabel.getY());
		
		/*Place the capitalInvested Label */
		capitalInvested=0;
		capitalLabel=new GLabel ("Capital Invested: $" + capitalInvested);
		capitalLabel.setFont(new Font("Serif", Font.BOLD, 18));
		capitalLabel.setColor(Color.RED.darker());
		add(capitalLabel,lostSalesLabel1.getX(),lostSalesLabel3.getY()+lostSalesLabel3.getHeight()*2);
	}
	
	
	
	/* Generates random order times for the orders of each type in the Available state */
	private void generateRandomOrderTimes () {
		orderTime1=System.currentTimeMillis()+nextRandomTime(ARRIVAL1)-t0;
		orderTime2=System.currentTimeMillis()+nextRandomTime(ARRIVAL2)-t0;
		orderTime3=System.currentTimeMillis()+nextRandomTime(ARRIVAL2)-t0;
		
		double [] arrivalFrequencies = {ARRIVAL1,ARRIVAL2,ARRIVAL3};
		orderTimes.add(System.currentTimeMillis()+nextRandomTime(ARRIVAL1)-t0);
		orderTimes.add(System.currentTimeMillis()+nextRandomTime(ARRIVAL2)-t0);
		orderTimes.add(System.currentTimeMillis()+nextRandomTime(ARRIVAL3)-t0);
			
		/*for (double counter : arrivalFrequencies) {
			System.out.println("t0 is = "+t0+" and current time is "+currentTime);
			currentTime=System.currentTimeMillis()-t0;
			orderTimes.add(System.currentTimeMillis()+nextRandomTime(counter)-t0);
		}*/
	}
	
	/*Generates a random exponential distribution random time difference vs. timeIn based on the frequency lambda */
	private double nextRandomTime(double lambda) {
		double timeLag=0;
		/*System.out.println("I am computing nextRandomTime");
		System.out.println("When I am getting timeIn of " +timeIn+" and lambda of "+lambda);*/
		double random=Math.random();
		timeLag=-Math.log(random)/(lambda/(LENGTHOFADAY*1000));
		/*System.out.println("I am returning a time difference of "+timeLag);*/
		return timeLag;
	}
	
	
	/*Fills the Available state */
	private void fillAvailable(int t1, int t2, int t3) {
		
		/*Fill the type1 in the available state */
		for (int i=1;i<=t1;i++) {
			Equipment e = new Equipment();
			e.state=1;
			e.type=1;
			e.ident=i;
			e.c=equipColor(e.type);
			availEquipment.add(e);
		}
		
		/*Fill the type2 in the available state */
		for (int i=1;i<=t2;i++) {
			Equipment e = new Equipment();
			e.state=1;
			e.type=2;
			e.ident=i;
			e.c=equipColor(e.type);
			availEquipment.add(e);
		}
		
		/*Fill the type3 in the available state */
		for (int i=1;i<=t3;i++) {
			Equipment e = new Equipment();
			e.state=1;
			e.type=3;
			e.ident=i;
			e.c=equipColor(e.type);
			availEquipment.add(e);
		}
		
		computeInitialTimes ();
		assignShapes ();
	}
	
	
	/*Creates the equipment in all states to initiate the game */
	private void fillStates(int sAvail, int sRent, int sShop) {
		int [] statesSizes = {sAvail, sRent, sShop};
		int s=1;
		for (int stateSize : statesSizes) {
			for (int t=1;t<=TYPES;t++) {
				for (int i=1;i<=stateSize;i++) {
					Equipment e = new Equipment();
					e.state=s;
					e.type=t;
					int sMax=Math.max(s-2, 0); /*formula to make sure identifiers resume counting and stay unique */
					int sMin=Math.min(s-1, 1); /*formula to make sure identifiers resume counting and stay unique */
					e.ident=i+(sMin)*sAvail+(sMax)*sRent;
					e.c=equipColor(e.type);
					switch (s) {
					case 1: 
						availEquipment.add(e);
						break;
					case 2:
						rentEquipment.add(e);
						break;
					case 3:
						shopEquipment.add(e);
						break;
					}
				}	
			}
			s++;
		}
		computeInitialTimes ();
		assignShapes ();
	}
	
	/* Assign shapes to the Equipment based on state, type and order */
	private void assignShapes () {
		int order=0;
		int x=0;
		int y=0;
		
		/*Assign shapes to Equipment in available status (organized by types) */
		for (Equipment e : availEquipment) {
			order = checkMyOrder (e);
			x = getX(e.state,e.type,order);
			y = getY(e.state,e.type,order);
			e.shape = new GRect (x,y,EQUIPWIDTH,EQUIPHEIGHT);
			e.shape.setFilled(true);
			e.shape.setColor(e.c);
			e.label= new GLabel (""+e.ident, x+EQUIPVERTGAP/2, y+EQUIPWIDTH);
		}
		
		/*Assign shapes to Equipment in onRent status */
		for (Equipment e : rentEquipment) {
			order=rentEquipment.indexOf(e);
			x = getX(e.state,e.type,order);
			y = getY(e.state,e.type,order);
			e.shape = new GRect (x,y,EQUIPWIDTH,EQUIPHEIGHT);
			e.shape.setFilled(true);
			e.shape.setColor(e.c);
			e.label= new GLabel (""+e.ident, x+EQUIPVERTGAP/2, y+EQUIPWIDTH);
		}
		
		for (Equipment e : shopEquipment) {
			order=shopEquipment.indexOf(e);
			x = getX(e.state,e.type,order);
			y = getY(e.state,e.type,order);
			e.shape = new GRect (x,y,EQUIPWIDTH,EQUIPHEIGHT);
			e.shape.setFilled(true);
			e.shape.setColor(e.c);
			e.label= new GLabel (""+e.ident, x+EQUIPVERTGAP/2, y+EQUIPWIDTH);
		}
	}
	
	/*Scans all equipments and identifies the ones that need to be moved */
	private void scanEquipments () {
		double timeDiff=0;
		int orderToMove=-1;
		double [] arrivalFrequencies = {ARRIVAL1,ARRIVAL2,ARRIVAL3};
		
		/*In the available state, the scan should not happen on the equipment as it's external customer demand and we should record a "lost sale" when there is no equipment*/
		for (int counter=0; counter<TYPES;counter++) {
			//System.out.println("I am computing for arrival frequency "+arrivalFrequencies[counter]+ " and the corresponding time is "+orderTimes.get(counter));
			if (orderTimes.get(counter)<System.currentTimeMillis()-t0) {
				System.out.println("Time is "+System.currentTimeMillis());
				orderTimes.set(counter, System.currentTimeMillis()+nextRandomTime(arrivalFrequencies[counter])-t0);
				System.out.println ("I have just computed a new exit time for state 1 and type "+counter+" = "+orderTimes.get(counter));
				orderToMove=checkOrder(1, counter+1); // checks the order of the type being moved in the available arraylist
				if (orderToMove>=0) {
					Equipment e = availEquipment.get(orderToMove);
					timeDiff=nextRandomTime(RENTALFREQUENCY[e.type-1]); // Equipment is moving to rent - so its next timeOut should be that of rent
					e.timeOut=System.currentTimeMillis()+timeDiff-t0;
					moveEquip(1,orderToMove);	
					sales=sales+RATES[counter];
					salesLabel.setLabel("SALES : $"+sales);
				}
				else {
					System.out.println("You just lost a sale");
					int ls=lostSales.get(counter);
					lostSales.set(counter, ls+1);
					switch (counter) {
					case 0:
						lostSalesLabel1.setLabel("Lost Sales HR = "+ls+1);
						break;
					case 1:
						lostSalesLabel2.setLabel("Lost Sales MR = "+ls+1);
						break;
					case 2:
						lostSalesLabel3.setLabel("Lost Sales LR = "+ls+1);
						break;
					}
					
				}
				
			}
		}
		
		for (Equipment e : rentEquipment) {
			orderToMove=scan (e);
			if (orderToMove>=0) { /*Entering this loop means there is an equipment to move */
				timeDiff=nextRandomTime(SHOPFREQUENCY[e.type-1]); // Equipment is moving to shop so its next timeOut is that of shop
				//System.out.println("Rental frequency is "+RENTALFREQUENCY[e.type-1]+" and timeDiff is "+timeDiff);
				e.timeOut=System.currentTimeMillis()+timeDiff-t0;
				System.out.println("the new timeOut is "+e.timeOut);
				break;
			}
		}
		if (orderToMove>=0) {
			moveEquip(2, orderToMove);
			//System.out.println("I have just moved equipment from state 2 and of order "+orderToMove);
			//placeEquipments();
		}
		
		for (Equipment e : shopEquipment) {
			orderToMove=scan (e);
			if (orderToMove>=0) { /*Entering this loop means there is an equipment to move */
				e.timeOut=0; // Doesn't matter what the time-out is as it's not governed by the equipment
				break;
			}	
		}
		if (orderToMove>=0) {
			moveEquip(3, orderToMove);
			//placeEquipments();
		}	
	}
	
	
	/*Scans the particular equipment and decides whether or not it shoud be moved */
	private int scan(Equipment equip) {
		double currentTime=System.currentTimeMillis()-t0;
		int orderMoved=-1;
		int or=-1;
		switch (equip.state) {
		case 1: or=availEquipment.indexOf(equip); break;
		case 2: or=rentEquipment.indexOf(equip); break;
		case 3: or=shopEquipment.indexOf(equip); break;	
		}
		//System.out.println("Now looking at state "+equip.state+" and type "+equip.type+" and order" +or);
		//System.out.println("Current time is "+currentTime+" while equip.timeOut time is "+equip.timeOut+" and the difference is "+(equip.timeOut-currentTime));
		
		switch (equip.state) {
		case 1:
			break;
		case 2:
			if (equip.timeOut<System.currentTimeMillis()-t0) {	
				orderMoved=rentEquipment.indexOf(equip);
				return orderMoved;
			}
			break;
		case 3:
			if (equip.timeOut<System.currentTimeMillis()-t0) {	
				orderMoved=shopEquipment.indexOf(equip);
				//System.out.println("I have just moved equipment from state 3 "+equip.state+" of type "+equip.type+" and of order "+shopEquipment.indexOf(equip));
				return orderMoved;
			}
			break;
		}
		return orderMoved;
	}
	
	/*compute the initial Time outs for all equipments */
	private void computeInitialTimes () {
		System.out.println("t0 is"+t0);
		double timeDiff=0;	
		
		/*Times for available equipment will depend on the type and position in queue as it's FIFO */
		generateRandomOrderTimes();
		
		
		/*for equipment on rent, it's simply at the end of the rental period */
		for (Equipment e : rentEquipment) {
			e.timeIn=System.currentTimeMillis()-t0;
			timeDiff=nextRandomTime(RENTALFREQUENCY[e.type-1]);
			e.timeOut=System.currentTimeMillis()+timeDiff-t0;
			System.out.println("I am in state " +e.state +" and order "+rentEquipment.indexOf(e));
			System.out.println("My time in is " + e.timeIn+ " And my time out is "+e.timeOut+" and the difference between the 2 is "+(e.timeOut-e.timeIn));
			/*System.out.println("and nextRandomTime returns" + nextRandomTime(e.timeIn,RENTALFREQUENCY[e.type-1]) +" for type "+e.type+" for RentalFrequency "+RENTALFREQUENCY[e.type-1]);
			System.out.println("For equipment in state "+e.state+" and order "+rentEquipment.indexOf(e)+" : inter-arrival is "+(e.timeOut-e.timeIn)); */
		}
		
		/*for equipment in the shop, it's simply at the end of the rental period */
		for (Equipment e : shopEquipment) {
			e.timeIn=System.currentTimeMillis()-t0;
			timeDiff=nextRandomTime(SHOPFREQUENCY[e.type-1]);
			e.timeOut=System.currentTimeMillis()+timeDiff-t0;
			System.out.println("I am in state " +e.state +" and order "+shopEquipment.indexOf(e));
			System.out.println("My time in is " + e.timeIn+ " And my time out is "+e.timeOut+" and the difference between the 2 is "+(e.timeOut-e.timeIn));
			/*System.out.println("For equipment in state "+e.state+" and order "+shopEquipment.indexOf(e)+" : inter-arrival is "+(e.timeOut-e.timeIn)); */
		}
		
	} 
	
	/*Checks the order of an Equipment of  a certain type in State available */
	private int checkMyOrder (Equipment e) {
		int o=0;
		int t = e.type;
		for (int i=0; i<=availEquipment.indexOf(e);i++) {
			if (availEquipment.get(i).type==t) {
				o++;
			}
		}
		return o;
	}
	
	/* Draws the Equipment in the various states */
	private void placeEquipments () {
		for (Equipment e : availEquipment) {
			placeEquipment (e);
		}
		for (Equipment e: shopEquipment) {
			placeEquipment (e);
		}
		for (Equipment e: rentEquipment) {
			placeEquipment (e);
		}
	}
	
	/*places the equipment in the Available state based on the availEquipment ArrayList */
	private void placeEquipment (Equipment eT) {
		add(eT.shape);
		position(eT.label);
	}
	
	/* Gives the x coordinate based on State, Type and Order in queue */
	private int getX (int st, int ty, int or) {
		int xcor=0;
		switch (st) {
		case 1: 
			xcor=AVAILX+AVAILWIDTH-((or)*(EQUIPWIDTH+EQUIPHORIZGAP)+EQUIPHORIZGAP); 
			break;
		case 2:
			int o = randomOrder.get(or);
			int xorder = o%RENTCAPACITYX;
			xcor=RENTX+(xorder)*(EQUIPWIDTH+EQUIPHORIZGAP)+EQUIPHORIZGAP; 
			break;
		case 3:
			double od = sqrt (or);
			int oi = (int) od +1;
			xcor=SHOPX+SHOPWIDTH-(oi*(EQUIPWIDTH+EQUIPHORIZGAP)+EQUIPHORIZGAP);
			break;
		}
		return xcor;
	}
	
	/* Gives the y coordinate based on State, Type and Order in queue */
	private int getY (int st, int ty, int or) {
		int ycor=0;
		switch (st) {
		case 1: 
			ycor=AVAILY+(ty-1)*(EQUIPHEIGHT+EQUIPVERTGAP)+EQUIPVERTGAP/2;
			break;
		case 2:
			int o = randomOrder.get(or);
			int yorder = (int)(o/RENTCAPACITYX);
			ycor=RENTY+(yorder)*(EQUIPHEIGHT+EQUIPVERTGAP)+EQUIPVERTGAP/2;
			break;
		case 3:
			int firstEqu = SHOPY-(EQUIPHEIGHT+EQUIPVERTGAP)/2; /*Same height as the middle of the rental */
			double od = sqrt (or);
			int oi = (int) od +1;
			ycor= firstEqu +(oi)*(EQUIPHEIGHT+EQUIPVERTGAP)+((or-1)-oi*oi)*((EQUIPHEIGHT+EQUIPVERTGAP));
			break;
		}
		return ycor;
	}
	
	
	/*Returns the color of the equipment based on type */
	private Color equipColor (int type) {
		Color col = Color.BLACK;
		switch (type) {
			case 1:
				col = LRCOLOR;
				break;
			case 2:
				col = MRCOLOR;
				break;
			case 3:
				col=HRCOLOR;
				break;	
		}
		return col;
	}

	
	/* Defines and places the Shop as a horizontal pyramid */
	public void placeShop (int x, int y, int width, int height, Color shopColor) {
		int sWidth=EQUIPWIDTH+EQUIPHORIZGAP;
		int eHeight=EQUIPHEIGHT+EQUIPVERTGAP;
		int sX=x+width-sWidth/2;
		int sY=y-eHeight/2-EQUIPVERTGAP/2;
		int sHeight;
		for (int i=1; i<=8;i++) {
			sHeight = (2*i-1)*eHeight;
			sX = sX - sWidth;
			sY = sY - eHeight;
			place (sX,sY,sWidth,sHeight,shopColor);
		}
	}
	
	/*Defines and places the Rent state as a circle */
	private void placeRent (int x, int y, int width, int height, Color rentColor) {
		GOval shape = new GOval (x,y,width,height);
		shape.setFilled(true);
		shape.setColor(rentColor);
		add (shape);
	}
	
	/** Places a rectangle at x,y with width, height and specified color */
	public void place (int x, int y, int width, int height,Color shapeColor) {
			GRect shape = new GRect (x,y,width,height);
			shape.setFilled(true);
			shape.setColor(shapeColor);
			add (shape);
	}
	
	/** positions a label in a particular spot */
	private void position (GLabel label_positioned) {
		GLabel box = label_positioned;
		box.setFont(new Font("Serif", Font.BOLD, FONTSIZE));
		box.setColor(FONTCOLOR);
		add(box);
	}
	
	/*Create an array list of random orders for the available status - this will help put the equipment randomly*/
	private void randomizeOrderRent () {
		for (int counter = 0; counter<RENTCAPACITYX*RENTCAPACITYY;counter++) {
			randomOrder.add(counter);
		}
		Collections.shuffle(randomOrder);
	}
		
	/*Moves equipment from State s and Order o to the next State */
	private void moveEquip (int s, int o) {
		
		Equipment movedEquip = new Equipment();
		switch (s) {
		case 1:
			movedEquip = availEquipment.get(o);
			movedEquip.state=2;
			rentEquipment.add(movedEquip);
			availEquipment.remove(o); /*removes the actual equipment from the Arraylist */
			break;
		case 2:
			movedEquip = rentEquipment.get(o);
			movedEquip.state=3;
			shopEquipment.add(movedEquip);
			rentEquipment.remove(movedEquip);
			break;
		case 3:
			movedEquip = shopEquipment.get(o);
			movedEquip.state=1;
			availEquipment.add(movedEquip);
			shopEquipment.remove(movedEquip);
			break;
		}
		eraseOldShapes ();
		assignShapes();
		placeEquipments();
	}
	
	
	/* Erase all the old shapes*/
	private void eraseOldShapes () {
		for (Equipment e : availEquipment) {
			remove(e.shape);
			remove(e.label);
		}
		for (Equipment e: shopEquipment) {
			remove(e.shape);
			remove(e.label);
		}
		for (Equipment e: rentEquipment) {
			remove(e.shape);
			remove(e.label);
		}
	}
	
	/*Checks the order of the first equipment of type T in the array */
	private int checkOrder (int state, int type) {
		int counter = 0;
		int noType=-1;
		switch (state) {
		case 1:
			for (Equipment e : availEquipment) {
				if (e.type!=type) counter++;
				else return counter;
				}
			break;
		case 2:
			for (Equipment e : rentEquipment) {
				if (e.type!=type) counter++;
				else return counter;
				}
			break;
		case 3:
			for (Equipment e : shopEquipment) {
				if (e.type!=type) counter++;
				else return counter;
				}
			break;
		}
		return noType;
	}
	
	
	
	
	/* private instance variables */
	ArrayList<Equipment> availEquipment= new ArrayList<Equipment>();
	ArrayList<Equipment> rentEquipment= new ArrayList<Equipment>();
	ArrayList<Equipment> shopEquipment= new ArrayList<Equipment>();
	ArrayList<Integer> randomOrder=new ArrayList<Integer>();
	ArrayList<Integer> movesState1=new ArrayList<Integer>(); /*Will help track the moves of state 1 */
	ArrayList<Integer> movesState2=new ArrayList<Integer>(); /*Will help track the moves of state 2 */
	ArrayList<Integer> movesState3=new ArrayList<Integer>(); /*Will help track the moves of state 3 */
	ArrayList<Double> orderTimes=new ArrayList<Double>(); /*Random variable indicating the time for the next customer request of the various types */
	double t0=0; /*Will help track the last time stamp */
	double currentTime=0; /*helps track the current time measured with regards to to (beginning of the game)*/
	ArrayList<Integer> lostSales=new ArrayList<Integer>(); /*Will track the lost sales */
	double capitalInvested; //Tracks the capital invested
	double sales; // Tracks the sales in USD
	int daysElapsed; // Tracks the days since start
	double orderTime1;
	double orderTime2;
	double orderTime3;

	
	/*The labels that will be used */
	GLabel lostSalesLabel1 = new GLabel("");
	GLabel lostSalesLabel2 = new GLabel("");
	GLabel lostSalesLabel3 = new GLabel("");
	GLabel daysElapsedLabel= new GLabel("");
	GLabel salesLabel = new GLabel("");
	GLabel capitalLabel = new GLabel("");

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 1300;
	public static final int APPLICATION_HEIGHT = 750;

/** Dimensions of game board (usually the same) */
	private static final int GWIDTH = APPLICATION_WIDTH;
	private static final int GHEIGHT = APPLICATION_HEIGHT;
	
/** Align game board in center of application window */
	private static final int START_X = APPLICATION_WIDTH/12;
	private static final int START_Y = APPLICATION_HEIGHT/10;
	private static final int HORIZDIST = GWIDTH/2;
	
/** Key parameters */
	private static final int STATES = 3;
	private static final int TYPES = 3;
	
/** Dimensions of of the states */
	
	private static final int AVAILWIDTH = GWIDTH/3;
	private static final int AVAILHEIGHT = GHEIGHT/5;
	private static final int AVAILX=START_X+GWIDTH/10+HORIZDIST;
	private static final int AVAILY=START_Y+GHEIGHT*2/3;
	
	private static final int SHOPWIDTH = GWIDTH/6;
	private static final int SHOPHEIGHT = GHEIGHT/5;
	private static final int SHOPX =START_X+GWIDTH/10;
	private static final int SHOPY = AVAILY+AVAILHEIGHT/2;
	
	private static final int RENTWIDTH = GWIDTH/3;
	private static final int RENTHEIGHT = GHEIGHT/3;
	private static final int RENTX=START_X+GWIDTH/10+HORIZDIST/2;
	private static final int RENTY=START_Y+GHEIGHT*1/10;
	
/** Defining the various colors */
	private static final Color SHOPCOLOR = Color.LIGHT_GRAY;
	private static final Color AVAILCOLOR = Color.LIGHT_GRAY;
	private static final Color RENTCOLOR = Color.LIGHT_GRAY;
	private static final Color LRCOLOR=Color.GREEN; /*Low Runner color */
	private static final Color MRCOLOR = Color.YELLOW; /*Medium Runner color */
	private static final Color HRCOLOR = Color.RED; /*High Runner color */

/** Defining the label */
	private static final int FONTSIZE = 10;
	private static final int BIGFONTSIZE = 18;
	private static final Color FONTCOLOR=Color.black;
	
	
/** Dimensions of pieces of equipment (aerial or forklift) */
	private static final int EQUIPWIDTH = AVAILWIDTH / 30;
	private static final int EQUIPHORIZGAP = EQUIPWIDTH / 2;
	private static final int EQUIPHEIGHT = AVAILHEIGHT / 6;
	private static final int EQUIPVERTGAP = EQUIPHEIGHT/2;
	private static final int AVAILCAPACITY = AVAILWIDTH / (EQUIPWIDTH+EQUIPHORIZGAP);
	private static final int RENTCAPACITYX = RENTWIDTH / (EQUIPWIDTH+EQUIPHORIZGAP);
	private static final int RENTCAPACITYY = RENTHEIGHT / (EQUIPHEIGHT+EQUIPVERTGAP);
	
	private static final int TYPESOFEQUIPMENT=3; /*How many equipment types */
	private static final int TOTALCAPACITY=300; /*How many pieces of equipment per type*/
	private static final int INITAVAIL = 10; /*Pieces of equipment per type in available status at the beginning of the game*/
	private static final int INITRENT = 0; /*Pieces of equipment per type in rent status at the beginning of the game */
	private static final int INITSHOP = 0; /*Pieces of equipment per type in shop status at the beginning of the game */
	
	
	/** Parameters of the simulation */ 
	private static final double LENGTHOFADAY=5; /* How many seconds is a day? This represents the speed of the game*/
	private static final double ARRIVAL1 =10; /*average number of customers per day for type 1*/
	private static final double ARRIVAL2 = 4; /*average number of customer per day for type 2*/
	private static final double ARRIVAL3 = 2; /*average number of customers per day for type 3*/
	private static final double RENTALTIME1=3; /*average length of rental time for type 1*/
	private static final double RENTALTIME2=7; /*average length of rental time for type 2*/
	private static final double RENTALTIME3=11; /*average length of rental time for type 3*/
	private static final double SHOPTIME1=2; /*average length of shop time for type 1*/
	private static final double SHOPTIME2=3; /*average length of shop time for type 2*/
	private static final double SHOPTIME3=4; /*average length of shop time for type 3*/
	private static final double ENDOFGAMETIME=120000; /*Time for the end of game in ms*/
	private static final double [] ARRIVALFREQUENCY = {ARRIVAL1, ARRIVAL2, ARRIVAL3};
	private static final double [] RENTALFREQUENCY = {1/RENTALTIME1, 1/RENTALTIME2, 1/RENTALTIME3};
	private static final double [] SHOPFREQUENCY = {1/SHOPTIME1, 1/SHOPTIME2, 1/SHOPTIME3};
	
	/** Parameters of the scores and pricing */
	private static final double [] EQUIPMENTCOSTS = {300,200,100}; // Prices per equipment 
	private static final double [] RATES = {1.5,1.0,0.5}; // Rates per equipment per day
	
	
	/** -Math.log(1.0 - rand.nextDouble()) / lambda. 
	 mulate%20inter-arrival%20times%20in%20m%2Fm%2F1&f=false */
	/* 2nd change */
	
}
