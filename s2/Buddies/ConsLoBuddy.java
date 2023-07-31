// represents a list of Person's buddies
class ConsLoBuddy implements ILoBuddy {
  Person first;
  ILoBuddy rest;

  public ConsLoBuddy(Person first, ILoBuddy rest) {
    this.first = first;
    this.rest = rest;
  }

  // counts the length of the list
  public int length() {
    return 1 + this.rest.length();
  }

  // does this list contain the given Person
  public boolean containsBuddy(Person buddy) {
    return this.first.sameUsername(buddy.username)
        || this.rest.containsBuddy(buddy);
  }

  // counts the number of common entries between this list and the given
  public int countCommonBuddies(ILoBuddy otherBuddies) {
    if (otherBuddies.containsBuddy(this.first)) {
      return 1 + this.rest.countCommonBuddies(otherBuddies);
    }
    return this.rest.countCommonBuddies(otherBuddies);
  }

  // returns true if any Person in this list has that as an extended buddy
  public boolean haveDirectBuddy(Person that) {
    return this.first.hasExtendedBuddy(that)
        || this.rest.haveDirectBuddy(that);
  }

  // accumulates extended buddies to the extendedBuddies list
  // while tracking the previously counted buddies in visitedBuddies
  public ILoBuddy getExtendedBuddies(ILoBuddy extendedBuddies, ILoBuddy visitedBuddies) {
    // if first is not yet visited, then get all extended buddies of first
    //   and add the first onto the visitedBuddies 
    //   then pass them to the rest.getExtendedBuddies(..., ...)
    if (!visitedBuddies.containsBuddy(this.first)) {
      return this.rest.getExtendedBuddies(
          this.first.getExtendedBuddies(extendedBuddies, visitedBuddies),
          new ConsLoBuddy(this.first, visitedBuddies));
    }
    return this.rest.getExtendedBuddies(extendedBuddies, visitedBuddies);
  }

  // adds on the given buddies to this list without repeats
  public ILoBuddy add(ILoBuddy buddies) {
    if (!buddies.containsBuddy(this.first)) {
      return this.rest.add(new ConsLoBuddy(this.first, buddies));
    }
    return this.rest.add(buddies);
  }

  // calculates the best chance that some Person's message from this list gets to that Person
  // and accumulates the list of visitedBuddies to prevent cycles
  public double maxLikelihoodHelper(Person that, ILoBuddy visitedBuddies) {
    if (!visitedBuddies.containsBuddy(this.first)) {
      return Math.max(
          this.first.maxLikelihoodHelper(that, visitedBuddies), 
          this.rest.maxLikelihoodHelper(that, new ConsLoBuddy(this.first, visitedBuddies)));
    }
    return this.rest.maxLikelihoodHelper(that, visitedBuddies);
  }
}
