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

    public static int[] sumStackArrayValues(ArrayStack stackOfarrays){
        int[] total = new int[8];
        while(!stackOfarrays.isEmpty()){
            int[] tmp = stackOfarrays.pop();
            for(int i=0; i<tmp.length; i++){
                total[i]+=tmp[i];
            }
        }
        return total;
    }

    public static double averageAcrossArray(int[] array){
        int total=0;
        for(int i:array){
            total+=i;
        }

        return (double)total/array.length;
    }
}
