import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.enums.StreamEmgType;

import java.io.*;
import javax.sound.sampled.*;

public class HacknissMyodeen {

    static final int frequency = 50; // Read data from hub ever x miliseconds
    Hub hub; // Myo Hub Object
    Myo myo;
    DeviceListener emgCollector; // Delegate to collect EMG
    DeviceListener dataCollector; // Delegate to collect regular data
    static final HacknissGUI GUI = new HacknissGUI();
    ClassLoader loader = getClass().getClassLoader();

    public static void main(String[] args) {
        HacknissMyodeen app = new HacknissMyodeen();
        
        app.init();
        long past = System.currentTimeMillis();
        
        // Run app until manual termination
        while(true){
            app.hub.run(frequency);
            KEStateMachine.STATE_MACHINE.performAction(app.myo);
            
            if((System.currentTimeMillis()-past)>=1500){
            	if(KEStateMachine.STATE_MACHINE.toWarn()){
            		//TODO: Perform warning action
            		past=System.currentTimeMillis();
            		app.playWarning();
            	}
            }
            
        }
    }
    
    public void playWarning(){
    	try {
    	    AudioInputStream stream;
    	    AudioFormat format;
    	    DataLine.Info info;
    	    Clip clip;

    	    stream = AudioSystem.getAudioInputStream(loader.getResourceAsStream("beep.wav"));
    	    format = stream.getFormat();
    	    info = new DataLine.Info(Clip.class, format);
    	    clip = (Clip) AudioSystem.getLine(info);
    	    clip.open(stream);
    	    clip.start();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
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