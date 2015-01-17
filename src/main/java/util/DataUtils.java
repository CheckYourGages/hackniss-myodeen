package util;

/**
 * Created by Leo on 1/17/15.
 */
public class DataUtils {
    public static void allValuesAbsolute(int[] array){
        for(int i=0; i<array.length; i++){
            array[i]=Math.abs(array[i]);
        }
    }
}
