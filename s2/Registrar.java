import tester.*;

// represents generic for functions with signature [A -> R]
interface IFunc<A, R> {
  R apply(A arg);
}

//generic for function-objects with signature [A1, A2 -> R]
interface IFunc2<A1, A2, R> {
  R apply(A1 arg1, A2 arg2);
}

// represents generic functions [X -> Boolean]
interface IPred<X> extends IFunc<X, Boolean> {
}

// checks to see if students has the same id
class SameStudentID implements IPred<Student> {
  int id;

  // takes in id to check against each element of the list
  SameStudentID(int id) {
    this.id = id;
  }

  // checks whether ids are equal
  public Boolean apply(Student s) {
    return this.id == s.id;
  }
}

// checks if student with id is enrolled in any of the courses
class SameStudentIDCourses implements IPred<Course> {
  int id;

  // takes in ID to check against each student list of courses
  SameStudentIDCourses(int id) {
    this.id = id;
  }

  // checks whether id appears in any student lists of courses
  public Boolean apply(Course c) {
    return new Ormap<Student>(new SameStudentID(this.id)).apply(c.students);
  }
}

// counts how many times a student appears in courses
class DejavuCounter implements IFunc<Instructor, Integer> {
  int id;

  // takes in ID to check for instances of the student in the instructor's list of courses
  DejavuCounter(int id) {
    this.id = id;
  }

  // returns number of times student appears in courses
  public Integer apply(Instructor i) {
    return new DejavuCounterHelperCourse(this.id).apply(i.courses);
  }
}

// counts when a student appears in the course
class DejavuCounterHelperCourse implements IListVisitor<Course, Integer> {
  int id;

  // takes in ID to check for instances of the student in the courses
  DejavuCounterHelperCourse(int id) {
    this.id = id;
  }

  // from IListVisitor interface
  // applies the function object to the empty list
  public Integer visitMt(MtList<Course> mt) {
    return 0;
  }

  // applies the function object to the non-empty list
  public Integer visitCons(ConsList<Course> cons) {
    return new Utils().numAppearCourse(cons, this.id);
  }

  // from IFunc interface
  // applies the function object to the given list
  public Integer apply(IList<Course> l) {
    return l.accept(this);
  }
}

// counts how many times a student with a certain id appears given number of times so far
class StudentAppearsCourse implements IFunc2<Course, Integer, Integer> {
  int id;

  // to construct a StudentAppearCourse
  StudentAppearsCourse(int id) {
    this.id = id;
  }

  // a student can appear at most once in a course
  // if a student appears in a course, increment total times
  // student has appeared in instuctors courses by 1
  public Integer apply(Course c, Integer numAppearancesSoFar) {
    if (new Ormap<Student>(new SameStudentID(this.id)).apply(c.students)) {
      return numAppearancesSoFar + 1;
    }
    return numAppearancesSoFar;
  }

}

// to represent a course
class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  // course constructor
  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.students = new MtList<Student>();
    prof.courses = prof.courses.add(this);
  }

  // EFFECT: adds given student to this course
  void addStudent(Student s) {
    // throws exception if student tries to enroll in a class they're already in
    if (new Ormap<Student>(new SameStudentID(s.id)).apply(this.students)) {
      throw new RuntimeException("student is already enrolled in this course");
    }
    this.students = this.students.add(s);
  }
}

// to represent an instructor
class Instructor {
  String name;
  IList<Course> courses;

  // instructor constructor
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  // determines whether given student is in more than one of this instructors courses
  boolean dejavu(Student c) {
    return new DejavuCounter(c.id).apply(this) > 1;
  }

}

// to represent a student
class Student {
  String name;
  int id;
  IList<Course> courses;

  // student constructor
  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  // EFFECT: enrolls (adds) a student in the given course
  void enroll(Course c) {
    // adds student to the course
    c.addStudent(this);
    // updates courses the student is taking
    this.courses = this.courses.add(c);
  }

  // determines whether given student is in any of the same classes as this student
  boolean classmates(Student c) {
    // map across this students courses and see if any contains the other student's id
    return new Ormap<Course>(new SameStudentIDCourses(c.id)).apply(this.courses);
  }
}

//an IListVisitor is a function over IList<T>s
interface IListVisitor<T, R> extends IFunc<IList<T>, R> {

  // visits empty case
  R visitMt(MtList<T> mt);

  // visits cons case
  R visitCons(ConsList<T> cons);

}

// Ormaps over a list
class Ormap<T> implements IListVisitor<T, Boolean> {
  IPred<T> f;

  // to construct a OrMap<T>
  Ormap(IPred<T> f) {
    this.f = f;
  }

  // from IListVisitor interface
  // for empty case returns false
  public Boolean visitMt(MtList<T> mt) {
    return false;
  }

  // for cons case, returns the predicate resultant of first or rest
  public Boolean visitCons(ConsList<T> cons) {
    return this.f.apply(cons.first) || this.apply(cons.rest);
  }

  // from IFunc
  // applies l to this
  public Boolean apply(IList<T> l) {
    return l.accept(this);
  }
}

// represents a List of T
interface IList<T> {

  // to return the result of applying IListVisitor to this list
  <R> R accept(IListVisitor<T, R> visitor);

  // adds an element to the list
  IList<T> add(T t);

  // folds right from base case
  <U> U foldr(IFunc2<T, U, U> func, U base);

}

// represents an empty list of t
class MtList<T> implements IList<T> {

  // directs to given visitor
  public <R> R accept(IListVisitor<T, R> visitor) {
    return visitor.visitMt(this);
  }

  public IList<T> add(T t) {
    return new ConsList<T>(t, this);
  }

  // returns base case
  public <U> U foldr(IFunc2<T, U, U> func, U base) {
    return base;
  }
}

// represents a nonempty list of t
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  // cons list constructor
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // directs to given visitor
  public <R> R accept(IListVisitor<T, R> visitor) {
    return visitor.visitCons(this);
  }

  // adds on an element to the end of a list
  public IList<T> add(T t) {
    return new ConsList<T>(t, this);
  }

  // folds right recursively from base
  public <U> U foldr(IFunc2<T, U, U> func, U base) {
    return func.apply(this.first,
        this.rest.foldr(func, base));
  }
}

// utilities
class Utils {

  // uses foldr to find number of times a student appears in a list of courses
  Integer numAppearCourse(IList<Course> l, int id) {
    return l.foldr(new StudentAppearsCourse(id), 0);
  }

}

// Examples of classes and tests for methods
class ExamplesRegistrar {
  // examples and constant data
  Course nullCourse;
  Course research;
  Course math;
  Course science;
  Course history;
  Course english;
  Instructor prof0;
  Instructor prof1;
  Instructor prof2;
  Student student0;
  Student student1;
  Student student2;
  Student student3;
  Student student4;
  SameStudentID sameSID0;
  SameStudentID sameSID1;
  SameStudentID sameSID2;
  SameStudentID sameSID3;
  SameStudentID sameSID4;
  SameStudentIDCourses sameSIDC0;
  SameStudentIDCourses sameSIDC1;
  SameStudentIDCourses sameSIDC2;
  SameStudentIDCourses sameSIDC3;
  SameStudentIDCourses sameSIDC4;
  DejavuCounter dejavuC0;
  DejavuCounter dejavuC1;
  DejavuCounter dejavuC2;
  DejavuCounter dejavuC3;
  DejavuCounter dejavuC4;
  DejavuCounterHelperCourse dejavuCHC0;
  DejavuCounterHelperCourse dejavuCHC1;
  DejavuCounterHelperCourse dejavuCHC2;
  DejavuCounterHelperCourse dejavuCHC3;
  DejavuCounterHelperCourse dejavuCHC4;
  StudentAppearsCourse studentAC0;  
  StudentAppearsCourse studentAC1;  
  StudentAppearsCourse studentAC2;  
  StudentAppearsCourse studentAC3;  
  StudentAppearsCourse studentAC4; 
  // lists of courses
  IList<Course> mtCourses = new MtList<Course>();
  IList<Course> st0Courses = this.mtCourses;
  IList<Course> st1Courses = new ConsList<Course>(this.english, 
      new ConsList<Course>(this.history, 
          new ConsList<Course>(this.research, this.mtCourses)));
  IList<Course> st2Courses = new ConsList<Course>(this.english, 
      new ConsList<Course>(this.math, this.mtCourses));
  IList<Course> st3Courses = new ConsList<Course>(this.math, 
      new ConsList<Course>(this.science, this.mtCourses));
  IList<Course> st4Courses = new ConsList<Course>(this.english, 
      new ConsList<Course>(this.math,
          new ConsList<Course>(this.history,
              new ConsList<Course>(this.science,
                  new ConsList<Course>(this.research, this.mtCourses)))));
  // lists of students
  IList<Student> mtStudents = new MtList<Student>();
  IList<Student> englishStudents = new ConsList<Student>(this.student1, 
      new ConsList<Student>(this.student2, 
          new ConsList<Student>(this.student4, this.mtStudents)));
  IList<Student> mathStudents = new ConsList<Student>(this.student2, 
      new ConsList<Student>(this.student3, 
          new ConsList<Student>(this.student4, this.mtStudents)));
  IList<Student> scienceStudents = new ConsList<Student>(this.student3, 
      new ConsList<Student>(this.student4, this.mtStudents));
  IList<Student> historyStudents = new ConsList<Student>(this.student1, 
      new ConsList<Student>(this.student4, this.mtStudents));
  IList<Student> researchStudents = new ConsList<Student>(this.student1, 
      new ConsList<Student>(this.student4, this.mtStudents));
  
  // initial data state
  void initData() {
    // Students examples
    this.student0 = new Student("Zero", 0);
    this.student1 = new Student("One", 1);
    this.student2 = new Student("Two", 2);
    this.student3 = new Student("Three", 3);
    this.student4 = new Student("Four", 4);
    // Instructor examples
    this.prof0 = new Instructor("Dr. Zero");
    this.prof1 = new Instructor("Dr. One");
    this.prof2 = new Instructor("Dr. Two");
    // Courses examples
    this.nullCourse = new Course("", this.prof0);
    this.research = new Course("Research", this.prof1);
    this.math = new Course("Math", this.prof1);
    this.science = new Course("Science", this.prof1);
    this.history = new Course("History", this.prof2);
    this.english = new Course("English", this.prof2);
    // SameStudentID examples
    this.sameSID0 = new SameStudentID(0);
    this.sameSID1 = new SameStudentID(1);
    this.sameSID2 = new SameStudentID(2);
    this.sameSID3 = new SameStudentID(3);
    this.sameSID4 = new SameStudentID(4);
    // SameStudentIDCourses examples
    this.sameSIDC0 = new SameStudentIDCourses(0);
    this.sameSIDC1 = new SameStudentIDCourses(1);
    this.sameSIDC2 = new SameStudentIDCourses(2);
    this.sameSIDC3 = new SameStudentIDCourses(3);
    this.sameSIDC4 = new SameStudentIDCourses(4);
    // DejavuCounter examples
    this.dejavuC0 = new DejavuCounter(0);
    this.dejavuC1 = new DejavuCounter(1);
    this.dejavuC2 = new DejavuCounter(2);
    this.dejavuC3 = new DejavuCounter(3);
    this.dejavuC4 = new DejavuCounter(4);
    // DejavuCounterHelperCourse examples
    this.dejavuCHC0 = new DejavuCounterHelperCourse(0);
    this.dejavuCHC1 = new DejavuCounterHelperCourse(1);
    this.dejavuCHC2 = new DejavuCounterHelperCourse(2);
    this.dejavuCHC3 = new DejavuCounterHelperCourse(3);
    this.dejavuCHC4 = new DejavuCounterHelperCourse(4);
    // StudentAppearsCourse examples
    this.studentAC0 = new StudentAppearsCourse(0);  
    this.studentAC1 = new StudentAppearsCourse(1);  
    this.studentAC2 = new StudentAppearsCourse(2);  
    this.studentAC3 = new StudentAppearsCourse(3);  
    this.studentAC4 = new StudentAppearsCourse(4);
    // Adding students and classes
    this.student1.enroll(this.english);
    this.student2.enroll(this.english);
    this.student4.enroll(this.english);
    this.student2.enroll(this.math);
    this.student3.enroll(this.math);
    this.student4.enroll(this.math);
    this.student3.enroll(this.science);
    this.student4.enroll(this.science);
    this.student1.enroll(this.history);
    this.student4.enroll(this.history);
    this.student1.enroll(this.research);
    this.student4.enroll(this.research);
    this.prof1.courses = new ConsList<Course>(this.research,
        new ConsList<Course>(this.math, new ConsList<Course>(this.science, this.mtCourses)));
    this.prof2.courses = new ConsList<Course>(this.history,
        new ConsList<Course>(this.english, this.mtCourses));
  }

  // tests in the Student class
  void testStudentMethods(Tester t) {
    // this.enroll(Course c) tests
    initData();
    this.student0.enroll(this.english);
    this.student0.enroll(this.history);
    this.student0.enroll(this.research);
    t.checkExpect(this.student0.classmates(this.student1), true);
    this.student0.name = "One";
    this.student0.id = 1;
    t.checkExpect(this.student0, this.student1); // check equality
    initData();
    this.student0.enroll(this.english);
    this.student0.enroll(this.math);
    t.checkExpect(this.student0.classmates(this.student2), true);
    this.student0.name = "Two";
    this.student0.id = 2;
    t.checkExpect(this.student0, this.student2); // check equality
    initData();
    this.student0.enroll(this.math);
    this.student0.enroll(this.science);
    t.checkExpect(this.student0.classmates(this.student3), true);
    this.student0.name = "Three";
    this.student0.id = 3;
    t.checkExpect(this.student0, this.student3); // check equality
    initData();
    this.student0.enroll(this.english);
    this.student0.enroll(this.math);
    this.student0.enroll(this.science);
    this.student0.enroll(this.history);
    this.student0.enroll(this.research);
    t.checkExpect(this.student0.classmates(this.student4), true);
    this.student0.name = "Four";
    this.student0.id = 4;
    t.checkExpect(this.student0, this.student4); // check equality

    // this.enroll(Course c) Runtime Exception errors
    initData();
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student1, "enroll", this.english);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student1, "enroll", this.history);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student1, "enroll", this.research);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student2, "enroll", this.english);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student2, "enroll", this.math);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student3, "enroll", this.math);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student3, "enroll", this.science);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student4, "enroll", this.english);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student4, "enroll", this.math);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student4, "enroll", this.science);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student4, "enroll", this.history);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.student4, "enroll", this.research);

    // this.classmates(Student c) tests
    initData();
    t.checkExpect(this.student0.classmates(this.student0), false); // false because empty list
    t.checkExpect(this.student0.classmates(this.student1), false);
    t.checkExpect(this.student0.classmates(this.student2), false);
    t.checkExpect(this.student0.classmates(this.student3), false);
    t.checkExpect(this.student0.classmates(this.student4), false);
    t.checkExpect(this.student1.classmates(this.student0), false);
    t.checkExpect(this.student1.classmates(this.student1), true);
    t.checkExpect(this.student1.classmates(this.student2), true);
    t.checkExpect(this.student1.classmates(this.student3), false);
    t.checkExpect(this.student1.classmates(this.student4), true);
    t.checkExpect(this.student2.classmates(this.student0), false);
    t.checkExpect(this.student2.classmates(this.student1), true);
    t.checkExpect(this.student2.classmates(this.student2), true);
    t.checkExpect(this.student2.classmates(this.student3), true);
    t.checkExpect(this.student2.classmates(this.student4), true);    
    t.checkExpect(this.student3.classmates(this.student0), false);
    t.checkExpect(this.student3.classmates(this.student1), false);
    t.checkExpect(this.student3.classmates(this.student2), true);
    t.checkExpect(this.student3.classmates(this.student3), true);
    t.checkExpect(this.student3.classmates(this.student4), true);    
    t.checkExpect(this.student4.classmates(this.student0), false);
    t.checkExpect(this.student4.classmates(this.student1), true);
    t.checkExpect(this.student4.classmates(this.student2), true);
    t.checkExpect(this.student4.classmates(this.student3), true);
    t.checkExpect(this.student4.classmates(this.student4), true);    
  }

  // tests in the Instructor class
  void testInstructorMethods(Tester t) {
    // this.dejavu(Student c) tests
    initData();
    t.checkExpect(this.prof0.dejavu(this.student0), false);
    t.checkExpect(this.prof0.dejavu(this.student1), false);
    t.checkExpect(this.prof0.dejavu(this.student2), false);
    t.checkExpect(this.prof0.dejavu(this.student3), false);
    t.checkExpect(this.prof0.dejavu(this.student4), false);
    t.checkExpect(this.prof1.dejavu(this.student0), false);
    t.checkExpect(this.prof1.dejavu(this.student1), false);
    t.checkExpect(this.prof1.dejavu(this.student2), false);
    t.checkExpect(this.prof1.dejavu(this.student3), true);
    t.checkExpect(this.prof1.dejavu(this.student4), true);
    t.checkExpect(this.prof2.dejavu(this.student0), false);
    t.checkExpect(this.prof2.dejavu(this.student1), true);
    t.checkExpect(this.prof2.dejavu(this.student2), false);
    t.checkExpect(this.prof2.dejavu(this.student3), false);
    t.checkExpect(this.prof2.dejavu(this.student4), true);
  }

  // tests in the Course class
  void testCourseMethods(Tester t) {
    // this.addStudents(Student c) tests
    initData();
    this.nullCourse.addStudent(this.student1);
    this.nullCourse.addStudent(this.student2);
    this.nullCourse.addStudent(this.student4);
    this.nullCourse.students.equals(this.englishStudents);
    this.nullCourse.name = "English";
    this.nullCourse.prof = this.prof2;
    t.checkExpect(this.nullCourse, this.english);
    initData();
    this.nullCourse.addStudent(this.student2);
    this.nullCourse.addStudent(this.student3);
    this.nullCourse.addStudent(this.student4);
    this.nullCourse.students.equals(this.mathStudents);
    this.nullCourse.name = "Math";
    this.nullCourse.prof = this.prof1;
    t.checkExpect(this.nullCourse, this.math);
    initData();
    this.nullCourse.addStudent(this.student3);
    this.nullCourse.addStudent(this.student4);
    this.nullCourse.students.equals(this.scienceStudents);
    this.nullCourse.name = "Science";
    this.nullCourse.prof = this.prof1;
    t.checkExpect(this.nullCourse, this.science);
    initData();
    this.nullCourse.addStudent(this.student1);
    this.nullCourse.addStudent(this.student4);
    this.nullCourse.students.equals(this.historyStudents);
    this.nullCourse.name = "History";
    this.nullCourse.prof = this.prof2;
    t.checkExpect(this.nullCourse, this.history);
    initData();
    this.nullCourse.addStudent(this.student1);
    this.nullCourse.addStudent(this.student4);
    this.nullCourse.students.equals(this.researchStudents);
    this.nullCourse.name = "Research";
    this.nullCourse.prof = this.prof1;
    t.checkExpect(this.nullCourse, this.research);

    // runtime exception errors
    initData();
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.english, "addStudent", this.student1);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.english, "addStudent", this.student2);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.english, "addStudent", this.student4);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.math, "addStudent", this.student2);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.math, "addStudent", this.student3);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.math, "addStudent", this.student4);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.science, "addStudent", this.student3);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.science, "addStudent", this.student4);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.history, "addStudent", this.student1);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.history, "addStudent", this.student4);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.research, "addStudent", this.student1);
    t.checkException(new RuntimeException("student is already enrolled in this course"), 
        this.research, "addStudent", this.student4);
  }

  // tests in the SameStudentID class
  void testSameStudentID(Tester t) {
    // this.apply(Student c) tests
    this.initData();
    t.checkExpect(this.sameSID0.apply(this.student0), true);
    t.checkExpect(this.sameSID0.apply(this.student1), false);
    t.checkExpect(this.sameSID0.apply(this.student2), false);
    t.checkExpect(this.sameSID0.apply(this.student3), false);
    t.checkExpect(this.sameSID0.apply(this.student4), false);
    t.checkExpect(this.sameSID1.apply(this.student0), false);
    t.checkExpect(this.sameSID1.apply(this.student1), true);
    t.checkExpect(this.sameSID1.apply(this.student2), false);
    t.checkExpect(this.sameSID1.apply(this.student3), false);
    t.checkExpect(this.sameSID1.apply(this.student4), false);
    t.checkExpect(this.sameSID2.apply(this.student0), false);
    t.checkExpect(this.sameSID2.apply(this.student1), false);
    t.checkExpect(this.sameSID2.apply(this.student2), true);
    t.checkExpect(this.sameSID2.apply(this.student3), false);
    t.checkExpect(this.sameSID2.apply(this.student4), false);
    t.checkExpect(this.sameSID3.apply(this.student0), false);
    t.checkExpect(this.sameSID3.apply(this.student1), false);
    t.checkExpect(this.sameSID3.apply(this.student2), false);
    t.checkExpect(this.sameSID3.apply(this.student3), true);
    t.checkExpect(this.sameSID3.apply(this.student4), false);
    t.checkExpect(this.sameSID4.apply(this.student0), false);
    t.checkExpect(this.sameSID4.apply(this.student1), false);
    t.checkExpect(this.sameSID4.apply(this.student2), false);
    t.checkExpect(this.sameSID4.apply(this.student3), false);
    t.checkExpect(this.sameSID4.apply(this.student4), true);
  }

  // tests in the SameStudentIDCourses class
  void testSameStudentIDCourses(Tester t) {
    // this.apply(Course c) tests
    initData();
    t.checkExpect(this.sameSIDC0.apply(this.nullCourse), false);
    t.checkExpect(this.sameSIDC0.apply(this.english), false);
    t.checkExpect(this.sameSIDC0.apply(this.math), false);
    t.checkExpect(this.sameSIDC0.apply(this.science), false);
    t.checkExpect(this.sameSIDC0.apply(this.history), false);
    t.checkExpect(this.sameSIDC0.apply(this.research), false);
    t.checkExpect(this.sameSIDC1.apply(this.nullCourse), false);
    t.checkExpect(this.sameSIDC1.apply(this.english), true);
    t.checkExpect(this.sameSIDC1.apply(this.math), false);
    t.checkExpect(this.sameSIDC1.apply(this.science), false);
    t.checkExpect(this.sameSIDC1.apply(this.history), true);
    t.checkExpect(this.sameSIDC1.apply(this.research), true);
    t.checkExpect(this.sameSIDC2.apply(this.nullCourse), false);
    t.checkExpect(this.sameSIDC2.apply(this.english), true);
    t.checkExpect(this.sameSIDC2.apply(this.math), true);
    t.checkExpect(this.sameSIDC2.apply(this.science), false);
    t.checkExpect(this.sameSIDC2.apply(this.history), false);
    t.checkExpect(this.sameSIDC2.apply(this.research), false);
    t.checkExpect(this.sameSIDC3.apply(this.nullCourse), false);
    t.checkExpect(this.sameSIDC3.apply(this.english), false);
    t.checkExpect(this.sameSIDC3.apply(this.math), true);
    t.checkExpect(this.sameSIDC3.apply(this.science), true);
    t.checkExpect(this.sameSIDC3.apply(this.history), false);
    t.checkExpect(this.sameSIDC3.apply(this.research), false);
    t.checkExpect(this.sameSIDC4.apply(this.nullCourse), false);
    t.checkExpect(this.sameSIDC4.apply(this.english), true);
    t.checkExpect(this.sameSIDC4.apply(this.math), true);
    t.checkExpect(this.sameSIDC4.apply(this.science), true);
    t.checkExpect(this.sameSIDC4.apply(this.history), true);
    t.checkExpect(this.sameSIDC4.apply(this.research), true);
  }

  // tests in the DejavuCounter class
  void testDejavuCounter(Tester t) {
    // this.dejavuCounter(Instructor i) tests
    initData();
    t.checkExpect(this.dejavuC0.apply(this.prof0), 0);
    t.checkExpect(this.dejavuC0.apply(this.prof1), 0);
    t.checkExpect(this.dejavuC0.apply(this.prof2), 0);
    t.checkExpect(this.dejavuC1.apply(this.prof0), 0);
    t.checkExpect(this.dejavuC1.apply(this.prof1), 1);
    t.checkExpect(this.dejavuC1.apply(this.prof2), 2);
    t.checkExpect(this.dejavuC2.apply(this.prof0), 0);
    t.checkExpect(this.dejavuC2.apply(this.prof1), 1);
    t.checkExpect(this.dejavuC2.apply(this.prof2), 1);
    t.checkExpect(this.dejavuC3.apply(this.prof0), 0);
    t.checkExpect(this.dejavuC3.apply(this.prof1), 2);
    t.checkExpect(this.dejavuC3.apply(this.prof2), 0);
    t.checkExpect(this.dejavuC4.apply(this.prof0), 0);
    t.checkExpect(this.dejavuC4.apply(this.prof1), 3);
    t.checkExpect(this.dejavuC4.apply(this.prof2), 2);
  }

  // tests in the DejavuCounterHelperCourse class
  void testDejavuCounterHelperCourse(Tester t) {
    // this.apply(IList<Course>) tests
    initData();
    t.checkExpect(this.dejavuCHC0.apply(this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC0.apply(this.student1.courses), 0);
    t.checkExpect(this.dejavuCHC0.apply(this.student2.courses), 0);
    t.checkExpect(this.dejavuCHC0.apply(this.student3.courses), 0);
    t.checkExpect(this.dejavuCHC0.apply(this.student4.courses), 0);
    t.checkExpect(this.dejavuCHC1.apply(this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC1.apply(this.student1.courses), 3);
    t.checkExpect(this.dejavuCHC1.apply(this.student2.courses), 1);
    t.checkExpect(this.dejavuCHC1.apply(this.student3.courses), 0);
    t.checkExpect(this.dejavuCHC1.apply(this.student4.courses), 3);
    t.checkExpect(this.dejavuCHC2.apply(this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC2.apply(this.student1.courses), 1);
    t.checkExpect(this.dejavuCHC2.apply(this.student2.courses), 2);
    t.checkExpect(this.dejavuCHC2.apply(this.student3.courses), 1);
    t.checkExpect(this.dejavuCHC2.apply(this.student4.courses), 2);
    t.checkExpect(this.dejavuCHC3.apply(this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC3.apply(this.student1.courses), 0);
    t.checkExpect(this.dejavuCHC3.apply(this.student2.courses), 1);
    t.checkExpect(this.dejavuCHC3.apply(this.student3.courses), 2);
    t.checkExpect(this.dejavuCHC3.apply(this.student4.courses), 2);
    t.checkExpect(this.dejavuCHC4.apply(this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC4.apply(this.student1.courses), 3);
    t.checkExpect(this.dejavuCHC4.apply(this.student2.courses), 2);
    t.checkExpect(this.dejavuCHC4.apply(this.student3.courses), 2);
    t.checkExpect(this.dejavuCHC4.apply(this.student4.courses), 5);

    // this.visitMt(MtList<Course> mt) tests
    initData();
    t.checkExpect(this.dejavuCHC0.visitMt((MtList<Course>) this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC1.visitMt((MtList<Course>) this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC2.visitMt((MtList<Course>) this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC3.visitMt((MtList<Course>) this.student0.courses), 0);
    t.checkExpect(this.dejavuCHC4.visitMt((MtList<Course>) this.student0.courses), 0);
    
    // this.visitCons(ConsList<Course> cons) tests
    initData();
    t.checkExpect(this.dejavuCHC0.visitCons((ConsList<Course>) this.student1.courses), 0);
    t.checkExpect(this.dejavuCHC1.visitCons((ConsList<Course>) this.student1.courses), 3);
    t.checkExpect(this.dejavuCHC2.visitCons((ConsList<Course>) this.student1.courses), 1);
    t.checkExpect(this.dejavuCHC3.visitCons((ConsList<Course>) this.student1.courses), 0);
    t.checkExpect(this.dejavuCHC4.visitCons((ConsList<Course>) this.student1.courses), 3);
    t.checkExpect(this.dejavuCHC0.visitCons((ConsList<Course>) this.student2.courses), 0);
    t.checkExpect(this.dejavuCHC1.visitCons((ConsList<Course>) this.student2.courses), 1);
    t.checkExpect(this.dejavuCHC2.visitCons((ConsList<Course>) this.student2.courses), 2);
    t.checkExpect(this.dejavuCHC3.visitCons((ConsList<Course>) this.student2.courses), 1);
    t.checkExpect(this.dejavuCHC4.visitCons((ConsList<Course>) this.student2.courses), 2);
    t.checkExpect(this.dejavuCHC0.visitCons((ConsList<Course>) this.student3.courses), 0);
    t.checkExpect(this.dejavuCHC1.visitCons((ConsList<Course>) this.student3.courses), 0);
    t.checkExpect(this.dejavuCHC2.visitCons((ConsList<Course>) this.student3.courses), 1);
    t.checkExpect(this.dejavuCHC3.visitCons((ConsList<Course>) this.student3.courses), 2);
    t.checkExpect(this.dejavuCHC4.visitCons((ConsList<Course>) this.student3.courses), 2);
    t.checkExpect(this.dejavuCHC0.visitCons((ConsList<Course>) this.student4.courses), 0);
    t.checkExpect(this.dejavuCHC1.visitCons((ConsList<Course>) this.student4.courses), 3);
    t.checkExpect(this.dejavuCHC2.visitCons((ConsList<Course>) this.student4.courses), 2);
    t.checkExpect(this.dejavuCHC3.visitCons((ConsList<Course>) this.student4.courses), 2);
    t.checkExpect(this.dejavuCHC4.visitCons((ConsList<Course>) this.student4.courses), 5);
  }

  // tests in the StudentAppearsCourse class
  void testStudentAppearsCourse(Tester t) {
    // this.apply(Course c, int numAppearancesSoFar) tests
    initData();
    t.checkExpect(this.studentAC0.apply(this.nullCourse, 0), 0);  
    t.checkExpect(this.studentAC0.apply(this.nullCourse, 17), 17);  
    t.checkExpect(this.studentAC0.apply(this.nullCourse, -5), -5);  
    t.checkExpect(this.studentAC0.apply(this.english, 0), 0);  
    t.checkExpect(this.studentAC0.apply(this.english, 12), 12);  
    t.checkExpect(this.studentAC0.apply(this.english, -4), -4);  
    t.checkExpect(this.studentAC0.apply(this.math, 0), 0);  
    t.checkExpect(this.studentAC0.apply(this.math, 17), 17);  
    t.checkExpect(this.studentAC0.apply(this.math, -5), -5);  
    t.checkExpect(this.studentAC0.apply(this.science, 0), 0);  
    t.checkExpect(this.studentAC0.apply(this.science, 12), 12);  
    t.checkExpect(this.studentAC0.apply(this.science, -4), -4);  
    t.checkExpect(this.studentAC0.apply(this.history, 0), 0);  
    t.checkExpect(this.studentAC0.apply(this.history, 17), 17);  
    t.checkExpect(this.studentAC0.apply(this.history, -5), -5);  
    t.checkExpect(this.studentAC0.apply(this.research, 0), 0);  
    t.checkExpect(this.studentAC0.apply(this.research, 12), 12);  
    t.checkExpect(this.studentAC0.apply(this.research, -4), -4);  
    t.checkExpect(this.studentAC1.apply(this.nullCourse, 0), 0);  
    t.checkExpect(this.studentAC1.apply(this.english, 0), 1);  
    t.checkExpect(this.studentAC1.apply(this.math, 0), 0);  
    t.checkExpect(this.studentAC1.apply(this.science, 0), 0);  
    t.checkExpect(this.studentAC1.apply(this.history, 0), 1);  
    t.checkExpect(this.studentAC1.apply(this.research, 0), 1);  
    t.checkExpect(this.studentAC2.apply(this.nullCourse, 0), 0);  
    t.checkExpect(this.studentAC2.apply(this.english, 0), 1);  
    t.checkExpect(this.studentAC2.apply(this.math, 0), 1);  
    t.checkExpect(this.studentAC2.apply(this.science, 0), 0);  
    t.checkExpect(this.studentAC2.apply(this.history, 0), 0);  
    t.checkExpect(this.studentAC2.apply(this.research, 0), 0);  
    t.checkExpect(this.studentAC3.apply(this.nullCourse, 0), 0);  
    t.checkExpect(this.studentAC3.apply(this.english, 0), 0);  
    t.checkExpect(this.studentAC3.apply(this.math, 0), 1);  
    t.checkExpect(this.studentAC3.apply(this.science, 0), 1);  
    t.checkExpect(this.studentAC3.apply(this.history, 0), 0);  
    t.checkExpect(this.studentAC3.apply(this.research, 0), 0);  
    t.checkExpect(this.studentAC4.apply(this.nullCourse, 0), 0);  
    t.checkExpect(this.studentAC4.apply(this.english, 0), 1);  
    t.checkExpect(this.studentAC4.apply(this.math, 0), 1);  
    t.checkExpect(this.studentAC4.apply(this.science, 0), 1);  
    t.checkExpect(this.studentAC4.apply(this.history, 0), 1);  
    t.checkExpect(this.studentAC4.apply(this.research, 0), 1);  
  }
}
