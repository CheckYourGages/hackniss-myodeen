import com.thalmic.myo.enums.PoseType;
import com.thalmic.myo.enums.VibrationType;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.UnlockType;

import util.ArrayStack;
import util.DataUtils;

/**
 * Represents the states of the Hackniss Myodeen app and behavior
 *
 * @author Leo Xuzhang Lin
 */
public class KEStateMachine {

	// Singleton state machine
	public static final KEStateMachine STATE_MACHINE = new KEStateMachine();

	private STATE state;

	static MyoDataCollector data = null;
	static MyoEmgDataCollector emg = null;

	// Used to deal with data control
	static ArrayStack workstack = new ArrayStack();

	// Timer variables
	Long valStart;//=System.currentTimeMillis();
	Long varStart;//=System.currentTimeMillis();

	String[] workset = new String[5];
	boolean reminded=false;

	public enum STATE{LOCKED, REST, FIST_MADE, SPREAD_MADE;}
	
	//Warning state machine
	Boolean warning;

	// Enforce Singleton
	private KEStateMachine(){state = STATE.LOCKED; warning = false;}

	/**
	 * Sets the collectors
	 * @param dataC
	 * @param emgC
	 */
	public static void setCollectors(MyoDataCollector dataC, MyoEmgDataCollector emgC){data = dataC; emg = emgC;}

	// Returns the state's name
	public String getStateName(){
		return this.state.name();
	}

	public Boolean toWarn(){ return warning; }
	
	public STATE getState() { return this.state;}

	public void lockHappened(Myo myo){
		reset();

		state = STATE.LOCKED;
	}

	public void unlockHappened(Myo myo){    	
		myo.unlock(UnlockType.UNLOCK_HOLD);
		reset();
		HacknissMyodeen.GUI.toggle(1, true);    

		state = STATE.REST;
	}

	// Callback from MyoDataCollector where pose happened
	public void happened(Myo myo, Pose pose){
		switch(state){

		case REST:
			if(pose.getType()==PoseType.FIST){
				workset = new String[5];
				HacknissMyodeen.GUI.setTime("0.0");
				warning = false;
				
				//Start recording Fist To Release
				System.out.println("You made a fist, going to fist mode. Recording FTR");
				
				HacknissMyodeen.GUI.toggle(2, true);
				
				valStart=System.currentTimeMillis();
				varStart=System.currentTimeMillis();
				
				myo.vibrate(VibrationType.VIBRATION_SHORT);
				state = STATE.FIST_MADE;
			}
			break;

		case FIST_MADE:
			if(valStart==null || varStart==null){valStart=System.currentTimeMillis(); varStart=System.currentTimeMillis();}
			
			// Transition into finger spread state
			if(pose.getType()==PoseType.FINGERS_SPREAD || pose.getType()==PoseType.WAVE_OUT || pose.getType()==PoseType.WAVE_IN){
				
				// Sum all arrays in the work stack for FTR values
				int [] FTRArray = DataUtils.sumAndAverageStackArrayValues(workstack);
				double FTR = DataUtils.averageAcrossArray(FTRArray);
				
				// FTR Average
				System.out.println("Fist to Release Average: "+FTR);
				System.out.println("In fist mode, release detected, Recording release to rest");
				
				// Set the Draw comment 
				if(FTR>=180 || FTR<50){workset[0]="Bad";}
				else workset[0]="Good";
				
				// Set the Draw Time
				workset[1] = ""+(System.currentTimeMillis()-varStart)/1000;
				
				// Mark spread active
				HacknissMyodeen.GUI.toggle(3, true);
				
				// Clear work stack available for spread dataset
				workstack.clear();
				
				// New variable start
				varStart= System.currentTimeMillis();
				state = STATE.SPREAD_MADE;
			}
			break;

		case SPREAD_MADE:
			
			if(pose.getType()==PoseType.REST){
				
				// Sum of all array in the work set and average
				int [] RTRArray = DataUtils.sumAndAverageStackArrayValues(workstack);
				double RTR = DataUtils.averageAcrossArray(RTRArray);
				
				System.out.println("Release to Rest Average: "+ RTR);
				System.out.println("In spread mode, rest detected, ending release to rest recording");

				// Set comment for release
				if(RTR>20){workset[2]="Bad";}
				else workset[2]="Good";
				
				// Set time for release
				workset[3] = ""+(System.currentTimeMillis()-varStart);

				HacknissMyodeen.GUI.addRow(workset);
				myo.unlock(UnlockType.UNLOCK_TIMED);
				reset();
				HacknissMyodeen.GUI.toggle(1, true);
				
				state=STATE.REST;
			}
			
			break;

		default:
			reset();
			myo.lock();
			break;
		}
	}
	
	// Action performance called every single time a read is done on the data set
	public void performAction(Myo myo){
		
		// Fist state actions 
		if(STATE_MACHINE.getState() == STATE.FIST_MADE){
			
			readEmgToWorkStack();
			double row = data.getRoll();
			
			// Live determining if the posture is bad/good
			if(row<10.9 || row>11.6){
				workset[4]= "Bad";
				reminded=false;
			}
			else{workset[4] = "Good";
			if(!reminded){
				myo.vibrate(VibrationType.VIBRATION_SHORT);
					reminded=true;
				}
			}
			
			updateGUITime();
			checkSetWarning();		
		}
		
		//Action based on spread
		if(STATE_MACHINE.getState() == STATE.SPREAD_MADE){
			readEmgToWorkStack();
			updateGUITime();
			checkSetWarning();
		}
	}
	
	private void readEmgToWorkStack(){
		int[] array = MyoEmgDataCollector.collectorToArray(emg);
		DataUtils.allValuesAbsolute(array);
		workstack.push(array);
	}

	private void reset(){
		workset = new String[5];
		HacknissMyodeen.GUI.toggleAll(false);
		HacknissMyodeen.GUI.setTime("0.0");
		warning = false;
	}
	
	private void checkSetWarning(){
		if((System.currentTimeMillis()-valStart)>=7000){
			warning = true;
		}
	}
	
	private void updateGUITime(){
		HacknissMyodeen.GUI.setTime(String.valueOf((double)(System.currentTimeMillis()-varStart)/1000));
	}
}
