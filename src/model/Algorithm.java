package model;

import java.time.LocalTime;

public interface Algorithm {
    TimeBlock applyAlgorithm(Schedule schedule, Task task);
    public LocalTime getNightStart();

    public void setNightStart(LocalTime nightStart);

    public LocalTime getNightEnd();

    public void setNightEnd(LocalTime nightEnd);

    public void setNightCheck(boolean checkNight);
    
    public boolean getNightCheck();
}
