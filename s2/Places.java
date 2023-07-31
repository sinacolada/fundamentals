import tester.*;

class Place {
  String name;
  ILoFeature features;
  
  Place(String name, ILoFeature features) {
    this.name = name;
    this.features = features;
  }
  
  /* 
   * TEMPLATE:
   * ---------
   * Fields:
   * ... this.name ... --String
   * ... this.features ... --ILoFeature
   * Methods:
   * ... this.totalCapacity() ... --int
   * ... this.foodinessRating() ... --double
   * ... this.restarantInfo() ... --String
   * Methods for Fields:
   * ... this.features.findCapacity() ... --int
   * ... this.features.numRestaurants() ... --int
   * ... this.features.sumRating() ... --double
   * ... this.features.resInfo() ... --String
   */
  
  // returns total capacity of all reachable venues from THIS place
  int totalCapacity() {
    return this.features.findCapacity();
  }
  
  // returns average off all restaurant ratings reachable from THIS place
  double foodinessRating() {
    if (this.features.numRestaurants() == 0) {
      return 0.0;
    }
    else {
      return this.features.sumRating() / this.features.numRestaurants();
    }
  }
  
  // returns a String that has all reachable restaurants reachable from a place,
  // their food types in parenthesis, with each separated by comma and space
  String restaurantInfo() {
    return this.features.resInfo();
  }
}

interface ILoFeature {
  int findCapacity();
  
  int numRestaurants();
  
  double sumRating();
  
  String resInfo();
}

class MtLoFeature implements ILoFeature {
  MtLoFeature() {
  }
  
  // capacity count for empty feature (0)
  public int findCapacity() {
    return 0;
  }
  
  // restaurant count for empty feature (0)
  public int numRestaurants() {
    return 0;
  }
  
  // rating sum for empty feature (0.0)
  public double sumRating() {
    return 0.0;
  }
  
  // returns info for empty feature ("")
  public String resInfo() {
    return "";
  }
}

class ConsLoFeature implements ILoFeature {
  IFeature first;
  ILoFeature rest;
  
  ConsLoFeature(IFeature first, ILoFeature rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.first ... --IFeature
   * ... this.rest ... --ILoFeature
   * Methods: 
   * ... this.findCapacity() ... --int
   * ... this.numRestaurants() ... --int
   * ... this.sumRating() ... --double
   * ... this.resInfo() ... --String
   * Methods for Fields:
   * ... this.first.getCapacity() ... --int
   * ... this.first.restaurant() ... --int
   * ... this.first.getRating() ... --int
   * ... this.first.getResInfo() ... --String
   */
  
  // returns capacity of all reachable venues
  public int findCapacity() {
    return this.first.getCapacity() + this.rest.findCapacity();
  }
  
  // counts total number of restaurants in this feature list
  public int numRestaurants() {
    return this.first.restaurant() + this.rest.numRestaurants();
  }
  
  // sums rating for all restaurants in this feature list
  public double sumRating() {
    return this.first.getRating() + this.rest.sumRating();
  }
  
  // returns String with all restaurants and their types
  public String resInfo() {
    if (this.first.getResInfo().equals("")) {
      return this.rest.resInfo();
    }
    else if (this.rest.resInfo().equals("")) {
      return this.first.getResInfo();
    }
    else {
      return this.first.getResInfo() + ", " + this.rest.resInfo();
    }
  }
}

interface IFeature {
  int getCapacity();
  
  int restaurant();
  
  double getRating();
  
  String getResInfo();
}

class Restaurant implements IFeature {
  String name;
  String type;
  double averageRating;
  
  Restaurant(String name, String type, double averageRating) {
    this.name = name;
    this.type = type;
    this.averageRating = averageRating;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.name ... --String
   * ... this.type ... --String
   * ... this.averageRating ... --double
   * ... this.getRestInfo() ... --String
   * Methods: 
   * ... this.getCapacity() ... --int
   * ... this.restaurant() ... --int
   * ... this.getRating() ... --double
   */
  
  // returns venue capacity of a restaurant (0)
  public int getCapacity() {
    return 0;
  }
  
  // returns 1 (this feature is a restaurant)
  public int restaurant() {
    return 1;
  }
  
  // gets rating for the restaurant
  public double getRating() {
    return this.averageRating;
  }
  
  // returns restaurant name (type) for this restaurant
  public String getResInfo() {
    return this.name + " (" + this.type + ")";
  }
}

class Venue implements IFeature {
  String name;
  String type;
  int capacity;
  
  Venue(String name, String type, int capacity) {
    this.name = name;
    this.type = type;
    this.capacity = capacity;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.name ... --String
   * ... this.type ... --String
   * ... this.capacity ... --int
   * ... this.getResInfo() ... --String
   * Methods: 
   * ... this.getCapacity() ... --int
   * ... this.restaurant() ... --int
   * ... this.getRating() ... --double
   */
  
  // returns venue capacity
  public int getCapacity() {
    return this.capacity;
  }
  
  // returns 0 (not a restaurant)
  public int restaurant() {
    return 0;
  }
  
  // returns 0 (no restaurant)
  public double getRating() {
    return 0.0;
  }
  
  // returns "" (not restaurant)
  public String getResInfo() {
    return "";
  }
  
}

class ShuttleBus implements IFeature {
  String name;
  Place destination;
  
  ShuttleBus(String name, Place destination) {
    this.name = name;
    this.destination = destination;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.name ... --String
   * ... this.destination ... --Place
   * Methods: 
   * ... this.getCapacity() ... --int
   * ... this.restaurant() ... --int
   * ... this.getRating() ... --double
   * ... this.getResInfo() ... --String
   * Methods for Fields:
   * ... this.destination.totalCapacity() ... --int
   * ... this.destination.features.numRestaurants() ... --int
   * ... this.destination.features.sumRating() ... --double
   * ... this.destination.restaurantInfo() ... --String
   */
  
  // returns capacity of connected place venues
  public int getCapacity() {
    return this.destination.totalCapacity();
  }
  
  // returns number of restaurants in place connected
  public int restaurant() {
    return this.destination.features.numRestaurants();
  }
  
  // returns sum of ratings in connected place
  public double getRating() {
    return this.destination.features.sumRating();
  }
  
  // returns restaurant info n connected place
  public String getResInfo() {
    return this.destination.restaurantInfo();
  }
}

class ExamplesPlaces {
  
  ExamplesPlaces() {
  }
  
  ILoFeature mtList = new MtLoFeature();
  Place mtPlace = new Place("", this.mtList);
  
  IFeature tdGarden = new Venue("TD Garden", "stadium", 19580);
  IFeature dailyCatch = new Restaurant("The Daily Catch", "Sicilian", 4.4);
  ILoFeature northEndFeatures = new ConsLoFeature(this.tdGarden,
      new ConsLoFeature(this.dailyCatch, this.mtList));
  Place northEnd = new Place("North End", this.northEndFeatures);
  
  IFeature freshmen15 = new ShuttleBus("Freshmen-15", this.northEnd);
  IFeature borderCafe = new Restaurant("Border Cafe", "Tex-Mex", 4.5);
  IFeature harvardStadium = new Venue("Harvard Stadium", "football", 30323);
  ILoFeature harvardFeatures = new ConsLoFeature(this.freshmen15,
      new ConsLoFeature(this.borderCafe,
          new ConsLoFeature(this.harvardStadium, this.mtList)));
  Place harvard = new Place("Harvard", this.harvardFeatures);
  
  IFeature littleItalyExpress = new ShuttleBus("Little Italy Express", this.northEnd);
  IFeature reginasPizza = new Restaurant("Regina's Pizza", "pizza", 4.0);
  IFeature crimsonCruiser = new ShuttleBus("Crimson Cruiser", this.harvard);
  IFeature bostonCommon = new Venue("Boston Common", "public", 150000);
  ILoFeature southStationFeatures = new ConsLoFeature(this.littleItalyExpress,
      new ConsLoFeature(this.reginasPizza,
          new ConsLoFeature(this.crimsonCruiser,
              new ConsLoFeature(this.bostonCommon, this.mtList))));
  Place southStation = new Place("South Station", this.southStationFeatures);
  
  IFeature sarkuJapan = new Restaurant("Sarku Japan", "teriyaki", 3.9);
  IFeature starbucks = new Restaurant("Starbucks", "coffee", 4.1);
  IFeature bridgeShuttle = new ShuttleBus("bridge shuttle", this.southStation);
  ILoFeature cambridgeSideFeatures = new ConsLoFeature(this.sarkuJapan,
      new ConsLoFeature(this.starbucks,
          new ConsLoFeature(this.bridgeShuttle, this.mtList)));
  Place cambridgeSide = new Place("CambridgeSide Galleria", this.cambridgeSideFeatures);
  
  // tests functionality of totalCapacity method
  boolean testCapacity(Tester t) {
    return t.checkExpect(this.mtPlace.totalCapacity(), 0)
        && t.checkExpect(this.cambridgeSide.totalCapacity(), 19580 + 19580 + 30323 + 150000)
        && t.checkExpect(this.northEnd.totalCapacity(), 19580);
  }
  
  //tests functionality of findCapacity method
  boolean testFindCapacity(Tester t) {
    return t.checkExpect(this.mtList.findCapacity(), 0)
        && t.checkExpect(this.cambridgeSideFeatures.findCapacity(), 19580 + 19580 + 30323 + 150000)
        && t.checkExpect(this.northEndFeatures.findCapacity(), 19580);
  }
  
  //tests functionality of getCapacity method
  boolean testGetCapacity(Tester t) {
    return t.checkExpect(this.starbucks.getCapacity(), 0)
        && t.checkExpect(this.tdGarden.getCapacity(), 19580)
        && t.checkExpect(this.bridgeShuttle.getCapacity(), 219483);
  }
  
  // tests functionality of foodinessRating method
  boolean testRating(Tester t) {
    return t.checkExpect(this.northEnd.foodinessRating(), 4.4)
        && t.checkInexact(this.mtPlace.foodinessRating(), 0.0, 0.00001)
        && t.checkInexact(this.cambridgeSide.foodinessRating(), 253 / 60.0, 0.00001);
  }
  
  //tests functionality of numRestaurants method
  boolean testnumRestaurants(Tester t) {
    return t.checkExpect(this.mtList.numRestaurants(), 0)
        && t.checkExpect(this.northEndFeatures.numRestaurants(), 1)
        && t.checkExpect(this.cambridgeSideFeatures.numRestaurants(), 6);
  }
  
  //tests functionality of sumRating method
  boolean testSumRating(Tester t) {
    return t.checkExpect(this.mtList.sumRating(), 0.0)
        && t.checkExpect(this.northEndFeatures.sumRating(), 4.4)
        && t.checkInexact(this.cambridgeSideFeatures.sumRating(), 25.29, 0.01);
  }
  
  //tests functionality of restaurant method
  boolean testRestaurant(Tester t) {
    return t.checkExpect(this.starbucks.restaurant(), 1)
        && t.checkExpect(this.tdGarden.restaurant(), 0)
        && t.checkExpect(this.bridgeShuttle.restaurant(), 4);
  }
  
  //tests functionality of getRating method
  boolean testGetRating(Tester t) {
    return t.checkExpect(this.starbucks.getRating(), 4.1)
        && t.checkExpect(this.tdGarden.getRating(), 0.0)
        && t.checkExpect(this.bridgeShuttle.getRating(), 17.3);
  }
  
  // tests functionality of restaurantInfo method
  boolean testInfo(Tester t) {
    return t.checkExpect(this.northEnd.restaurantInfo(), "The Daily Catch (Sicilian)")
        && t.checkExpect(this.cambridgeSide.restaurantInfo(), "Sarku Japan (teriyaki),"
            + " Starbucks (coffee), The Daily Catch (Sicilian), Regina's Pizza (pizza), "
            + "The Daily Catch (Sicilian), Border Cafe (Tex-Mex)");
  }
  
  //tests functionality of resInfo method
  boolean testResInfo(Tester t) {
    return t.checkExpect(this.mtList.resInfo(), "")
        && t.checkExpect(this.northEndFeatures.resInfo(), "The Daily Catch (Sicilian)")
        && t.checkExpect(this.cambridgeSideFeatures.resInfo(), "Sarku Japan (teriyaki),"
            + " Starbucks (coffee), The Daily Catch (Sicilian), Regina's Pizza (pizza), "
            + "The Daily Catch (Sicilian), Border Cafe (Tex-Mex)");
  }
  
  //tests functionality of getResInfo method
  boolean testGetRestInfo(Tester t) {
    return t.checkExpect(this.starbucks.getResInfo(), "Starbucks (coffee)")
        && t.checkExpect(this.tdGarden.getResInfo(), "")
        && t.checkExpect(this.bridgeShuttle.getResInfo(), "The Daily Catch (Sicilian),"
            + " Regina's Pizza (pizza), The Daily Catch (Sicilian), Border Cafe (Tex-Mex)");
  }
  
  
  /* the totalCapacity, foodinessRating, and restaurantInfo double count 
   * due to the fact that multiple buses have destinations as northEnd, and since
   * the destination field has these methods called upon it to to find 'all reachable
   * possibilities', the northEnd place will have these methods called upon it twice,
   * so double counting of this place will result in duplication
   */
}
