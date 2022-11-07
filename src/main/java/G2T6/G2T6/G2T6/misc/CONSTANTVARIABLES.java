package G2T6.G2T6.G2T6.misc;

import java.text.SimpleDateFormat;

import G2T6.G2T6.G2T6.misc.State;

public class CONSTANTVARIABLES {
    private CONSTANTVARIABLES() {
    } // prevent other copys

    public static final int EMISSIONVALUE = 0;
    public static final int MORALEVALUE = 0;
    public static final int INCOMEVALUE = 0;

    public static final State DEFAULTSTATE = State.start;
    public static final int DEFAULTYEAR = 0;
    public static final long DEFAULTGAMEID = 0;

    public static final boolean OPEN_ENDED = true;
    public static final boolean NOT_OPEN_ENDED = false;

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
