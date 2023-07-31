// represents an empty list of Person's buddies
class MtLoBuddy implements ILoBuddy {
  MtLoBuddy() {}

  // counts the length of the list
  public int length() {
    return 0;
  }

  // does this list contain the given Person
  public boolean containsBuddy(Person buddy) {
    return false;
  }

  // counts the number of common entries between this list and the given
  public int countCommonBuddies(ILoBuddy otherBuddies) {
    return 0;
  }

  // returns true if any Person in this list has that as an extended buddy
  public boolean haveDirectBuddy(Person that) {
    return false;
  }

  // accumulates extended buddies to the extendedBuddies list
  // while tracking the previously counted buddies in visitedBuddies
  public ILoBuddy getExtendedBuddies(ILoBuddy extendedBuddies, ILoBuddy visitedBuddies) {
    // TERMINATION: 
    // returns extendedBuddies because the empty list has been reached
    return extendedBuddies;
  }

  // adds on the given buddies to this list without repeats
  public ILoBuddy add(ILoBuddy buddies) {
    return buddies;
  }

  // calculates the best chance this Person's message gets to that Person
  public double maxLikelihoodHelper(Person that, ILoBuddy visitedBuddies) {
    return 0;
  }
}
