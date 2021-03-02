import java.util.Arrays;

public class Utils {

    public boolean checkValidAddres(String address) {
        String[] parts = address.split("\\.");

        if(Float.parseFloat(parts[0])<224 || Float.parseFloat(parts[0])>239) return false;
        if(Float.parseFloat(parts[1])<0||Float.parseFloat(parts[0])>255
            || Float.parseFloat(parts[2])<0||Float.parseFloat(parts[2])>255
            || Float.parseFloat(parts[3])<0||Float.parseFloat(parts[3])>255) return false;
        return !address.equals("224.0.0.0");
    }
}
