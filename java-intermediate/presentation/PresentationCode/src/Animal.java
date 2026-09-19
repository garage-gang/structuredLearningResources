public class Animal{
    private String name;
    private double weightIbs;

    public Animal(String name, double weightIbs){
        this.name = name;
        this.weightIbs = weightIbs;
    }

    public String getName(){
        return name;
    }

    public double getWeight(){
        return weightIbs;
    }

    public void eat(){
        System.out.println(getName() + ": nom nom nom gulp");
    }

    public void jump(){
        System.out.println(getName() + " jumped 1 ft");
    }
}



