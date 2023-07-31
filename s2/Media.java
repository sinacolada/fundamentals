import tester.Tester;

// a piece of media
interface IMedia {
  
  // is this media really old?
  boolean isReallyOld();
  
  // are captions available in this language?
  boolean isCaptionAvailable(String language);
  
  // a string showing the proper display of the media
  String format();
}

abstract class AMedia implements IMedia {
  String title;
  ILoString captionOptions;
  
  AMedia(String title, ILoString captionOptions) {
    this.title = title;
    this.captionOptions = captionOptions;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.title ... --String
   * ... this.captionOptions ... --ILoString
   * Methods:
   * ... this.isReallyOld() ... -boolean
   * ... this.isCaptionAvailable(String language) ... --boolean
   * ... this.format() ... --String
   * Methods for Fields:
   * ... this.captionOptions.containsCaption(String language) ... --boolean
   */
  
  public boolean isReallyOld() {
    return false;
  }
  
  public boolean isCaptionAvailable(String language) {
    return this.captionOptions.containsCaption(language);
  }
  
  public abstract String format();
}

// represents a movie
class Movie extends AMedia {
  int year;
  
  Movie(String title, int year, ILoString captionOptions) {
    super(title, captionOptions);
    this.year = year;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.title ... --String
   * ... this.year ... --int
   * ... this.captionOptions ... --ILoString
   * Methods:
   * ... this.isReallyOld() ... -boolean
   * ... this.isCaptionAvailable(String language) ... --boolean
   * ... this.format() ... --String
   * Methods for Fields:
   * ... this.captionOptions.containsCaption(String language) ... --boolean
   */
  
  public boolean isReallyOld() {
    return this.year < 1930;
  }
  
  public String format() {
    return this.title + " (" + this.year + ")";
  }
}

// represents a TV episode
class TVEpisode extends AMedia {
  String showName;
  int seasonNumber;
  int episodeOfSeason;

  TVEpisode(String title, String showName, int seasonNumber, int episodeOfSeason,
      ILoString captionOptions) {
    super(title, captionOptions);
    this.showName = showName;
    this.seasonNumber = seasonNumber;
    this.episodeOfSeason = episodeOfSeason;
  }

  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.title ... --String
   * ... this.showName ... --String
   * ... this.seasonNumber ... --int
   * ... this.episodeOfSeason ... --int 
   * ... this.captionOptions ... --ILoString
   * Methods:
   * ... this.isReallyOld() ... -boolean
   * ... this.isCaptionAvailable(String language) ... --boolean
   * ... this.format() ... --String
   * Methods for Fields:
   * ... this.captionOptions.containsCaption(String language) ... --boolean
   */

  public String format() {
    return this.showName + " " + this.seasonNumber + "."
        + this.episodeOfSeason + " - " + this.title;
  }
}

// represents a YouTube video
class YTVideo extends AMedia {
  String channelName;
  
  public YTVideo(String title, String channelName, ILoString captionOptions) {
    super(title, captionOptions);
    this.channelName = channelName;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.title ... --String
   * ... this.channelName ... --String
   * ... this.captionOptions ... --ILoString
   * Methods:
   * ... this.isReallyOld() ... -boolean
   * ... this.isCaptionAvailable(String language) ... --boolean
   * ... this.format() ... --String
   * Methods for Fields:
   * ... this.captionOptions.containsCaption(String language) ... --boolean
   */
  
  public String format() {
    return this.title + " by " + this.channelName;
  }
  
}

// lists of strings
interface ILoString {
  
  // does list of string contain the given caption option
  boolean containsCaption(String language);
}

// an empty list of strings
class MtLoString implements ILoString {
  MtLoString(){
  }
  
  public boolean containsCaption(String language) {
    return false;
  }
}

// a non-empty list of strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;
  
  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /*
   * TEMPLATE:
   * -----------
   * Fields:
   * ... this.first ... --String
   * ... this.rest ... --ILoString
   * Methods:
   * ... this.containsCaption(String language) ... --boolean
   * Methods for Fields:
   * ... this.rest.containsCaption(String language) ... --boolean
   */
  
  public boolean containsCaption(String language) {
    if (this.first.equals(language)) {
      return true;
    }
    return this.rest.containsCaption(language);
  }
}

class ExamplesMedia {
  ExamplesMedia(){
  }
  
  ILoString empty = new MtLoString();
  
  ILoString fantasiaCaptions = new ConsLoString("English",
      new ConsLoString("Russian", new ConsLoString("French", 
      new ConsLoString("Spanish", this.empty))));
  ILoString moonTripCaptions = new ConsLoString("French",
      new ConsLoString("English", this.empty));
  IMedia fantasia = new Movie("Fantasia", 1940, this.fantasiaCaptions);
  IMedia moonTrip = new Movie("A Trip to the Moon", 1902, this.moonTripCaptions);
  
  ILoString dbzCaptions = new ConsLoString("English",
      new ConsLoString("Japanese", this.empty));
  ILoString codeGeassCaptions = new ConsLoString("English",
      new ConsLoString("Japanese", this.empty));
  IMedia dbzEp1 = new TVEpisode("A Home for Infinite Losers", "Dragon Ball Z", 1, 1, 
      this.dbzCaptions);
  IMedia codeGeassEp1 = new TVEpisode("The Day a New Demon Was Born", "Code Geass", 1, 1,
      this.codeGeassCaptions);
  
  ILoString vsauseCaptions = new ConsLoString("English",
      new ConsLoString("Spanish", this.empty));
  ILoString veritasiumCaptions = new ConsLoString("English", 
      new ConsLoString("Arabic", this.empty));
  IMedia vsauseYT = new YTVideo("What's The Most Dangerous Place on Earth?", "Vsauce",
      this.vsauseCaptions);
  IMedia veritasiumYT = new YTVideo("Spinning Black Holes", "Veritasium", 
      this.veritasiumCaptions);
  
  // tests functionality of isReallyOld method on example media
  boolean testIsReallyOld(Tester t) {
    return t.checkExpect(this.fantasia.isReallyOld(), false)
        && t.checkExpect(this.moonTrip.isReallyOld(), true)
        && t.checkExpect(this.dbzEp1.isReallyOld(), false)
        && t.checkExpect(this.codeGeassEp1.isReallyOld(), false)
        && t.checkExpect(this.vsauseYT.isReallyOld(), false)
        && t.checkExpect(this.veritasiumYT.isReallyOld(), false);
  }
  
  // tests functionality of isCaptionAvailable method on example media
  boolean testIsCaptionAvailable(Tester t) {
    return t.checkExpect(this.fantasia.isCaptionAvailable("Arabic"), false)
        && t.checkExpect(this.moonTrip.isCaptionAvailable("French"), true)
        && t.checkExpect(this.dbzEp1.isCaptionAvailable("Japanese"), true)
        && t.checkExpect(this.codeGeassEp1.isCaptionAvailable("Swedish"), false)
        && t.checkExpect(this.vsauseYT.isCaptionAvailable("French"), false)
        && t.checkExpect(this.veritasiumYT.isCaptionAvailable("Arabic"), true);
  }
  
  // tests functionality of format method on example media
  boolean testFormat(Tester t) {
    return t.checkExpect(this.fantasia.format(), "Fantasia (1940)")
        && t.checkExpect(this.moonTrip.format(), "A Trip to the Moon (1902)")
        && t.checkExpect(this.dbzEp1.format(), "Dragon Ball Z 1.1 - A Home for Infinite Losers")
        && t.checkExpect(this.codeGeassEp1.format(), 
            "Code Geass 1.1 - The Day a New Demon Was Born")
        && t.checkExpect(this.vsauseYT.format(), 
            "What's The Most Dangerous Place on Earth? by Vsauce")
        && t.checkExpect(this.veritasiumYT.format(), "Spinning Black Holes by Veritasium");
  }
  
  // tests functionality of containsCaption
  boolean testContainsCaption(Tester t) {
    return t.checkExpect(this.vsauseCaptions.containsCaption("English"), true)
        && t.checkExpect(this.empty.containsCaption("Spanish"), false)
        && t.checkExpect(this.fantasiaCaptions.containsCaption("English"), true);
  }
}