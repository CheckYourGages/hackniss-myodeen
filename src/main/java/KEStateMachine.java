import com.thalmic.myo.enums.PoseType;
import com.thalmic.myo.enums.VibrationType;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.UnlockType;

import util.ArrayStack;
import util.DataUtils;

/**
 * Represents the states of the Katniss Evermyo app and behavior
 *
 * @author Leo Xuzhang Lin
 */
public class KEStateMachine {

    public static final KEStateMachine STATE_MACHINE = new KEStateMachine();
    private STATE state;
    static MyoDataCollector data = null;
    static MyoEmgDataCollector emg = null;
    static ArrayStack workstack = new ArrayStack();
    long start_seven=System.currentTimeMillis();
    long start=System.currentTimeMillis();
    String[] workset = new String[5];
    boolean reminded=false;

    public enum STATE{
        LOCKED, REST, FIST_MADE, SPREAD_MADE;
    }

    private KEStateMachine(){
        state = STATE.LOCKED;
    }

    public static void setCollectors(MyoDataCollector dataC, MyoEmgDataCollector emgC){
        data = dataC;
        emg = emgC;
    }

    // Returns the state's name
    public String getStateName(){
        return this.state.name();
    }

    public STATE getState() { return this.state;}

    // Callback from MyoDataCollector where pose happened
    public void happened(Myo myo, Pose pose){
        switch(state){
            case REST:
                if(pose.getType()==PoseType.FIST){
                	workset = new String[5];
                	
                    //Start recording Fist To Release
                	HacknissMyodeen.GUI.setTime("0.0");
                    System.out.println("You made a fist, going to fist mode. Recording Fist to release");
                    HacknissMyodeen.GUI.toggle(2, true);
                    state = STATE.FIST_MADE;
                    start_seven=System.currentTimeMillis();
                    start=System.currentTimeMillis();
                }
            break;

            case FIST_MADE:
                if(pose.getType()==PoseType.FINGERS_SPREAD || pose.getType()==PoseType.WAVE_OUT || pose.getType()==PoseType.WAVE_IN){
                    // Start recording Release To Rest

                    int [] totalFTRMade = DataUtils.sumStackArrayValues(workstack);
                    for(int i = 0; i<totalFTRMade.length; i++){
                        totalFTRMade[i]=totalFTRMade[i]/totalFTRMade.length;
                    }
                    double FTR = DataUtils.averageAcrossArray(totalFTRMade);
                    System.out.println("Fist to Release Average: "+FTR);

                    System.out.println("In fist mode, release detected, Recording release to rest");
                    if(FTR>=180 || FTR<50){workset[0]="Bad";}
                    else workset[0]="Good";
                    workset[1] = ""+(System.currentTimeMillis()-start)/1000;
                    HacknissMyodeen.GUI.toggle(3, true);
                    workstack.clear();
                    start= System.currentTimeMillis();
                    state = STATE.SPREAD_MADE;
                }
            break;

            case SPREAD_MADE:
                if(pose.getType()==PoseType.REST){
                    // Terminate Release to Rest recording

                    int [] totalRTRMade = DataUtils.sumStackArrayValues(workstack);
                    for(int i = 0; i<totalRTRMade.length; i++){
                        totalRTRMade[i]=totalRTRMade[i]/totalRTRMade.length;
                    }
                    double RTR = DataUtils.averageAcrossArray(totalRTRMade);
                    System.out.println("Release to Rest Average: "+ RTR);
                    System.out.println("In spread mode, rest detected, ending release to rest recording");

                    if(RTR>20){workset[2]="Bad";}
                    else workset[2]="Good";
                    workset[3] = ""+(System.currentTimeMillis()-start);
                    
                    HacknissMyodeen.GUI.addRow(workset);
                    myo.unlock(UnlockType.UNLOCK_TIMED);
                    HacknissMyodeen.GUI.toggle(2, false);
                    HacknissMyodeen.GUI.toggle(3, false);
                    HacknissMyodeen.GUI.setTime("0.0");
                    state=STATE.REST;
                }
            break;

            default:
            	workset = new String[5];
                state=STATE.LOCKED;
                myo.lock();
            break;
        }
    }

    public void performAction(Myo myo){
        if(STATE_MACHINE.getState() == STATE.FIST_MADE){
            int[] array = MyoEmgDataCollector.collectorToArray(emg);
            DataUtils.allValuesAbsolute(array);
            workstack.push(array);
            
            double row = data.getRoll();
            if(row<11 || row>12){
            	workset[4]= "Bad";
            	if(!reminded){
            		myo.vibrate(VibrationType.VIBRATION_LONG);
            		reminded=true;
            	}
            }
            else{
            	workset[4] = "Good";
            	reminded=false;
            }
            
            if((System.currentTimeMillis()-start_seven) > 7000){
            }
            
            HacknissMyodeen.GUI.setTime(String.valueOf((double)(System.currentTimeMillis()-start)/1000));
        }
        if(STATE_MACHINE.getState() == STATE.SPREAD_MADE){
            int[] array = MyoEmgDataCollector.collectorToArray(emg);
            DataUtils.allValuesAbsolute(array);
            workstack.push(array);
            
            if((System.currentTimeMillis()-start_seven) > 7000){
            }
            
            HacknissMyodeen.GUI.setTime(String.valueOf((double)(System.currentTimeMillis()-start)/1000));
        }
    }

    public void lockHappened(Myo myo){
        state = STATE.LOCKED;
        workset = new String[5];
        HacknissMyodeen.GUI.toggle(1, false);
        HacknissMyodeen.GUI.toggle(2, false);
        HacknissMyodeen.GUI.toggle(3, false);
        HacknissMyodeen.GUI.setTime("0.0");
    }

    public void unlockHappened(Myo myo){
        myo.unlock(UnlockType.UNLOCK_HOLD);
        workset = new String[5];
        HacknissMyodeen.GUI.toggle(1, true);
        state = STATE.REST;
    }
}
