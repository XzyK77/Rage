class Room{

  Vec2f loc;
  float size;
  color c;
  byte roomType;
  int blockSize;
  Block[] allBlocks;
  float[] xPts, yPts;
  
  Room(){ this(new Vec2f(0.0,0.0), 10, byte(0), color(255,255,255), 5, loadImage("Block01.png")); }
  
  Room(Vec2f _loc, float _roomSize, byte _roomType, color _c, int _blockSize, PImage _sprite){
  
    loc = _loc;
    size = _roomSize;
    blockSize = _blockSize;
    c = _c; 
    roomType = _roomType;
    allBlocks = new Block[0];
    xPts = new float[]{0,0,0};
    yPts = new float[]{0,0,0};
    
    switch(_roomType){
     
      case 0: break;                                      //Empty
      case 1:                                             //Start
        xPts = new float[]{ -1000, -1250, -1250,  1250};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{   800,   800,  1250,  1250};
        break;
      case 2:                                             //End
        xPts = new float[]{ -1200,  1100, 1100, 600, 600, 1150,  1150,  1250};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{  1200,  1200,    0,   0, -60,  -60,  1200,  1250};
        break;
      case 3:                                             //Half - A
        xPts = new float[]{ -750, -250, 100,  600, 1250,  350,   100,  -100, -900};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{ 1000,  100, 100, 1000, 1000, -750, -1250, -1250, -300};
        break; 
      case 4:                                             //Arena Square
        xPts = new float[]{ -1250, -1250, 1250,  1250,  -900};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{ -1250,  1250, 1250, -1250, -1250};
        break;
      case 5:                                             //Room G
        xPts = new float[]{  1250,  1100,     0, -1100, -1250, -1600, -1250, -1100,    0, 1100, 1250, 1600,  1250, -450, -800, -800, -200, 400, 400, 0};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{ -1250, -1400, -1600, -1400, -1250,     0,  1250,  1400, 1600, 1400, 1250,    0,  -800, -900, -700,  700, 1000, 900, 500, 0};
        break;
      default:  println("DEFAULT:: CLASS:: ROOM:: TYPE");
    }
    setAllBlocks(xPts, yPts, _sprite);
  }
  
  void display(Vec2f _offset){
    
    //ellipse(loc.x + _offset.x, loc.y + _offset.y, size, size);                        //Can display size of room with this.
    for(int i = 0; i < allBlocks.length; i++){ allBlocks[i].displayWithResize(_offset, true); }
    
  }
  
  void setAllBlocks(float[] xPts, float[] yPts, PImage _sprite){                        //Uses an array of points to place blocks along those points.  Will scale points to fit into circle's size.
    
    float biggestX = 0;                                                                 //Need to scale according to room size
    int xIndex = 0;
    float biggestY = 0;
    int yIndex = 0;
    int lgValIndex = 0;
    
    for(int i = 0; i < xPts.length; i++){                                               //loops only call xPts as both xPts and yPts are the same sizes
      
      float xCandidate = sqrt(xPts[i] * xPts[i]);                                       //checks x point for biggest size
      if(xCandidate >= biggestX){ xIndex = i; biggestX = xCandidate;}
      float bigYCandidate = sqrt(yPts[i] * yPts[i]);                                    //checks y point for biggest size
      if(bigYCandidate >= biggestY){ yIndex = i; biggestY = bigYCandidate; }
    }
    
    if(biggestX > biggestY){ lgValIndex = xIndex; }
    else{ lgValIndex = yIndex; }                                                        //Only thing we are looking for is the index with the largest value to base scaleAdjustment.
    
    float scaleAdjustment = (size / 3) / Tri.getC(xPts[lgValIndex], yPts[lgValIndex]);  //Finds scale adjustment for use with all points based on modified size and actual distance of farthest point
    
    for(int i = 0; i < xPts.length; i++){                                               //Adjusts all values with the scaleAdjustment
    
      xPts[i] = xPts[i] * scaleAdjustment; 
      yPts[i] = yPts[i] * scaleAdjustment; 
    }     
    
    for(int i = 0; i < xPts.length - 1; i++){
      
      Tri.a = xPts[i] - xPts[i + 1];                                                    //Find distance between this point and next point
      Tri.b = yPts[i] - yPts[i + 1];
      float distance = Tri.getC();
      float numOfBlocks = distance / blockSize;                                         //will result in number with remainder, need to divvy out remaider over rest of blocks
      float newBlockSize = blockSize + ((distance % blockSize) / numOfBlocks);          //Distributes remainder of block space into all blocks
      
      numOfBlocks = int(distance / newBlockSize);
      float subA = Tri.a / numOfBlocks;
      float subB = Tri.b / numOfBlocks;
      
      Block[] segmentBlocks = new Block[int(numOfBlocks)];                              //initialize this segment for block generation
        
      for(int j = 0; j < int(numOfBlocks); j++){ segmentBlocks[j] = new Block(new Vec3f(loc.x + xPts[i] - (j * subA), loc.y + yPts[i] - (j * subB), newBlockSize), true, _sprite); }  //generate blocks on line
      
      Block[] temp = new Block[allBlocks.length + segmentBlocks.length];                                                                                                     //Merge array with allBlocks
       
      for(int j = 0; j < allBlocks.length; j++){ temp[j] = allBlocks[j]; }                                                                                                   //copy allBlocks into new array
      for(int j = allBlocks.length; j < allBlocks.length + segmentBlocks.length; j++){ temp[j] = segmentBlocks[j - allBlocks.length]; }                                      //Add this segment to new array
      
      allBlocks = new Block[temp.length];                                                                                                                                    //re-initialize allBlocks to correct size
      for(int j = 0; j < temp.length; j++){ allBlocks[j] = temp[j]; }                                                                                                        //Set allBlocks to new array
    }
  }
}
