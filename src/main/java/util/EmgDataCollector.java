package util;

import java.util.Arrays;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.FirmwareVersion;
import com.thalmic.myo.Myo;

public class EmgDataCollector extends AbstractDeviceListener {

    protected byte[] emgSamples;

    public EmgDataCollector() {

    }

    @Override
    public void onPair(Myo myo, long timestamp, FirmwareVersion firmwareVersion) {
        if (emgSamples != null) {
            for (int i = 0; i < emgSamples.length; i++) {
                emgSamples[i] = 0;
            }
        }
    }

    @Override
    public void onEmgData(Myo myo, long timestamp, byte[] emg) {
        this.emgSamples = emg;
    }

    @Override
    public String toString() {
        return Arrays.toString(emgSamples);
    }

    /**
     * Converts data from a collector to an integer array
     */
    public static int[] collectorToArray(EmgDataCollector collector){
        if(collector.emgSamples != null) {
            int[] array = new int[collector.emgSamples.length];
            for (int i = 0; i < collector.emgSamples.length; i++) {
                array[i] = ((Byte) collector.emgSamples[i]).intValue();
            }
            return array;
        }
        else
            return null;
    }
}
