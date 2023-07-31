import tester.*;

//used to represent an individual mathematical Monomial with a degree and coefficient
class Monomial {
  int degree;
  int coefficient;
  
  Monomial(int degree, int coefficient) {
    if (degree >= 0) {
      this.degree = degree;
    }
    else {
      throw new IllegalArgumentException("Degree of monomial must be positive. "
          + "Degree provided: " + Integer.toString(degree));
    }
    this.coefficient = coefficient;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.degree ... --int
   * ... this.coefficient ... --int
   * Methods: 
   * ... this.notRepeated(ILoMonomial monomialList)... --boolean
   * ... this.unequalDegree(int otherDegree) ... --boolean
   * ... this.coefficientIsZero() ... --boolean
   * ... this.sameMono(Monomial other) ... --boolean
   * Fields for Parameters:
   * ... other.degree ... --int
   * ... other.coefficient ... --int
   * Methods for Parameters:
   * ... monomialList.doesNotContain(int this.degree) ... --boolean
   */
  
  //returns whether monomial degree isn't repeated
  public boolean notRepeated(ILoMonomial monomialList) {
    return monomialList.doesNotContain(this.degree);
  }
  
  //returns whether this degree equals another (zero is ok, "nonexistant term")
  public boolean unequalDegree(int otherDegree) {
    return this.degree != otherDegree;
  }
  
  //determines if the coefficient is zero 
  public boolean coefficientIsZero() {
    return this.coefficient == 0;
  }
  
  //determines if the monomials are the same
  public boolean sameMono(Monomial other) {
    return this.degree == other.degree 
        && this.coefficient == other.coefficient;
  }
}

//Used to represent a mathematical Polynomial with a list of monomials
class Polynomial {
  ILoMonomial monomials;
  
  Polynomial(ILoMonomial monomials) {
    //defines constructor if degrees are unique
    if (monomials.uniqueDegrees()) {
      this.monomials = monomials;
    }
    //throws an error if the degrees are not unique
    else {
      throw new IllegalArgumentException("One or more monomials in this polynomial"
          + " have the same degree.");
    }
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.monomials ... --ILoMonomials
   * Methods: 
   * ... this.samePolynomial(Polynomial other) ... --boolean
   * Methods for Fields:
   * ... this.monomials.sameILM(ILoMonomials other.monomials) ... -- boolean
   * Fields for Parameters:
   * ... other.monomials ... --ILoMonomials 
   */
  
  //determines if this polynomial is the same as other
  public boolean samePolynomial(Polynomial other) {
    return this.monomials.sameILM(other.monomials);
  }
}

//represents a list of Monomials
interface ILoMonomial {
  
  // returns if all the degrees of the monomials are different
  boolean uniqueDegrees();
  
  // returns whether degree of monomial isn't repeated anywhere else in the monomial list
  boolean doesNotContain(int degree);
  
  boolean sameILM(ILoMonomial other);
  
  boolean sameMLM(ILoMonomial other);
  
  ILoMonomial removeFoundPair(Monomial other);
  
  boolean allZeroCoefficients();
  
  boolean findPair(Monomial other);
}

//Used to end a list of Monomials
class MtLoMonomial implements ILoMonomial {
  
  /*
   * TEMPLATE:
   * -----------
   * Methods: 
   * ... this.uniqueDegrees()... --boolean
   * ... this.doesNotContain(int degree) ... --boolean
   * ... this.sameILM(ILoMonomial other) ... --boolean
   * ... this.sameMLM(ILoMonomial other) ... --boolean
   * ... this.removeFoundPair(Monomial other) ... --boolean
   * ... this.allZeroCoefficients() ... --boolean
   * ... this.findPair(Monomial other) ... --boolean
   * Methods for Parameters:
   * ... other.sameMLM(ILoMonomial this) ... --boolean
   * ... other.allZeroCoefficients() ... --boolean
   */
  
  // empty list contains uniqueDegrees (no degrees repeat, there are none)
  public boolean uniqueDegrees() {
    return true;
  }
  
  // empty list doesn't contain a degree b/c it has none
  public boolean doesNotContain(int degree) {
    return true;
  }
  
  //determines if other is also empty and if so returns true as they are equal
  public boolean sameILM(ILoMonomial other) {
    return other.sameMLM(this) || other.allZeroCoefficients();
  }
  
  //since this is empty like other was, return true
  public boolean sameMLM(ILoMonomial other) {
    return true;
  }
  
  //returns this when this is an MtLoMonomial
  public ILoMonomial removeFoundPair(Monomial other) {
    return this;
  }
  
  //returns true since there is only a empty item in the list and no items without a zero
  //coefficient
  public boolean allZeroCoefficients() {
    return true;
  }
  
  //if this is empty, return false because other is not empty or there are no more
  //monomials to look through in the list
  public boolean findPair(Monomial other) {
    return false;
  }
}

//used to create a new piece of a ILoMonomial
class ConsLoMonomial implements ILoMonomial {
  Monomial first;
  ILoMonomial rest;
  
  ConsLoMonomial(Monomial first, ILoMonomial rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.first ... --Monomial
   * ... this.rest ... --ILoMonomial
   * Methods: 
   * ... this.uniqueDegrees()... --boolean
   * ... this.doesNotContain(int degree) ... --boolean
   * ... this.sameILM(ILoMonomial other) ... --boolean
   * ... this.sameMLM(ILoMonomial other) ... --boolean
   * ... this.removeFoundPair(Monomial other) ... --boolean
   * ... this.allZeroCoefficients() ... --boolean
   * ... this.findPair(Monomial other) ... --boolean
   * Methods for Fields:
   * ... this.first.notRepeated(ILoMonomial this.rest) ... -- boolean
   * ... this.rest.uniqueDegrees() ... --boolean
   * ... this.first.unequalDegree(int degree)  ... -- boolean
   * ... this.rest.doesNotContain(int degree)... --boolean
   * ... this.first.coefficientIsZero()  ... -- boolean
   * ... this.rest.sameILM(ILoMonomial other)... --ILoMonomial
   * ... this.first.sameMono(Monomial other) ... --boolean
   * ... this.rest.removeFoundPair(ILoMonomial other) ... --ILoMonomial
   * ... this.first.coefficientIsZero() ... --boolean
   * ... this.rest.allZeroCoefficients() ... --boolean
   * ... this.rest.findPair(other) ... --boolean
   * Fields for Parameters:
   * ... other.first ... --Monomial
   * ... other.rest ... --ILoMonomial
   * Methods for Parameters:
   * ... other.findPair(Monomial this.first) ... --ILoMonomial
   * ... other.removeFoundPair(Monomial this.first) ... --boolean
   */
  
  // returns if all the degrees of the ILoMonomial are different
  public boolean uniqueDegrees() {
    return this.first.notRepeated(this.rest) 
        && this.rest.uniqueDegrees();
  }
  
  // returns whether degree of monomial isn't repeated anywhere else in the ILoMonomial
  public boolean doesNotContain(int degree) {
    return this.first.unequalDegree(degree)
        && this.rest.doesNotContain(degree);
  }
  
  //determines if this ILM and other are the same 
  public boolean sameILM(ILoMonomial other) {
    if (this.first.coefficientIsZero()) {
      return this.rest.sameILM(other);
    }
    else {
      return other.findPair(this.first) && this.rest.sameILM(other.removeFoundPair(this.first));
    }
  }
  
  //this is not empty like other was so return false
  public boolean sameMLM(ILoMonomial other) {
    return false;
  }
  
  //removes the first item from this ILoMonomial if it is the same as other
  public ILoMonomial removeFoundPair(Monomial other) {
    if (this.first.sameMono(other)) {
      return this.rest.removeFoundPair(other);
    }
    else {
      return new ConsLoMonomial(this.first, this.rest.removeFoundPair(other));
    }
  }
  
  //determines if all the items in the list have zero coefficients
  public boolean allZeroCoefficients() {
    return this.first.coefficientIsZero() && this.rest.allZeroCoefficients();
  }
  
  //determines if the first of this ILoMonomial is the same as other and then continues
  //checking the list in search of a pair for other
  public boolean findPair(Monomial other) {
    return this.first.sameMono(other) || this.rest.findPair(other);
  }
}

//Used to test classes and methods
class ExamplesPolynomial {
  Monomial m0 = new Monomial(1, 0);
  Monomial m1 = new Monomial(2, 3);
  Monomial m2 = new Monomial(0, 5);
  
  ILoMonomial mtM = new MtLoMonomial();
  
  ILoMonomial p1M1 = new ConsLoMonomial(this.m1, 
      new ConsLoMonomial(this.m2, this.mtM));
  ILoMonomial p1M2 = new ConsLoMonomial(m2, 
      new ConsLoMonomial(this.m1, this.mtM));
  ILoMonomial p1M3 = new ConsLoMonomial(this.m2, 
      new ConsLoMonomial(this.m0, new ConsLoMonomial(this.m1, this.mtM)));
  ILoMonomial basicCLM = new ConsLoMonomial(this.m0, this.mtM);
  
  Polynomial p1_1 = new Polynomial(this.p1M1);
  Polynomial p1_2 = new Polynomial(this.p1M2);
  Polynomial p1_3 = new Polynomial(this.p1M3);
  
  Monomial m3 = new Monomial(8, 8);
  Monomial m4 = new Monomial(4, 4);
  Monomial m5 = new Monomial(2, 2);
  Monomial m6 = new Monomial(0, 1);
  Monomial m7 = new Monomial(1, -3);
  
  ILoMonomial p2M1 = new ConsLoMonomial(this.m3,
      new ConsLoMonomial(this.m4, new ConsLoMonomial(this.m5, 
          new ConsLoMonomial(this.m6, this.mtM))));
  ILoMonomial p2M1Rest = new ConsLoMonomial(this.m4, new ConsLoMonomial(this.m5, 
      new ConsLoMonomial(this.m6, this.mtM)));
  ILoMonomial p2M2 = new ConsLoMonomial(this.m6,
      new ConsLoMonomial(this.m5, new ConsLoMonomial(this.m3, 
          new ConsLoMonomial(this.m4, this.mtM))));
  ILoMonomial p2M3 = new ConsLoMonomial(this.m5,
      new ConsLoMonomial(this.m6, new ConsLoMonomial(this.m3, 
          new ConsLoMonomial(this.m4, this.mtM))));
  ILoMonomial p2M4 = new ConsLoMonomial(this.m7, this.p2M3);
  
  Polynomial p2_1 = new Polynomial(this.p2M1);
  Polynomial p2_2 = new Polynomial(this.p2M2);
  Polynomial p2_3 = new Polynomial(this.p2M3);
  Polynomial p2_4 = new Polynomial(this.p2M4);
  
  Monomial failM1 = new Monomial(6, 4);
  Monomial failM2 = new Monomial(3, 2);
  Monomial failM3 = new Monomial(3, 5);
  Monomial failM4 = new Monomial(0, 4);
  Monomial failM5 = new Monomial(0, 2);
  
  ILoMonomial failPM = new ConsLoMonomial(this.failM1, 
      new ConsLoMonomial(this.failM2, new ConsLoMonomial(this.failM3, this.mtM)));
  ILoMonomial failPMRest1 = new ConsLoMonomial(this.failM2, new ConsLoMonomial(this.failM3, 
      this.mtM));
  ILoMonomial failPMRest2 = new ConsLoMonomial(this.failM3, this.mtM);
  ILoMonomial failPM2 = new ConsLoMonomial(this.failM4,
      new ConsLoMonomial(this.failM5, this.mtM));
  
  //checks for constructor exceptions in Monomial and Polynomial    
  boolean testConstructorException(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Degree of monomial"
        + " must be positive. Degree provided: " + Integer.toString(-1)), "Monomial", -1, 3)
        && t.checkConstructorException(new IllegalArgumentException("One or more"
        + " monomials in this polynomial have the same degree."), "Polynomial",
        this.failPM)
        && t.checkConstructorException(new IllegalArgumentException("One or more"
        + " monomials in this polynomial have the same degree."), "Polynomial",
        this.failPM2);
  }
  
  //checks functionality of findILM method 
  boolean testSameILM(Tester t) {
    return t.checkExpect(this.p1M1.sameILM(this.mtM), false)
        && t.checkExpect(this.mtM.sameILM(this.p1M1), false)
        && t.checkExpect(this.basicCLM.sameILM(this.basicCLM), true)
        && t.checkExpect(this.mtM.sameILM(this.mtM), true)
        && t.checkExpect(this.p1M1.sameILM(this.p1M1), true);
  }
  
  //checks functionality of findPair method 
  boolean testFindPair(Tester t) {
    return t.checkExpect(this.p1M1.findPair(this.m1), true)
        && t.checkExpect(this.p1M1.findPair(this.m0), false)
        && t.checkExpect(this.mtM.findPair(this.m0), false);
  }
  
  //checks functionality of sameMono method 
  boolean testSameMono(Tester t) {
    return t.checkExpect(this.m0.sameMono(this.m0), true)
        && t.checkExpect(this.m0.sameMono(new Monomial(1,1)), false)
        && t.checkExpect(this.m0.sameMono(new Monomial(0,1)), false);
  }
  
  //checks functionality of coefficentIsZero method 
  boolean testCoefficentIsZero(Tester t) {
    return t.checkExpect(this.m0.coefficientIsZero(), true)
        && t.checkExpect(this.m1.coefficientIsZero(), false);
  }
  
  //checks functionality of allZeroCoefficients method 
  boolean testAllZeroCoefficients(Tester t) {
    return t.checkExpect(this.basicCLM.allZeroCoefficients(), true)
        && t.checkExpect(this.p1M3.allZeroCoefficients(), false);
  }
  
  //checks functionality of samePolynomial method 
  boolean testSamePolynomial(Tester t) {
    return t.checkExpect(this.p1_1.samePolynomial(this.p1_2), true)
        && t.checkExpect(this.p1_2.samePolynomial(this.p1_3), true)
        && t.checkExpect(this.p1_3.samePolynomial(this.p1_1), true)
        && t.checkExpect(this.p2_1.samePolynomial(this.p2_2), true)
        && t.checkExpect(this.p2_2.samePolynomial(this.p2_3), true)
        && t.checkExpect(this.p2_3.samePolynomial(this.p2_1), true)
        && t.checkExpect(this.p1_1.samePolynomial(this.p2_1), false)
        && t.checkExpect(this.p2_4.samePolynomial(this.p2_4), true)
        && t.checkExpect(this.p1_3.samePolynomial(this.p2_2), false);
  }
  
  // checks functionality of notRepeated method on example Monomial
  boolean testNotRepeated(Tester t) {
    return t.checkExpect(m3.notRepeated(p2M1Rest), true)
        && t.checkExpect(this.failM1.notRepeated(failPMRest1), true)
        && t.checkExpect(this.failM2.notRepeated(failPMRest2), false);
  }
  
  // checks functionality of unequalDegree method on example Monomial
  boolean testUnequalDegree(Tester t) {
    return t.checkExpect(this.failM1.unequalDegree(0), true)
        && t.checkExpect(this.failM1.unequalDegree(6), false)
        && t.checkExpect(this.failM4.unequalDegree(0), false);
  }
  
  // checks functionality of uniqueDegrees method on example ILoMonomial
  boolean testUniqueDegrees(Tester t) {
    return t.checkExpect(this.failPM.uniqueDegrees(), false)
        && t.checkExpect(this.p1M1.uniqueDegrees(), true)
        && t.checkExpect(this.p2M3.uniqueDegrees(), true);
  }
}