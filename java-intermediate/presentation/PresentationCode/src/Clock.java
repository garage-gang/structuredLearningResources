public class Clock {
    private int ticks = 0;
    public Clock(){
        Thread ticker = new Thread(() -> tickLoop());
        ticker.start();
    }

    private void tickLoop(){
        while(ticks < 6000000){
            ticks++;
        }
    }

    public int getTicks(){
        return ticks;
    }
}
