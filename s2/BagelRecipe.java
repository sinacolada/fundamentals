import tester.*;

class Utils {
  
  /*
   * TEMPLATE
   * ------------
   * Methods:
   * ... this.checkWeight(double ingr1, double ingr2) ... -> boolean
   * ... this.checkWeight(double first, double second, double epsilon, String msg) ... -> boolean
   */
  
  //checks to see if the first weight minus the second weight is less than the epsilon (equality)
  //if it isn't then an exception is returned
  double checkWeight(double first, double second, double epsilon, String msg) {
    if (Math.abs(first - second) < epsilon) {
      return first;
    }
    else {
      throw new IllegalArgumentException(msg);
    }
  }
  
  //checks if weight of two ingredients are within 0.001 oz
  boolean checkWeight(double ingr1, double ingr2) {
    return Math.abs(ingr1 - ingr2) < 0.001;
  } 
}

class BagelRecipe {
  double flour;
  double water;
  double yeast;
  double malt;
  double salt;
  
  BagelRecipe(double flour, double water, double yeast, double salt, double malt) {
    this.flour = new Utils().checkWeight(flour, water, 0.00001, 
        "Invalid Flour Amount: " + Double.toString(flour) 
        + " & Invalid Water Amount: " + Double.toString(water));
    this.water = this.flour;
    this.yeast = new Utils().checkWeight(yeast, malt, 0.00001,
        "Invalid Yeast Amount: " + Double.toString(yeast) 
        + " & Invalid Malt Amount: " + Double.toString(malt));
    this.salt = new Utils().checkWeight(yeast + salt, flour / 20.0, 0.00001,
        "Invalid Salt Amount: " + Double.toString(salt));
    this.malt = this.yeast;
  }
  
  BagelRecipe(double flour, double yeast) {
    this(flour, flour, yeast, (flour / 20.0) - yeast,  yeast);
  }
  
  //flour and water are measured in cups while yeast, salt, and malt are measured in teaspoons
  BagelRecipe(double flour, double yeast, double salt) {
    this(4.25 * flour, 4.25 * flour, yeast * (5.0 / 48.0), salt * (10.0 / 48.0), 
        yeast * (5.0 / 48.0));
  }
  
  /*
   * TEMPLATE
   * ------------
   * Fields:
   * ... this.flour ... --double
   * ... this.water ... --double
   * ... this.yeast ... --double
   * ... this.salt ... --double
   * ... this.malt ... --double
   * Methods:
   * ... this.sameRecipe(BagelRecipe) ... -> boolean
   * Methods on fields:
   * Fields on parameters:
   * Methods on parameters:
   */
  
  // returns whether other bagel recipe has same weight ingredients
  // within 0.001 ounces as this bagel recipe
  boolean sameRecipe(BagelRecipe other) {
    return new Utils().checkWeight(this.flour, other.flour)
        && new Utils().checkWeight(this.water, other.water)
        && new Utils().checkWeight(this.yeast, other.yeast)
        && new Utils().checkWeight(this.salt, other.salt)
        && new Utils().checkWeight(this.malt, other.malt);
  }
}

class ExamplesBagelRecipe {
  
  BagelRecipe con1Bagel = new BagelRecipe(300.0, 300.0, 10.0, 5.0, 10.0);
  BagelRecipe con2Bagel = new BagelRecipe(300.0, 10.0);
  BagelRecipe con3Bagel = new BagelRecipe(100.0, 100.0, 52.0);
  
  // checkConstructorExceptions
  boolean testConstructorExceptions(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException(
        "Invalid Flour Amount: " + Double.toString(100.0) 
        + " & Invalid Water Amount: " + Double.toString(50.0)),
        "BagelRecipe", 100.0, 50.0, 75.0, 20.0, 75.0)
        && t.checkConstructorException(new IllegalArgumentException(
            "Invalid Yeast Amount: " + Double.toString(20.0) 
            + " & Invalid Malt Amount: " + Double.toString(30.0)),
            "BagelRecipe", 100.0, 100.0, 20.0, 15.0, 30.0)
        && t.checkConstructorException(new IllegalArgumentException(
            "Invalid Salt Amount: " + Double.toString(100.0)),
            "BagelRecipe", 100.0, 100.0, 50.0, 100.0, 50.0)
        && t.checkConstructorException(new IllegalArgumentException(
            "Invalid Salt Amount: " + Double.toString(65.0 / 6.0)),
            "BagelRecipe", 100.0, 95.0, 52.0);
  }
  
  //tests functionality of second defined checkWeight() method
  boolean testCheckWeightSecondType(Tester t) {
    return t.checkExpect(new Utils().checkWeight(300.0, 300.0), true)
        && t.checkExpect(new Utils().checkWeight(10.0, 5.0), false);
  }
  
  //tests functionality of first defined checkWeight() method 
  boolean testCheckWeightFirstType(Tester t) {
    return t.checkExpect(new Utils().checkWeight(300.0, 300.0, 0.00001, "error"), 300.0);
  }
  
  //tests functionality of sameRecipe method
  boolean testSameRecipe(Tester t) {
    return t.checkExpect(this.con1Bagel.sameRecipe(this.con1Bagel), true)
        && t.checkExpect(this.con1Bagel.sameRecipe(this.con2Bagel), true)
        && t.checkExpect(this.con2Bagel.sameRecipe(this.con3Bagel), false); 
  }
}