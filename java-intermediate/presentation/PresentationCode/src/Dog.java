public class Dog extends Animal{

    private String favoriteToy;

    public Dog(String name, double weight, String favoriteToy) {
        super(name, weight);
        this.favoriteToy = favoriteToy;
    }

    public String getFavoriteToy(){
        return favoriteToy;
    }
    
    public void woof(){
        System.out.println(getName() + ": woof");
    }

    public void jump(){
        System.out.println(getName() + " jumped 2 ft");
    }
}


