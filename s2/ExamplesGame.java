// to represent a player's resources
interface IResource {
}

// to represent a player's actions
interface IAction {
}

// to represent a denial resource
class Denial implements IResource {
  String subject;
  int believability;
  
  Denial(String subject, int believability) {
    this.subject = subject;
    this.believability = believability;
  }
}

// to represent a bribe resource
class Bribe implements IResource {
  String target;
  int value;
  
  Bribe(String target, int value) {
    this.target = target;
    this.value = value;
  }
}

// to represent an apology resource
class Apology implements IResource {
  String excuse;
  boolean reusable;
  
  Apology(String excuse, boolean reusable) {
    this.excuse = excuse;
    this.reusable = reusable;
  }
}

// to represent a purchase action
class Purchase implements IAction {
  int cost;
  IResource item;
  
  Purchase(int cost, IResource item) {
    this.cost = cost;
    this.item = item;
  }
}

// to represent a swap action
class Swap implements IAction {
  IResource consumed;
  IResource received;
  
  Swap(IResource consumed, IResource received) {
    this.consumed = consumed;
    this.received = received;
  }
}

class ExamplesGame {
  
  ExamplesGame() {
  }
  
  // iDidntKnow: subject "knowledge", believability 51
  // witness: target "innocent witness", value 49
  // iWontDoItAgain: excuse "I won't do it again", not reusable
  // itDidntHappen: subject "knowledge", believability 5
  // brainiac: target "mastermind", value 100
  // prank: excuse "just a prank", reusable
  IResource iDidntKnow = new Denial("knowledge", 51);
  IResource witness = new Bribe("innocent witness", 49);
  IResource iWontDoItAgain = new Apology("I won't do it again", false);
  IResource itDidntHappen = new Denial("knowledge", 51);
  IResource brainiac = new Bribe("mastermind", 100);
  IResource prank = new Apology("just a prank", true);
  // received "value" cannot be more than 2 consumed "value"
  // purchase witness for 50
  // purchase prank for 10
  // swap witness for iDidntKnow
  // swap iDidntKnow for itDidntHappen
  IAction purchase1 = new Purchase(50, this.witness);
  IAction purchase2 = new Purchase(10, this.prank);
  IAction swap1 = new Swap(this.witness, this.iDidntKnow);
  IAction swap2 = new Swap(this.iDidntKnow, this.itDidntHappen);
}