public class Cat extends Animal{

    private String nappingSpot;

    public Cat(String name, double weight, String nappingSpot) {
        super(name, weight);
        this.nappingSpot = nappingSpot;
    }

    public String getNappingSpot(){
        return nappingSpot;
    }
    
    public void meow(){
        System.out.println(getName() + ": meow");
    }

    public void jump(){
        System.out.println(getName() + " jumped 5 ft");
    }
}


