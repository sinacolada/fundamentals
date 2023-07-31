import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import javalib.worldcanvas.*;
import java.awt.Color;

interface ITree {
  
  // renders ITree to a picture
  WorldImage draw();
  
  // returns whether any twig is pointing downwards rather than upwards
  boolean isDrooping();
  
  // rotates ITree to the left leftTheta degrees
  ITree rotateLeft(double leftTheta);
  
  //rotates ITree to the right rightTheta degrees
  ITree rotateRight(double rightTheta);
  
  // combines this tree with given tree on the right
  ITree combine(int leftLength, int rightLength, double leftTheta,
      double rightTheta, ITree otherTree);
  
  // gets rightMost value of tree
  // accumulates rightmost point of the image
  double getRightMost(double current, double rightMost);
  
  // gets leftmost value of tree
  // accumulates leftMost point of image
  double getLeftMost(double current, double leftMost);
  
  // returns the width of the tree 
  double getWidth();
}
 
class Leaf implements ITree {
  int size; // represents the radius of the leaf
  Color color; // the color to draw it
  
  Leaf(int size, Color color) {
    this.size = size;
    this.color = color;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.size ... --int
   * ... this.color ... --Color
   * Methods:
   * ... this.draw() ... --WorldImage
   * ... this.isDrooping() ... --boolean
   * ... this.combine() ... --ITree
   * ... this.rotateLeft(leftTheta) ... --ITree
   * ... this.rotateRight(rightTheta) ... --ITree
   * ... this.getWidth() ... --double
   * ... this.getRightMost(double current, double rightMost) ... --double
   * ... this.getLeftMost(double current, double leftMost) ... --double
   * Methods for Fields:
   */
  
  public WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, this.color);
  }
  
  public boolean isDrooping() {
    return false;
  }
  
  public ITree rotateLeft(double leftTheta) {
    return this;
  }
  
  public ITree rotateRight(double rightTheta) {
    return this;
  }
  
  public ITree combine(int leftLength, int rightLength, double leftTheta,
      double rightTheta, ITree otherTree) {
    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
        this.rotateLeft(leftTheta), otherTree.rotateRight(rightTheta));
  }
  
  public double getRightMost(double current, double rightMost) {
    return this.size + current;
  }
  
  public double getLeftMost(double current, double leftMost) {
    return -1 * this.size + current;
  }
  
  /*
  public double getWidth() {
    return this.getRightMost(0.0, 0.0) - this.getLeftMost(0.0, 0.0);
  }
  */
  
  public double getWidth() {
    return this.draw().getWidth();
  }
}
 
class Stem implements ITree {
  // How long this stick is
  int length;
  // The angle (in degrees) of this stem, relative to the +x axis
  double theta;
  // The rest of the tree
  ITree tree;
  
  Stem(int length, double theta, ITree tree) {
    this.length = length;
    this.theta = theta;
    this.tree = tree;
  } 
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.length ... --int
   * ... this.theta ... --double
   * ... this.tree ... --ITree
   * Methods:
   * ... this.draw() ... --WorldImage
   * ... this.isDrooping() ... --boolean
   * ... this.combine() ... --ITree
   * ... this.rotateLeft(leftTheta) ... --ITree
   * ... this.rotateRight(rightTheta) ... --ITree
   * ... this.getWidth() ... --double
   * ... this.getRightMost(double current, double rightMost) ... --double
   * ... this.getLeftMost(double current, double leftMost) ... --double
   * Methods for Fields:
   */
  
  public WorldImage draw() {
    WorldImage bT = this.tree.draw();
    double rad = Math.toRadians(this.theta);
    double unitX = Math.cos(rad);
    double unitY = Math.sin(rad);
    double x = this.length * unitX;
    double y = -1 * this.length * unitY;
    int xPos = (int)x;
    int yPos = (int)y;
    Posn pos = new Posn(xPos, yPos);
    WorldImage line = new LineImage(pos, Color.BLACK).movePinhole(x * 0.5, y * 0.5);
    WorldImage lineOverlay = new OverlayImage(bT, line);
    WorldImage finalStem = lineOverlay.movePinhole(-1 * x, -1 * y);
    return finalStem;
  }
  
  public boolean isDrooping() {
    return this.tree.isDrooping() || this.theta % 360 > 180.0;
  }
  
  public ITree rotateLeft(double leftTheta) {
    return new Stem(this.length, this.theta + leftTheta - 90, this.tree.rotateLeft(leftTheta));
  }
  
  public ITree rotateRight(double rightTheta) {
    return new Stem(this.length, this.theta + rightTheta - 90, this.tree.rotateLeft(rightTheta));
  }
  
  public ITree combine(int leftLength, int rightLength, double leftTheta,
      double rightTheta, ITree otherTree) {
    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
        this.rotateLeft(leftTheta), otherTree.rotateRight(rightTheta));
  }
  
  public double getRightMost(double current, double rightMost) {
    if (this.theta % 360 > 270.0 || this.theta % 360 < 90.0) {
      if (this.length * Math.cos(Math.toRadians(this.theta)) + current > rightMost) {
        rightMost = this.length * Math.cos(Math.toRadians(this.theta)) + current;
      }
      return this.tree.getRightMost(this.length * Math.cos(Math.toRadians(this.theta))
          + current, rightMost);
    }
    return this.tree.getRightMost(current, rightMost);
  }
  
  public double getLeftMost(double current, double leftMost) {
    if (this.theta % 360 > 90.0 && this.theta % 360 < 270.0) {
      if (-1 * this.length * Math.cos(Math.toRadians(this.theta)) + current > leftMost) {
        leftMost = -1 * this.length * Math.cos(Math.toRadians(this.theta)) + current;
      }
      return this.tree.getLeftMost(-1 * this.length * Math.cos(Math.toRadians(this.theta))
          + current, leftMost);
    }
    return this.tree.getLeftMost(current, leftMost);
  }
  
  /*
  public double getWidth() {
    return this.getRightMost(0.0, 0.0) - this.getLeftMost(0.0, 0.0);
  }
  */
  
  public double getWidth() {
    return this.draw().getWidth();
  }
}
 
class Branch implements ITree {
  // How long the left and right branches are
  int leftLength;
  int rightLength;
  // The angle (in degrees) of the two branches, relative to the +x axis,
  double leftTheta;
  double rightTheta;
  // The remaining parts of the tree
  ITree left;
  ITree right;
  
  Branch(int leftLength, int rightLength, 
      double leftTheta, double rightTheta, ITree left, ITree right) {
    this.leftLength = leftLength;
    this.rightLength = rightLength;
    this.leftTheta = leftTheta;
    this.rightTheta = rightTheta;
    this.left = left;
    this.right = right;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.leftLength ... --int
   * ... this.rightLength ... --int
   * ... this.leftTheta ... --double
   * ... this.rightTheta ... --double
   * ... this.left ... --ITree
   * ... this.right ... --ITree
   * Methods:
   * ... this.draw() ... --WorldImage
   * ... this.isDrooping() ... --boolean
   * ... this.combine() ... --ITree
   * ... this.rotateLeft(leftTheta) ... --ITree
   * ... this.rotateRight(rightTheta) ... --ITree
   * ... this.getWidth() ... --double
   * ... this.getRightMost(double current, double rightMost) ... --double
   * ... this.getLeftMost(double current, double leftMost) ... --double
   * Methods for Fields:
   */
  
  public WorldImage draw() {
    return new OverlayImage(
        new Stem(this.leftLength, this.leftTheta, this.left).draw(),
        new Stem(this.rightLength, this.rightTheta, this.right).draw());
  }
  
  public boolean isDrooping() {
    return this.leftTheta % 360 > 180 || this.rightTheta % 360 > 180
        || this.left.isDrooping() || this.right.isDrooping();
  }
  
  public ITree rotateLeft(double leftTheta) {
    return new Branch(this.leftLength, this.rightLength, 
        this.leftTheta + leftTheta - 90, this.rightTheta + leftTheta - 90, 
        this.left.rotateLeft(leftTheta), this.right.rotateLeft(leftTheta));
  }
  
  public ITree rotateRight(double rightTheta) {
    return new Branch(this.leftLength, this.rightLength, 
        this.leftTheta + rightTheta - 90, this.rightTheta + rightTheta - 90, 
        this.left.rotateRight(rightTheta), this.right.rotateRight(rightTheta));
  }
  
  public ITree combine(int leftLength, int rightLength, double leftTheta,
      double rightTheta, ITree otherTree) {
    return new Branch(leftLength, rightLength, leftTheta, rightTheta,
        this.rotateLeft(leftTheta), otherTree.rotateRight(rightTheta));
  }
  
  public double getRightMost(double current, double rightMost) {
    if (this.rightTheta % 360 > 90.0 && this.rightTheta % 360 < 270.0) {
      if (-1 * this.rightLength * Math.cos(Math.toRadians(this.rightTheta)) + current > rightMost) {
        rightMost = -1 * this.rightLength * Math.cos(Math.toRadians(this.rightTheta)) + current;
      }
      return this.left.getLeftMost(-1 * this.rightLength * Math.cos(Math.toRadians(this.rightTheta))
          + current, rightMost);
    }
    return Math.min(this.left.getLeftMost(current, rightMost),
        this.right.getLeftMost(current, rightMost));
  }
  
  public double getLeftMost(double current, double leftMost) {
    if (this.leftTheta % 360 > 90.0 && this.leftTheta % 360 < 270.0) {
      if (-1 * this.leftLength * Math.cos(Math.toRadians(this.leftTheta)) + current > leftMost) {
        leftMost = -1 * this.leftLength * Math.cos(Math.toRadians(this.leftTheta)) + current;
      }
      return this.left.getLeftMost(-1 * this.leftLength * Math.cos(Math.toRadians(this.leftTheta))
          + current, leftMost);
    }
    return Math.min(this.left.getLeftMost(current, leftMost),
        this.right.getLeftMost(current, leftMost));
  }
  
  /*
  public double getWidth() {
    return this.getRightMost(0.0, 0.0) - this.getLeftMost(0.0, 0.0);
  }
  */
  
  public double getWidth() {
    return this.draw().getWidth();
  }
}

class ExamplesTrees {
  ExamplesTrees() {
  }
  
  ITree trivialLeaf = new Leaf(0, Color.BLACK);
  ITree trivialStem = new Stem(0, 0.0, this.trivialLeaf);
  ITree trivialBranch = new Branch(0, 0, 0.0, 0.0, this.trivialLeaf, this.trivialLeaf);
  
  ITree t0leaf = new Leaf(15, Color.YELLOW);
  ITree tree0 = new Stem(100, 45.0, this.t0leaf);
  
  ITree t1Leaf1 = new Leaf(20, Color.BLUE);
  ITree t1Leaf2 = new Leaf(30, Color.RED);
  ITree t1Branch1 = new Branch(50, 50, 135.0, 45.0, this.t1Leaf1, this.t1Leaf2);
  ITree tree1 = new Stem(100, 90.0, this.t1Branch1);
  
  ITree t2Leaf1 = new Leaf(25, Color.BLACK);
  ITree t2Leaf2 = new Leaf(25, Color.BLACK);
  ITree t2Leaf3 = new Leaf(25, Color.BLACK);
  ITree t2Stem1 = new Stem(50, 90.0, this.t2Leaf2);
  ITree t2Stem2 = new Stem(50, 90.0, this.t2Leaf3);
  ITree t2Branch1 = new Branch(50, 50, 135.0, 45.0, this.t2Stem1, this.t2Stem2);
  ITree t2Branch2 = new Branch(50, 50, 135.0, 45.0, this.t2Leaf1, this.t2Branch1);
  ITree tree2 = new Stem(50, 90.0, this.t2Branch2);
  
  ITree t3Leaf1 = new Leaf(10, Color.GREEN);
  ITree t3Leaf2 = new Leaf(15, Color.YELLOW);
  ITree t3Branch1 = new Branch(50, 50, 270.0, 180.0, this.t3Leaf1, this.t3Leaf2);
  ITree tree3 = new Stem(100, 225.0, this.t3Branch1);
  
  ITree TREE1 = new Branch(30, 30, 135, 40, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE));
  ITree TREE2 = new Branch(30, 30, 115, 65, new Leaf(15, Color.GREEN), new Leaf(8, Color.ORANGE));
  ITree wrongCombine = new Branch(40, 50, 150, 30, TREE1, TREE2);
  ITree combineTree = TREE1.combine(40, 50, 150, 30, this.TREE2);
  ITree combineCheck1 = new Branch(40, 50, 150, 30,
      new Branch(30, 30, 195.0, 100.0, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE)),
      new Branch(30, 30, 55.0, 5.0, new Leaf(15, Color.GREEN), new Leaf(8, Color.ORANGE)));
  ITree combineCheck2 = new Branch(50, 40, 30, 150,
      new Branch(30, 30, 55.0, 5.0, new Leaf(15, Color.GREEN), new Leaf(8, Color.ORANGE)),
      new Branch(30, 30, 195.0, 100.0, new Leaf(10, Color.RED), new Leaf(15, Color.BLUE)));
  ITree combineCheck0 = new Branch(50, 50, 60, 120, new Stem(100, 15.0, this.t0leaf),
      new Stem(100, 75.0, this.t0leaf));
  ITree combineCheckComplex = new Branch(50, 50, 60, 120, 
      new Stem(100, 60.0, new Branch(50, 50, 105.0, 15.0, this.t1Leaf1, this.t1Leaf2)),
      new Stem(100, 75.0, this.t0leaf));
  
  WorldImage tree0Image = this.tree0.draw();
  WorldImage tree1Image = this.tree1.draw();
  WorldImage tree2Image = this.tree2.draw();
  WorldImage tree3Image = this.tree3.draw();
  WorldImage combineTreeImage = this.combineTree.draw();
  WorldImage tree1ImageExpansion = new OverlayImage(
      new OverlayImage(
          new CircleImage(10, OutlineMode.SOLID, Color.RED), 
          new LineImage(
              new Posn((int)(30 * Math.cos(Math.toRadians(135))),
                  (int)(-30 * Math.sin(Math.toRadians(135)))),
              Color.BLACK).movePinhole(0.5 * 30 * Math.cos(Math.toRadians(135)),
                  0.5 * -30 * Math.sin(Math.toRadians(135))))
      .movePinhole(-30 * Math.cos(Math.toRadians(135)),
          30 * Math.sin(Math.toRadians(135))),
      new OverlayImage(
          new CircleImage(15, OutlineMode.SOLID, Color.BLUE), 
          new LineImage(
              new Posn((int)(30 * Math.cos(Math.toRadians(40))),
                  (int)(-30 * Math.sin(Math.toRadians(40)))),
              Color.BLACK).movePinhole(0.5 * 30 * Math.cos(Math.toRadians(40)),
                  0.5 * -30 * Math.sin(Math.toRadians(40))))
      .movePinhole(-30 * Math.cos(Math.toRadians(40)),
          30 * Math.sin(Math.toRadians(40))));
  
  //boolean testImages(Tester t) {
  //  return t.checkExpect(new RectangleImage(30, 20, OutlineMode.SOLID, Color.GRAY),
  //                       new RectangleImage(30, 20, OutlineMode.SOLID, Color.GRAY));
  //}
  
  //boolean testFailure(Tester t) {
  //  return t.checkExpect(
  //      new ScaleImageXY(new RectangleImage(60, 40, OutlineMode.SOLID, Color.GRAY), 0.5, 0.25),
  //      new RectangleImage(30, 15, OutlineMode.SOLID, Color.GRAY));
  //}
  
  // draws example tree
  boolean testDrawTree(Tester t) {
    WorldCanvas c = new WorldCanvas(500, 500);
    WorldScene s = new WorldScene(500, 500);
    return c.drawScene(s.placeImageXY(this.combineTreeImage, 250, 250))
        && c.show();
  } 
  
  // tests functionality of draw method on example trees
  boolean testDraw(Tester t) {
    return t.checkExpect(this.TREE1.draw(), this.tree1ImageExpansion);
  }
  
  // tests functionality of isDrooping method on example trees
  boolean testIsDrooping(Tester t) {
    return t.checkExpect(this.TREE1.isDrooping(), false)
        && t.checkExpect(this.TREE2.isDrooping(), false)
        && t.checkExpect(this.wrongCombine.isDrooping(), false)
        && t.checkExpect(this.trivialBranch.isDrooping(), false)
        && t.checkExpect(this.trivialStem.isDrooping(), false)
        && t.checkExpect(this.trivialLeaf.isDrooping(), false)
        && t.checkExpect(this.tree0.isDrooping(), false)
        && t.checkExpect(this.tree1.isDrooping(), false)
        && t.checkExpect(this.tree2.isDrooping(), false)
        && t.checkExpect(this.tree3.isDrooping(), true);
  }
  
  // tests functionality of combine method on example trees
  boolean testCombine(Tester t) {
    return t.checkExpect(this.TREE1.combine(40, 50, 150, 30, this.TREE2), this.combineCheck1)
        && t.checkExpect(this.TREE2.combine(50, 40, 30, 150, this.TREE1), this.combineCheck2)
        && t.checkExpect(this.tree0.combine(50, 50, 60, 120, this.tree0), this.combineCheck0)
        && t.checkExpect(this.tree1.combine(50, 50, 60, 120, this.tree0), 
            this.combineCheckComplex);
  }
  
  //tests functionality of getWidth method on example trees
  /*
  boolean testGetWidth(Tester t) {
    return t.checkInexact(this.TREE1.getWidth(), 67.264961, 0.00001)
        && t.checkInexact(this.TREE2.getWidth(), 62.867781, 0.00001)
        && t.checkInexact(this.trivialBranch.getWidth(), 0.0, 0.00001)
        && t.checkInexact(this.trivialStem.getWidth(), 0.0, 0.00001)
        && t.checkInexact(this.trivialLeaf.getWidth(), 0.0, 0.00001)
        && t.checkInexact(this.tree0.getWidth(), 85.710678, 0.00001)
        && t.checkInexact(this.tree2.getWidth(), 152.066017, 0.00001);
  }
  */
}