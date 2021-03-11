static class Tri{                                                          //Used statically for saved space as well as versatility

  static float a, b, c;                                                    //can be set and used with getC() with no argument if values are to be retained for other calculations.

  static float getC(){ return sqrt((a * a) + (b * b)); }                   //Use if Tri.a and Tri.b are already set and want c calculated from them.  Good for retaining a and b within calculations area for other uses.
  static float getC(float a, float b){ return sqrt((a * a) + (b * b)); }   //Use if no setting to Tri is desired, mor of a one off

}
