import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.enums.PoseType;
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
                    //Start recording Fist To Release
                    System.out.println("You made a fist, going to fist mode. Recording Fist to release");
                    state = STATE.FIST_MADE;
                }
            break;

            case FIST_MADE:
                if(pose.getType()==PoseType.FINGERS_SPREAD || pose.getType()==PoseType.WAVE_OUT){
                    // Start recording Release To Rest

                    int [] totalFTRMade = DataUtils.sumStackArrayValues(workstack);
                    for(int i = 0; i<totalFTRMade.length; i++){
                        totalFTRMade[i]=totalFTRMade[i]/totalFTRMade.length;
                    }
                    System.out.println("Fist to Release Average: "+DataUtils.averageAcrossArray(totalFTRMade));

                    System.out.println("In fist mode, release detected, Recording release to rest");
                    workstack.clear();
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
                    System.out.println("Release to Rest Average: "+DataUtils.averageAcrossArray(totalRTRMade));
                    System.out.println("In spread mode, rest detected, ending release to rest recording");

                    myo.unlock(UnlockType.UNLOCK_TIMED);
                    state=STATE.REST;
                }
            break;

            default:
                state=STATE.LOCKED;
                myo.lock();
            break;
        }
    }

    public void performAction(){
        if(STATE_MACHINE.getState() == STATE.FIST_MADE){
            int[] array = MyoEmgDataCollector.collectorToArray(emg);
            DataUtils.allValuesAbsolute(array);
            workstack.push(array);
        }
        if(STATE_MACHINE.getState() == STATE.SPREAD_MADE){
            int[] array = MyoEmgDataCollector.collectorToArray(emg);
            DataUtils.allValuesAbsolute(array);
            workstack.push(array);
        }
    }

    public void lockHappened(Myo myo){
        state = STATE.LOCKED;
    }

    public void unlockHappened(Myo myo){
        myo.unlock(UnlockType.UNLOCK_HOLD);
        state = STATE.REST;
    }
}