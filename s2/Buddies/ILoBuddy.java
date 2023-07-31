// represents a list of Person's buddies
interface ILoBuddy {
  // counts the length of the list
  public int length();

  // does this list contain the given Person
  public boolean containsBuddy(Person buddy);

  // counts the number of common direct buddies between this list and the given
  public int countCommonBuddies(ILoBuddy otherBuddies);

  // returns true if any Person in this list has that as an extended buddy
  public boolean haveDirectBuddy(Person that);

  // accumulates extended buddies to the extendedBuddies list
  // while tracking the previously counted buddies in visitedBuddies
  public ILoBuddy getExtendedBuddies(ILoBuddy extendedBuddies, ILoBuddy visitedBuddies);

  // adds on the given buddies to this list without repeats
  public ILoBuddy add(ILoBuddy buddies);

  // calculates the best chance that some Person's message from this list gets to that Person
  // and accumulates the list of visitedBuddies to prevent cycles
  public double maxLikelihoodHelper(Person that, ILoBuddy visitedBuddies);
}
