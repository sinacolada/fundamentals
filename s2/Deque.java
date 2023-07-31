import tester.*;


// represents a boolean-valued question over values of type T
interface IPred<T> {
  boolean apply(T t);
}

// predicate for determining if a string has length 3
class LengthThree implements IPred<String> {
  public boolean apply(String s) {
    return s.length() == 3;
  }
}

// predicate for determining if a string starts with a c
class StartsWithC implements IPred<String> {
  public boolean apply(String s) {
    return s.substring(0, 1).equalsIgnoreCase("c");
  }
}

//predicate for determining if an integer is even
class IsEven implements IPred<Integer> {
  public boolean apply(Integer i) {
    return (i % 2) == 0;
  }
}

//predicate for determining if an integer is 0
class IsZero implements IPred<Integer> {
  public boolean apply(Integer i) {
    return i == 0;
  }
}

// to represent a double-ended queue
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // counts number of nodes not including the header
  // we can use header.next as we know exactly how a sentinel behaves
  int size() {
    return this.header.next.getSize(0);
  }

  // EFFECT: adds data of type T at head of this list
  void addAtHead(T newData) {
    ANode<T> newNode = new Node<T>(newData);
    this.header.addNodeFront(newNode);
  }

  // EFFECT: adds data of type T at tail of this list
  void addAtTail(T newData) {
    ANode<T> newNode = new Node<T>(newData);
    this.header.addNodeEnd(newNode);
  }

  // EFFECT: removes first node of this list and returns it
  T removeFromHead() {
    return this.header.removeNodeHead();
  }

  // EFFECT: removes last node of this list and returns it
  T removeFromTail() {
    return this.header.removeNodeTail();
  }

  // produces first node in this deque for which given predicate returns true on its data
  // returns header if none of list data values return true
  // we can use header.next as we know exactly how a sentinel behaves
  ANode<T> find(IPred<T> pred) {
    return this.header.next.findNode(pred);
  }

  // EFFECT: removes given node from this deque
  // we can use header.next as we know exactly how a sentinel behaves
  void removeNode(ANode<T> that) {
    this.header.next.removeNode(that);
  }
}

// to represent any node in a queue
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  //EFFECT: updates this node such that prev refers to given node
  void updatePrev(ANode<T> node) {
    if (node == null) {
      throw new IllegalArgumentException("can't set prev node to null");
    }
    this.prev = node;
    node.next = this;
  }

  // EFFECT: updates this node such that next node refers to given node
  void updateNext(ANode<T> node) {
    if (node == null) {
      throw new IllegalArgumentException("can't set next node to null");
    }
    this.next = node;
    node.prev = this;
  }

  // returns number of nodes
  abstract int getSize(int count);

  // returns this node's index (with the header at 0, and nodes starting at 1)
  abstract int getIndex();

  // EFFECT: updates nodes before and after this when this node is removed from head
  // also returns removed node (this)
  abstract T removeNodeHead();

  // EFFECT: updates nodes before and after this when this node is removed fromt tail
  // also returns removed node (this)
  abstract T removeNodeTail();

  // returns first value where predicate applied returns true, sentinel if no value found
  abstract ANode<T> findNode(IPred<T> pred);

  // EFFECT: removes given node from this list
  void removeNode(ANode<T> that) {
    if (this.sameANode(that)) {
      this.prev.updateNext(this.next);
    }
    else {
      this.next.removeNode(that);
    }
  }

  // checks sameness between two ANodes 
  abstract boolean sameANode(ANode<T> that);

  // checks for sameness between this and that sentinel
  boolean sameSentinel(ANode<T> that) {
    return false;
  }

  // checks for sameness between this and that node
  boolean sameNode(Node<T> that) {
    return false;
  }

}

// to represent a sentinel node in a queue
class Sentinel<T> extends ANode<T> {
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // doesn't count header (sentinel)
  int getSize(int count) {
    return count;
  }

  // returns this node's index (with the header at 0, and nodes starting at 1)
  int getIndex() {
    return 0;
  }

  // EFFECT: adds node to front of list, updating all connections
  void addNodeFront(ANode<T> newNode) {
    newNode.updateNext(this.next);
    newNode.updatePrev(this);
  }

  // EFFECT: adds node to tail of list, updating all connections
  void addNodeEnd(ANode<T> newNode) {
    newNode.updatePrev(this.prev);
    newNode.updateNext(this);
  }

  // EFFECT: removes first node and returns it, error if empty list
  T removeNodeHead() {
    if (this.next.equals(this)) {
      throw new RuntimeException("cannot remove node from an empty list");
    }
    return this.next.removeNodeHead();
  }

  // EFFECT: removes last node and returns it, error if list empty
  T removeNodeTail() {
    if (this.prev.equals(this)) {
      throw new RuntimeException("cannot remove node from an empty list");
    }
    return this.prev.removeNodeTail();
  }

  // returns first value where predicate applied returns true, sentinel if no value found
  ANode<T> findNode(IPred<T> pred) {
    return this;
  }

  // EFFECT: does nothing, because this method must terminate at the sentinel
  void removeNode(ANode<T> that) {
    // empty to terminate the void program
    // cannot return any value else signature will be violated
  }

  //checks for sameness between this and that ANode
  boolean sameANode(ANode<T> that) {
    return that.sameSentinel(this);
  }

  //checks for sameness between this and that Sentinel
  boolean sameSentinel(Sentinel<T> that) {
    return this.equals(that)
        && this.getIndex() == that.getIndex()
        && this.next.getIndex() == that.next.getIndex()
        && this.prev.getIndex() == that.prev.getIndex();
  }

}

// to represent a node (holding data) in a queue
class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    this.data = data;
    this.next = null;
    this.prev = null;
  }

  Node(T data, ANode<T> next, ANode<T> prev) {
    this.data = data;
    this.next = next;
    this.prev = prev;

    // update given nodes to refer to this node
    next.updatePrev(this);
    prev.updateNext(this);
  }

  // counts number of nodes
  int getSize(int count) {
    return this.next.getSize(count + 1);
  }

  // returns this node's index (with the header at 0, and nodes starting at 1)
  int getIndex() {
    return 1 + this.prev.getIndex();
  }

  // EFFECT: updates nodes before and after this when this node is removed from head
  // also returns removed node (this)
  T removeNodeHead() {
    Node<T> temp = this;
    this.next.updatePrev(this.prev);
    return temp.data;
  }

  // EFFECT: updates nodes before and after this when this node is removed fromt tail
  T removeNodeTail() {
    Node<T> temp = this;
    this.prev.updateNext(this.next);
    return temp.data;
  }

  // returns first node where predicate applied to its data returns true
  ANode<T> findNode(IPred<T> pred) {
    if (pred.apply(this.data)) {
      return this;
    }
    return this.next.findNode(pred);
  }

  //checks for sameness between this and that ANode
  boolean sameANode(ANode<T> that) {
    return that.sameNode(this);
  }

  // returns whether this node is the same as that
  boolean sameNode(Node<T> that) {
    return this.data.equals(that.data)
        && this.getIndex() == that.getIndex()
        && this.next.getIndex() == that.next.getIndex()
        && this.prev.getIndex() == that.prev.getIndex();
  }
}

// to represent examples of deques
class ExamplesDeque {

  // nodes & sentinels
  Sentinel<String> sentinelS1;
  Sentinel<String> sentinelS2;
  Sentinel<String> sentinelS3;
  Sentinel<Integer> sentinelS4;

  ANode<String> node1;
  ANode<String> node2;
  ANode<String> node3;
  ANode<String> node4;

  ANode<String> nodeA;
  ANode<String> nodeB;
  ANode<String> nodeC;
  ANode<String> nodeD;
  ANode<String> nodeE;

  ANode<Integer> node0Int;
  ANode<Integer> node1Int;
  ANode<Integer> node2Int;
  ANode<Integer> node3Int;
  ANode<Integer> node4Int;

  // lists
  Deque<String> deque1;
  Deque<String> deque2;
  Deque<String> deque3;
  Deque<Integer> deque4;

  //IPred classes
  IPred<String> lengthThree = new LengthThree();
  IPred<String> startsWithC = new StartsWithC();
  IPred<Integer> isEven = new IsEven();
  IPred<Integer> isZero = new IsZero();

  // initial data state
  void initDeque() {

    this.sentinelS1 = new Sentinel<String>();
    this.sentinelS2 = new Sentinel<String>();
    this.sentinelS3 = new Sentinel<String>();
    this.sentinelS4 = new Sentinel<Integer>();

    this.node1 = new Node<String>("abc", this.sentinelS2, this.sentinelS2);
    this.node2 = new Node<String>("bcd", this.sentinelS2, this.node1);
    this.node3 = new Node<String>("cde", this.sentinelS2, this.node2);
    this.node4 = new Node<String>("def", this.sentinelS2, this.node3);

    this.nodeA = new Node<String>("A", this.sentinelS3, this.sentinelS3);
    this.nodeB = new Node<String>("B", this.sentinelS3, this.nodeA);
    this.nodeC = new Node<String>("C", this.sentinelS3, this.nodeB);
    this.nodeD = new Node<String>("D", this.sentinelS3, this.nodeC);
    this.nodeE = new Node<String>("E", this.sentinelS3, this.nodeD);

    this.node0Int = new Node<Integer>(1);
    this.node1Int = new Node<Integer>(1, this.sentinelS4, this.sentinelS4);
    this.node2Int = new Node<Integer>(2, this.sentinelS4, this.node1Int);
    this.node3Int = new Node<Integer>(3, this.sentinelS4, this.node2Int);
    this.node4Int = new Node<Integer>(4, this.sentinelS4, this.node3Int);

    this.deque1 = new Deque<String>();
    this.deque2 = new Deque<String>(this.sentinelS2);
    this.deque3 = new Deque<String>(this.sentinelS3);
    this.deque4 = new Deque<Integer>(this.sentinelS4);
  }

  // *** CHECK CONSTRUCTOR EXCEPTIONS ***

  // ... do here ...

  // tests in the LengthThree class
  void testLengthThree(Tester t) {
    t.checkExpect(this.lengthThree.apply("a"), false);
    t.checkExpect(this.lengthThree.apply("ab"), false);
    t.checkExpect(this.lengthThree.apply("abc"), true);
    t.checkExpect(this.lengthThree.apply("abcd"), false);
  }

  // tests in the StartsWithC class
  void testStartsWithC(Tester t) {
    t.checkExpect(this.startsWithC.apply("ac"), false);
    t.checkExpect(this.startsWithC.apply("b"), false);
    t.checkExpect(this.startsWithC.apply("c"), true);
    t.checkExpect(this.startsWithC.apply(" c"), false);
  }

  // tests in the IsEven class
  void testIsEven(Tester t) {
    t.checkExpect(this.isEven.apply(1), false);
    t.checkExpect(this.isEven.apply(2), true);
    t.checkExpect(this.isEven.apply(3), false);
    t.checkExpect(this.isEven.apply(4), true);
  }

  // tests in the IsZero class
  void testIsZero(Tester t) {
    t.checkExpect(this.isZero.apply(-1), false);
    t.checkExpect(this.isZero.apply(0), true);
    t.checkExpect(this.isZero.apply(2), false);
    t.checkExpect(this.isZero.apply(5), false);
  }

  // tests the size() method and its helpers
  void testSize(Tester t) {

    // tests functionality of size() method on example Deque
    initDeque(); // reset data
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 5);

    // tests functionality of getSize(int count) method on Sentinel
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.getSize(0), 0);
    t.checkExpect(this.sentinelS1.getSize(-1), -1);
    t.checkExpect(this.sentinelS1.getSize(10), 10);
    t.checkExpect(this.sentinelS4.getSize(0), 0);
    t.checkExpect(this.sentinelS4.getSize(-1), -1);
    t.checkExpect(this.sentinelS4.getSize(10), 10);

    // tests functionality of getSize(int count) method on Node
    initDeque(); // reset data
    t.checkExpect(this.node2.getSize(0), 3);
    t.checkExpect(this.node2.getSize(-1), 2);
    t.checkExpect(this.node2.getSize(10), 13);
    t.checkExpect(this.node3Int.getSize(0), 2);
    t.checkExpect(this.node3Int.getSize(-1), 1);
    t.checkExpect(this.node3Int.getSize(10), 12);

  }

  // tests the getIndex() method
  void testGetIndex(Tester t) {

    // tests functionality of getIndex() method on Sentinel
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.getIndex(), 0);
    t.checkExpect(this.sentinelS2.getIndex(), 0);
    t.checkExpect(this.sentinelS3.getIndex(), 0);
    t.checkExpect(this.sentinelS4.getIndex(), 0);

    // tests functionality of getIndex() method on Node
    initDeque(); // reset data
    t.checkExpect(this.node1.getIndex(), 1);
    t.checkExpect(this.node2.getIndex(), 2);
    t.checkExpect(this.nodeB.getIndex(), 2);
    t.checkExpect(this.nodeE.getIndex(), 5);
    t.checkExpect(this.node2Int.getIndex(), 2);

  }

  // tests the updateNext and updatePrev methods
  void testUpdateNextAndPrev(Tester t) {

    // tests functionality of updateNext and updatePrev methods on ANode
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1);
    this.sentinelS1.updateNext(this.sentinelS1);
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1); // updating MtDeque w/ self does nothing

    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1);
    this.sentinelS1.updatePrev(this.sentinelS1);
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1); // same using updatePrev

    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1);
    this.sentinelS1.updateNext(this.node1); // sets sentinel's next to node1
    this.sentinelS1.updatePrev(this.node1); // sets sentinel's prev to node1 -> done
    t.checkExpect(this.sentinelS1.next, this.node1);
    t.checkExpect(this.node1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS1); // updating MtDeque w/ node

    // updating end w/ sentinel does nothing (with updateNext)
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    // ... till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    // ... till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.node4.updateNext(this.sentinelS2); 
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    // ... till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    // ... till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);

    // updating front w/ sentinel does nothing (with updatePrev)
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    // ... till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    // ... till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.node1.updatePrev(this.sentinelS2); 
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    // ... till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    // ... till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);

    // updating front w/ sentinel cuts deque (for updateNext)
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    // ... till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    // ... till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.node1.updateNext(this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);

    // updating end w/ sentinel cuts deque (for updatePrev)
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    // ... till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    // ... till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.node4.updatePrev(this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.sentinelS2);
  }

  // tests the addAtHead method and its helpers
  void testAddAtHead(Tester t) {

    // tests functionality of addAtHead method on example deque
    initDeque();
    // check init elements of deque1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // check init elements of deque2
    t.checkExpect(this.deque2.header, this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    // check init elements of deque3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.nodeE);
    t.checkExpect(this.nodeE.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeE);
    t.checkExpect(this.nodeE.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // mutate
    this.deque1.addAtHead("add front");
    this.deque2.addAtHead("add front");
    this.deque3.addAtHead("add front 1");
    this.deque3.addAtHead("add front 2");
    // check altered state
    t.checkExpect(this.deque1.size(), 1);
    t.checkExpect(this.deque2.size(), 5);
    t.checkExpect(this.deque3.size(), 7);
    // also check elements (structure) of deque1
    Node<String> addAtHeadDeque1 = (Node<String>)this.deque1.header.next;
    t.checkExpect(this.deque1.header.next, addAtHeadDeque1);
    t.checkExpect(addAtHeadDeque1.next, this.deque1.header);
    t.checkExpect(this.deque1.header.prev, addAtHeadDeque1);
    t.checkExpect(addAtHeadDeque1.prev, this.deque1.header);
    // also check elements (structure) of deque2
    Node<String> addAtHeadDeque2 = (Node<String>)this.deque2.header.next;
    t.checkExpect(this.deque2.header, this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, addAtHeadDeque2);
    t.checkExpect(addAtHeadDeque2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2); // and so on till node 4
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3); // and so on till node 1
    t.checkExpect(this.node1.prev, addAtHeadDeque2);
    t.checkExpect(addAtHeadDeque2.prev, this.sentinelS2);

    // tests the functionality of the addNodeFront method on Sentinel
    // adding to MtDeque
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1);
    this.sentinelS1.addNodeFront(this.node1);
    t.checkExpect(this.sentinelS1.next, this.node1);
    t.checkExpect(this.node1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS1);
    // adding to Deque
    initDeque(); // reset data
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.node3Int);
    t.checkExpect(this.node3Int.next, this.node4Int);
    t.checkExpect(this.node4Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node4Int);
    t.checkExpect(this.node4Int.prev, this.node3Int);
    t.checkExpect(this.node3Int.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);
    this.sentinelS4.addNodeFront(this.node0Int);
    t.checkExpect(this.sentinelS4.next, this.node0Int);
    t.checkExpect(this.node0Int.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.node3Int);
    t.checkExpect(this.node3Int.next, this.node4Int);
    t.checkExpect(this.node4Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node4Int);
    t.checkExpect(this.node4Int.prev, this.node3Int);
    t.checkExpect(this.node3Int.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.node0Int);
    t.checkExpect(this.node0Int.prev, this.sentinelS4);
  }

  // tests the addAtTail method and its helpers
  void testAddAtTail(Tester t) {
    // tests functionality of addAtHead method on example deque
    // check initial state
    initDeque();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 5);
    // check init elements of deque1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // check init elements of deque2
    t.checkExpect(this.deque2.header, this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    // check init elements of deque3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.nodeE);
    t.checkExpect(this.nodeE.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeE);
    t.checkExpect(this.nodeE.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // mutate
    this.deque1.addAtTail("add tail");
    this.deque2.addAtTail("add tail 1");
    this.deque2.addAtTail("add tail 2");
    this.deque3.addAtTail("add tail");
    // check altered state
    t.checkExpect(this.deque1.size(), 1);
    t.checkExpect(this.deque2.size(), 6);
    t.checkExpect(this.deque3.size(), 6);
    // also check elements (structure) of deque1
    Node<String> addAtTailDeque1 = (Node<String>)this.deque1.header.prev;
    t.checkExpect(this.deque1.header.next, addAtTailDeque1);
    t.checkExpect(addAtTailDeque1.next, this.deque1.header);
    t.checkExpect(this.deque1.header.prev, addAtTailDeque1);
    t.checkExpect(addAtTailDeque1.prev, this.deque1.header);
    // also check elements (structure) of deque2
    Node<String> addAtTail1Deque2 = (Node<String>)this.deque2.header.prev.prev;
    Node<String> addAtTail2Deque2 = (Node<String>)this.deque2.header.prev;
    t.checkExpect(this.deque2.header, this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node1); // next
    t.checkExpect(this.node1.next, this.node2); // and so on till node 4
    t.checkExpect(this.node4.next, addAtTail1Deque2);
    t.checkExpect(addAtTail1Deque2.next, addAtTail2Deque2);
    t.checkExpect(addAtTail2Deque2.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, addAtTail2Deque2); // prev
    t.checkExpect(addAtTail2Deque2.prev, addAtTail1Deque2);
    t.checkExpect(addAtTail1Deque2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3); // and so on till node 1
    t.checkExpect(this.node1.prev, this.sentinelS2);

    // tests the functionality of the addNodeEnd method on Sentinel
    // adding to MtDeque
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.sentinelS1);
    this.sentinelS1.addNodeEnd(this.node1);
    t.checkExpect(this.sentinelS1.next, this.node1);
    t.checkExpect(this.node1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS1);
    // adding to Deque
    initDeque(); // reset data
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.node3Int);
    t.checkExpect(this.node3Int.next, this.node4Int);
    t.checkExpect(this.node4Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node4Int);
    t.checkExpect(this.node4Int.prev, this.node3Int);
    t.checkExpect(this.node3Int.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);
    this.sentinelS4.addNodeEnd(this.node0Int);
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.node3Int);
    t.checkExpect(this.node3Int.next, this.node4Int);
    t.checkExpect(this.node4Int.next, this.node0Int);
    t.checkExpect(this.node0Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node0Int);
    t.checkExpect(this.node0Int.prev, this.node4Int);
    t.checkExpect(this.node4Int.prev, this.node3Int);
    t.checkExpect(this.node3Int.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);
  }

  // tests the removeFromHead method and its helpers
  void testRemoveFromHead(Tester t) {

    // tests functionality of removeFromHead method on example deque
    // check initial state
    initDeque();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque3.size(), 5);
    // check init elements of deque1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // check init elements of deque2
    t.checkExpect(this.deque2.header, this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    // check init elements of deque3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.nodeE);
    t.checkExpect(this.nodeE.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeE);
    t.checkExpect(this.nodeE.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // check RuntimeException
    t.checkException(new RuntimeException("cannot remove node from an empty list"),
        this.deque1, "removeFromHead");
    // mutate + check returns
    t.checkExpect(this.deque2.removeFromHead(), "abc");
    t.checkExpect(this.deque3.removeFromHead(), "A");
    t.checkExpect(this.deque3.removeFromHead(), "B");
    // check altered state
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque2.size(), 3);
    t.checkExpect(this.deque3.size(), 3);
    // also check elements (structure) in deque 1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // also check elements (structure) in deque 2
    t.checkExpect(this.deque2.header, this.sentinelS2);
    t.checkExpect(this.sentinelS2.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.sentinelS2);
    // also check elements (structure) in deque 3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.nodeE);
    t.checkExpect(this.nodeE.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeE);
    t.checkExpect(this.nodeE.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.sentinelS3);

    // tests the functionality of the removeNodeHead method on Sentinel
    // runtime exception because MtDeque
    initDeque(); // reset data
    t.checkException(new RuntimeException("cannot remove node from an empty list"),
        this.sentinelS1, "removeNodeHead");
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.sentinelS2.removeNodeHead();
    t.checkExpect(this.sentinelS2.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.sentinelS2);

    // tests the functionality of the removeNodeHead method on Node
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    t.checkExpect(this.node3.removeNodeHead(), "cde"); // remove @ 3 assumes 3 is the head
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node4);  
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    t.checkExpect(this.node1.removeNodeHead(), "abc"); // remove @ 1 assumes 1 is the head
    t.checkExpect(this.sentinelS2.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.sentinelS2);
  }

  // tests the removeFromTail method and its helpers 
  void testRemoveFromTail(Tester t) {

    // tests functionality of removeFromTail method on example deque
    // check initial state
    initDeque();
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque3.size(), 5);
    t.checkExpect(this.deque4.size(), 4);
    // check init elements of deque1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // check init elements of deque3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.nodeE);
    t.checkExpect(this.nodeE.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeE);
    t.checkExpect(this.nodeE.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // check init elements of deque4
    t.checkExpect(this.deque4.header, this.sentinelS4);
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.node3Int);
    t.checkExpect(this.node3Int.next, this.node4Int);
    t.checkExpect(this.node4Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node4Int);
    t.checkExpect(this.node4Int.prev, this.node3Int);
    t.checkExpect(this.node3Int.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);
    // check RuntimeException
    t.checkException(new RuntimeException("cannot remove node from an empty list"),
        this.deque1, "removeFromTail");
    // mutate + check returns
    t.checkExpect(this.deque3.removeFromTail(), "E");
    t.checkExpect(this.deque4.removeFromTail(), 4);
    t.checkExpect(this.deque4.removeFromTail(), 3);
    // check altered state
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque3.size(), 4);
    t.checkExpect(this.deque4.size(), 2);
    // also check elements (structure) in deque 1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // also check elements (structure) in deque 3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // also check elements (structure) in deque 4
    t.checkExpect(this.deque4.header, this.sentinelS4);
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);

    // tests the functionality of the removeNodeTail method on Sentinel
    // runtime exception because MtDeque
    initDeque(); // reset data
    t.checkException(new RuntimeException("cannot remove node from an empty list"),
        this.sentinelS1, "removeNodeTail");
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.sentinelS2.removeNodeTail();
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);

    // tests the functionality of the removeNodeTail method on Node
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    t.checkExpect(this.node3.removeNodeTail(), "cde"); // remove @ 3 assumes 3 is the tail
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node4);  
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    t.checkExpect(this.node1.removeNodeTail(), "abc"); // remove @ 1 assumes 1 is the tail
    t.checkExpect(this.sentinelS2.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.sentinelS2);
  }

  // tests the find method and its helpers
  void testFind(Tester t) {

    // tests functionality of find method on Deque
    initDeque(); // reset data
    t.checkExpect(this.deque1.find(new LengthThree()), this.deque1.header);
    t.checkExpect(this.deque1.find(new StartsWithC()), this.deque1.header);
    t.checkExpect(this.deque2.find(new LengthThree()), this.deque2.header.next);
    t.checkExpect(this.deque2.find(new StartsWithC()), this.deque2.header.prev.prev);
    t.checkExpect(this.deque3.find(new LengthThree()), this.deque3.header);
    t.checkExpect(this.deque3.find(new StartsWithC()), this.deque3.header.next.next.next);
    t.checkExpect(this.deque4.find(new IsEven()), this.deque4.header.next.next);
    t.checkExpect(this.deque4.find(new IsZero()), this.deque4.header);

    // tests functionality of find method on Sentinel
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.findNode(this.lengthThree), this.sentinelS1);
    t.checkExpect(this.sentinelS1.findNode(this.startsWithC), this.sentinelS1);
    t.checkExpect(this.sentinelS2.findNode(this.lengthThree), this.sentinelS2);
    t.checkExpect(this.sentinelS2.findNode(this.startsWithC), this.sentinelS2);
    t.checkExpect(this.sentinelS3.findNode(this.lengthThree), this.sentinelS3);
    t.checkExpect(this.sentinelS3.findNode(this.startsWithC), this.sentinelS3);
    t.checkExpect(this.sentinelS4.findNode(this.isEven), this.sentinelS4);
    t.checkExpect(this.sentinelS4.findNode(this.isZero), this.sentinelS4);

    // tests functionality of find method on Node
    initDeque(); // reset data
    t.checkExpect(this.node1.findNode(this.lengthThree), this.node1);
    t.checkExpect(this.node1.findNode(this.startsWithC), this.node3);
    t.checkExpect(this.node3.findNode(this.lengthThree), this.node3);
    t.checkExpect(this.node3.findNode(this.startsWithC), this.node3);
    t.checkExpect(this.node2Int.findNode(this.isEven), this.node2Int);
    t.checkExpect(this.node2Int.findNode(this.isZero), this.sentinelS4);
  }

  // tests the removeNode method and its helpers
  void testRemoveNode(Tester t) {

    // tests functionality of removeNode method on example Deque
    initDeque(); // reset data
    // check initial state
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque3.size(), 5);
    t.checkExpect(this.deque4.size(), 4);
    // check init elements of deque1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // check init elements of deque3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.nodeE);
    t.checkExpect(this.nodeE.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeE);
    t.checkExpect(this.nodeE.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // check init elements of deque4
    t.checkExpect(this.deque4.header, this.sentinelS4);
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.node3Int);
    t.checkExpect(this.node3Int.next, this.node4Int);
    t.checkExpect(this.node4Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node4Int);
    t.checkExpect(this.node4Int.prev, this.node3Int);
    t.checkExpect(this.node3Int.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);
    // mutate + check returns
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    this.deque1.removeNode(this.sentinelS1); // try to remove header
    this.deque1.removeNode(this.node1);      // try to remove node not in deque
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1); // no changes to empty deque
    t.checkExpect(this.deque3.removeFromTail(), "E");
    t.checkExpect(this.deque4.removeFromTail(), 4);
    t.checkExpect(this.deque4.removeFromTail(), 3);
    // check altered state
    t.checkExpect(this.deque1.size(), 0);
    t.checkExpect(this.deque3.size(), 4);
    t.checkExpect(this.deque4.size(), 2);
    // also check elements (structure) in deque 1
    t.checkExpect(this.deque1.header, this.sentinelS1);
    t.checkExpect(this.deque1.header.next, this.sentinelS1);
    t.checkExpect(this.deque1.header.prev, this.sentinelS1);
    // also check elements (structure) in deque 3
    t.checkExpect(this.deque3.header, this.sentinelS3);
    t.checkExpect(this.sentinelS3.next, this.nodeA);
    t.checkExpect(this.nodeA.next, this.nodeB);
    t.checkExpect(this.nodeB.next, this.nodeC);
    t.checkExpect(this.nodeC.next, this.nodeD);
    t.checkExpect(this.nodeD.next, this.sentinelS3);
    t.checkExpect(this.sentinelS3.prev, this.nodeD);
    t.checkExpect(this.nodeD.prev, this.nodeC);
    t.checkExpect(this.nodeC.prev, this.nodeB);
    t.checkExpect(this.nodeB.prev, this.nodeA);
    t.checkExpect(this.nodeA.prev, this.sentinelS3);
    // also check elements (structure) in deque 4
    t.checkExpect(this.deque4.header, this.sentinelS4);
    t.checkExpect(this.sentinelS4.next, this.node1Int);
    t.checkExpect(this.node1Int.next, this.node2Int);
    t.checkExpect(this.node2Int.next, this.sentinelS4);
    t.checkExpect(this.sentinelS4.prev, this.node2Int);
    t.checkExpect(this.node2Int.prev, this.node1Int);
    t.checkExpect(this.node1Int.prev, this.sentinelS4);

    // tests functionality of removeNode method on Sentinel
    // MtDeque, when applied on Sentinel it must terminate -> no change
    initDeque(); // reset data
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    this.sentinelS1.removeNode(this.node2);
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    t.checkExpect(this.sentinelS1.next, this.sentinelS1);
    // MtDeque, when applied on Sentinel it must terminate -> no change
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.sentinelS2.removeNode(this.node2);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);

    // tests the functionality of removeNode on Node
    // Deque that doesn't contain given element -> no change
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.node1.removeNode(this.nodeE);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    // Deque that contains given element -> change
    initDeque(); // reset data
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node2);
    t.checkExpect(this.node2.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node2);
    t.checkExpect(this.node2.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
    this.node1.removeNode(this.node2);
    t.checkExpect(this.sentinelS2.next, this.node1);
    t.checkExpect(this.node1.next, this.node3);
    t.checkExpect(this.node3.next, this.node4);
    t.checkExpect(this.node4.next, this.sentinelS2);
    t.checkExpect(this.sentinelS2.prev, this.node4);
    t.checkExpect(this.node4.prev, this.node3);
    t.checkExpect(this.node3.prev, this.node1);
    t.checkExpect(this.node1.prev, this.sentinelS2);
  }

  // tests the sameANode method and its helpers
  void testSameANode(Tester t) {
    initDeque(); // reset data (only once because we are not mutating)

    // tests functionality of sameANode method on ANode
    t.checkExpect(this.deque1.header.sameANode(this.deque1.header), false);
    t.checkExpect(this.sentinelS1.sameANode(this.sentinelS2), false);
    t.checkExpect(this.sentinelS1.sameANode(this.node2), false);
    t.checkExpect(this.sentinelS4.sameANode(this.sentinelS4), false);
    t.checkExpect(this.sentinelS4.sameANode(this.node1Int), false);
    t.checkExpect(this.node1.sameANode(this.node1), true);
    t.checkExpect(this.node1.sameANode(this.node2), false);
    t.checkExpect(this.node1.sameANode(this.nodeE), false);
    t.checkExpect(this.node3Int.sameANode(this.node3Int), true);
    t.checkExpect(this.node3Int.sameANode(this.node2Int), false);
    t.checkExpect(this.node3Int.sameANode(this.sentinelS4), false);

    // tests the functionality of the sameSentinel method on Sentinel
    t.checkExpect(this.sentinelS1.sameSentinel(this.sentinelS1), true);
    t.checkExpect(this.sentinelS1.sameSentinel(this.node4), false);
    t.checkExpect(this.sentinelS1.sameSentinel(this.sentinelS2), false);
    t.checkExpect(this.sentinelS4.sameSentinel(this.sentinelS4), true);
    t.checkExpect(this.sentinelS4.sameSentinel(this.node3Int), false);

    // tests the functionality of the sameSentinel method on Node
    t.checkExpect(this.node1.sameSentinel(this.node1), false);
    t.checkExpect(this.node1.sameSentinel(this.sentinelS1), false);
    t.checkExpect(this.node1.sameSentinel(this.sentinelS2), false);

    // tests the functionality of the sameNode method on Sentinel
    initDeque();
    t.checkExpect(this.sentinelS1.sameNode((Node<String>)this.node1), false);
    t.checkExpect(this.sentinelS1.sameNode((Node<String>)this.node4), false);

    // tests the functionality of the sameNode method on Node
    t.checkExpect(this.node1.sameNode((Node<String>)this.node1), true);
    t.checkExpect(this.node1.sameNode((Node<String>)this.node4), false);
    t.checkExpect(this.node3Int.sameNode((Node<Integer>)this.node3Int), true);
    t.checkExpect(this.node3Int.sameNode((Node<Integer>)this.node2Int), false);
  }
}

