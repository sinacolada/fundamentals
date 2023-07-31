// represents a Person with a user name and a list of buddies
class Person {
  String username;
  ILoBuddy buddies;
  double diction;
  double hearing;

  public Person(String username, double diction, double hearing) {
    this.username = username;
    this.buddies = new MtLoBuddy();
    this.diction = diction;
    this.hearing = hearing;
  }

  public Person(String username) {
    this.username = username;
    this.buddies = new MtLoBuddy();
    this.diction = 0.0;
    this.hearing = 0.0;
  }

  //---------------------------------------------------------------------------

  //EFFECT:
  //Change this person's buddy list so that it includes the given person
  public void addBuddy(Person buddy) {
    ILoBuddy pastBuddies = this.buddies;
    ILoBuddy newBuddies = new ConsLoBuddy(buddy, pastBuddies);
    this.buddies = newBuddies;
  }

  //---------------------------------------------------------------------------

  // returns true if this Person has that as a direct buddy
  public boolean hasDirectBuddy(Person buddy) {
    return this.buddies.containsBuddy(buddy);
  }

  // is this person's username the same as the given username
  public boolean sameUsername(String otherUsername) {
    return this.username.equals(otherUsername);
  }

  //---------------------------------------------------------------------------

  // returns the number of people that are direct buddies 
  // of both this and that person
  public int countCommonBuddies(Person that) {
    return this.buddies.countCommonBuddies(that.buddies);
  }

  //---------------------------------------------------------------------------

  // will the given person be invited to a party 
  // organized by this person?
  public boolean hasExtendedBuddy(Person that) {
    ILoBuddy extendedBuddies = this.getExtendedBuddies(new MtLoBuddy(), new MtLoBuddy());
    return extendedBuddies.containsBuddy(that);
  }

  //---------------------------------------------------------------------------

  // returns the number of people who will show up at the party 
  // given by this person
  public int partyCount() {
    ILoBuddy extendedBuddies = this.getExtendedBuddies(new MtLoBuddy(), new MtLoBuddy());
    if (extendedBuddies.containsBuddy(this)) {
      return extendedBuddies.length();
    }
    return 1 + extendedBuddies.length();
  }

  // returns the list of extended buddies of this person
  public ILoBuddy getExtendedBuddies(ILoBuddy extendedBuddies, ILoBuddy visitedBuddies) {
    return this.buddies.getExtendedBuddies(
        extendedBuddies.add(this.buddies), new ConsLoBuddy(this, visitedBuddies));
  }

  //---------------------------------------------------------------------------

  // calculates the best chance this Person's message gets to that Person
  public double maxLikelihood(Person that) {
    if (this.sameUsername(that.username)) {
      return 1.0;
    }
    return this.maxLikelihoodHelper(that, new MtLoBuddy())
        / this.hearing;
  }

  // calculates the best chance this Person's message gets to that Person
  // and accumulates the list of visitedBuddies to prevent cycles
  public double maxLikelihoodHelper(Person that, ILoBuddy visitedBuddies) {
    if (this.hasDirectBuddy(that)) {
      return this.hearing * this.diction * that.hearing;
    }
    if (this.hasExtendedBuddy(that)) {
      return this.hearing * this.diction * 
          this.buddies.maxLikelihoodHelper(that, new ConsLoBuddy(this, visitedBuddies));
    }
    return 0;
  }
}
