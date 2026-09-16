import java.util.function.Supplier;

public class App {
    public enum Name{
        Tim,
        Nat,
        Drew,
        Mandy
    }
    public enum Drink{
        Lemonade,
        Red_Wine,
        Gatorade,
        Water
    }
    public static void main(String[] args) throws Exception {
        animalExample();
        vendingMachineExample();
        drinkExample();
        clockExample();
        mathExample();
    }

    public static void mathExample(){
        System.out.println(Math.min(5, 10));
        System.out.println(Math.max(5, 10));
        System.out.println(Math.clamp(2, 5, 10));
        System.out.println(Math.abs(-6));
        System.out.println(Math.sqrt(25));
        System.out.println(Math.pow(5, 10));
        System.out.println(Math.sin(Math.PI/2));
        System.out.println(Math.atan2(0, -1));
        System.out.println(Math.E);
        // and much much more
    }

    public static void clockExample(){
        Clock clock = new Clock();
        Supplier<Integer> time = clock::getTicks;
        while(time.get() < 6000000){
            System.out.println(time.get());
        }
    }

    public static void drinkExample(){
        System.out.println(getFavoriteDrink(Name.Tim));
        System.out.println(getFavoriteDrink(Name.Nat));
        System.out.println(getFavoriteDrink(Name.Drew));
        System.out.println(getFavoriteDrink(Name.Mandy));
    }

    public static Drink getFavoriteDrink(Name name){
        Drink drink;
        switch (name) {
            case Tim:
                drink = Drink.Lemonade;
                break;
            case Nat:
                drink = Drink.Red_Wine;
                break;
            case Drew:
                drink = Drink.Gatorade;
                break;
            default:
                drink = Drink.Water;
                break;
        }
        return drink;
    }

    public static void vendingMachineExample(){
        boolean useNewVendingMachine = true;
        VendingMachine machine;
        if(useNewVendingMachine){
            machine = new VendingMachineB();
        } else {
            machine = new VendingMachineA();
        }
        runVendingMachine(machine);
    }

    public static void runVendingMachine(VendingMachine machine){
        machine.button1();
        machine.button2();
        machine.button3();
    }

    public static void animalExample(){
        Animal biscuit = new Animal("Biscuit", 50);
        Dog milo = new Dog("Milo", 30, "Frisbee");
        Cat buttons = new Cat("Buttons", 40, "Couch");

        biscuit.eat();
        milo.eat();
        buttons.eat();

        milo.woof();
        buttons.meow();

        System.out.println("Milo's favorite toy: " + milo.getFavoriteToy());
        System.out.println("Button's napping spot: " + buttons.getNappingSpot());

        Animal[] animals = {biscuit, milo, buttons};

        for(Animal animal : animals){
            System.out.println(animal.getName() + "'s weight is " + animal.getWeight());
            animal.jump();
        }
    }
}
