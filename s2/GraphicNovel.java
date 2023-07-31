// to represent information about a graphic novel
class GraphicNovel {
  String title;
  String author;
  String artist;
  int year; // when published
  double cost; // in USD
  boolean monochrome; // black and white
  
  GraphicNovel(String title, String author, String artist,
      int year, double cost, boolean monochrome) {
    this.title = title;
    this.author = author;
    this.artist = artist;
    this.year = year;
    this.cost = cost;
    this.monochrome = monochrome;
  }
}

class ExamplesGraphicNovel {
  
  ExamplesGraphicNovel() {
  }
  
  // Maus, written and drawn by Spiegelman, 1980, $17.60, black and white
  // Logicomix, written by Doxiadis, art by Papadatos, 2009, $21.00, colored
  // Jane Eyre, written and Drawn by Charlotte Bronte, 1847, $8.71, black and white
  GraphicNovel maus = new GraphicNovel("Maus", "Spiegelman", "Spiegelman", 1980, 17.60, true);
  GraphicNovel logicomix = 
      new GraphicNovel("Logicomix", "Doxiadis", "Papadatos", 2009, 21.00, false);
  GraphicNovel janeEyre = 
      new GraphicNovel("Jane Eyre", "Charlotte Bronte", "Charlotte Bronte", 1847, 8.71, true);
}