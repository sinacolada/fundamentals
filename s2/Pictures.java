import tester.*;

// to represent a Picture
interface IPicture {
  int getWidth();
  
  int countShapes();
  
  int comboDepth();
  
  IPicture mirror();
  
  String pictureRecipe(int depth);
}

// to represent a Shape
class Shape implements IPicture {
  String kind; 
  int size;
  
  Shape(String kind, int size) {
    this.kind = kind;
    this.size = size;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.kind ... --String
   * ... this.size ... --int
   * Methods: 
   * ... this.getWidth() ... --int
   * ... this.countShapes() ... --int
   * ... this.comboDepth() ... --int
   * ... this.mirror() ... --IPicture
   * ... this.pictureRecipe(int depth) ... --String
   * Methods for Fields:
   */
  
  // returns width of shape
  public int getWidth() {
    return this.size;
  }
  
  // returns number of shapes in this single shape (1)
  public int countShapes() {
    return 1;
  }
  
  // returns combo depth of this single shape (0)
  public int comboDepth() {
    return 0;
  }
  
  // returns the same shape when mirrored
  public IPicture mirror() {
    return new Shape(this.kind, this.size);
  }
  
  // returns shape kind for picture depth string
  public String pictureRecipe(int depth) {
    return this.kind;
  }
}

// to represent a Combo
class Combo implements IPicture {
  String name;
  IOperation operation;
  
  Combo(String name, IOperation operation) {
    this.name = name;
    this.operation = operation;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.name ... --String
   * ... this.operation ... --IOperation
   * Methods: 
   * ... this.getWidth() ... --int
   * ... this.countShapes() ... --int
   * ... this.comboDepth() ... --int
   * ... this.mirror() ... --IPicture
   * ... this.pictureRecipe(int depth) -- IPicture
   * Methods for Fields:
   * ... this.operation.findWidth() ... --int
   * ... this.operation.numShapes() ... --int
   * ... this.operation.numCombos() ... --int
   * ... this.operation.mirrorOp() ... --IOperation
   * ... this.operation.opRecipe(int depth) ... --String
   */
  
  // returns width of this combo shape
  public int getWidth() {
    return this.operation.findWidth();
  }
  
  // returns number of shapes in this combo shape
  public int countShapes() {
    return this.operation.numShapes();
  }
  
  // finds the number of operations used to create this combination shape
  public int comboDepth() {
    return this.operation.numCombos();
  }
  
  // returns the same combo reflected on the y axis for Beside
  public IPicture mirror() {
    return new Combo(this.name, this.operation.mirrorOp());
  }
  
  // returns recipe string for given depth
  public String pictureRecipe(int depth) {
    if (depth <= 0) {
      return this.name;
    }
    else {
      return this.operation.opRecipe(depth);
    }
  }
}

// to represent a Operation
interface IOperation {
  int findWidth();
  
  int numShapes();
  
  int numCombos();
  
  IOperation mirrorOp();
  
  String opRecipe(int depth);
}

// to represent Scale
// draws single picture twice as large
class Scale implements IOperation {
  IPicture picture;
  
  Scale(IPicture picture) {
    this.picture = picture;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.picture ... --IPicture
   * Methods: 
   * ... this.getWidth() ... --int
   * ... this.numShapes() ... --int
   * ... this.numCombos() ... --int
   * ... this.mirrorOp() ... --IOperation
   * ... this.opRecipe(int depth) --String
   * Methods for Fields:
   * ... this.picture.getWidth() ... --int
   * ... this.picture.countShapes() ... --int
   * ... this.picture.comboDepth() ... --int
   * ... this.picture.mirror() ... --IPicture
   * ... this.picture.pictureRecipe(int depth) ... --String
   */
  
  // returns width of this scaled picture
  public int findWidth() { 
    return this.picture.getWidth() * 2;
  }
  
  // returns number of shapes of this scale
  public int numShapes() {
    return this.picture.countShapes();
  }
  
  // returns number of operations used to create components of this combo
  public int numCombos() {
    return 1 + this.picture.comboDepth();
  }
  
  // returns the same scale when mirrored
  public IOperation mirrorOp() {
    return new Scale(this.picture.mirror());
  }
  
  // returns expanded string recipe of a scale combo
  public String opRecipe(int depth) {
    return "scale(" + this.picture.pictureRecipe(depth - 1) + ")";
  }
  
}

// to represent Beside
// draws picture1 to the left of picture2
class Beside implements IOperation {
  IPicture picture1;
  IPicture picture2;
  
  Beside(IPicture picture1, IPicture picture2) {
    this.picture1 = picture1;
    this.picture2 = picture2;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.picture1 ... --IPicture
   * ... this.picture2 ... --IPicture
   * Methods: 
   * ... this.getWidth() ... --int
   * ... this.numShapes() ... --int
   * ... this.numCombos() ... --int
   * ... this.opRecipe() ... --String
   * Methods for Fields:
   * ... this.picture1.getWidth() ... --int
   * ... this.picture2.getWidth() ... --int
   * ... this.picture1.countShapes() ... --int
   * ... this.picture2.countShapes() ... -int
   * ... this.picture1.comboDepth() ... --int
   * ... this.picture2.comboDepth() ... --int
   * ... this.picture1.mirror() ... --IPicture
   * ... this.picture2.mirror() ... --IPicture
   * ... this.picture1.pictureRecipe(int depth) ... --String
   * ... this.picture2.pictureRecipe(int depth) ... --String
   */
  
  // returns total width of these pictures beside each other
  public int findWidth() {
    return this.picture1.getWidth() + this.picture2.getWidth();
  }
  
  // returns number of pictures in this beside
  public int numShapes() {
    return this.picture1.countShapes() + this.picture2.countShapes();
  }
  
  // returns the larger number of combos in This Combo
  public int numCombos() {
    if (this.picture1.comboDepth() > this.picture2.comboDepth()) {
      return 1 + this.picture1.comboDepth();
    }
    else {
      return 1 + this.picture2.comboDepth();
    }
  }
  
  // returns a new beside with picture 1 and 2 flipped
  public IOperation mirrorOp() {
    return new Beside(this.picture2.mirror(), this.picture1.mirror());
  }
  
  // returns expanded string recipe of a beside combo
  public String opRecipe(int depth) {
    return "beside(" + this.picture1.pictureRecipe(depth - 1) + ", " 
        + this.picture2.pictureRecipe(depth - 1) + ")";
  }
}

// to represent Overlay
// draws topPicture on top of bottomPicture
class Overlay implements IOperation {
  IPicture topPicture;
  IPicture bottomPicture;
  
  Overlay(IPicture topPicture, IPicture bottomPicture) {
    this.topPicture = topPicture;
    this.bottomPicture = bottomPicture;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.topPicture ... --IPicture
   * ... this.bottomPicture ... --IPicture
   * Methods: 
   * ... this.getWidth() ... --int
   * ... this.numShapes() ... --int
   * ... this.numCombos() ... --int
   * ... this.mirrorOp() ... -IOperation
   * Methods for Fields:
   * ... this.topPicture.countShapes() ... --int
   * ... this.bottomPicture.countShapes() ... --int
   * ... this.topPicture.comboDepth() ... --int
   * ... this.bottomPicture.comboDepth() ... -int
   * ... this.topPicture.mirror() ... --IPicture
   * ... this.bottomPicture.mirror() ... --IPicture
   * ... this.topPicture.pictureRecipe() ... --String
   * ... this.bottomPicture.pictureRecipe() ... --String
   */
  
  // returns total width of these pictures overlaid 
  public int findWidth() {
    if (this.topPicture.getWidth() > this.bottomPicture.getWidth()) {
      return this.topPicture.getWidth();
    }
    else {
      return this.bottomPicture.getWidth();
    }
  }
  
  // returns total number of shapes of these pictures overlaid
  public int numShapes() {
    return this.topPicture.countShapes() + this.bottomPicture.countShapes();
  }
  
  //returns the larger number of combos in This Combo
  public int numCombos() {
    if (this.topPicture.comboDepth() > this.bottomPicture.comboDepth()) {
      return 1 + this.topPicture.comboDepth();
    }
    else {
      return 1 + this.bottomPicture.comboDepth();
    }
  }
  
  //returns the same overlay when mirrored
  public IOperation mirrorOp() {
    return new Overlay(this.topPicture.mirror(), this.bottomPicture.mirror());
  }
  
  //returns expanded string recipe of an overlay combo
  public String opRecipe(int depth) {
    return "overlay(" + this.topPicture.pictureRecipe(depth - 1) + ", " 
        + this.bottomPicture.pictureRecipe(depth - 1) + ")";
  }
}

class ExamplesPicture {
  
  ExamplesPicture() {
  }
  
  IPicture circle = new Shape("circle", 20);
  IPicture square = new Shape("square", 30);
  IOperation circleScaling = new Scale(this.circle);
  IPicture bigCircle = new Combo("big circle", this.circleScaling);
  IOperation scOverlay = new Overlay(this.square, this.bigCircle);
  IPicture squareOnCircle = new Combo("square on circle", this.scOverlay);
  IOperation dsocBeside = new Beside(this.squareOnCircle, this.squareOnCircle);
  IPicture doubledSquareOnCircle = new Combo("doubled square on circle", this.dsocBeside);
  
  IOperation squareOnCircleScaling = new Scale(this.squareOnCircle);
  IOperation socDoubleSocOverlay = new Overlay(this.squareOnCircle, this.doubledSquareOnCircle);
  IOperation tsocBeside = new Beside(this.squareOnCircle, this.doubledSquareOnCircle);
  IOperation tsocBesideMirror = new Beside(this.doubledSquareOnCircle, this.squareOnCircle);
  
  IPicture bigSquareOnCircle = new Combo("big square on circle", this.squareOnCircleScaling);
  IPicture socOnDoubleSoc = new Combo("square on circle on double square on circle", 
      this.socDoubleSocOverlay);
  IPicture tripledSquareOnCircle = new Combo("tripled square on circle", this.tsocBeside);
  IPicture tripledSquareOnCircleMirror = new Combo("tripled square on circle", 
      this.tsocBesideMirror);
  IOperation squareBesideCircle = new Beside(this.square, this.circle);
  IPicture squareCircle = new Combo("square beside square on circle", this.squareBesideCircle);
  IOperation squareBesideCircleMirror = new Beside(this.circle, this.square);
  IPicture squareCircleMirror = new Combo("square beside square on circle", 
      this.squareBesideCircleMirror);
  IOperation layerOp = new Overlay(this.squareCircle, this.squareCircleMirror);
  IPicture squareCircleLayered = new Combo("square circle layered", this.layerOp);
  IOperation layerOpMirror = new Overlay(this.squareCircleMirror, this.squareCircle);
  IPicture squareCircleLayeredMirror = new Combo("square circle layered", this.layerOpMirror);
  
  // tests functionality of getWidth method on example shapes
  boolean testWidth(Tester t) {
    return t.checkExpect(this.circle.getWidth(), 20)
        && t.checkExpect(this.square.getWidth(), 30)
        && t.checkExpect(this.bigCircle.getWidth(), 40)
        && t.checkExpect(this.squareOnCircle.getWidth(), 40)
        && t.checkExpect(this.doubledSquareOnCircle.getWidth(), 80)
        && t.checkExpect(this.bigSquareOnCircle.getWidth(), 80)
        && t.checkExpect(this.socOnDoubleSoc.getWidth(), 80)
        && t.checkExpect(this.tripledSquareOnCircle.getWidth(), 120);
  }
  
  // tests functionality of findWidth method on example shapes
  boolean testFindWidth(Tester t) {
    return t.checkExpect(this.circleScaling.findWidth(), 40)
        && t.checkExpect(this.scOverlay.findWidth(), 40)
        && t.checkExpect(this.dsocBeside.findWidth(), 80);
  }
  
  // tests functionality of countShapes method on example shapes
  boolean testNumberShapes(Tester t) {
    return t.checkExpect(this.circle.countShapes(), 1)
        && t.checkExpect(this.square.countShapes(), 1)
        && t.checkExpect(this.bigCircle.countShapes(), 1)
        && t.checkExpect(this.squareOnCircle.countShapes(), 2)
        && t.checkExpect(this.doubledSquareOnCircle.countShapes(), 4)
        && t.checkExpect(this.bigSquareOnCircle.countShapes(), 2)
        && t.checkExpect(this.socOnDoubleSoc.countShapes(), 6)
        && t.checkExpect(this.tripledSquareOnCircle.countShapes(), 6);
  }
  
  // tests functionality of numShapes method on example shapes
  boolean testNuShapes(Tester t) {
    return t.checkExpect(this.circleScaling.numShapes(), 1)
        && t.checkExpect(this.scOverlay.numShapes(), 2)
        && t.checkExpect(this.dsocBeside.numShapes(), 4);
  }
  
  
  // tests functionality of comboDepth method on example shapes
  boolean testNumCombos(Tester t) {
    return t.checkExpect(this.circle.comboDepth(), 0)
        && t.checkExpect(this.square.comboDepth(), 0)
        && t.checkExpect(this.bigCircle.comboDepth(), 1)
        && t.checkExpect(this.squareOnCircle.comboDepth(), 2)
        && t.checkExpect(this.doubledSquareOnCircle.comboDepth(), 3)
        && t.checkExpect(this.bigSquareOnCircle.comboDepth(), 3)
        && t.checkExpect(this.socOnDoubleSoc.comboDepth(), 4)
        && t.checkExpect(this.tripledSquareOnCircle.comboDepth(), 4);
  }
  
  // tests functionality of numCombos method on example shapes
  boolean testnumCombos(Tester t) {
    return t.checkExpect(this.circleScaling.numCombos(), 1)
        && t.checkExpect(this.scOverlay.numCombos(), 2)
        && t.checkExpect(this.dsocBeside.numCombos(), 3);
  }
  
  // tests for functionality of mirror method on example shapes
  boolean testMirror(Tester t) {
    return t.checkExpect(this.circle.mirror(), this.circle)
        && t.checkExpect(this.square.mirror(), this.square)
        && t.checkExpect(this.bigCircle.mirror(), this.bigCircle)
        && t.checkExpect(this.squareOnCircle.mirror(), this.squareOnCircle)
        && t.checkExpect(this.doubledSquareOnCircle.mirror(), this.doubledSquareOnCircle)
        && t.checkExpect(this.bigSquareOnCircle.mirror(), this.bigSquareOnCircle)
        && t.checkExpect(this.socOnDoubleSoc.mirror(), this.socOnDoubleSoc)
        && t.checkExpect(this.tripledSquareOnCircle.mirror(), this.tripledSquareOnCircleMirror)
        && t.checkExpect(this.squareCircle.mirror(), this.squareCircleMirror)
        && t.checkExpect(this.squareCircleLayered.mirror(), this.squareCircleLayeredMirror);
  }
  
  // tests for functionality of mirrorOp method
  boolean testMirrorOp(Tester t) {
    return t.checkExpect(this.squareBesideCircle.mirrorOp(), this.squareBesideCircleMirror)
        && t.checkExpect(this.tsocBeside.mirrorOp(), this.tsocBesideMirror)
        && t.checkExpect(this.layerOp.mirrorOp(), this.layerOpMirror);
  }
  
  // tests for functionality of pictureRecipe method on example shaper
  boolean testRecipe(Tester t) {
    return t.checkExpect(this.circle.pictureRecipe(2), "circle")
        && t.checkExpect(this.square.pictureRecipe(-5), "square")
        && t.checkExpect(this.bigCircle.pictureRecipe(-2), "big circle")
        && t.checkExpect(this.bigCircle.pictureRecipe(5), "scale(circle)")
        && t.checkExpect(this.squareOnCircle.pictureRecipe(2), "overlay(square, scale(circle))")
        && t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(2),
            "beside(overlay(square, big circle), overlay(square, big circle))")
        && t.checkExpect(this.doubledSquareOnCircle.pictureRecipe(100), 
            "beside(overlay(square, scale(circle)), overlay(square, scale(circle)))")
        && t.checkExpect(this.bigSquareOnCircle.pictureRecipe(1), "scale(square on circle)")
        && t.checkExpect(this.socOnDoubleSoc.pictureRecipe(3),
            "overlay(overlay(square, scale(circle)), "
            + "beside(overlay(square, big circle), overlay(square, big circle)))");
  }
  
//tests for functionality of opRecipe method
 boolean testOpRecipe(Tester t) {
   return t.checkExpect(this.circleScaling.opRecipe(5), "scale(circle)")
       && t.checkExpect(this.scOverlay.opRecipe(2), "overlay(square, scale(circle))")
       && t.checkExpect(this.dsocBeside.opRecipe(2), "beside(overlay(square, big circle), overlay(square, big circle))");
 }
  
  
}
