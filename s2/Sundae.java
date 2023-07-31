// to represent a sundae
interface ISundae {  
}

// to represent a scoop
class Scoop implements ISundae {
  String flavor;
  
  Scoop(String flavor) {
    this.flavor = flavor;
  }
}

// to represent a topping
class Topping implements ISundae {
  ISundae inner;
  String name;
  
  Topping(ISundae inner, String name) {
    this.inner = inner;
    this.name = name;
  }
}

class ExamplesSundae {
  
  ExamplesSundae() {
  }
  
  // a "chocolate" scoop topped by "rainbow sprinkles" topped by "caramel" topped by "whipped cream"
  ISundae chocolateScoop = new Scoop("chocolate");
  ISundae rainbowSprinkles = new Topping(this.chocolateScoop, "rainbow sprinkles");
  ISundae caramel = new Topping(this.rainbowSprinkles, "caramel");
  ISundae yummy = new Topping(this.caramel, "whipped cream");
  // a "vanilla" scoop topped by "chocolate sprinkles" topped by "fudge" topped by "plum sauce"
  ISundae vanillaScoop = new Scoop("vanilla");
  ISundae chocolateSprinkles = new Topping(this.vanillaScoop, "chocolate sprinkles");
  ISundae fudge = new Topping(this.chocolateSprinkles, "fudge");
  ISundae noThankYou = new Topping(this.fudge, "plum sauce");
}