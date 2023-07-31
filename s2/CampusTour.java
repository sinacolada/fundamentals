import tester.*;

// a campus tour
class CampusTour {
  int startTime; // minutes from midnight
  ITourLocation startingLocation;

  CampusTour(int startTime, ITourLocation startingLocation) {
    this.startTime = startTime;
    this.startingLocation = startingLocation;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.startTime ... --int
   * ... this.startingLocation ... --ITourLocation
   * Methods: 
   * ... this.sameTour(CampusTour other) ... --boolean
   * Methods for Fields:
   * ... this.startingLocation.sameITL(ITourLocation other.startingLocation) ... -- boolean
   */

  // is this tour the same tour as the given one?
  boolean sameTour(CampusTour other) {
    return this.startTime == other.startTime 
        && this.startingLocation.sameITL(other.startingLocation);
  }
}

// a spot on the tour
interface ITourLocation {
  
  boolean sameITL(ITourLocation other);
  
  boolean sameTourEnd(TourEnd other);
  
  boolean sameMandatory(Mandatory other);
  
  boolean sameBranchingTour(BranchingTour other);
  
}

abstract class ATourLocation implements ITourLocation {
  String speech; // the speech to give at this spot on the tour

  ATourLocation(String speech) {
    this.speech = speech;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.speech ... --String
   * Methods: 
   * ... this.sameTourEnd(TourEnd other) ... --boolean
   * ... this.sameMandatory(Mandatory other) ... --boolean
   * ... this.sameBranchingTour(BranchingTour other) ... --boolean
   * ... this.sameITL(ITourLocation other) ... --boolean
   */
  
  //Are the the TourEnds the same?
  public boolean sameTourEnd(TourEnd other) {
    return false;
  }
  
  //Are the the Mandatory the same?
  public boolean sameMandatory(Mandatory other) {
    return false;
  }
  
  //Are the the BranchingTour the same?
  public boolean sameBranchingTour(BranchingTour other) {
    return false;
  } 
  
  public abstract boolean sameITL(ITourLocation other);
}

// the end of the tour
class TourEnd extends ATourLocation {
  ICampusLocation location;

  TourEnd(String speech, ICampusLocation location) {
    super(speech);
    this.location = location;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.location ... --ICampusLocation
   * Methods: 
   * ... this.sameTourEnd(TourEnd other) ... --boolean
   * ... this.sameITL(ITourLocation other) ... --boolean
   * Methods for Fields:
   * ... this.speech.equals(String other.speech) ... --boolean
   * ... this.location.sameICL(ICampusLocation other.location) ... --boolean
   * Fields for Parameters:
   * ... other.location ... --ICampusLocation
   * Methods for Parameters:
   * ... other.sameTourEnd(TourEnd this) ... --boolean
   */
  
  //Are the TourEnds the same?
  public boolean sameTourEnd(TourEnd other) {
    return this.speech.equals(other.speech)
        && this.location.sameICL(other.location);
  }
  
  //Are the TourEnds the same?
  public boolean sameITL(ITourLocation other) {
    return other.sameTourEnd(this);
  } 
}

//a mandatory spot on the tour with the next place to go
class Mandatory extends ATourLocation {
  ICampusLocation location;
  ITourLocation next;

  Mandatory(String speech, ICampusLocation location, ITourLocation next) {
    super(speech);
    this.location = location;
    this.next = next;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.location ... --ICampusLocation
   * ... this.next ... --ITourLocation
   * Methods: 
   * ... this.sameMandatory(Mandatory other)... --boolean
   * ... this.sameITL(ITourLocation other) ... --boolean
   * Methods for Fields:
   * ... this.speech.equals(String other.speech) ... -- boolean
   * ... this.location.sameICL(ICampusLocation other.location) ... --boolean
   * ... this.next.sameITL(ITourLocation other.next);
   * Fields for Parameters:
   * ... other.location ... --ICampusLocation
   * ... other.next ... --ITourLocation
   * Methods for Parameters:
   * ... other.sameMandatory(Mandatory this) ... --boolean
   */
  
  //Are the Mandatorys the same?
  public boolean sameMandatory(Mandatory other) {
    return this.speech.equals(other.speech)
        && this.location.sameICL(other.location)
        && this.next.sameITL(other.next);
  }
  
  //Are the Mandatorys the same?
  public boolean sameITL(ITourLocation other) {
    return other.sameMandatory(this);
  } 
}

// up to the tour guide where to go next
class BranchingTour extends ATourLocation {
  ITourLocation option1;
  ITourLocation option2;

  BranchingTour(String speech, ITourLocation option1, ITourLocation option2) {
    super(speech);
    this.option1 = option1;
    this.option2 = option2;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.option1 ... --ITourLocation
   * ... this.option2 ... --ITourLocation
   * Methods: 
   * ... this.sameBranchingTour(BranchingTour other)... --boolean
   * ... this.sameITL(ITourLocation other) ... --boolean
   * Methods for Fields:
   * ... this.speech.equals(String other.speech) ... -- boolean
   * ... this.option1.sameITL(ITourLocation other.option1) ... --boolean
   * ... this.option2.sameITL(ITourLocation other.option2)) ... --boolean
   * ... this.next.sameITL(ITourLocation other.next);
   * Fields for Parameters:
   * ... other.option1 ... --ITourLocation
   * ... other.option2 ... --ITourLocation
   * Methods for Parameters:
   * ... other.sameBranchingTour(BranchingTour this) ... --boolean
   */
  
  //Are the BranchingTours the same?
  public boolean sameBranchingTour(BranchingTour other) {
    return this.speech.equals(other.speech)
        && ((this.option1.sameITL(other.option1) && this.option2.sameITL(other.option2))
        || (this.option1.sameITL(other.option2) && this.option2.sameITL(other.option1)));
  }
  
  //Are the BranchingTours the same?
  public boolean sameITL(ITourLocation other) {
    return other.sameBranchingTour(this);
  } 
}

interface ICampusLocation {
  
  boolean sameICL(ICampusLocation other);
  
  boolean sameBuilding(Building other);
  
  boolean sameQuad(Quad other);
  
}

class Building implements ICampusLocation {
  String name;
  Address address;

  Building(String name, Address address) {
    this.name = name;
    this.address = address;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.name ... --String
   * ... this.address ... --Address
   * Methods: 
   * ... this.sameBuilding(Building other)... --boolean
   * ... this.sameQuad(Quad other) ... --boolean
   * ... this.sameICL(ICampusLocation other) ... --boolean
   * Methods for Fields:
   * ... this.name.equals(String other.name)  ... -- boolean
   * ... this.address.sameAddress(Address other.address) ... --boolean
   * Fields for Parameters:
   * ... other.name ... --String
   * ... other.address ... --Address
   * Methods for Parameters:
   * ... other.sameBuilding(ICampusLocation this) ... --boolean
   */
  
  //Are the buildings the same?
  public boolean sameBuilding(Building other) {
    return this.name.equals(other.name) 
        && this.address.sameAddress(other.address);
  }
  
  //if this a Building, return false
  public boolean sameQuad(Quad other) {
    return false;
  }
  
  //Are the buildings the same?
  public boolean sameICL(ICampusLocation other) {
    return other.sameBuilding(this);
  }
}

class Address {
  String street;
  int number;

  Address(String street, int number) {
    this.number = number;
    this.street = street;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.number ... --int
   * ... this.street ... --String
   * Methods: 
   * ... this.sameAddress(Address other) ... --boolean
   * Methods for Fields:
   * ... this.street.equals(String other.street) ... --boolean
   * Fields for Parameters:
   * ... other.number ... --int
   * ... other.street ... --String
   */
  
  //Are the addresses the same?
  public boolean sameAddress(Address other) {
    return this.number == other.number 
        && this.street.equals(other.street);
  } 
}

class Quad implements ICampusLocation {
  String name;
  ILoCampusLocation surroundings; // in clockwise order, starting north

  Quad(String name, ILoCampusLocation surroundings) {
    this.name = name;
    this.surroundings = surroundings;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.name ... --String
   * ... this.surroundings ... --ILoCampusLocation
   * Methods: 
   * ... this.sameQuad(Quad other)... --boolean
   * ... this.sameBuilding(Building other) ... --boolean
   * ... this.sameICL(ICampusLocation other) ... --boolean
   * Methods for Fields:
   * ... this.name.equals(String other.name)  ... -- boolean
   * ... this.surroundings.sameILCL(ILoCampusLocation other.surroundings) ... --boolean
   * Fields for Parameters:
   * ... other.name ... --String
   * ... other.surroundings ... --ILoCampusLocation
   * Methods for Parameters:
   * ... other.sameQuad(Quad this) ... --boolean
   */
  
  //Are the Quads the same?
  public boolean sameQuad(Quad other) {
    return this.name.equals(other.name) 
        && this.surroundings.sameILCL(other.surroundings);
  }
  
  //If this is a building, return false
  public boolean sameBuilding(Building other) {
    return false;
  }
  
  //Are the Quads the same?
  public boolean sameICL(ICampusLocation other) {
    return other.sameQuad(this);
  }
}

interface ILoCampusLocation {
  boolean sameILCL(ILoCampusLocation other);
  
  boolean sameMtLoCampusLocation(MtLoCampusLocation other);
  
  boolean sameConsLoCampusLocation(ConsLoCampusLocation other);
  
}

class MtLoCampusLocation implements ILoCampusLocation {
  
  /*
   * TEMPLATE:
   * -----------
   * Methods: 
   * ... this.sameILCL(ILoCampusLocation other)... --boolean
   * ... this.sameMtLoCampusLocation(MtLoCampusLocation other) ... --boolean
   * ... this.sameConsLoCampusLocation(ConsLoCampusLocation other) ... --boolean
   * Methods for Parameters:
   * ... other.sameMtLoCampusLocation(MtLoCampusLocation this)) ... --boolean
   */
  
  //returns false since this is MtLoCampusLocation but other is not
  public boolean sameILCL(ILoCampusLocation other) {
    return other.sameMtLoCampusLocation(this);
  }
  
  //this is a MtLoCampusLocation while other is not, so return false (impossible since sameILCL 
  //resolves this case before this function would be used)
  public boolean sameMtLoCampusLocation(MtLoCampusLocation other) {
    return true;
  }
  
  //this is empty while the other is not, so return false
  public boolean sameConsLoCampusLocation(ConsLoCampusLocation other) {
    return false;
  }
}

class ConsLoCampusLocation implements ILoCampusLocation {
  ICampusLocation first;
  ILoCampusLocation rest;

  ConsLoCampusLocation(ICampusLocation first, ILoCampusLocation rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.first ... --ICampusLocation
   * ... this.rest ... --ILoCampusLocation
   * Methods: 
   * ... this.sameILCL(ILoCampusLocation other)... --boolean
   * ... this.sameMtLoCampusLocation(MtLoCampusLocation other) ... --boolean
   * ... this.sameConsLoCampusLocation(ConsLoCampusLocation other) ... --boolean
   * Methods for Fields:
   * ... this.first.sameICL(ICampusLocation other.first)  ... -- boolean
   * ... this.surroundings.sameILCL(ILoCampusLocation other.surroundings) ... --boolean
   * Fields for Parameters:
   * ... other.first ... --ICampusLocation
   * ... other.rest ... --ILoCampusLocation
   * Methods for Parameters:
   * ... other.sameConsLoCampusLocation(ConsLoCampusLocation this) ... --boolean
   */
  
  // Are the ILoCampusLocations the same?
  public boolean sameILCL(ILoCampusLocation other) {
    return other.sameConsLoCampusLocation(this);
  }
  
  // this is not empty, so return false
  public boolean sameMtLoCampusLocation(MtLoCampusLocation other) {
    return false;
  }
  
  // Are the ILoCampusLocations the same?
  public boolean sameConsLoCampusLocation(ConsLoCampusLocation other) {
    return this.first.sameICL(other.first) 
        && this.rest.sameILCL(other.rest);
  }
}

class ExamplesCampus {
  
  Address addressMarino = new Address("Huntington Avenue", 259);
  Address addressIsec = new Address("Columbus Avenue", 805);
  Address addressSnell = new Address("Huntington Avenue", 360);
  Address addressEllHall = new Address("Huntington Avenue", 346);
  Address addressDodgeHall = new Address("Huntington Avenue", 360);
  Address addressCurry = new Address("Huntington Avenue", 360);
  Address addressWVH = new Address("Huntington Avenue", 440);
  
  ICampusLocation buildingMarino = new Building("Marino", this.addressMarino);
  ICampusLocation buildingIsec = new Building("ISEC", this.addressIsec);
  ICampusLocation buildingSnell = new Building("Snell Library", this.addressSnell);
  ICampusLocation buildingEllHall = new Building("Ell Hall", this.addressEllHall);
  ICampusLocation buildingDodgeHall = new Building("Dodge Hall", this.addressDodgeHall);
  ICampusLocation buildingCurry = new Building("Curry Student Center", this.addressCurry);
  ICampusLocation buildingWVH = new Building("West Village H", this.addressWVH);
  
  ILoCampusLocation empty = new MtLoCampusLocation();
  ILoCampusLocation krentzmanSurroundings = new ConsLoCampusLocation(this.buildingEllHall,
      new ConsLoCampusLocation(this.buildingDodgeHall, this.empty));
  ILoCampusLocation fakeSurroundings = new ConsLoCampusLocation(this.buildingDodgeHall,
      new ConsLoCampusLocation(this.buildingEllHall, this.empty));
  ILoCampusLocation centennialSurroundings = new ConsLoCampusLocation(this.buildingWVH,
      new ConsLoCampusLocation(this.buildingSnell, this.empty));
  
  ICampusLocation quadKrentzman = new Quad("Krentzman Quadrangle", this.krentzmanSurroundings);
  ICampusLocation quadFake = new Quad("Krentzman Quadrangle", this.fakeSurroundings);
  ICampusLocation quadCentennial = new Quad("Centennial Common", this.centennialSurroundings);
  
  ITourLocation branch2Loc3 = new TourEnd("Finance!", this.buildingDodgeHall);
  ITourLocation branch3Loc3 = new TourEnd("Finance!", this.buildingDodgeHall);
  ITourLocation branch4Loc3 = new TourEnd("CS!", this.buildingWVH);
  
  ITourLocation branch1Loc2 = new TourEnd("Study!", this.buildingSnell);
  ITourLocation branch2Loc2 = new Mandatory("Aud!", this.buildingEllHall, this.branch2Loc3);
  ITourLocation branch3Loc2 = new Mandatory("Aud!", this.buildingEllHall, this.branch3Loc3);
  ITourLocation branch4Loc2 = new Mandatory(
      "Student Center!", this.buildingCurry, this.branch4Loc2);
  
  ITourLocation branchTour1 = new Mandatory("Split!", this.buildingIsec, this.branch1Loc2);
  ITourLocation branchTour2 = new Mandatory("Split!", this.quadKrentzman, this.branch2Loc2);
  ITourLocation branchTour3 = new Mandatory("Split!", this.quadFake, this.branch3Loc2);
  ITourLocation branchTour4 = new Mandatory("Split!", this.quadCentennial, this.branch4Loc2);
  
  ITourLocation tour1Loc2 = new BranchingTour("Hello!", this.branchTour1, this.branchTour2);
  ITourLocation tour2Loc2 = new BranchingTour("Hello!", this.branchTour2, this.branchTour1);
  ITourLocation tour3Loc2 = new BranchingTour("Hello!", this.branchTour3, this.branchTour1);
  ITourLocation tour4Loc2 = new BranchingTour("Hello!", this.branchTour4, this.branchTour1);
  
  ITourLocation stLoc1 = new Mandatory("Hi!", this.buildingMarino, this.tour1Loc2);
  ITourLocation stLoc2 = new Mandatory("Hi!", this.buildingMarino, this.tour2Loc2);
  ITourLocation stLoc3 = new Mandatory("Hi!", this.buildingMarino, this.tour3Loc2);
  ITourLocation stLoc4 = new Mandatory("Hi!", this.buildingMarino, this.tour4Loc2);
  
  CampusTour ct1 = new CampusTour(600, this.stLoc1);
  CampusTour ct2 = new CampusTour(600, this.stLoc2);
  CampusTour ct3 = new CampusTour(600, this.stLoc3);
  CampusTour ct4 = new CampusTour(600, this.stLoc4);
  CampusTour ct5 = new CampusTour(500, this.stLoc3);
  
  // tests the functionality of the sameTour method on example CampusTours
  boolean testSameTour(Tester t) {
    return t.checkExpect(this.ct1.sameTour(this.ct2), true)
        && t.checkExpect(this.ct2.sameTour(this.ct1), true)
        && t.checkExpect(this.ct1.sameTour(this.ct3), false)
        && t.checkExpect(this.ct3.sameTour(this.ct1), false)
        && t.checkExpect(this.ct2.sameTour(this.ct3), false)
        && t.checkExpect(this.ct3.sameTour(this.ct5), false)
        && t.checkExpect(this.ct4.sameTour(this.ct2), false);
  }
  
  //tests the functionality of the sameICL method
  boolean testSameICL(Tester t) {
    return t.checkExpect(this.buildingMarino.sameICL(this.buildingDodgeHall), false)
        && t.checkExpect(this.buildingCurry.sameICL(this.buildingCurry), true)
        && t.checkExpect(this.quadKrentzman.sameICL(this.buildingCurry), false)
        && t.checkExpect(this.quadKrentzman.sameICL(this.quadKrentzman), true)
        && t.checkExpect(this.quadFake.sameICL(this.quadKrentzman), false);
  }
  
  //tests the functionality of the sameBuilding method
  boolean testSameBuilding(Tester t) {
    return t.checkExpect(this.buildingMarino.sameBuilding((Building) this.buildingMarino), true)
        && t.checkExpect(this.buildingCurry.sameBuilding((Building) this.buildingMarino), false);
  }
  
  //tests the functionality of the sameAddress method
  boolean testSameAddress(Tester t) {
    return t.checkExpect(this.addressEllHall.sameAddress(new Address("Huntington Avenue", 24)), 
        false)
        && t.checkExpect(this.addressEllHall.sameAddress(new Address("Ell Hall", 346)), false)
        && t.checkExpect(this.addressEllHall.sameAddress(this.addressEllHall), true);
  }
  
  // tests the functionality of the sameQuad method on example Quads
  boolean testSameQuad(Tester t) {
    return t.checkExpect(this.quadCentennial.sameICL((Quad) this.quadKrentzman), false)
        && t.checkExpect(this.buildingDodgeHall.sameICL((Building) this.buildingCurry), false)
        && t.checkExpect(this.quadKrentzman.sameICL((Quad) this.quadKrentzman), true)
        && t.checkExpect(this.buildingCurry.sameICL((Quad) this.quadKrentzman), false)
        && t.checkExpect(this.quadKrentzman.sameICL((Quad) this.quadFake), false);
  }
  
  // tests the functionality of the sameILCL method on example ILoCampusLocations
  boolean testSameILCL(Tester t) {
    return t.checkExpect(this.empty.sameILCL(this.empty), true)
        && t.checkExpect(this.krentzmanSurroundings.sameILCL(krentzmanSurroundings), true)
        && t.checkExpect(this.krentzmanSurroundings.sameILCL(this.centennialSurroundings), false)
        && t.checkExpect(this.krentzmanSurroundings.sameILCL(this.fakeSurroundings), false)
        && t.checkExpect(this.krentzmanSurroundings.sameILCL(new ConsLoCampusLocation(
            this.buildingCurry,
            this.empty)), false)
        && t.checkExpect(this.empty.sameILCL(this.centennialSurroundings), false)
        && t.checkExpect(this.centennialSurroundings.sameILCL(this.centennialSurroundings), true);
  }

  //tests the functionality of the sameConsLoCampusLocation method
  boolean testSameMtLoCampusLocation(Tester t) {
    return t.checkExpect(this.empty.sameMtLoCampusLocation((MtLoCampusLocation) this.empty), true)
        && t.checkExpect(this.centennialSurroundings.sameMtLoCampusLocation(
            (MtLoCampusLocation) this.empty), false);
  }
  
  //tests the functionality of the sameConsLoCampusLocation method
  boolean testSameConsLoCampusLocation(Tester t) {
    return t.checkExpect(this.empty.sameConsLoCampusLocation(
        (ConsLoCampusLocation) this.centennialSurroundings), false)
        && t.checkExpect(this.empty.sameConsLoCampusLocation(
            (ConsLoCampusLocation) this.centennialSurroundings), false)
        && t.checkExpect(this.centennialSurroundings.sameConsLoCampusLocation(
            (ConsLoCampusLocation) this.centennialSurroundings), true)
        && t.checkExpect(this.krentzmanSurroundings.sameConsLoCampusLocation(
            (ConsLoCampusLocation) this.centennialSurroundings), false);
  }
  
  // tests the functionality of the sameTourEnd method on examples
  boolean testSameTourEnd(Tester t) {
    return t.checkExpect(this.branch2Loc3.sameTourEnd((TourEnd) this.branch3Loc3), true)
        && t.checkExpect(this.branch2Loc3.sameTourEnd((TourEnd) this.branch4Loc3), false)
        && t.checkExpect(this.tour1Loc2.sameTourEnd((TourEnd) this.branch3Loc3), false)
        && t.checkExpect(this.stLoc1.sameTourEnd((TourEnd) this.branch4Loc3), false);
  }
  
  // tests the functionality of the sameBranchingTour method on examples
  boolean testSameBranchingTour(Tester t) {
    return t.checkExpect(this.tour1Loc2.sameBranchingTour((BranchingTour) this.tour2Loc2), true)
        && t.checkExpect(this.tour1Loc2.sameBranchingTour((BranchingTour) this.tour3Loc2), false)
        && t.checkExpect(this.branch2Loc3.sameBranchingTour((BranchingTour) this.tour1Loc2), false)
        && t.checkExpect(this.stLoc1.sameBranchingTour((BranchingTour) this.tour1Loc2), false);
  }
  
  // tests the functionality of the sameMandatory method on examples
  boolean testSameMandatory(Tester t) {
    return t.checkExpect(this.stLoc1.sameMandatory((Mandatory) this.stLoc2), true)
        && t.checkExpect(this.stLoc1.sameMandatory((Mandatory) this.stLoc4), false)
        && t.checkExpect(this.branch2Loc3.sameMandatory((Mandatory) this.stLoc1), false)
        && t.checkExpect(this.tour1Loc2.sameMandatory((Mandatory) this.stLoc1), false);
  }
}