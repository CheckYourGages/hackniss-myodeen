import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

public class HacknissMyodeen {

    static final int frequency = 50; // Read data from hub ever x miliseconds
    Hub hub; // Myo Hub Object
    Myo myo;
    DeviceListener emgCollector; // Delegate to collect EMG
    DeviceListener dataCollector; // Delegate to collect regular data
    static final HacknissGUI GUI = new HacknissGUI();

    public static void main(String[] args) {
        HacknissMyodeen app = new HacknissMyodeen();
        

        app.init();

        // Run app until manual termination
        while(true){
            app.hub.run(frequency);
            KEStateMachine.STATE_MACHINE.performAction(app.myo);
            //System.out.println(app.dataCollector);
        }


    }

    // Init function
    public void init(){
        hub = new Hub("com.example.emg-data-sample");
        System.out.println("Attempting to find a Myo...");
        myo = hub.waitForMyo(10000);

        if (myo == null) {
            throw new RuntimeException("Unable to find a Myo!");
        }

        System.out.println("Connected to a Myo armband!");

        myo.setStreamEmg(StreamEmgType.STREAM_EMG_ENABLED);
        emgCollector = new MyoEmgDataCollector();
        dataCollector = new MyoDataCollector();
        hub.addListener(emgCollector);
        hub.addListener(dataCollector);
        KEStateMachine.setCollectors((MyoDataCollector)dataCollector, (MyoEmgDataCollector)emgCollector);
    }
}