package model;

public abstract class Activity {

    private String id;
    private String name;
    private int duration; //en minutos
    private boolean completed = false;

    public Activity (String id, String name, int duration, boolean completed){
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public int getDuration(){
        return duration;
    }

    public boolean getCompleted(){
        return completed;
    }

    public void setCompleted(boolean completed){
        this.completed = completed;
    }


    //las subclases deben implementarlo
    public abstract int calculatePoints();


    @Override
    public String toString() {
        return String.format("%s[id=%s, name='%s', duration=%d, completed=%s]",
                getClass().getSimpleName(), id, name, duration, completed);
    }

}