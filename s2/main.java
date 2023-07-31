import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

// World for our game
class NBullets extends World {
  int numBullets;
  int shipsDestroyed;
  ILoShip shipList;
  ILoBullet bulletList;
  int freq;
  int rand;

  // constructor with all needed fields of this world
  NBullets(int numBullets, int shipsDestroyed, ILoShip shipList, ILoBullet bulletList,
      int freq, int rand) {
    this.numBullets = numBullets;
    this.shipsDestroyed = shipsDestroyed;
    this.shipList = shipList;
    this.bulletList = bulletList;
    this.freq = freq;
    this.rand = rand;
  }

  // constructor w/ number of bullets player has to shoot and a given random number seed
  NBullets(int numBullets, int rand) {
    this(numBullets, 0, new MtLoShip(), new MtLoBullet(), 27, rand);
  }

  // constructor w/ number of bullets player has to shoot
  NBullets(int numBullets) {
    this(numBullets, 0, new MtLoShip(), new MtLoBullet(), 27, new Random().nextInt());
  }

  /* TEMPLATE:
   * Fields: 
   * ... this.numBullets ...                                         -- int
   * ... this.shipdDestroyed ...                                     -- int
   * ... this.shipList ...                                           -- ILoShip
   * ... this.bulletList ...                                         -- ILoBullet
   * ... this.freq ...                                               -- int
   * ... this.rand ...                                               -- int
   * Methods:
   * ... this.text(WorldScene scene) ...                             -- WorldScene
   * ... this.smakeScene() ...                                       -- WorldScene
   * ... this.worldEnds() ...                                        -- WorldEnd
   * ... this.onKeyEvent(String key) ...                             -- World
   * ... this.updateShips() ...                                      -- NBullets
   * ... this.updateBullets() ...                                    -- NBullets
   * ... this.generateNewShips() ...                                 -- NBullets
   * ... this.generateNewShips(int seed) ...                         -- NBullets
   * ... this.collisionHandler() ...                                 -- NBullets
   * ... this.onTick() ...                                           -- World
   * ... this.onTickTestable() ...                                   -- World
   * Methods for Fields:
   * ... shipList. ...
   * ...
   * ... this.bulletList. ...
   * ...
   */

  // text image to place at bottom
  public WorldScene text(WorldScene scene) {
    String text = "bullets left: " + this.numBullets + "; ships destroyed: "
        + this.shipsDestroyed;
    WorldImage textImage = new TextImage(text, 13, Color.BLACK);
    return scene.placeImageXY(textImage, 100, 285);
  }

  // draws the WorldScene
  public WorldScene makeScene() {
    return this.text(
        this.shipList.placeAllShips(
            this.bulletList.placeAllBullets(
                this.getEmptyScene())));
  }

  // world ends when no more bullets to fire and no bullets on screen
  public WorldEnd worldEnds() {
    if (this.numBullets == 0 && this.bulletList.noBulletsOnScreen()) {
      return new WorldEnd(true, this.makeScene());
    }
    return new WorldEnd(false, this.makeScene());
  }

  // on space fires bullet from bottom center of screen
  public World onKeyEvent(String key) {
    if (key.equals(" ") && this.numBullets > 0) {
      return new NBullets(
          this.numBullets - 1,
          this.shipsDestroyed, this.shipList,
          this.bulletList.addFiredBullet(), this.freq, this.rand);
    }
    return this;
  }

  // updates and spawns ships
  public NBullets updateShips() {
    ILoShip updatedShipList = this.shipList.removeOffScreenShips().moveAllShips();
    return new NBullets(this.numBullets, this.shipsDestroyed,
        updatedShipList, this.bulletList, this.freq, this.rand);
  }

  // updates bullets
  public NBullets updateBullets() {
    ILoBullet updatedBulletList = this.bulletList.removeOffScreenBullets().moveAllBullets();
    return new NBullets(this.numBullets, this.shipsDestroyed, 
        this.shipList, updatedBulletList, this.freq, this.rand);
  }

  // generates new ships
  public NBullets generateNewShips() {
    if (this.freq % 28 == 0) {
      int numShips = new Random().nextInt(3) + 1;
      ILoShip newShipList = this.shipList.generateShips(numShips);
      return new NBullets(this.numBullets, this.shipsDestroyed,
          newShipList, this.bulletList, 27, this.rand);
    }
    return new NBullets(this.numBullets, this.shipsDestroyed,
        this.shipList, this.bulletList, this.freq - 1, this.rand);
  }

  // TESTABLE VERSION: generates new ships, takes a seed to fix the game
  public NBullets generateNewShips(int seed) {
    if (this.freq % 28 == 0) {
      int numShips = new Random(seed).nextInt(3) + 1;
      ILoShip newShipList = this.shipList.generateShips(numShips, seed);
      return new NBullets(this.numBullets, this.shipsDestroyed,
          newShipList, this.bulletList, 27, this.rand);
    }
    return new NBullets(this.numBullets, this.shipsDestroyed,
        this.shipList, this.bulletList, this.freq - 1, this.rand);
  }

  // handles collisions between bullets and ships
  public NBullets collisionHandler() {
    ILoShip collisionShipList = this.shipList.updateCollisions(this.bulletList);
    ILoBullet collisionBulletList = this.bulletList.updateCollisions(this.shipList);
    int updatedShipsDestroyed = this.shipsDestroyed
        + (this.shipList.length() - collisionShipList.length());
    return new NBullets(this.numBullets, updatedShipsDestroyed,
        collisionShipList, collisionBulletList, this.freq, this.rand);
  }

  // on tick, updates world
  public World onTick() {
    return this
        .collisionHandler()
        .updateShips()
        .updateBullets()
        .generateNewShips();
  }

  // TESTABLE VERSION: on tick, updates world, passes an int to generateNewShips to fix the game
  public World onTickTestable() {
    return this
        .collisionHandler()
        .updateShips()
        .updateBullets()
        .generateNewShips(this.rand);
  }

}

// represents a ship in the game
class Ship {
  int xPos;
  int yPos;
  int xVel;

  Ship(int xPos, int yPos, int xVel) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.xVel = xVel;
  }

  /* TEMPLATE:
   * Fields: 
   * ... this.xPos ...                                               -- int
   * ... this.yPos ...                                               -- int
   * ... this.xVel ...                                               -- int
   * Methods:
   * ... this.getXPos() ...                                          -- int
   * ... this.getYPos() ...                                          -- int
   * ... this.draw() ...                                             -- WorldImage
   * ... this.place(WorldScene scene) ...                            -- WorldScene
   * ... this.moveShip() ...                                         -- Ship
   * ... this.isShipOffScreen() ...                                  -- boolean
   * ... this.isTouchingBullets() ...                                -- boolean
   * Methods for Fields:
   * ... N/A ...
   */

  // retrieves the xPos
  int getXPos() {
    return this.xPos;
  }

  // retrieves the xPos
  int getYPos() {
    return this.yPos;
  }

  // draws ship
  WorldImage draw() {
    return new CircleImage(10, OutlineMode.SOLID, Color.CYAN);
  }

  // places ship on scene
  WorldScene place(WorldScene scene) {
    return scene.placeImageXY(this.draw(), this.xPos, this.yPos);
  }

  // moves ship one tick
  Ship moveShip() {
    return new Ship(this.xPos + this.xVel, this.yPos, this.xVel);
  }

  // returns whether the ship is off-screen
  boolean isShipOffScreen() {
    int expandedOrigin = -10;
    int expandedWidth = 510;
    int expandedHeight = 310;
    return this.xPos < expandedOrigin || expandedWidth < this.xPos
        || this.yPos < expandedOrigin || expandedHeight < this.yPos;
  }

  // returns whether the ship is touching one of the bullets
  boolean isTouching(ILoBullet bulletList) {
    return bulletList.isTouching(this);
  }

  // is this ship within 10 units of the given bullet's x, y coordinates and size?
  boolean withinRange(int x, int y, int bulletSize) {
    return Math.hypot(this.xPos - x, this.yPos - y) <= (10 + bulletSize);
  }
}

// represents a bullet in the game
class Bullet {
  int xPos;
  int yPos;
  double angle;
  int xVel;
  int yVel;
  int size;
  int explosions;

  // to construct a bullet
  Bullet(int xPos, int yPos, double angle, int size, int explosions) {
    this.xPos = xPos;
    this.yPos = yPos;
    this.angle = angle;
    this.xVel = (int)(Math.cos(Math.toRadians(angle)) * 8);
    this.yVel = -1 * (int)(Math.sin(Math.toRadians(angle)) * 8);
    this.size = size;
    this.explosions = explosions;
  }

  // to construct a bullet without velocities or size data
  Bullet(int xPos, int yPos, double angle, int explosions) {
    this(xPos, yPos, angle, Math.min(2 * explosions, 10), explosions);
  }

  /* TEMPLATE:
   * Fields: 
   * ... this.xPos ...                                               -- int
   * ... this.yPos ...                                               -- int
   * ... this.angle ...                                              -- double
   * ... this.xVel ...                                               -- int
   * ... this.yVel ...                                               -- int
   * ... this.size ...                                               -- int
   * ... this.explosions ...                                         -- int
   * Methods:
   * ... this.getXPos() ...                                          -- int
   * ... this.getYPos() ...                                          -- int
   * ... this.draw() ...                                             -- WorldImage
   * ... this.place(WorldScene scene) ...                            -- WorldScene
   * ... this.moveBullet() ...                                       -- Bullet
   * ... this.isBulletOffScreen() ...                                -- boolean
   * Methods for Fields:
   * ... N/A ...
   */

  // retrieves the xPos
  int getXPos() {
    return this.xPos;
  }

  // retrieves the xPos
  int getYPos() {
    return this.yPos;
  }

  // retrieves the explosions
  int getExplosions() {
    return this.explosions;
  }

  // draws bullet
  WorldImage draw() {
    return new CircleImage(this.size, OutlineMode.SOLID, Color.PINK);
  }

  // places bullet on scene
  WorldScene place(WorldScene scene) {
    return scene.placeImageXY(this.draw(), this.xPos, this.yPos);
  }

  // moves bullet one tick
  Bullet moveBullet() {
    return new Bullet(this.xPos + this.xVel, this.yPos + this.yVel, this.angle,
        this.size, this.explosions);
  }

  // returns whether the bullet is off-screen
  boolean isBulletOffScreen() {
    int expandedOrigin = 0 - this.size;
    int expandedWidth = 500 + this.size;
    int expandedHeight = 300 + this.size;
    return this.xPos < expandedOrigin || expandedWidth < this.xPos
        || this.yPos < expandedOrigin || expandedHeight < this.yPos;
  }

  // returns whether the bullet is touching one of the ships
  boolean isTouching(ILoShip shipList) {
    return shipList.isTouching(this);
  }

  // is this bullet within 10 units of the given ship's x, y coordinates?
  boolean withinRange(int x, int y) {
    return Math.hypot(this.xPos - x, this.yPos - y) <= (10 + this.size);
  }
}

// list of Ships
interface ILoShip {

  // return length of this ship list
  int length();

  // moves all ships 
  ILoShip moveAllShips();

  // removes off-screen ships
  ILoShip removeOffScreenShips();

  // places all ships on given WorldScene
  WorldScene placeAllShips(WorldScene scene);

  // generates numShip new ships
  ILoShip generateShips(int numShips);

  // TESTABLE VERSION: generates numShip new ships
  ILoShip generateShips(int numShips, int seed);

  // deletes ships hit by bullets
  ILoShip updateCollisions(ILoBullet bulletList);

  // is any ship in this list touching the given bullet?
  boolean isTouching(Bullet bullet);
}

//empty list of Ships
class MtLoShip implements ILoShip {

  /* TEMPLATE:
   * Fields: 
   * ... N/A ...
   * Methods:
   * ... this.length() ...                                           -- int
   * ... this.moveAllShips() ...                                     -- ILoShip
   * ... this.removeOffScreenShips() ...                             -- ILoShip
   * ... this.placeAllShips(WorldScene scene) ...                    -- WorldScene
   * ... this.generateShips(int numShips) ...                        -- ILoShip
   * ... this.generateShips(int numShips, int seed) ...              -- ILoShip
   * ... this.updateCollisions(ILoBullet bulletList) ...             -- ILoShip
   * ... this.isTouching(Bullet bullet) ...                          -- boolean
   * Methods for Fields:
   * ... N/A ...
   */

  // return length of this ship list
  public int length() {
    return 0;
  }

  // moves all ships 
  public ILoShip moveAllShips() {
    return this;
  }

  // removes off-screen ships
  public ILoShip removeOffScreenShips() {
    return this;
  }

  // places all ships on given WorldScene
  public WorldScene placeAllShips(WorldScene scene) {
    return scene;
  }

  // generates numShip new ships
  public ILoShip generateShips(int numShips) {
    // generate which side the ship will spawn
    // 0 -> left side, 1 -> right side
    int sideValue = new Random().nextInt(2);
    // generate random yPos for ship to spawn
    // that is not in the top or bottom 1/7 of the screen
    int illegalZoneSize = (int)(300 / 7);
    int randYPos = new Random().nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;
    // termination
    if (numShips == 0) {
      return this;
    }
    // left side
    if (sideValue == 0) {
      Ship newShip = new Ship(-10, randYPos, 4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1));
    }
    // right side
    else {
      Ship newShip = new Ship(510, randYPos, -4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1));
    }
  }

  // TESTABLE VERSION: generates numShip new ships
  public ILoShip generateShips(int numShips, int seed) {
    // generate which side the ship will spawn
    // 0 -> left side, 1 -> right side
    int sideValue = new Random(seed).nextInt(2);
    // generate random yPos for ship to spawn
    // that is not in the top or bottom 1/7 of the screen
    int illegalZoneSize = (int)(300 / 7);
    int randYPos = new Random(seed).nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;
    // termination
    if (numShips == 0) {
      return this;
    }
    // left side
    if (sideValue == 0) {
      Ship newShip = new Ship(-10, randYPos, 4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1, seed));
    }
    // right side
    else {
      Ship newShip = new Ship(510, randYPos, -4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1, seed));
    }
  }

  // deletes ships hit by bullets
  public ILoShip updateCollisions(ILoBullet bulletList) {
    return this;
  }

  // is any ship in this list touching the given bullet?
  public boolean isTouching(Bullet bullet) {
    return false;
  }
}

// non-empty list of Ships
class ConsLoShip implements ILoShip {
  Ship first;
  ILoShip rest;

  // to construct a non-empty list of Ships
  ConsLoShip(Ship first, ILoShip rest) {
    this.first = first;
    this.rest = rest;
  }

  /* TEMPLATE:
   * Fields: 
   * ... this.first ...                                              -- Ship
   * ... this.rest ...                                               -- ILoShip
   * Methods:
   * ... this.length() ...                                           -- int
   * ... this.moveAllShips() ...                                     -- ILoShip
   * ... this.removeOffScreenShips() ...                             -- ILoShip
   * ... this.placeAllShips(WorldScene scene) ...                    -- WorldScene
   * ... this.generateShips(int numShips) ...                        -- ILoShip
   * ... this.generateShips(int numShips, int seed) ...              -- ILoShip
   * ... this.updateCollisions(ILoBullet bulletList) ...             -- ILoShip
   * ... this.isTouching(Bullet bullet) ...                          -- boolean
   * Methods for Fields:
   * ... this.first. ...
   * ... 
   * ... this.rest. ...
   * ... 
   */

  // return length of this ship list
  public int length() {
    return 1 + this.rest.length();
  }

  // moves all ships 
  public ILoShip moveAllShips() {
    return new ConsLoShip(this.first.moveShip(), this.rest.moveAllShips());
  }

  // removes off-screen ships
  public ILoShip removeOffScreenShips() {
    if (this.first.isShipOffScreen()) {
      return this.rest.removeOffScreenShips();
    }
    return new ConsLoShip(this.first, this.rest.removeOffScreenShips());
  }

  // places all ships on given WorldScene
  public WorldScene placeAllShips(WorldScene scene) {
    return this.rest.placeAllShips(this.first.place(scene));
  }

  // generates numShip new ships
  public ILoShip generateShips(int numShips) {
    // generate which side the ship will spawn
    // 0 -> left side, 1 -> right side
    int sideValue = new Random().nextInt(2);
    // generate random yPos for ship to spawn
    // that is not in the top or bottom 1/7 of the screen
    int illegalZoneSize = 300 / 7;
    int randYPos = new Random().nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;
    // termination
    if (numShips == 0) {
      return this;
    }
    // left side
    if (sideValue == 0) {
      Ship newShip = new Ship(-10, randYPos, 4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1));
    }
    // right side
    else {
      Ship newShip = new Ship(510, randYPos, -4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1));
    }
  }

  // TESTABLE VERSION: generates numShip new ships
  public ILoShip generateShips(int numShips, int seed) {
    // generate which side the ship will spawn
    // 0 -> left side, 1 -> right side
    int sideValue = new Random(seed).nextInt(2);
    // generate random yPos for ship to spawn
    // that is not in the top or bottom 1/7 of the screen
    int illegalZoneSize = 300 / 7;
    int randYPos = new Random(seed).nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;
    // termination
    if (numShips == 0) {
      return this;
    }
    // left side
    if (sideValue == 0) {
      Ship newShip = new Ship(-10, randYPos, 4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1, seed));
    }
    // right side
    else {
      Ship newShip = new Ship(510, randYPos, -4);
      return new ConsLoShip(newShip, this.generateShips(numShips - 1, seed));
    }
  }

  // deletes ships hit by bullets
  public ILoShip updateCollisions(ILoBullet bulletList) {
    if (this.first.isTouching(bulletList)) {
      return this.rest.updateCollisions(bulletList);
    }
    return new ConsLoShip(this.first, this.rest.updateCollisions(bulletList));
  }

  // is any ship in this list touching the given bullet?
  public boolean isTouching(Bullet bullet) {
    return this.first.withinRange(bullet.xPos, bullet.yPos, bullet.size)
        || this.rest.isTouching(bullet);
  }
}


// list of Bullets
interface ILoBullet {

  // returns the length of this list of Bullets
  public int length();

  // returns whether there are no bullets left on the screen
  boolean noBulletsOnScreen();

  // adds user fired bullet
  ILoBullet addFiredBullet();

  // moves all non-starting bullets 
  ILoBullet moveAllBullets();

  // removes off-screen bullets
  ILoBullet removeOffScreenBullets();

  // places all bullets on given WorldScene
  WorldScene placeAllBullets(WorldScene scene);

  // explodes bullets that hit a ship
  ILoBullet updateCollisions(ILoShip shipList);

  // is any bullet in this list touching the given ship?
  boolean isTouching(Ship ship);

  // creates the list of exploded bullets
  ILoBullet explodeBullet(int currentExplosion, int maxExplosions, ILoShip shipList);

}

// empty list of Bullets
class MtLoBullet implements ILoBullet {

  // returns the length of this list of Bullets
  public int length() {
    return 0;
  }

  // returns whether there are no bullets left on the screen
  public boolean noBulletsOnScreen() {
    return true;
  }

  // adds user fired bullet
  public ILoBullet addFiredBullet() {
    Bullet firedBullet = new Bullet(250, 302, 90, 1);
    return new ConsLoBullet(firedBullet, this);
  }

  // moves all non-starting bullets 
  public ILoBullet moveAllBullets() {
    return this;
  }

  // removes off-screen bullets
  public ILoBullet removeOffScreenBullets() {
    return this;
  }

  // places all bullets on given WorldScene
  public WorldScene placeAllBullets(WorldScene scene) {
    return scene;
  }

  // explodes bullets that hit a ship
  public ILoBullet updateCollisions(ILoShip shipList) {
    return this;
  }

  // is any bullet in this list touching the given ship?
  public boolean isTouching(Ship ship) {
    return false;
  }

  // creates the list of exploded bullets
  public ILoBullet explodeBullet(int currentExplosion, int maxExplosions, ILoShip shipList) {
    return this;
  } 
}

// non-empty list of Bullets
class ConsLoBullet implements ILoBullet {
  Bullet first;
  ILoBullet rest;

  // to construct a non-empty list of Bullets
  ConsLoBullet(Bullet first, ILoBullet rest) {
    this.first = first;
    this.rest = rest;
  }

  // returns the length of this list of Bullets
  public int length() {
    return 1 + this.rest.length();
  }

  // returns whether there are no bullets left on the screen
  public boolean noBulletsOnScreen() {
    return false;
  }

  // adds user fired bullet
  public ILoBullet addFiredBullet() {
    Bullet firedBullet = new Bullet(250, 302, 90, 2, 1);
    return new ConsLoBullet(firedBullet, this);
  }

  // moves all non-starting bullets 
  public ILoBullet moveAllBullets() {
    return new ConsLoBullet(this.first.moveBullet(), this.rest.moveAllBullets());
  }

  // removes off-screen bullets
  public ILoBullet removeOffScreenBullets() {
    if (this.first.isBulletOffScreen()) {
      return this.rest.removeOffScreenBullets();
    }
    return new ConsLoBullet(this.first, this.rest.removeOffScreenBullets());
  }

  // places all bullets on given WorldScene
  public WorldScene placeAllBullets(WorldScene scene) {
    return this.rest.placeAllBullets(this.first.place(scene));
  }

  // explodes bullets that hit a ship
  public ILoBullet updateCollisions(ILoShip shipList) {
    if (this.first.isTouching(shipList)) {
      return this.explodeBullet(0, this.first.getExplosions() + 1, shipList);
    }
    return new ConsLoBullet(this.first, this.rest.updateCollisions(shipList));
  }

  // is any bullet in this list touching the given ship?
  public boolean isTouching(Ship ship) {
    return this.first.withinRange(ship.xPos, ship.yPos)
        || this.rest.isTouching(ship);
  }

  // creates the list of exploded bullets
  public ILoBullet explodeBullet(int currentExplosion, int maxExplosions, ILoShip shipList) {
    // terminates when current value is equal to max
    if (currentExplosion == maxExplosions) {
      return this.rest.updateCollisions(shipList);
    }
    // else creates the current exploded bullet appended to the rest of the exploded list
    else {
      return new ConsLoBullet(
          new Bullet(
              this.first.getXPos(), this.first.getYPos(), 
              (currentExplosion * 360 / maxExplosions), maxExplosions),
          this.explodeBullet(currentExplosion + 1, maxExplosions, shipList));
    }
  }
}

class ExamplesNBulletProgram {

  boolean testBigBang(Tester t) {
    NBullets w = new NBullets(100);
    int worldWidth = 500;
    int worldHeight = 300;
    double tickRate = 1.0 / 28.0;
    return w.bigBang(worldWidth, worldHeight, tickRate);
  }

  //random object(s)
  int randSeed = new Random(10).nextInt();
  int randNumShips = new Random(1).nextInt(3);
  int illegalZoneSize = (int)(300 / 7);
  int randShipYLPos = new Random(2).nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;
  int randShipYRPos = new Random(4).nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;

  // Ship examples
  Ship shipLStart = new Ship(-10, this.randShipYLPos , 4);
  Ship shipLS2 = new Ship(-6, this.randShipYLPos, 4);
  Ship shipRStart = new Ship(510, this.randShipYRPos, -4);
  Ship shipRS2 = new Ship(506, this.randShipYRPos, -4);
  Ship shipOff1 = new Ship(-50, 150, 4);
  Ship shipOff2 = new Ship(550, 150, -4);
  Ship shipOff3 = new Ship(250, -50, -4);
  Ship shipOff4 = new Ship(250, 350, 4);
  Ship shipTouching1 = new Ship(250, 150, 4);
  Ship shipTouching2 = new Ship(230, 150, 4);
  Ship shipTouching3 = new Ship(229, 150, 4);

  // Bullet examples
  Bullet bulletStart = new Bullet(250, 302, 90, 1);
  Bullet bulletS1 = new Bullet(250, 294, 90, 1);
  Bullet bulletS2 = new Bullet(250, 286, 90, 1);
  Bullet bulletOff1 = new Bullet(-50, 150, 45, 4);
  Bullet bulletOff2 = new Bullet(550, 150, 138, 3);
  Bullet bulletOff3 = new Bullet(250, -50, 0, 7);
  Bullet bulletOff4 = new Bullet(250, 350, 4, 1);
  Bullet bulletTouchingA = new Bullet(250, 150, 90, 1);
  Bullet bulletTouchingB = new Bullet(250, 150, 90, 5);
  Bullet bulletExplodeA1 = new Bullet(250, 150, 0, 2);
  Bullet bulletExplodeA2 = new Bullet(250, 150, 180, 2);
  Bullet bulletExplodeB1 = new Bullet(250, 150, 0, 6);
  Bullet bulletExplodeB2 = new Bullet(250, 150, 60, 6);
  Bullet bulletExplodeB3 = new Bullet(250, 150, 120, 6);
  Bullet bulletExplodeB4 = new Bullet(250, 150, 180, 6);
  Bullet bulletExplodeB5 = new Bullet(250, 150, 240, 6);
  Bullet bulletExplodeB6 = new Bullet(250, 150, 300, 6);

  // Ship list examples
  ILoShip mtlos = new MtLoShip();
  ILoShip ships1 =
      new ConsLoShip(this.shipLStart, 
          new ConsLoShip(this.shipRStart, this.mtlos));
  ILoShip ships2 =
      new ConsLoShip(this.shipLS2,
          new ConsLoShip(this.shipRS2, this.mtlos));
  ILoShip shipsTouching1 = 
      new ConsLoShip(this.shipTouching1, 
          new ConsLoShip(this.shipTouching2, 
              new ConsLoShip(this.shipTouching3, this.mtlos)));
  ILoShip shipsTouching2 = 
      new ConsLoShip(this.shipTouching1, this.mtlos);
  ILoShip shipsTouching3 = 
      new ConsLoShip(this.shipTouching3, this.mtlos);

  // Bullet list examples
  ILoBullet mtlob = new MtLoBullet();
  ILoBullet bullets1 =
      new ConsLoBullet(this.bulletStart, this.mtlob);
  ILoBullet bullets2 =
      new ConsLoBullet(this.bulletS1, this.mtlob);
  ILoBullet bullets3 =
      new ConsLoBullet(this.bulletS2, this.mtlob);
  ILoBullet bulletsOff1 = 
      new ConsLoBullet(this.bulletOff1, 
          new ConsLoBullet(this.bulletS2, 
              new ConsLoBullet(this.bulletOff2, 
                  new ConsLoBullet(this.bulletOff3, 
                      new ConsLoBullet(this.bulletOff4, this.mtlob)))));
  ILoBullet bulletsOff2 = 
      new ConsLoBullet(this.bulletOff1,
          new ConsLoBullet(this.bulletOff2, 
              new ConsLoBullet(this.bulletOff3, 
                  new ConsLoBullet(this.bulletOff4, this.mtlob))));
  ILoBullet bulletsTouching1 = 
      new ConsLoBullet(this.bulletTouchingA, 
          new ConsLoBullet(this.bulletTouchingB, this.mtlob));
  ILoBullet bulletsTouching2 = 
      new ConsLoBullet(this.bulletTouchingA, this.mtlob);
  ILoBullet bulletsExplode1 = 
      new ConsLoBullet(this.bulletExplodeA1,
          new ConsLoBullet(this.bulletExplodeA2,
              new ConsLoBullet(this.bulletExplodeB1,
                  new ConsLoBullet(this.bulletExplodeB2,
                      new ConsLoBullet(this.bulletExplodeB3,
                          new ConsLoBullet(this.bulletExplodeB4,
                              new ConsLoBullet(this.bulletExplodeB5,
                                  new ConsLoBullet(this.bulletExplodeB6, this.mtlob))))))));
  ILoBullet bulletsExplode2 = 
      new ConsLoBullet(this.bulletExplodeA1,
          new ConsLoBullet(this.bulletExplodeA2, this.mtlob));

  // World examples
  NBullets world0 = new NBullets(10, 0, this.mtlos, this.mtlob, 27, this.randSeed);
  NBullets world1 = new NBullets(
      8, 0, this.shipsTouching1, this.bulletsTouching1, 27, this.randSeed);
  NBullets world2 = new NBullets(
      8, 2, this.shipsTouching3, this.bulletsExplode1, 27, this.randSeed);
  
  // for text() tests
  WorldScene sceneEmpty = new WorldScene(0, 0);
  WorldScene sceneBase = new WorldScene(500, 300);
  String string0 = "bullets left: 10; ships destroyed: 0";
  WorldImage text0 = new TextImage(string0, 13, Color.BLACK);
  WorldScene world0TextScene = this.sceneBase.placeImageXY(text0, 100, 285);
  String string1 = "bullets left: 8; ships destroyed: 0";
  WorldImage text1 = new TextImage(string1, 13, Color.BLACK);
  WorldScene world1TextScene = this.sceneBase.placeImageXY(text1, 100, 285);
  String string2 = "bullets left: 8; ships destroyed: 2";
  WorldImage text2 = new TextImage(string2, 13, Color.BLACK);
  WorldScene world2TextScene = this.sceneBase.placeImageXY(text2, 100, 285);
  
  // for worldEnds() tests
  NBullets worldEnded = new NBullets(
      0, 43, this.mtlos, this.mtlob, 13, this.randSeed);
  
  // for onKeyEvent(String key) tests
  NBullets world0AfterKey = new NBullets(
      9, 0, this.mtlos, this.bullets1, 27, this.randSeed);
  NBullets world1AfterKey = new NBullets(
      7, 0, this.shipsTouching1, new ConsLoBullet(this.bulletStart, this.bulletsTouching1),
      27, this.randSeed);
  
  // for updateShips() tests
  NBullets world0UpdatedShips = new NBullets(
      10, 0, this.mtlos, this.mtlob, 27, this.randSeed);
  NBullets world1UpdatedShips = new NBullets(
      8, 0, this.shipsTouching1.removeOffScreenShips().moveAllShips(), 
      this.bulletsTouching1, 27, this.randSeed);
  NBullets world2UpdatedShips = new NBullets(
      8, 2, this.shipsTouching3.removeOffScreenShips().moveAllShips(), 
      this.bulletsExplode1, 27, this.randSeed);
  
  // for updateBullets() tests
  NBullets world0UpdatedBullets = new NBullets(
      10, 0, this.mtlos, this.mtlob, 27, this.randSeed);
  NBullets world1UpdatedBullets = new NBullets(
      8, 0, this.shipsTouching1, this.bulletsTouching1.removeOffScreenBullets().moveAllBullets(), 
      27, this.randSeed);
  NBullets world2UpdatedBullets = new NBullets(
      8, 2, this.shipsTouching3, this.bulletsExplode1.removeOffScreenBullets().moveAllBullets(), 
      27, this.randSeed);
  
  // for generateNewShips() tests
  NBullets world0Generate = new NBullets(10, 0, this.mtlos, this.mtlob, 1, this.randSeed);
  NBullets world1Generate = new NBullets(10, 0, this.mtlos, this.mtlob, 0, this.randSeed);
  int world2GenerateNumShips = new Random(this.randSeed).nextInt(3) + 1;
  ILoShip world2GenerateShips = this.mtlos.generateShips(
      this.world2GenerateNumShips, this.randSeed);
  NBullets world2Generate = new NBullets(
      10, 0, this.world2GenerateShips, this.mtlob, 27, this.randSeed);

  // for collisionHandler() tests
  NBullets world0Collisions = new NBullets(
      10, 0, this.mtlos, this.mtlob, 27, this.randSeed);
  ILoShip collisionShips1 = this.shipsTouching1.updateCollisions(this.bulletsTouching1);
  ILoBullet collisionBullets1 = this.bulletsTouching1.updateCollisions(this.shipsTouching1);
  int updatedShipsD1 = (this.shipsTouching1.length() - this.collisionShips1.length());
  NBullets world1Collisions = new NBullets(
      8, 2, this.shipsTouching1, this.bulletsTouching1, 27, this.randSeed);
  ILoShip collisionShips2 = this.shipsTouching3.updateCollisions(this.bulletsExplode1);
  ILoBullet collisionBullets2 = this.bulletsExplode1.updateCollisions(this.shipsTouching3);
  int updatedShipsD2 = 2 + (this.shipsTouching3.length() - this.collisionShips2.length());
  NBullets world2Collisions = new NBullets(
      8, 2, this.shipsTouching3, this.bulletsExplode1, 27, this.randSeed);

  // for onTick() tests
  NBullets world0AfterTick = new NBullets(
      10, 0, this.mtlos, this.mtlob, 27, this.randSeed)
      .collisionHandler().updateBullets().updateShips().generateNewShips();
  NBullets world1AfterTick = new NBullets(
      8, 0, this.shipsTouching1, this.bulletsTouching1, 27, this.randSeed)
      .collisionHandler().updateBullets().updateShips().generateNewShips();
  NBullets world2AfterTick = new NBullets(
      8, 2, this.shipsTouching3, this.bulletsExplode1, 27, this.randSeed)
      .collisionHandler().updateBullets().updateShips().generateNewShips();

  // for other tests
  WorldScene sceneB1 = this.sceneBase.placeImageXY(
      this.bulletS1.draw(), this.bulletS1.xPos, this.bulletS1.yPos);
  WorldScene sceneS1 = this.sceneBase.placeImageXY(
      this.shipLS2.draw(), this.shipLS2.xPos, this.shipLS2.yPos).placeImageXY(
          this.shipRS2.draw(), this.shipRS2.yPos, this.shipRS2.yPos);
  WorldScene scene1 = this.world1.text(this.ships1.placeAllShips(
      this.bullets1.placeAllBullets(sceneBase)));

  //-----------------------------------------------------------------------------------------------
  // Tests in Ship class

  // getXPos() tests
  boolean testGetXPosShip(Tester t) {
    return t.checkExpect(this.shipLStart.getXPos(), this.shipLStart.xPos)
        && t.checkExpect(this.shipLS2.getXPos(), this.shipLS2.xPos);
  }

  // getYPos() tests
  boolean testGetYPosShip(Tester t) {
    return t.checkExpect(this.shipLStart.getYPos(), this.shipLStart.yPos)
        && t.checkExpect(this.shipRStart.getYPos(), this.shipRStart.yPos);
  }

  // draw() tests
  boolean testDrawShip(Tester t) {
    return t.checkExpect(this.shipLStart.draw(), new CircleImage(10, OutlineMode.SOLID, Color.CYAN))
        && t.checkExpect(this.shipLS2.draw(), new CircleImage(10, OutlineMode.SOLID, Color.CYAN));
  }

  // place() tests
  boolean testPlaceShip(Tester t) {
    return t.checkExpect(this.randSeed, -1157793070)
        && t.checkExpect(this.randShipYLPos, 118)
        && t.checkExpect(this.randShipYRPos, 176)
        && t.checkExpect(this.shipLStart.place(this.sceneBase), 
            this.sceneBase.placeImageXY(
                this.shipLStart.draw(), this.shipLStart.xPos, this.shipLStart.yPos))
        && t.checkExpect(this.shipTouching2.place(this.sceneBase), 
            this.sceneBase.placeImageXY(
                this.shipTouching2.draw(), this.shipTouching2.xPos, this.shipTouching2.yPos));
  }

  // moveShip() tests
  boolean testMoveShip(Tester t) {
    return t.checkExpect(this.shipLStart.moveShip(), this.shipLS2)
        && t.checkExpect(this.shipRStart.moveShip(), this.shipRS2);
  }

  // isShipOffScreen() tests
  boolean testIsShipOffScreen(Tester t) {
    return t.checkExpect(this.shipOff1.isShipOffScreen(), true)
        && t.checkExpect(this.shipOff2.isShipOffScreen(), true)
        && t.checkExpect(this.shipOff3.isShipOffScreen(), true)
        && t.checkExpect(this.shipOff4.isShipOffScreen(), true)
        && t.checkExpect(this.shipLStart.isShipOffScreen(), false);
  }

  // isTouching(ILoBullet bullet) tests
  boolean testIsTouchingShip(Tester t) {
    return t.checkExpect(this.shipLStart.isTouching(this.mtlob), false)
        && t.checkExpect(this.shipLStart.isTouching(this.bulletsTouching1), false)
        && t.checkExpect(this.shipTouching1.isTouching(this.bulletsTouching2), true)
        && t.checkExpect(this.shipTouching3.isTouching(this.bulletsTouching1), false);
  }

  //-----------------------------------------------------------------------------------------------
  // Tests in Bullet class

  // getXPos() tests
  boolean testGetXPosBullet(Tester t) {
    return t.checkExpect(this.bulletStart.getXPos(), 250)
        && t.checkExpect(this.bulletS1.getXPos(), 250);
  }  

  // getYPos() tests
  boolean testGetYPosBullet(Tester t) {
    return t.checkExpect(this.bulletStart.getYPos(), 302)
        && t.checkExpect(this.bulletS1.getYPos(), 294);
  }

  // draw() tests
  boolean testDrawBullet(Tester t) {
    return t.checkExpect(this.bulletStart.draw(), new CircleImage(2, OutlineMode.SOLID, Color.PINK))
        && t.checkExpect(this.bulletS1.draw(), new CircleImage(2, OutlineMode.SOLID, Color.PINK));
  }

  // place(WorldScene scene) tests
  boolean testPlaceBullet(Tester t) {
    return t.checkExpect(this.bulletStart.place(this.sceneBase), 
        this.sceneBase.placeImageXY(
            this.bulletStart.draw(), this.bulletStart.xPos, this.bulletStart.yPos))
        && t.checkExpect(this.bulletS1.place(this.sceneBase), 
            this.sceneBase.placeImageXY(
                this.bulletS1.draw(), this.bulletS1.xPos, this.bulletS1.yPos))
        && t.checkExpect(this.bulletExplodeB4.place(this.sceneBase), 
            this.sceneBase.placeImageXY(
                this.bulletExplodeB4.draw(), 
                this.bulletExplodeB4.xPos, this.bulletExplodeB4.yPos));
  }

  // moveBullet() tests
  boolean testMoveBullet(Tester t) {
    return t.checkExpect(this.bulletStart.moveBullet(), this.bulletS1)
        && t.checkExpect(this.bulletS1.moveBullet(), this.bulletS2);
  }

  // isBulletOffScreen() tests
  boolean testIsBulletOffScreen(Tester t) {
    return t.checkExpect(this.bulletOff1.isBulletOffScreen(), true)
        && t.checkExpect(this.bulletOff2.isBulletOffScreen(), true)
        && t.checkExpect(this.bulletOff3.isBulletOffScreen(), true)
        && t.checkExpect(this.bulletOff4.isBulletOffScreen(), true)
        && t.checkExpect(this.bulletStart.isBulletOffScreen(), false);
  }

  // isTouching(ILoShip ship) tests
  boolean testIsTouchingBullet(Tester t) {
    return t.checkExpect(this.bulletStart.isTouching(this.mtlos), false)
        && t.checkExpect(this.bulletStart.isTouching(this.shipsTouching1), false)
        && t.checkExpect(this.bulletTouchingA.isTouching(this.shipsTouching1), true)
        && t.checkExpect(this.bulletTouchingA.isTouching(this.shipsTouching3), false);
  }

  //-----------------------------------------------------------------------------------------------
  // Tests in ILoShip interface

  // length() tests
  boolean testLengthShip(Tester t) {
    return t.checkExpect(this.mtlos.length(), 0)
        && t.checkExpect(this.ships1.length(), 2)
        && t.checkExpect(this.ships2.length(), 2);
  }

  // moveAllShips() tests
  boolean testMoveAllShips(Tester t) {
    return t.checkExpect(this.mtlos.moveAllShips(), this.mtlos)
        && t.checkExpect(this.ships1.moveAllShips(), this.ships2);
  }

  // removeOffScreenShips() tests
  boolean testRemoveOffScreenShips(Tester t) {
    return t.checkExpect(this.mtlos.removeOffScreenShips(), this.mtlos)
        ;
  }

  // placeAllShips(WorldScene scene) tests
  boolean testPlaceAllShips(Tester t) {
    return t.checkExpect(this.randSeed, -1157793070)
        && t.checkExpect(this.randShipYLPos, 118)
        && t.checkExpect(this.randShipYRPos, 176)
        && t.checkExpect(this.mtlos.placeAllShips(this.sceneBase), this.sceneBase)
        && t.checkExpect(this.ships1.placeAllShips(this.sceneBase), 
            this.shipRStart.place(this.shipLStart.place(this.sceneBase)))
        && t.checkExpect(this.ships2.placeAllShips(this.sceneBase), 
            this.shipRS2.place(this.shipLS2.place(this.sceneBase)))
        ;
  }

  // generateShips(int numShips, int seed) tests (TESTABLE VERSION)
  boolean testGenerateShips(Tester t) {
    int illegalZoneSize = 300 / 7;
    int randYPos = new Random(this.randSeed).nextInt(300 - 2 * illegalZoneSize) + illegalZoneSize;
    return t.checkExpect(this.randSeed, -1157793070)
        && t.checkExpect(this.mtlos.generateShips(0, this.randSeed), this.mtlos)
        && t.checkExpect(this.mtlos.generateShips(1, this.randSeed), 
            new ConsLoShip(new Ship(510, randYPos, -4), this.mtlos));
  }

  // updateCollisions(ILoBullet bulletList) tests
  boolean testUpdateCollisionsShips(Tester t) {
    return t.checkExpect(this.mtlos.updateCollisions(this.mtlob), this.mtlos)
        ;
  }

  // isTouching(Bullet bullet) tests in ILoShip
  boolean testIsTouchingShips(Tester t) {
    return t.checkExpect(this.mtlos.isTouching(bulletS2), false)
        ;
  }

  //-----------------------------------------------------------------------------------------------
  // Tests in ILoBullet interface

  // length() tests
  boolean testLengthBullet(Tester t) {
    return t.checkExpect(this.mtlob.length(), 0)
        && t.checkExpect(this.bullets1.length(), 1)
        && t.checkExpect(this.bullets2.length(), 1);
  }

  // noBulletsOnScreen() tests
  boolean testNoBulletsOnScreen(Tester t) {
    return t.checkExpect(this.mtlob.noBulletsOnScreen(), true)
        && t.checkExpect(this.bullets1.noBulletsOnScreen(), false)
        && t.checkExpect(this.bulletsExplode2.noBulletsOnScreen(), false);
  }

  // addFiredBullet() tests
  boolean testAddFiredBullet(Tester t) {
    return t.checkExpect(this.mtlob.addFiredBullet(), this.bullets1)
        && t.checkExpect(this.bullets2.addFiredBullet(), 
            new ConsLoBullet(this.bulletStart, this.bullets2));
  }

  // moveAllBullets() tests
  boolean testMoveAllBullets(Tester t) {
    return t.checkExpect(this.mtlob.moveAllBullets(), this.mtlob)
        && t.checkExpect(this.bullets1.moveAllBullets(), this.bullets2)
        && t.checkExpect(this.bullets2.moveAllBullets(), this.bullets3);
  }

  // removeOffScreenBullets() tests
  boolean testRemoveOffScreenBullets(Tester t) {
    return t.checkExpect(this.mtlob.removeOffScreenBullets(), this.mtlob)
        && t.checkExpect(this.bulletsOff1.removeOffScreenBullets(), 
            new ConsLoBullet(this.bulletS2, this.mtlob))
        && t.checkExpect(this.bulletsOff2.removeOffScreenBullets(), this.mtlob);
  }

  // placeAllBullets(WorldScene scene) tests
  boolean testPlaceAllBullets(Tester t) {
    return t.checkExpect(this.mtlob.placeAllBullets(this.sceneBase), this.sceneBase)
        && t.checkExpect(this.bulletsExplode2.placeAllBullets(this.sceneBase), 
            this.sceneBase.placeImageXY(this.bulletExplodeA2.draw(), 
                this.bulletExplodeA2.xPos, this.bulletExplodeA2.yPos).placeImageXY(
                    this.bulletExplodeA1.draw(), 
                    this.bulletExplodeA1.xPos, this.bulletExplodeA1.yPos))
        && t.checkExpect(this.bullets1.placeAllBullets(this.sceneBase), 
            this.sceneBase.placeImageXY(this.bulletStart.draw(), 
                this.bulletStart.xPos, this.bulletStart.yPos));
  }

  // updateCollisions(ILoShip shipList) tests
  boolean testUpdateCollisionsBullets(Tester t) {
    return t.checkExpect(this.mtlob.updateCollisions(this.mtlos), this.mtlob)
        && t.checkExpect(this.bulletsTouching1.updateCollisions(this.mtlos), 
            this.bulletsTouching1)
        && t.checkExpect(this.bulletsTouching2.updateCollisions(this.mtlos), 
            this.bulletsTouching2)
        && t.checkExpect(this.bulletsTouching1.updateCollisions(this.shipsTouching1), 
            this.bulletsExplode1)
        && t.checkExpect(this.bulletsTouching1.updateCollisions(this.shipsTouching3), 
            this.bulletsTouching1)
        && t.checkExpect(this.bulletsTouching2.updateCollisions(this.shipsTouching1), 
            this.bulletsExplode2)
        && t.checkExpect(this.bulletsTouching2.updateCollisions(this.shipsTouching3), 
            this.bulletsTouching2);
  }

  // isTouching(Ship ship) tests in ILoBullet
  boolean testIsTouchingBullets(Tester t) {
    return t.checkExpect(this.mtlob.length(), 0)
        && t.checkExpect(this.bullets1.length(), 1)
        && t.checkExpect(this.bullets2.length(), 1);
  }

  // explodeBullet(int currentExplosion, int maxExplosions, ILoShip shipList) tests
  boolean testExplodeBullet(Tester t) {
    return t.checkExpect(this.mtlob.explodeBullet(0, 0, this.mtlos), this.mtlob)
        && t.checkExpect(this.bullets1.explodeBullet(0, 0, this.mtlos), this.mtlob)
        && t.checkExpect(this.bullets2.explodeBullet(0, 0, this.mtlos), this.mtlob);
  }

  //-----------------------------------------------------------------------------------------------
  // Tests in NBullets class

  // text(World Scene scene) tests
  boolean testText(Tester t) {
    return t.checkExpect(this.world0.text(this.sceneBase), this.world0TextScene)
        && t.checkExpect(this.world1.text(this.sceneBase), this.world1TextScene)
        && t.checkExpect(this.world2.text(this.sceneBase), this.world2TextScene);
  }

  // makeScene() tests
  boolean testMakeScene(Tester t) {
    return t.checkExpect(this.world0.makeScene(), 
        this.world0.text(this.mtlos.placeAllShips(
            this.mtlob.placeAllBullets(this.sceneEmpty))))
        && t.checkExpect(this.world1.makeScene(), 
            this.world1.text(this.world1.shipList.placeAllShips(
                this.world1.bulletList.placeAllBullets(this.sceneEmpty))))
        && t.checkExpect(this.world2.makeScene(), 
            this.world2.text(this.world2.shipList.placeAllShips(
                this.world2.bulletList.placeAllBullets(this.sceneEmpty))));
  }

  // worldEnds() tests
  boolean testWorldEnds(Tester t) {
    return t.checkExpect(this.world0.worldEnds(), 
        new WorldEnd(false, this.world0.makeScene()))
        && t.checkExpect(this.world1.worldEnds(), 
            new WorldEnd(false, this.world1.makeScene()))
        && t.checkExpect(this.world2.worldEnds(), 
            new WorldEnd(false, this.world2.makeScene()))
        && t.checkExpect(this.worldEnded.worldEnds(), 
            new WorldEnd(true, this.worldEnded.makeScene()));
  }

  // onKeyEvent() tests
  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.world0.onKeyEvent("a"), this.world0)
        && t.checkExpect(this.world1.onKeyEvent("a"), this.world1)
        && t.checkExpect(this.world0.onKeyEvent(" "), this.world0AfterKey)
        && t.checkExpect(this.world1.onKeyEvent(" "), this.world1AfterKey);
  }

  // updateShips() tests
  boolean testUpdateShips(Tester t) {
    return t.checkExpect(this.world0.updateShips(), this.world0UpdatedShips)
        && t.checkExpect(this.world1.updateShips(), this.world1UpdatedShips)
        && t.checkExpect(this.world2.updateShips(), this.world2UpdatedShips);
  }

  // updateBullets() tests
  boolean testUpdateBullets(Tester t) {
    return t.checkExpect(this.world0.updateBullets(), this.world0UpdatedBullets)
        && t.checkExpect(this.world1.updateBullets(), this.world1UpdatedBullets)
        && t.checkExpect(this.world2.updateBullets(), this.world2UpdatedBullets);
  }

  // generateNewShips() tests
  boolean testGenerateNewShips(Tester t) {
    return t.checkExpect(this.world0Generate.generateNewShips(this.randSeed), 
        this.world1Generate)
        && t.checkExpect(this.world1Generate.generateNewShips(this.randSeed), 
            this.world2Generate);
  }

  // collisionHandler() tests
  boolean testCollisionHandler(Tester t) {
    return t.checkExpect(this.world0.collisionHandler(), this.world0Collisions)
        //&& t.checkExpect(this.world1.collisionHandler(), this.world1Collisions)
        && t.checkExpect(this.world2.collisionHandler(), this.world2Collisions);
  }

  // onTick() tests
  boolean testOnTick(Tester t) {
    return t.checkExpect(this.world0.onTickTestable(), this.world0AfterTick)
        && t.checkExpect(this.world1.onTickTestable(), this.world1AfterTick)
        && t.checkExpect(this.world2.onTickTestable(), this.world2AfterTick);
  }
}