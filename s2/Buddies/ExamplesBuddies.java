import tester.*;

// runs tests for the buddies problem
class ExamplesBuddies {
  
  // Examples and constants
  
  // For "Circularly Referential Data"
  Person ann;
  Person bob;
  Person cole;
  Person dan;
  Person ed;
  Person fay;
  Person gabi;
  Person hank;
  Person jan;
  Person kim;
  Person len;
  Person loser;
  
  // For "Game of telephone"
  Person a;
  Person b;
  Person c;
  Person d;
  Person e;
  
  ILoBuddy mt;

  // resets examples
  public void initBuddies() {
    this.mt = new MtLoBuddy();
    this.ann = new Person("Ann");
    this.bob = new Person("Bob");
    this.cole = new Person("Cole");
    this.dan = new Person("Dan");
    this.ed = new Person("Ed");
    this.fay = new Person("Fay");
    this.gabi = new Person("Gabi");
    this.hank = new Person("Hank");
    this.jan = new Person("Jan");
    this.kim = new Person("Kim");
    this.len = new Person("Len");
    this.loser = new Person("Loser");
    
    // adds buddies
    this.ann.addBuddy(this.bob);
    this.ann.addBuddy(this.cole);
    this.bob.addBuddy(this.ann);
    this.bob.addBuddy(this.ed);
    this.bob.addBuddy(this.hank);
    this.cole.addBuddy(this.dan);
    this.dan.addBuddy(this.cole);
    this.ed.addBuddy(this.fay);
    this.fay.addBuddy(this.ed);
    this.fay.addBuddy(this.gabi);
    this.gabi.addBuddy(this.ed);
    this.gabi.addBuddy(this.fay);
    this.jan.addBuddy(this.kim);
    this.jan.addBuddy(this.len);
    this.kim.addBuddy(this.jan);
    this.kim.addBuddy(this.len);
    this.len.addBuddy(this.kim);
    this.len.addBuddy(this.jan);

    this.a = new Person("A", 0.95, 0.8);
    this.b = new Person("B", 0.85, 0.99);
    this.c = new Person("C", 0.95, 0.9);
    this.d = new Person("D", 1.0, 0.95);
    this.e = new Person("E");
    // adds buddies
    this.a.addBuddy(this.b);
    this.a.addBuddy(this.c);
    this.b.addBuddy(this.d);
    this.c.addBuddy(this.d);
  }
  
  // tests functionality of containsBuddy method on example ilobuddy
  void testContainsBuddy(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.containsBuddy(this.a), false);
    t.checkExpect(this.a.buddies.containsBuddy(this.b), true);
    t.checkExpect(this.a.buddies.containsBuddy(this.c), true);
    t.checkExpect(this.b.buddies.containsBuddy(this.d), true);
    t.checkExpect(this.a.buddies.containsBuddy(this.d), false);
    t.checkExpect(this.d.buddies.containsBuddy(this.a), false);
    t.checkExpect(this.ann.buddies.containsBuddy(this.len), false);
    t.checkExpect(this.ann.buddies.containsBuddy(this.bob), true);
    t.checkExpect(this.gabi.buddies.containsBuddy(this.kim), false);
    t.checkExpect(this.loser.buddies.containsBuddy(this.len), false);
    t.checkExpect(this.cole.buddies.containsBuddy(this.dan), true);
  }
  
  // tests functionality of length method on example ilobuddy
  void testLength(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.length(), 0);
    t.checkExpect(this.a.buddies.length(), 2);
    t.checkExpect(this.d.buddies.length(), 0);
    t.checkExpect(this.ann.buddies.length(), 2);
    t.checkExpect(this.cole.buddies.length(), 1);
    t.checkExpect(this.fay.buddies.length(), 2);
    t.checkExpect(this.bob.buddies.length(), 3);
  }
  
  // tests functionality of countCommonBuddies method on example ilobuddy
  void testCountCommonBuddies(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.countCommonBuddies(this.a.buddies), 0);
    t.checkExpect(this.a.buddies.countCommonBuddies(this.b.buddies), 0);
    t.checkExpect(this.b.buddies.countCommonBuddies(this.c.buddies), 1);
    t.checkExpect(this.ann.buddies.countCommonBuddies(this.loser.buddies), 0);
    t.checkExpect(this.bob.buddies.countCommonBuddies(this.gabi.buddies), 1);
    t.checkExpect(this.cole.buddies.countCommonBuddies(this.fay.buddies), 0);
    t.checkExpect(this.len.buddies.countCommonBuddies(this.jan.buddies), 1);
    t.checkExpect(this.cole.buddies.countCommonBuddies(this.dan.buddies), 0);
  }
  
  // tests functionality of haveDirectBuddy method on example ilobuddy
  void testHaveDirectBuddy(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.haveDirectBuddy(this.a), false);
    t.checkExpect(this.a.buddies.haveDirectBuddy(this.d), true);
    t.checkExpect(this.a.buddies.haveDirectBuddy(this.e), false);
    t.checkExpect(this.c.buddies.haveDirectBuddy(this.a), false);
    t.checkExpect(this.a.buddies.haveDirectBuddy(this.c), false);
    t.checkExpect(this.len.buddies.haveDirectBuddy(this.kim), true);
    t.checkExpect(this.kim.buddies.haveDirectBuddy(this.len), true);
    t.checkExpect(this.bob.buddies.haveDirectBuddy(this.gabi), true);
    t.checkExpect(this.gabi.buddies.haveDirectBuddy(this.bob), false);
  }
  
  // tests functionality of getExtendedBuddies method on example ilobuddy
  void testGetExtendedBuddies(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.getExtendedBuddies(this.mt, this.mt), this.mt);
    t.checkExpect(this.a.buddies.getExtendedBuddies(this.mt, this.mt), 
        new ConsLoBuddy(this.d, this.mt));
    t.checkExpect(this.a.buddies.getExtendedBuddies(this.mt, new ConsLoBuddy(this.d, this.mt)), 
        new ConsLoBuddy(this.d, this.mt));
    t.checkExpect(this.d.buddies.getExtendedBuddies(this.mt, this.mt), 
        this.mt);
    t.checkExpect(this.cole.buddies.getExtendedBuddies(this.mt, this.mt), 
        new ConsLoBuddy(this.cole, new ConsLoBuddy(this.dan, this.mt)));
    t.checkExpect(this.kim.buddies.getExtendedBuddies(this.mt, this.mt), 
        new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len,
            new ConsLoBuddy(this.jan, this.mt))));
  }
  
  // tests functionality of add method on example ilobuddy
  void testAdd(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.add(this.mt), this.mt);
    t.checkExpect(this.mt.add(this.a.buddies), this.a.buddies);
    t.checkExpect(this.c.buddies.add(this.mt), this.c.buddies);
    t.checkExpect(this.a.buddies.add(this.b.buddies),
        new ConsLoBuddy(this.b, new ConsLoBuddy(this.c,
            new ConsLoBuddy(this.d, this.mt))));
    t.checkExpect(this.dan.buddies.add(this.cole.buddies),
        new ConsLoBuddy(this.cole, this.cole.buddies));
    t.checkExpect(this.len.buddies.add(this.ann.buddies),
        new ConsLoBuddy(this.kim, new ConsLoBuddy(this.jan, this.ann.buddies)));
  }
  
  // tests functionality of addBuddy method on example person
  void testAddBuddy(Tester t) {
    initBuddies();
    
    // check initial states
    t.checkExpect(this.d.buddies.containsBuddy(this.a), false);
    t.checkExpect(this.a.buddies.containsBuddy(this.d), false);
    t.checkExpect(this.b.buddies.containsBuddy(this.d), true);
    t.checkExpect(this.ann.buddies.containsBuddy(this.fay), false);
    t.checkExpect(this.cole.buddies.containsBuddy(this.len), false);
    t.checkExpect(this.len.buddies.containsBuddy(this.jan), true);
    
    // mutation effect
    this.d.addBuddy(this.a);
    this.a.addBuddy(this.d);
    this.ann.addBuddy(this.fay);
    this.len.addBuddy(this.jan);
    
    // check effected final states
    t.checkExpect(this.d.buddies.containsBuddy(this.a), true);
    t.checkExpect(this.a.buddies.containsBuddy(this.d), true);
    t.checkExpect(this.b.buddies.containsBuddy(this.d), true);
    t.checkExpect(this.ann.buddies.containsBuddy(this.fay), true);
    t.checkExpect(this.cole.buddies.containsBuddy(this.len), false);
    t.checkExpect(this.len.buddies.containsBuddy(this.jan), true);
  }
  
  // tests functionality of hasDirectBuddy method on example person
  void testHasDirectBuddy(Tester t) {
    initBuddies();
    t.checkExpect(this.a.hasDirectBuddy(this.d), false);
    t.checkExpect(this.c.hasDirectBuddy(this.d), true);
    t.checkExpect(this.d.hasDirectBuddy(this.c), false);
    t.checkExpect(this.a.hasDirectBuddy(this.c), true);
    t.checkExpect(this.loser.hasDirectBuddy(this.ann), false);
    t.checkExpect(this.gabi.hasDirectBuddy(this.loser), false);
    t.checkExpect(this.jan.hasDirectBuddy(this.len), true);
    t.checkExpect(this.len.hasDirectBuddy(this.jan), true);
    t.checkExpect(this.bob.hasDirectBuddy(this.ed), true);
    t.checkExpect(this.ed.hasDirectBuddy(this.bob), false);
  }
  
  // tests functionality of sameUsername method on example person
  void testSameUsername(Tester t) {
    initBuddies();
    t.checkExpect(this.loser.sameUsername("Loser"), true);
    t.checkExpect(this.ann.sameUsername("Ann"), true);
    t.checkExpect(this.c.sameUsername("C"), true);
    t.checkExpect(this.gabi.sameUsername("gabi"), false);
    t.checkExpect(this.b.sameUsername(""), false);
    t.checkExpect(this.cole.sameUsername("Dan"), false);
    
  }
  
  // tests functionality of countCommonBuddies method on example person
  void testCountCommonBuddiesPerson(Tester t) {
    initBuddies();
    t.checkExpect(this.loser.countCommonBuddies(this.a), 0);
    t.checkExpect(this.b.countCommonBuddies(this.c), 1);
    t.checkExpect(this.a.countCommonBuddies(this.a), 2);
    t.checkExpect(this.ann.countCommonBuddies(this.gabi), 0);
    t.checkExpect(this.kim.countCommonBuddies(this.len), 1);
    t.checkExpect(this.cole.countCommonBuddies(this.hank), 0);
    t.checkExpect(this.dan.countCommonBuddies(this.cole), 0);
    t.checkExpect(this.gabi.countCommonBuddies(this.ed), 1);
  }
  
  // tests functionality of hasExtendedBuddy method on example person
  void testHasExtendedBuddy(Tester t) {
    initBuddies();
    t.checkExpect(this.a.hasExtendedBuddy(this.d), true);
    t.checkExpect(this.b.hasExtendedBuddy(this.d), true);
    t.checkExpect(this.loser.hasExtendedBuddy(this.ann), false);
    t.checkExpect(this.ed.hasExtendedBuddy(this.loser), false);
    t.checkExpect(this.kim.hasExtendedBuddy(this.ann), false);
    t.checkExpect(this.bob.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.cole.hasExtendedBuddy(this.len), false);
    t.checkExpect(this.ann.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.gabi.hasExtendedBuddy(this.ann), false);
  }
  
  // tests functionality of partyCount method on example person
  void testPartyCount(Tester t) {
    initBuddies();
    t.checkExpect(this.loser.partyCount(), 1);
    t.checkExpect(this.a.partyCount(), 4);
    t.checkExpect(this.b.partyCount(), 2);
    t.checkExpect(this.ann.partyCount(), 8);
    t.checkExpect(this.len.partyCount(), 3);
    t.checkExpect(this.dan.partyCount(), 2);
    t.checkExpect(this.ed.partyCount(), 3);
    t.checkExpect(this.cole.partyCount(), 2);
    t.checkExpect(this.bob.partyCount(), 8);
  }
  
  // tests functionality of getExtendedBuddies method on example person
  void testGetExtendedBuddiesPerson(Tester t) {
    initBuddies();
    t.checkExpect(this.loser.getExtendedBuddies(this.mt, this.mt), 
        this.mt);
    t.checkExpect(this.a.getExtendedBuddies(this.mt, this.mt), 
        new ConsLoBuddy(this.d, new ConsLoBuddy(this.c,
            new ConsLoBuddy(this.b, this.mt))));
    t.checkExpect(this.d.getExtendedBuddies(this.mt, this.mt), 
        this.mt);
    t.checkExpect(this.len.getExtendedBuddies(this.mt, this.mt), 
        new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len,
            new ConsLoBuddy(this.jan, this.mt))));
    t.checkExpect(this.cole.getExtendedBuddies(this.mt, this.mt), 
        new ConsLoBuddy(this.dan, new ConsLoBuddy(this.cole, this.mt)));
  }
  
  // tests functionality of maxLikelihood method on example person
  void testMaxLikelihood(Tester t) {
    initBuddies();
    t.checkInexact(this.a.maxLikelihood(this.b), 0.9405, 0.0005);
    t.checkExpect(this.a.maxLikelihood(this.c), 0.855);
    t.checkInexact(this.a.maxLikelihood(this.d), 0.772, 0.0005);
    t.checkExpect(this.a.maxLikelihood(this.e), 0.0);
    t.checkExpect(this.b.maxLikelihood(this.c), 0.0);
    t.checkInexact(this.b.maxLikelihood(this.d), 0.8075, 0.0005);
    t.checkInexact(this.c.maxLikelihood(this.d), 0.9025, 0.0005);
  }
  
  // tests functionality of maxLikelihoodHelper method on example person
  void testMaxLikelihoodHelperPerson(Tester t) {
    initBuddies();
    t.checkInexact(this.a.maxLikelihoodHelper(this.d, this.mt), 0.6173, 0.005);
    t.checkInexact(this.a.maxLikelihoodHelper(this.c, this.mt), 0.684, 0.005);
    t.checkInexact(this.a.maxLikelihoodHelper(this.b, this.mt), 0.7524, 0.005);
    t.checkExpect(this.a.maxLikelihoodHelper(this.e, this.mt), 0.0);
    t.checkInexact(this.a.maxLikelihoodHelper(this.d, new ConsLoBuddy(this.c, this.mt)),
        0.6077, 0.005);
    t.checkInexact(this.b.maxLikelihoodHelper(this.d, this.mt), 0.8, 0.005);
    t.checkInexact(this.b.maxLikelihoodHelper(this.d, new ConsLoBuddy(this.c, this.mt)),
        0.8, 0.005);
  }
  
  // tests functionality of maxLikelihoodHelper method on example ilobuddy
  void testMaxLikelihoodHelper(Tester t) {
    initBuddies();
    t.checkExpect(this.mt.maxLikelihoodHelper(this.a, this.mt), 0.0);
    t.checkExpect(this.a.buddies.maxLikelihoodHelper(this.b, this.mt), 0.0);
    t.checkInexact(this.a.buddies.maxLikelihoodHelper(this.d, this.mt), 0.81225, 0.005);
    t.checkInexact(this.a.buddies.maxLikelihoodHelper(this.d, new ConsLoBuddy(this.c, this.mt)),
        0.79943, 0.005);
  }
}