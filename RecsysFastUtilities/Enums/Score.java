package reebo_cecere.Enums;

/**
 * Score that can be used when creating a URM
 * @author Andrea Riboni
 */
public enum Score {
    FIRST_TIME_WATCHED(1f),
    FIRST_TIME_CLICKED(1f),
    REWATCHED(0.1f),
    RECLICKED(0.1f),
    MALUS(-0.1f),
    NEUTRAL(0f),
    BONUS(0.1f),
    BIG_BONUS(0.2f),
    HUGE_BONUS(0.3f);

    private final float value;

    Score(float v){
        value = v;
    }

    public float getValue(){
        return value;
    }
}
